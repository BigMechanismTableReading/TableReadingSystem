package extract.lookup;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AbbreviationLookup {
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
	
	public static void main (String args[]){
		System.out.println(lookupAbbr("GH"));
		System.out.println(lookupAbbr("KEGG"));
		System.out.println(lookupAbbr("RING"));
		System.out.println(lookupAbbr("RAS"));
	}
}
