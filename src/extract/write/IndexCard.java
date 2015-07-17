package extract.write;

import java.util.HashMap;

import tablecontents.ParticipantA;
import extract.types.Reaction;

public class IndexCard {
	public HashMap<String, String> data;

	public IndexCard(Reaction r, String partB, String partBuntrans) {
		data = new HashMap<String, String>();
		data.put("modification_type", r.toString());
		data.put("entity_text_b", partBuntrans);
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
		// TODO Auto-generated method stub
		
	}
	
}
