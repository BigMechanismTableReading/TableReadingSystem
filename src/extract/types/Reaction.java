package extract.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import columncontents.ColumnContents;

public abstract class Reaction {
	HashSet<ColumnContents> data = new HashSet<ColumnContents>();
	ArrayList<String> conjugations = new ArrayList<String>();
	ArrayList<String> conjugationBase = new ArrayList<String>();
	public List<ColumnContents> getRequiredColumns() {
		ArrayList<ColumnContents> requiredTypes = new ArrayList<ColumnContents>();
		requiredTypes.addAll(data);
		return requiredTypes;
	}
	public List<String> getConjugationsList(){
		return conjugations;
	}
	public List<String> getConjugationBase(){
		return conjugationBase;
	}
	public Reaction() {
		conjugations.add("ing");
		conjugations.add("ion");
		conjugations.add("ed");
		conjugations.add("e");
		conjugations.add("es");
	}
	public abstract Reaction getInstance();
}
