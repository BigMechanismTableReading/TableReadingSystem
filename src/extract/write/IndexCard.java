package extract.write;

import java.util.HashMap;
import java.util.List;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.ParticipantA;
import tablecontents.Ratio;
import extract.buffer.TableBuf.Column;
import extract.types.Reaction;

/**
 * Stores information to be printed as an indexcard
 * Info is store in a HashMap of string to strings
 * @author sloates
 *
 */
public class IndexCard {
	
	public HashMap<String, String> data;
	
	/**
	 * Adds the row, participantB and reaction type to the index card hashmap
	 * @param r
	 * @param partB
	 * @param partBuntrans
	 * @param row
	 */
	public IndexCard(Reaction r, String partB, String partBuntrans, int row) {
		data = new HashMap<String, String>();
		data.put("row", row+ "");
		data.put("modification_type", r.toString());
		data.put("entity_text_b", partBuntrans);
		data.put("entity_type_b", "protein");
		data.put("identifier_b", partB);
	}
	
	/**
	 * Duplicates and indexcard
	 * @param card
	 */
	public IndexCard(IndexCard card) {
		data = new HashMap<String, String>();
		addInfo(card.data);
	}

	/**
	 * Helper for adding data to the index card
	 * @param extractData
	 */
	public void addInfo(HashMap<String, String> extractData) {
		for (String key : extractData.keySet()){
			data.put(key, extractData.get(key));
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
		data.put("identifier_a",aGrounded);
		data.put("entity_text_a",entry.getUntranslatedName());
		String identifierB = "";
		data.put("entity_type_a",entry.getType());
		data.put("confidence_level",entry.getConfidenceLevel());
		
		HashMap<ColumnContents, List<Column>> foldCols = entry.getFoldCols();
		Fold f = r.bestFold(foldCols);
		HashMap<String, String> foldData = null;
		if(f != null)
			 foldData = f.extractData(foldCols.get(f),row);
		
		if(foldData == null || foldData.isEmpty()){
			return false;
		}
		addInfo(foldData);
		return true;
	}
	
}
