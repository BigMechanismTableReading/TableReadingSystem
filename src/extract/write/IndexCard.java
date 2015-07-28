package extract.write;

import java.util.HashMap;
import java.util.List;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.ParticipantA;
import tablecontents.Ratio;
import extract.buffer.TableBuf.Column;
import extract.types.Reaction;

public class IndexCard {
	
	public HashMap<String, String> data;
	
	public IndexCard(Reaction r, String partB, String partBuntrans, int row) {
		data = new HashMap<String, String>();
		data.put("row", row+ "");
		data.put("modification_type", r.toString());
		data.put("entity_text_b", partBuntrans);
		data.put("entity_type_b", "protein");
		data.put("identifier_b", partB);
	}

	public IndexCard(IndexCard card) {
		data = new HashMap<String, String>();
		addInfo(card.data);
	}

	public void addInfo(HashMap<String, String> extractData) {
		for (String key : extractData.keySet()){
			data.put(key, extractData.get(key));
		}
	}
	
	public String getData(String key){
		return data.get(key);
	}

	public boolean addPartA(ParticipantA entry, int row) {
		Ratio r = Ratio.getInstance();
		String aGrounded = entry.getName();
		data.put("identifier_a",aGrounded);
		data.put("entity_text_a",entry.getUntranslatedName());
		String identifierB = "";
		data.put("entity_type_a",entry.getType());
		
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
