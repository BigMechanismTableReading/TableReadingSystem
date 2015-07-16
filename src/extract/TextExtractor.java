package extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import extract.types.Reaction;

public class TextExtractor {
	
	public static String parseHTMLTitle(String fileName){
		File document = new File("papers" + File.pathSeparator + fileName + ".html");
		System.out.println(fileName);
		String title = "";
		if(document.exists()){
			try{
				Document doc = Jsoup.parse(document, "UTF-8","");
				title = doc.getElementsByClass("content-title").toString();
			}catch(FileNotFoundException e){
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}

		return title;
	}
	/**
	 * Returns a list of sentences in an html document
	 * @param fileName path to html document
	 * @return list of sentences
	 */
	public static List<String> parseHTMLText(String fileName){
		File document = new File(fileName);
		
		if (document.exists()) {
			try {
				
				Document doc = Jsoup.parse(document, "UTF-8", "");
				LinkedList<String> list = new LinkedList<String>();
				
				String text = doc.text();
				String[] sentences = text.split("\\.");
				for(int i = 0; i < sentences.length; i++){
					//if(Pattern.matches(".*[^sS][T|t]able\\s*1.*", sentences[i])){
						list.add(sentences[i]);
					//}
				}
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static List<Reaction> getPossibleReactions(String PMCID){
		String name = "PMC" + PMCID;
		HashSet<String> wordSet = new HashSet<String>();
		Reaction[] allReactions = Reaction.allReactions;
		String paperPath = "papers" + File.separator + name + ".html";
		
		File document = new File(paperPath);
		if (document.exists()) {
			try {
				
				Document doc = Jsoup.parse(document, "UTF-8", "");
				
				String text = doc.text();
				String[] words = text.split("\\W");
				for(int i = 0; i < words.length; i++){
					String word = words[i].toLowerCase().replaceAll("\\p{Punct}", "");
					wordSet.add(word);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<Reaction> possibleReactions = new ArrayList<Reaction>();
		for (Reaction r : allReactions){
			if(containsConjugate(wordSet, r)){
				possibleReactions.add(r);
			}
		}
		return possibleReactions;
	}
	
	private static boolean containsConjugate(Set<String> words, Reaction r) {
		List<String> base = r.getConjugationBase();
		List<String> conjugations = r.getConjugationsList();
		for (String b : base) {
			for (String c : conjugations) {
				if (words.contains(b + c)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Splits sentences in a paper by conjugation bases (ex: "phosphorylat") 
	 * and returns groups of proteins around it.
	 * Example using "phosphorylat": 
	 * A sentence like "LATS1/WARTS phosphorylates MYPT1" will return this entry in the list:
	 * {{"LATS1","WARTS"},{"phosphorylates"},{"MYPT1"}}
	 * @param fileName path to html document
	 * @param conjugationBaseList the words to parse around
	 * @return list of sentences
	 */
	public static List<List<List<String>>> getReactionPairs(String fileName, List<String> conjugationBaseList) {
		File document = new File(fileName);
		
		if (document.exists()) {
			try {
				
				Document doc = Jsoup.parse(document, "UTF-8", "");
				LinkedList<List<List<String>>> list = new LinkedList<List<List<String>>>();
				
				String text = doc.text();
				String[] sentences = text.split("\\.");
				for(int i = 0; i < sentences.length; i++){
					for (String conjugationBase : conjugationBaseList){
						int breakpoint = sentences[i].toLowerCase().indexOf(conjugationBase);
						if(breakpoint != -1){
							LinkedList<List<String>> entry = new LinkedList<List<String>>();
							entry.add(findProteins(sentences[i].substring(0, breakpoint)));
							int start = sentences[i].lastIndexOf(" ", breakpoint);
							int end = sentences[i].indexOf(" ", breakpoint);
							LinkedList<String> type = new LinkedList<String>();
							if (start + 1 > 0 && end > 0)
							type.add(sentences[i].substring(start + 1, end));
							if(sentences[i].indexOf("by ", end) == end + 1){
								type.add("by");
							} else if(sentences[i].indexOf("of ", end) == end + 1){
								type.add("of");
							} 
							entry.add(type);
							entry.add(findProteins(sentences[i].substring(breakpoint)));
							
							list.add(entry);
						}
					}
				}
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Takes in a set of participant B's extracted from a table and compares them with 
	 * reaction sentence pairs extracted from the paper. The method returns a sorted
	 * list of likely participant A's retrieved from the text.
	 * @param participantBs The set of participant B's
	 * @param PMCID The PMC id of the paper to analyze
	 * @param conjugationBase The base word of the reaction to parse
	 * @return the sorted list of participant A's
	 */
	public static List<String> extractParticipantA(Set<String> participantBs, String PMCID, List<String> conjugationBase){
		//setPhosphoOne(false);
		String name = "PMC" + PMCID;
		List<List<List<String>>> list = getReactionPairs("papers" + File.separator + name + ".html", conjugationBase);
		HashMap<String, Integer> partAs = new HashMap<String, Integer>();
		if(list == null)
			return null;
		for(List<List<String>> entry : list){
			HashSet<String> partArow = new HashSet<String>();
			for (String partB : participantBs){
				if(entry.get(0).contains(partB) || entry.get(2).contains(partB)){
					for(int j = 0; j < entry.get(0).size(); j++){
						if(!participantBs.contains(entry.get(0).get(j))){
							partArow.add(entry.get(0).get(j));
						}
					}
					for(int j = 0; j < entry.get(2).size(); j++){
						if(!participantBs.contains(entry.get(2).get(j))){
							partArow.add(entry.get(2).get(j));
						}
					}
				}
			}
			Iterator<String> rowIter = partArow.iterator();
			while(rowIter.hasNext()){
				String next = rowIter.next();
				if(partAs.containsKey(next)){
					partAs.put(next, partAs.get(next) + 1);
				} else {
					partAs.put(next, 1);
				}
			}
		}
		
		List<String> sortedList = sortByValue(partAs);
		for (int i = 0; i < sortedList.size(); i++) {
			System.out.println(sortedList.get(i) + " : " + partAs.get(sortedList.get(i)));
		}
		return sortedList;
	}
	
	/**
	 * Finds all the proteins names in a particular string. This is similar to the one used in TableRelevance, except
	 * with an extended regex to account for spelling variations in the paper.
	 * @param sentence The string to find 
	 * @return the list of protein names
	 */
	private static List<String> findProteins(String sentence){
		List<String> proteins = new LinkedList<String>();
		sentence = sentence.trim();
		sentence = sentence.replace("-", "");
		if(Pattern.matches("^[A-Z[a-z[0-9]]].*", sentence)){	
			Pattern p = Pattern.compile("([A-Z[a-z]]\\w*[A-Z]\\w*)|([A-Z][A-Za-z]{1,3}[A-Z0-9]{1,4})");
			Matcher m = p.matcher(sentence);
			while(m.find()){
				proteins.add(m.group());
			}
		}
		
		return proteins;
	}
	
	/**
	 * Helper method used to sort a hashmap's key set by its value.
	 * @param map The map to sort
	 * @return The sorted key set as a list
	 */
	public static LinkedList<String> sortByValue (HashMap<String, Integer> map){
		ValueComparator comp = new ValueComparator(map);
		LinkedList<String> newList = new LinkedList<String>();
		Iterator<String> iterMap = map.keySet().iterator();
		while(iterMap.hasNext()){
			newList.add(iterMap.next());
		}
		Collections.sort(newList, comp);
		return newList;
	}


	private static class ValueComparator implements Comparator<String> {
		Map<String, Integer> mapToSort;
		public ValueComparator(Map<String, Integer> map){
			mapToSort = map;
		}
		public int compare(String a, String b){
			return mapToSort.get(b) - mapToSort.get(a);
		}
	}
}
