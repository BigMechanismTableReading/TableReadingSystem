package extract.lookup;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import tablecontents.PhosphoSite;

/**
 * Used to lookup specific word types in the larger chembl database
 * @author vincentH
 *
 */
public class ChEMBLLookup {
	
	public static HashMap<String, String> translatedChemicals = new HashMap<String, String>();
	/**
	 * Uses JSoup and Rest API to query ChEMBL for harder to ground chemicals
	 * @param abbr The abbreviation to query
	 * @return the long form of the abbreviation queried
	 */
	public static String lookupChemical(String chemical) {
		String trans = null; //instead of null
		
		try {
			String url = "https://www.ebi.ac.uk/ebisearch/search.ebi?db=SmallMolecules&t=" + chemical;
			URL urlObj = null;
			Document results = null;
			
			urlObj = new URL(url);
			results = Jsoup.parse(urlObj, 3000);
			
			Elements chResults = null;
			if(results != null)
				chResults = results.getElementsByClass("result");
			
			if(chResults != null && chResults.size() > 0){
				Elements metadata = chResults.get(0).getElementsByClass("result-meta");
				if (metadata != null && metadata.size() > 0){
					Elements entry_panel =  metadata.get(0).getElementsByClass("entry_actions_panel");
					if (entry_panel != null && entry_panel.size() > 0){
						Elements entry_source =  entry_panel.get(0).getElementsByClass("entry-source");
						if (entry_source != null && entry_source.size() > 0){
							trans =  entry_source.get(0).getElementsByClass("source-id").text().substring(4);
						}
					}
				}
			}
			return trans;

		} catch (org.jsoup.HttpStatusException e){
			//IGNORE HTTP STATUS EXCEPTION, return null?
		} catch (IOException e) {
			//dont want this outputting.
		}
		return trans;
		
	}
	

	/**
	 * Used to lookup a chemical in the ChEMBL database
	 * @param chemical
	 * @return
	 */
	public static String abbrLookup(String abbr) {
		if (translatedChemicals.containsKey(abbr)){
			return translatedChemicals.get(abbr);
		}
		boolean matches = false;
		for (int i : SuffixLookup.getInstance().suffixList.keySet()){
			if(abbr.length() > i){
				if (SuffixLookup.getInstance().suffixList.get(i).contains(abbr.substring(abbr.length() - i))){
					matches = true;
					break;
				} 
				
			}
		}
		
		if(abbr.matches(".*[A-Za-z].*[0-9]{3,}$") && PhosphoSite.getInstance().cellMatch(abbr) == null){
			matches = true;
		}
		
		if (!matches){
			translatedChemicals.put(abbr, null);
			return null;
		}
		
		System.out.println("looking up Chemical: " + abbr);
		
		String translatedChemical = lookupChemical(abbr.trim());
		
		translatedChemicals.put(abbr, translatedChemical);
		
		return translatedChemical;
	}
	
	public static void main (String args[]){
		System.out.println(lookupChemical("DYNAMIN"));
	}
}
