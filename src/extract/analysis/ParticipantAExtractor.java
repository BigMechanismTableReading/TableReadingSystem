package extract.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nxml12integration.FriesParser;
import tablecontents.ColumnContents;
import tablecontents.ParticipantA;
import tablecontents.Protein;
import extract.TextExtractor;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.lookup.AbbreviationLookup;
import extract.lookup.ChEMBLLookup;
import extract.lookup.ChemicalLookup;
import extract.lookup.Lookup;
import extract.lookup.TabLookup;
import extract.lookup.YeastLookup;
import extract.types.Reaction;

/**
 * Gets the possible participantA of the table
 * @author sloates, vhsiao
 *
 */
public class ParticipantAExtractor {

	/**
	 * Helper method to add new participantA
	 * @param participantAs
	 * @param partAuntrans
	 * @param partA
	 * @param f
	 * @param col
	 */
	private void addPartA(List<ParticipantA> participantAs,
			String partAuntrans, String partA, ColumnContents f, Column col) {
		for (ParticipantA partAentry : participantAs){
			if (partAentry.equalString(partA)){
				partAentry.addToData(f, col);
				return;
			}
		}
		ParticipantA newA = new ParticipantA(partA, partAuntrans);
		newA.addToData(f, col);
		participantAs.add(newA);
	}

	/**
	 * Used as a backup to ground yeast genes.
	 * @param partA
	 * @return
	 */
	private static String yeastGround(String partA){
		YeastLookup y = YeastLookup.getInstance();
		if(y.uniprot.containsKey(partA))
			return "Uniprot:" + y.uniprot.get(partA);
		if(y.swisprot.containsKey(partA))
			return "Uniprot:" +y.swisprot.get(partA);
		if(y.genename.containsKey(partA))
			return "Uniprot:" +y.genename.get(partA);
		if(y.english.containsKey(partA))
			return "Uniprot:" +  y.english.get(partA).get(0);
		return null;
	}
	/**
	 * Translates partA if it can be found in any of the lookups, 
	 * if not it returns null
	 * @param partA
	 * @return
	 */
	public static String translatePartA(String partA){
		ChemicalLookup chem = ChemicalLookup.getInstance();
		//TODO put in abbreviation and last capital method
		if(chem.chemicals.containsKey(partA))
			return chem.chemicals.get(partA);
		for (Protein p : Protein.protList){
			String trans = p.cellMatch(partA);
			if (trans != null){
				return trans;
			}
		}
		if(partA.length() > 3){
			String trans = ChEMBLLookup.abbrLookup(partA);
			if (trans != null){
				return trans;
			}
		}
		return yeastGround(partA);
	}

	/**
	 * Returns original and possible alternate forms of a potential participantA 
	 * @param partA
	 * @return
	 */
	private List<String> allForms(String partA){
		int firstCap = -1;
		List<String> allForms = new ArrayList<String>();
		allForms.add(partA);
		for(int i = partA.length()-1; i >= 0; i--){
			if(firstCap == -1 && !Character.isLowerCase(partA.charAt(i)) ){
				firstCap = i;
			}else if(firstCap != -1 && Character.isLowerCase(partA.charAt(i))){
				allForms.add(partA.substring(i+1,partA.length()));
				allForms.add(partA.substring(i+1, firstCap+1));
				i = -1;
			}
		}
		if(firstCap != -1){
			allForms.add(partA.substring(0,firstCap+1));
		}
		return allForms;	
	}

	/**
	 * Grounds potential participantA as an abbreviation or normal entity
	 * @param form
	 * @param partBs
	 * @param fold
	 * @param title
	 * @return
	 */
	private Pair<String, String> groundPartA(String form,Set<String>partBs,boolean fold,boolean title){
		String partA = null;
		if(form.length() > 2){
			partA = translatePartA(form.toUpperCase());
		}
		if (partA == null && form.length() > 1 && form.toUpperCase().equals(form)){
			partA = AbbreviationLookup.abbrLookup(form);
		}	
		if(partA != null &&  (!partBs.contains(partA) || fold == true ||title == true)){
			return new Pair<String, String>(form, partA);
		}
		return null;
	}

	/**
	 * Gets all possible substrings of partA that could match.
	 * Attempts to translate those strings as well as the original.
	 * Returns a Hashmap of translated to untranslated
	 * Returns null if no matches can be grounded.
	 * @param guess
	 * @param partBs
	 * @param fold
	 * @param title
	 * @return
	 */
	private HashMap<String,String> checkPartA(String guess, Set<String> partBs, boolean fold,boolean title){
		String normalized = guess.replaceAll("-","");
		String [] split = normalized.split("\\s|;|/|\\(|\\)");//TODO look at removing / from it
		HashMap<String,String> possA = new HashMap<String,String>();
		HashSet<String> checkWords = new HashSet<String>();
		for(String word: split){
			if(title || fold){
				for (String form : allForms(word)){
					if (!checkWords.contains(form)){
						Pair<String,String> partA = groundPartA(form,partBs,fold,title);
						if (partA != null){
							possA.put(partA.getB(),partA.getA());
						}
						checkWords.add(form);
					}
				}
			}else{
				if (!checkWords.contains(word)){
					Pair<String,String> partA = groundPartA(word,partBs,fold,title);
					if (partA != null){
						possA.put(partA.getB(),partA.getA());
					}
					checkWords.add(word);
				}
			}
		}
		if(!possA.isEmpty())
			return possA;
		return null;
	}

