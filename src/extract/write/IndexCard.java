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
	int row;
	public IndexCard(Reaction r, String partB, String partBuntrans, int row) {
		this.row = row;
		data = new HashMap<String, String>();
		data.put("modification_type", r.toString());
		data.put("entity_text_b", partBuntrans);
		data.put("entity_type_b", "protein");
		data.put("identifier_b", partB);
	}

	public IndexCard(IndexCard card) {
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

	public void addPartA(ParticipantA entry) {
		Ratio r = Ratio.getInstance();
		String aGrounded = entry.getName();
		data.put("identifier_a",aGrounded);
		data.put("entity_text_a",entry.getUntranslatedName());
		String identifierB = "";
		if(aGrounded.charAt(0) == 'U'){
			data.put("entity_type_a","protein");
		}else if (aGrounded.charAt(0) == 'C'){
			data.put("entity_type_a","chemical");
		}else{
			data.put("entity_type_a","unknown");
		}
		HashMap<ColumnContents, List<Column>> foldCols = entry.getFoldCols();
		Fold f = r.bestFold(foldCols);
		f.extractData(foldCols.get(f),row);
		
	}
	
}
