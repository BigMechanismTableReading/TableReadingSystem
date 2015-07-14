package extract.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import columncontents.ColumnContents;
import extract.analysis.TableType;

public abstract class Reaction {
	HashSet<ColumnContents> data = new HashSet<ColumnContents>();
	ArrayList<String> conjugations = new ArrayList<String>();
	public List<ColumnContents> getRequiredColumns() {
		ArrayList<ColumnContents> requiredTypes = new ArrayList<ColumnContents>();
		requiredTypes.addAll(data);
		return requiredTypes;
	}
	public List<String> getConjugationBase(){
		return conjugations;
	}
	public abstract Reaction getInstance();
}
