package extract.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import columncontents.ColumnContents;
import extract.analysis.TableType;

public abstract class Reaction {
	HashMap<ColumnContents, String[]> data = new HashMap<ColumnContents, String[]>();
	ArrayList<String> conjugations = new ArrayList<String>();
	public List<ColumnContents> getRequiredColumns() {
		ArrayList<ColumnContents> requiredTypes = new ArrayList<ColumnContents>();
		requiredTypes.addAll(data.keySet());
		return requiredTypes;
	}
	public String getHeaderRegex(TableType.ColumnTypes type) {
		if(data.containsKey(type)){
			return data.get(type)[0];
		} else {
			return null;
		}
	}
	public String getCellRegex(TableType.ColumnTypes type) {
		if(data.containsKey(type)){
			return data.get(type)[1];
		} else {
			return null;
		}
	}
	public List<String> getConjugationBase(){
		return conjugations;
	}
	public abstract Reaction getInstance();
}
