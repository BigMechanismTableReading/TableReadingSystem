package extract.write;

import java.util.HashMap;

import extract.types.Reaction;

public class IndexCard {
	public HashMap<String, String> data;

	public IndexCard(Reaction r, String string) {
		data = new HashMap<String, String>();
		data.put("modification_type", r.get);
	}

	public void addInfo(HashMap<String, String> extractData) {
		// TODO Auto-generated method stub
		
	}
	
	public String getData(String key){
		return data.get(key);
	}
	
}
