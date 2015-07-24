package extract.lookup;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AbbreviationLookup {
	
	public static HashMap<String, String> translatedAbbreviations = new HashMap<String, String>();
	
	/**
	 * Uses JSoup and Rest API to query Allie for Biological abbreviations
	 * @param abbr The abbreviation to query
	 * @return the long form of the abbreviation queried
	 */
	public static String lookupAbbr(String abbr) {
		String longform = "-12312451234335345982740298570864210987530294679212874"; //instead of null
		
		try {
			String url = "http://allie.dbcls.jp/rest/getPairsByAbbr?keywords=" + abbr;
			URL urlObj = null;
			Document results = null;
			
			urlObj = new URL(url);
			results = Jsoup.parse(urlObj, 3000);
			
			Elements abbResults = null;
			if(results != null)
				abbResults = results.select("item");
			
			if(abbResults != null && abbResults.size() > 0){
				longform = abbResults.get(0).select("long_form").text();
			}
			return longform;

		} catch (org.jsoup.HttpStatusException e){
			//IGNORE HTTP STATUS EXCEPTION, return null?
		} catch (IOException e) {
			//dont want this outputting.
		}
		return longform;
		
	}
	

	/**
	 * Used to lookup the abbreviation and see if it is in the allie database,
	 * If so it is then checked against the database of proteins
	 * @param abbr
	 * @return
	 */
	public static String abbrLookup(String abbr) {
		if (translatedAbbreviations.containsKey(abbr)){
			return translatedAbbreviations.get(abbr);
		}
		System.out.println("looking up: " + abbr);
		String longForm = lookupAbbr(abbr.trim()).replaceAll("\\W", " ").toUpperCase();
		TabLookup proteinBase = TabLookup.getInstance();
		ChemicalLookup chem = ChemicalLookup.getInstance();
		if(proteinBase.english.containsKey(longForm)){
			List<String> intersect = proteinBase.english.get(longForm);
			if(proteinBase.english.containsKey(abbr)){
				List<String> abbrList = new LinkedList<String>();
				abbrList.addAll(proteinBase.english.get(abbr));
				abbrList.retainAll(intersect);
				if (abbrList.size() > 0) {
					translatedAbbreviations.put(abbr, "Uniprot:" + abbrList.get(0));
					return "Uniprot:" + abbrList.get(0);
				} 
			}
		}
		translatedAbbreviations.put(abbr, null);
		return null;
	}
	
	public static void main (String args[]){
		System.out.println(lookupAbbr("GH"));
		System.out.println(lookupAbbr("KEGG"));
		System.out.println(lookupAbbr("RING"));
		System.out.println(lookupAbbr("RAS"));
	}
}
