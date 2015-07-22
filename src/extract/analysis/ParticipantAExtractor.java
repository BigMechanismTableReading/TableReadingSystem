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
			String trans = p.cellMatch(partA);
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
			}
		}
		if(firstCap != -1){
			allForms.add(partA.substring(0,firstCap+1));
		}
		return allForms;	
	}
	
	/**
	 * Used to lookup the abbreviation and see if it is in the allie database,
	 * If so it is then checked against the database of proteins
	 * @param abbr
	 * @return
	 */
	private String abbrLookup(String abbr) {
		String longForm = AbbreviationLookup.lookupAbbr(abbr.trim()).replaceAll("\\W", " ").toUpperCase();
		TabLookup proteinBase = TabLookup.getInstance();
		ChemicalLookup chem = ChemicalLookup.getInstance();
		if(proteinBase.english.containsKey(longForm)){
			List<String> intersect = proteinBase.english.get(longForm);
			if(proteinBase.english.containsKey(abbr)){
				List<String> abbrList = new LinkedList<String>();
				abbrList.addAll(proteinBase.english.get(abbr));
				abbrList.retainAll(intersect);
				if(abbrList.size() > 0)
					return abbrList.get(0);
			}
		}
		return null;
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
			partA = abbrLookup(form);
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
		for(String word: split){
			for (String form : allForms(word)){
				Pair<String,String> partA = groundPartA(form,partBs,fold,title);
				if (partA != null)
					//translated to untranslated
					possA.put(partA.getB(),partA.getA());
			}
		}
		if(!possA.isEmpty())
			return possA;
		return null;
	}
	
	/**
	 * Checks the possible A against possible As extracted from the text, returning a match
	 * @param allB
	 * @param pmcid
	 * @param r
	 * @param possA
	 * @return
	 */
	private String checkPartAText(Set<String> allB,String pmcid, Reaction r, Set<String> possA){
		List<String>  textA= TextExtractor.extractParticipantA(allB, pmcid,r.getConjugationBase());
//		System.out.println(possA);
//		System.out.println(textA);
		for(String aWord : textA){
			for(String aText : allForms(aWord)){
				Pair<String,String> transTextApair = groundPartA(aText,allB,true,true);
				if(transTextApair != null){
					String transTextA = transTextApair.getB();
//					System.out.println(transTextA);
//					System.out.println(transTextA + "  " + aText);
					for(String aTable : possA){
						if(aTable.equals(transTextA))
							return aTable;
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
	 * @return
	 */
	private List<ParticipantA> getFoldPartA(HashMap<ColumnContents,List<TableBuf.Column>> contents,
			Reaction r, Set<String> allB,TableBuf.Table table){
		//TODO Verify the ordering
		List<ParticipantA> participantAs = new ArrayList<ParticipantA>();
		for(ColumnContents f : contents.keySet()){
			for (TableBuf.Column col : contents.get(f)){
				HashMap<String,String> possA = checkPartA(col.getHeader().getData(), allB,true,false);
				if (possA != null){
					String partA = checkPartAText(allB, table.getSource().getPmcId().substring(3), r,possA.keySet());
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
		Set<String> allB = new HashSet<String>();
		TabLookup t = TabLookup.getInstance();
		makeAllBs(allB,partB.values(),partBUntrans.values(),t);
		List<ParticipantA> participantAs = getFoldPartA(contents, r, allB, table);
		if (participantAs.isEmpty()){
			HashMap<String, String> possA = new HashMap<String, String>();
			boolean title = true;
			for(String caption : table.getCaptionList()){
				caption = caption.replaceAll("-", "");
				Pattern p = Pattern.compile("[A-Z[a-z]][\\w]*[A-Z]+[\\w]*");//TODO examine this regex
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
			String partA = checkPartAText(allB, table.getSource().getPmcId().substring(3), r, possA.keySet());
			if(partA!= null){
				participantAs.add(new ParticipantA(partA, possA.get(partA), contents));
				return participantAs;
			}
		}else{
			return participantAs;
		}
		participantAs.add(new ParticipantA("unknown", "unknown", contents));
		return participantAs;
	}

}
