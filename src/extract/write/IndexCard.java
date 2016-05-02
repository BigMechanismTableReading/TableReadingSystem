package extract.write;

import java.util.HashMap;
import java.util.List;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.PValue;
import tablecontents.ParticipantA;
import tablecontents.Ratio;
import tableBuilder.TableBuf.Column;
import extract.types.Reaction;

/**
 * Stores information to be printed as an indexcard
 * Info is store in a HashMap of string to strings
 * @author sloates
 *
 */
public class IndexCard {
	
	public HashMap<String, String> data;
	public HashMap<String,String> partAData;
	public HashMap<String,String> partBData;
	public HashMap<String,String> evidenceData;
	public HashMap<String,String> extractedInfoData;
	
	/**
	 * Adds the row, participantB and reaction type to the index card hashmap
	 * @param r
	 * @param partB
	 * @param partBuntrans
	 * @param row
	 */
	public IndexCard(Reaction r, String partB, String partBuntrans, int row) {
		data = new HashMap<String, String>();
		partAData = new HashMap<String,String>();
		partBData = new HashMap<String,String>();
		evidenceData = new HashMap<String,String>();
		extractedInfoData = new HashMap<String,String>();
		partBData.put("row", row+ "");
		partBData.put("modification_type", r.toString());
		partBData.put("entity_text_b", partBuntrans);
		if(partB.contains("GO:")){
			partBData.put("entity_type_b", "Biological Process");
		}else{
			partBData.put("entity_type_b", "protein");
		}
		partBData.put("identifier_b", partB);
	}
	
	/**
	 * Duplicates and indexcard
	 * @param card
	 */
	public IndexCard(IndexCard card) {
		data = (HashMap<String, String>) card.data.clone();
		partAData = (HashMap<String, String>) card.partAData.clone();
		partBData = (HashMap<String, String>) card.partBData.clone();
		evidenceData = (HashMap<String, String>) card.evidenceData.clone();
		extractedInfoData = (HashMap<String, String>) card.extractedInfoData.clone();
		addInfo(card.data);
	}

	/**
	 * Helper for adding data to the index card
	 * @param extractData
	 */
	public void addInfo(HashMap<String, String> extractData) {
		for (String key : extractData.keySet()){
			if (key!=null && extractData.get(key)!=null){
				data.put(key, extractData.get(key));
			}
		}
	}
	/**
	 * Helper for adding data to part A of the index card
	 * @param extractData
	 */
	public void addPartAInfo(HashMap<String, String> extractData) {
		for (String key : extractData.keySet()){
			if (key!=null && extractData.get(key)!=null){
				partAData.put(key, extractData.get(key));
			}
		}
	}
	/**
	 * Helper for adding data to part B of the index card
	 * @param extractData
	 */
	public void addPartBInfo(HashMap<String, String> extractData) {
		for (String key : extractData.keySet()){
			if (key!=null && extractData.get(key)!=null){
				partBData.put(key, extractData.get(key));
			}
		}
	}
	/**
	 * Helper for adding data to extracted info part of the index card
	 * @param extractData
	 */
	public void addExtractedInfo(HashMap<String, String> extractData) {
		for (String key : extractData.keySet()){
			if (key!=null && extractData.get(key)!=null){
				extractedInfoData.put(key, extractData.get(key));
			}
		}
	}
	/**
	 * Helper for adding data to extract data part of the index card
	 * @param extractData
	 */
	public void addExtractData(HashMap<String, String> extractData) {
		for (String key : extractData.keySet()){
			if (key!=null && extractData.get(key)!=null){
				extractData.put(key, extractData.get(key));
			}
		}
	}
	
	/**
	 * Returns the data at a particular key
	 * @param key
	 * @return
	 */
	public String getData(String key){
		return data.get(key);
	}
	
	/**
	 * Takes in a ParticipantA object, finds the best fold column, extracts the data and determines the significance
	 * @param entry
	 * @param row
	 * @return
	 */
	public boolean addPartA(ParticipantA entry, int row) {
		Ratio r = Ratio.getInstance();
		String aGrounded = entry.getName();
		partAData.put("identifier_a",aGrounded);
		partAData.put("entity_text_a",entry.getUntranslatedName());
		partAData.put("entity_type_a",entry.getType());
		partAData.put("confidence_level",String.valueOf(entry.getConfidenceLevel()));
		partAData.put("list_position", entry.getListPosition());
		
		HashMap<ColumnContents, List<Column>> foldCols = entry.getFoldCols();
		System.err.println(foldCols.size());
		Fold f = r.bestFold(foldCols);
		System.err.println(f);
		HashMap<String, String> foldData = null;
		if(f != null)
			 foldData = f.extractData(foldCols.get(f),row);
		else if(foldCols.containsKey(PValue.getInstance())){
			 foldData = PValue.getInstance().extractData(foldCols.get(PValue.getInstance()),row);
		}
		if(foldData == null || foldData.isEmpty()){
			return false;
		}
		addPartAInfo(foldData);
		return true;
	}
	
}
