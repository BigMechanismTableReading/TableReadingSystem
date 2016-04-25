package extract;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.protobuf.Descriptors.FieldDescriptor;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.ParticipantA;
import tablecontents.Protein;
import utils.Pair;
import extract.TextExtractor;
import tableBuilder.TableBuf;
import tableBuilder.TableWrapper;
import tableBuilder.TableBuf.Column;
import tableBuilder.TableBuf.Table;
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
	HashMap<Pair<String,String>,Double>  a_conf = null;

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
				//System.err.println(aWord);
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
	 * Check if a single translated protein occurs within the text
	 * @param allB
	 * @param r
	 * @param possA
	 * @param textA
	 * @return
	 */
	private String checkSinglePartAText(Set<String> allB,Reaction r, String possA,List<String> textA){
		for(String aWord : textA){
			for(String aText : allForms(aWord)){
				Pair<String,String> transTextApair = groundPartA(aText,allB,true,true);
				if(transTextApair != null){
					String transTextA = transTextApair.getB();
					if(possA.equals(transTextA)){
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
	private HashMap<ParticipantA,Double> getFoldPartA(HashMap<ColumnContents,List<TableBuf.Column>> contents,
			Reaction r, Set<String> allB,TableBuf.Table table, List<String> textA){
		//Store the participantAs along with a confidence level
		List<ParticipantA> tempA = new ArrayList<ParticipantA>();
		HashMap<ParticipantA,Double> participantAs = new HashMap<ParticipantA,Double>();
		//For any fold columns, check to see if there is a potential participant A
		for(ColumnContents f : contents.keySet()){
			for (TableBuf.Column col : contents.get(f)){
				//gets all possA for each column
				HashMap<String,String> possA = checkPartA(col.getHeader().getData(), allB,true,false);
				for(String a: possA.keySet()){
					addPartA(tempA,possA.get(a),a,f,col,0);									
				}
			}
		}
		for(ParticipantA a: tempA){
			participantAs.put(a, 0.6);//TODO determine if this is a good value, no longer checks the text exactly
		}
		return participantAs;
	}

	/**
	 * Check the captions for possible participantAs
	 * @param contents
	 * @param r
	 * @param allB
	 * @param tblW
	 * @return
	 */
	private HashMap<ParticipantA,Double> getCaptionPartA(HashMap<ColumnContents,List<TableBuf.Column>> contents,
			Reaction r, Set<String> allB,TableWrapper tblW){
		List<ParticipantA> tempAs = new ArrayList<ParticipantA>();
		HashMap<ParticipantA,Double> participantAs = new HashMap<ParticipantA,Double>(); 
		HashMap<String, String> possA = new HashMap<String, String>();
		Table table = tblW.getTable();
		//Iterator <FieldDescriptor> it = table.getAllFields().keySet()
		if (table.getCaptionList().isEmpty()){
			File file = tblW.getFile();
			if (file.getName().endsWith(".html")){
				try {
					Document doc = Jsoup.parse(new String(Files.readAllBytes(file.toPath())));
					Elements e = doc.getElementsByTag("title");
					Iterator <Element> it = e.iterator();
					while (it.hasNext()){
						System.out.println("title: " + it.next());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		boolean title = true;
		for(String wholeCaption : table.getCaptionList()){
			String[] subtitles = wholeCaption.split(";");
			//Splits up the caption to individual word, and checks for the word if it matches the given regular expression
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
				//If part of the first caption/title of the table

				for(String a : possA.keySet()){
					if(a != null && title){
						//TODO add the participants A here without checking text	
						participantAs.put(new ParticipantA(a, possA.get(a),0),.5);
					}
					else if(a != null){
						participantAs.put(new ParticipantA(a, possA.get(a),0), .4);
					}
				
					title = false;
				}

			}
			return participantAs;
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
	public List<ParticipantA> getParticipantAs(TableWrapper tblWrap,
			HashMap<Integer,String> partB, 	HashMap<Integer,String> partBUntrans,
			HashMap<ColumnContents,List<TableBuf.Column>> contents,
			Reaction r ){
		Table table = tblWrap.getTable();
		Set<String> allB = new HashSet<String>();
		Lookup t = TabLookup.getInstance();
		makeAllBs(allB,partB.values(),partBUntrans.values(),t);
		HashMap<String, Integer> hashA= TextExtractor.extractParticipantA(allB, table.getSource().getPmcId().substring(3),
				r.getConjugationBase());
		String PMCID = table.getSource().getPmcId();
		possibleA = new HashMap<String,String>();
		a_conf = new HashMap<Pair<String,String>,Double>();

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
		//Checks the fold columns for potential participantA, assigns confidence level to each value
		HashMap<ParticipantA,Double> participantAFold = getFoldPartA(contents, r, allB, table,textA);

		//Checks the caption for a potential participantA

		HashMap<ParticipantA,Double> participantACaption = getCaptionPartA(contents, r, allB, tblWrap);
		HashMap<ParticipantA,Double> participantAText = new HashMap<ParticipantA,Double>();
		//TODO decide best way to do this, possibly use drug suffix lookup??
		int listPos =0;
		for(String a : textA){
			if(a.length() > 2){
				String aTrans = translatePartA(a.toUpperCase());
				//gets the best option that occurs in the text alone
			
				if(aTrans != null){
					System.out.println(aTrans);
					int value = hashA.get(a.toUpperCase());
					participantAText.put(new ParticipantA(aTrans,a,.0), Math.min(.4, value*.02));
				
				}
			}
			listPos++;
		}
		
		List<ParticipantA> participantAs = new ArrayList<ParticipantA>();
		double max = 0.0;
		List<ParticipantA> top_candidates = new ArrayList<ParticipantA>();
		List<ParticipantA> best_candidates = new ArrayList<ParticipantA>();
		double d = 0.0;
		max = addConfidence(allB,r,textA,participantAFold, max,top_candidates);
		max = addConfidence(allB,r,textA,participantACaption, max,top_candidates);
		max = addConfidence(allB,r,textA,participantAText, max,top_candidates);
		for(ParticipantA a: top_candidates){
			if(a.getConfidenceLevel() >= max){
				best_candidates.add(a);
				if(a.getFoldCols().size() == 0){
					System.err.println(a.getName() +" HERE ");
					for(ColumnContents c : contents.keySet()){
						if(c instanceof Fold){
							for(TableBuf.Column col : contents.get(c)){
								System.out.println(col.getDataCount());
								a.addToData(c, col);
							}
						}
					}
					
				}
			}
		}
		if(best_candidates.size()> 0){ 
			participantAs.addAll(best_candidates);
		}else{
			participantAs.add(new ParticipantA("unknown", "unknown", contents,0));
		}	
		return participantAs;
	}
	
	private double addConfidence(Set<String> allB,Reaction r, List<String> textA,HashMap<ParticipantA,Double> participantACurr,double max,List<ParticipantA> top_candidates){
		double d = 0.0;
		List<ParticipantA> possible_a = new ArrayList<ParticipantA>();
		for(ParticipantA a: participantACurr.keySet()){
			System.err.println(a.getName() + " " + max);
			a.setConfidenceLevel(participantACurr.get(a));
			if((checkSinglePartAText(allB, r, a.getName(), textA)) != null){
				a.setConfidenceLevel(a.getConfidenceLevel()+0.3);
			}
			
			if(( d = participantACurr.get(a)) > max){
				top_candidates.add(a);
				max =d;
			}
		}
		return max;
	}


}
