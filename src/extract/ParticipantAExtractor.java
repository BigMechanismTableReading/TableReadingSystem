package extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;
import tablecontents.ParticipantA;
import tablecontents.Protein;
import utils.Pair;
import extract.TextExtractor;
import tableBuilder.TableBuf;
import tableBuilder.TableBuf.Column;
import extract.index.ExtractBiopax;
import extract.lookup.AbbreviationLookup;
import extract.lookup.ChEMBLLookup;
import extract.lookup.ChemicalLookup;
import extract.lookup.Lookup;
import extract.lookup.TabLookup;
import extract.lookup.YeastLookup;
import extract.types.Reaction;
import main.TableReader;

/**
 * Gets the possible participantA of the table
 * @author sloates, vhsiao
 *
 */
public class ParticipantAExtractor {

	HashMap<String,String> possibleA = null;
	private enum ExtractionLocation{
		FOLD,TITLE,CAPTION,TEXT,NONE
	}
	/**
	 * Helper method to add new participantA
	 * @param participantAs
	 * @param partAuntrans
	 * @param partA
	 * @param f
	 * @param col
	 */
	private void addPartA(List<ParticipantA> participantAs,
			String partAuntrans, String partA, ColumnContents f, 
			Column col,double confidenceLevel) {
		for (ParticipantA partAentry : participantAs){
			if (partAentry.equalString(partA)){
				partAentry.addToData(f, col);
				return;
			}
		}
		ParticipantA newA = new ParticipantA(partA, partAuntrans,confidenceLevel);
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
			if(firstCap == -1 && !Character.isLowerCase(partA.charAt(i))){
				
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
		String [] split = normalized.split("\\s|;|/|\\(|\\)");
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
		/*if(!possA.isEmpty())
			return possA;
		return null;*/
		return possA;
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
		for(String aWord : textA){
			for(String aText : allForms(aWord)){
				System.err.println(aWord);
				Pair<String,String> transTextApair = groundPartA(aText,allB,true,true);
				if(transTextApair != null){
					String transTextA = transTextApair.getB();
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
		List<ParticipantA> participantAs = new ArrayList<ParticipantA>();
		for(ColumnContents f : contents.keySet()){
			for (TableBuf.Column col : contents.get(f)){
				HashMap<String,String> possA = checkPartA(col.getHeader().getData(), allB,true,false);
				if (possA != null && !possA.isEmpty()){
					possibleA.putAll(possA);
					String partA = checkPartAText(allB, r,possA.keySet(),textA);
					if(partA != null){
						addPartA(participantAs, possA.get(partA),partA, f, col,confidenceLevel(ExtractionLocation.FOLD));
					}
				}
			}
		}
		return participantAs;
	}
	
	private List<ParticipantA> getCaptionPartA(HashMap<ColumnContents,List<TableBuf.Column>> contents,
			Reaction r, Set<String> allB,TableBuf.Table table, List<String> textA){
		List<ParticipantA> participantAs = new ArrayList<ParticipantA>();
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
					if(wordList!= null && !wordList.isEmpty()){
						for(String word: wordList.keySet() ){
							possA.put(word, wordList.get(word));
						}
					}
				}
			
				if(title){
					String partA = checkPartAText(allB, r, possA.keySet(),textA);
					System.out.println(possA);
					if(partA!= null){
						participantAs.add(new ParticipantA(partA, possA.get(partA), 
								contents,confidenceLevel(ExtractionLocation.TITLE)));
						
						return participantAs;
					}
				}
				title = false;
			}
			
		}
		possibleA.putAll(possA);
		String partA = checkPartAText(allB, r, possA.keySet(),textA);
		System.err.println(partA);
		if(partA!= null){
			
			participantAs.add(new ParticipantA(partA, possA.get(partA), contents,confidenceLevel(ExtractionLocation.CAPTION)));
			return participantAs;
		}
		return participantAs;
	}
	
	/**
	 * Calculates the confidence level (that is used later by text reading teams)
	 * @param extractedFrom
	 * @return
	 */
	private double confidenceLevel(ExtractionLocation extractedFrom){
		double confidenceLevel = 1;
		double multiplier = .75;
		for(ExtractionLocation loc : ExtractionLocation.values()){
			if(loc == extractedFrom)
				return confidenceLevel;
			confidenceLevel *= multiplier;//TODO better math/reasoning here so that values make sense
			multiplier *= multiplier;
		}
		return -1;
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
			Reaction r ){
		Set<String> allB = new HashSet<String>();
		Lookup t = TabLookup.getInstance();
		makeAllBs(allB,partB.values(),partBUntrans.values(),t);
		HashMap<String, Integer> hashA= TextExtractor.extractParticipantA(allB, table.getSource().getPmcId().substring(3),
				r.getConjugationBase());
		
		String PMCID = table.getSource().getPmcId();
		possibleA = new HashMap<String,String>();

		
		/*//TODO: TOOK OUT FRIES/BIOPAX STUFF
		 * 
		 * ExtractBiopax extractor = new ExtractBiopax(PMCID + ".json", r.getConjugationBase().get(0));
		HashMap<String, Integer> friesA = extractor.getACount(allB);
		//why??
		boolean simple_reaction = TableReader.simple_reaction;
		//if(simple_reaction){
			possibleA = new HashMap<String,String>();
		//}
		for (String a : friesA.keySet()){
			if(hashA.containsKey(a)){
				hashA.put(a, hashA.get(a) + friesA.get(a));
			} else {
				hashA.put(a, friesA.get(a));
			}
		}*/
		List<String> textA = TextExtractor.sortByValue(hashA);
		List<ParticipantA> participantAs = getFoldPartA(contents, r, allB, table,textA);
		System.out.println(textA);
		
		if (participantAs.isEmpty()){
			participantAs = getCaptionPartA(contents, r, allB, table, textA);
			if (!participantAs.isEmpty()){
				return participantAs;
			}
		}else{
			return participantAs;
		}
		
		//BEST A IF NOTHING IS GOTTEN
		//TODO decide best way to do this, possibly use drug suffix lookup??
		int listPos =0;
		for(String a : textA){
			if(a.length() > 2){
				String aTrans = translatePartA(a.toUpperCase());
				if(aTrans != null){
					System.out.println("Text A: " + textA);
					System.out.println("All B: " + allB);
					participantAs.add(new ParticipantA(aTrans,a,contents,confidenceLevel(ExtractionLocation.TEXT),listPos));
					return participantAs;
				}
			}
			listPos++;
		}
		//If no A was found by the text extractor
		participantAs.add(new ParticipantA("unknown", "unknown", contents,confidenceLevel(ExtractionLocation.NONE)));
		return participantAs;
	}

}
