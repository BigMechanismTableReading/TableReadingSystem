package extract.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import extract.analysis.TableType;

public abstract class Reaction {
	HashMap<TableType.ColumnTypes, String[]> data = new HashMap<TableType.ColumnTypes, String[]>();
	ArrayList<String> conjugations = new ArrayList<String>();
	public List<TableType.ColumnTypes> getRequiredColumns() {
		ArrayList<TableType.ColumnTypes> requiredTypes = new ArrayList<TableType.ColumnTypes>();
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
}
