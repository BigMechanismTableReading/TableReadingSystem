package extract.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.GeneName;
import tablecontents.ParticipantA;
import tablecontents.Protein;
import extract.TextExtractor;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.lookup.AbbreviationLookup;
import extract.lookup.ChemicalLookup;
import extract.lookup.TabLookup;
import extract.types.Reaction;

/**
 * Gets the possible participantA
 * @author sloates
 *
 */
public class ParticipantAExtractor {

	/**
	 * Helps add a new participantA
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
	 * Translates partA if it can be found, else returns null
	 * @param partA
	 * @return
	 */
	private String translatePartA(String partA){
		ChemicalLookup chem = ChemicalLookup.getInstance();
		//TODO put in abbreviation and last capital method
		if(chem.chemicals.containsKey(partA.toUpperCase()))
			return chem.chemicals.get(partA.toUpperCase());
		for (Protein p : Protein.protList){
			String trans = p.cellMatch(partA.toUpperCase());
			if (trans != null){
				return trans;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns original and last capital of partA
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
	 * Grounds partA in various Databases
	 * @param form
	 * @param partBs
	 * @param fold
	 * @param title
	 * @return
	 */
	private Pair<String, String> groundPartA(String form,Set<String>partBs,boolean fold,boolean title){
		String partA = null;
		if(form.length() > 2){
			partA = translatePartA(form);
		}
		if (partA == null && form.toUpperCase().equals(form)){
			partA = AbbreviationLookup.abbrLookup(form);
		}		
		if(partA != null &&  (!partBs.contains(partA) || fold == true ||title == true)){
			return new Pair<String, String>(form, partA);
		}
		return null;
	}
	
	/**
	 * Gets all possible substrings of partA that could match, and checks those strings as well as the original.
	 * Returns null if no matches can be grounded
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
	private void makeAllBs(Set<String> allB, Collection<String> trans, Collection<String> untrans,TabLookup t){
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
	
	private List<ParticipantA> getCaptionA(){
		return null;
	}
	/**
	 * First checks the fold column for potential participantA then checks the title and caption.
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
		TabLookup t = TabLookup.getInstance();
		System.out.println("Making list of all B forms");
		makeAllBs(allB,partB.values(),partBUntrans.values(),t);
		System.out.println("Text Extractor");
		List<String>  textA= TextExtractor.extractParticipantA(allB, table.getSource().getPmcId().substring(3),
				r.getConjugationBase());
		System.out.println("Fold PartA");
		List<ParticipantA> participantAs = getFoldPartA(contents, r, allB, table,textA);
	//	List<ParticipantA> participantAs = new ArrayList<ParticipantA>();
		if (participantAs.isEmpty()){
			System.out.println("Caption partA");
			HashMap<String, String> possA = new HashMap<String, String>();
			boolean title = true;
			for(String caption : table.getCaptionList()){
				caption = caption.replaceAll("-", "");
				Pattern p = Pattern.compile("[A-Z[a-z]][\\w]*[A-Z0-9]+[\\w]*");//TODO examine this regex
				Matcher m = p.matcher(caption);
				while(m.find()){
					String a = m.group();
					HashMap<String, String> wordList = checkPartA(a,allB,false,title);//TODO decide how many checks are good
					if(wordList!= null){
						for(String word: wordList.keySet() ){
							possA.put(word, wordList.get(word));
						}
					}
				}
				title = false;
			}
			String partA = checkPartAText(allB, r, possA.keySet(),textA);
			if(partA!= null){
				participantAs.add(new ParticipantA(partA, possA.get(partA), contents));
				return participantAs;
			}
		}else{
			return participantAs;
		}
		System.out.println("here");
		System.out.println(allB);
		System.out.println(textA);
		//_______________________________________________________________________________________________________________
		//BEST A IF NOTHING IS GOTTEN
		for(String a : textA){
			String aTrans = translatePartA(a);
			if(aTrans != null){
				participantAs.add(new ParticipantA(aTrans,a,contents));
				return participantAs;
			}
		}
		//_________________________________________________________________________________________________________________
		participantAs.add(new ParticipantA("unknown", "unknown", contents));
		return participantAs;
	}

}