	/**
	 * Checks the possible A against possible As extracted from the text, returning a match
	 * if no match then this method returns null
	 * @param allB
	 * @param r
	 * @param possA
	 * @return
	 */
	private String checkPartAText(Set<String> allB,Reaction r, Set<String> possA,List<String> textA){

		//		System.out.println(possA);
		//		System.out.println(allB);
		//		System.out.println(textA);
		//		System.out.println(textA);
		Set<String> transA = new HashSet<String>();
		for(String aWord : textA){
			for(String aText : allForms(aWord)){

				Pair<String,String> transTextApair = groundPartA(aText,allB,true,true);
				if(transTextApair != null){
					String transTextA = transTextApair.getB();
					//					System.out.println(transTextA);
					//					System.out.println(transTextA + "  " + aText);
					if(possA.contains(transTextA)){
						return transTextA;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Creates a set of allBs in translated and untranslated forms
	 * @param allB
	 * @param trans
	 * @param untrans
	 * @param t
	 */
	private void makeAllBs(Set<String> allB, Collection<String> trans, Collection<String> untrans,Lookup t){
		allB.addAll(trans);
		allB.addAll(untrans);		
		for(String fullB : trans){
			if(fullB.length()>9){
				String b = fullB.substring(8);
				if(t.uniToGene.containsKey(b))
					allB.addAll(t.uniToGene.get(b));
			}
		}
	}

	/**
	 * Checks the fold columns for participantA
	 * @param contents
	 * @param r
	 * @param allB
	 * @param table
	 * @param textA 
	 * @return
	 */
	private List<ParticipantA> getFoldPartA(HashMap<ColumnContents,List<TableBuf.Column>> contents,
			Reaction r, Set<String> allB,TableBuf.Table table, List<String> textA){
		//TODO Verify the ordering
		List<ParticipantA> participantAs = new ArrayList<ParticipantA>();
		for(ColumnContents f : contents.keySet()){
			for (TableBuf.Column col : contents.get(f)){
				System.out.println(f + " " + col.getHeader().getData());
				HashMap<String,String> possA = checkPartA(col.getHeader().getData(), allB,true,false);
				if (possA != null){
					String partA = checkPartAText(allB, r,possA.keySet(),textA);
					if(partA != null){
						addPartA(participantAs, possA.get(partA),partA, f, col);
					}
				}
			}
		}
		return participantAs;
	}

	/**
	 * First checks the fold column for potential participantA then checks the title and caption.
	 * If no options are found, the top result from the text extractor is then used.
	 * @param table
	 * @param partB
	 * @param partBUntrans
	 * @param contents
	 * @param r
	 * @return
	 */
	public List<ParticipantA> getParticipantAs(TableBuf.Table table,
			HashMap<Integer,String> partB, 	HashMap<Integer,String> partBUntrans,
			HashMap<ColumnContents,List<TableBuf.Column>> contents,
			Reaction r){
		//TODO break up into two methods, fold partA and caption partA
		System.out.println("In partA");
		Set<String> allB = new HashSet<String>();
		Lookup t = TabLookup.getInstance();
		System.out.println("Making list of all B forms");
		makeAllBs(allB,partB.values(),partBUntrans.values(),t);
		System.out.println("Text Extractor");
		List<String>  textA= TextExtractor.extractParticipantA(allB, table.getSource().getPmcId().substring(3),
				r.getConjugationBase());		
		String PMCID = table.getSource().getPmcId();
		//TODO decide better way to use their system
		FriesParser fries = new FriesParser( PMCID + ".uaz.events.json",PMCID + ".uaz.entities.json");
		List<String> friesA = fries.getPossA(allB);
		textA.addAll(0, friesA);
		//TODO filter by the interaction type as to be more precise
		System.out.println("Fold PartA");
		List<ParticipantA> participantAs = getFoldPartA(contents, r, allB, table,textA);
		System.out.println(textA);
		if (participantAs.isEmpty()){
			System.out.println("Caption partA");
			HashMap<String, String> possA = new HashMap<String, String>();
			boolean title = true;
			for(String wholeCaption : table.getCaptionList()){
				String[] subtitles = wholeCaption.split(";");
				for (String caption : subtitles){
					caption = caption.replaceAll("-", "");
					Pattern p = Pattern.compile("[A-Z[a-z]][\\w]*[A-Z0-9]+[\\w]*");
					Matcher m = p.matcher(caption);
					while(m.find()){
						String a = m.group();
						HashMap<String, String> wordList = checkPartA(a,allB,false,title);
						if(wordList!= null){
							for(String word: wordList.keySet() ){
								possA.put(word, wordList.get(word));
							}
						}

					}
					if(title){
						String partA = checkPartAText(allB, r, possA.keySet(),textA);
						System.out.println(possA);
						if(partA!= null){
							participantAs.add(new ParticipantA(partA, possA.get(partA), contents));
							return participantAs;
						}
					}
					title = false;
				}
			}

			String partA = checkPartAText(allB, r, possA.keySet(),textA);
			System.out.println(possA);
			if(partA!= null){
				participantAs.add(new ParticipantA(partA, possA.get(partA), contents));
				return participantAs;
			}
		}else{
			return participantAs;
		}
		//_______________________________________________________________________________________________________________
		//BEST A IF NOTHING IS GOTTEN
		for(String a : textA){
			if(a.length() > 2){
				String aTrans = translatePartA(a.toUpperCase());
				if(aTrans != null){
					System.err.println(textA);
					participantAs.add(new ParticipantA(aTrans,a,contents));
					return participantAs;
				}
			}
		}
		//_________________________________________________________________________________________________________________
		participantAs.add(new ParticipantA("unknown", "unknown", contents));
		return participantAs;
	}

}
