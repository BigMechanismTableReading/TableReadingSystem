package extract.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import columncontents.ColumnContents;

public abstract class Reaction {
	public static Reaction[] allReactions = {Phosphorylation.getInstance(), Methylation.getInstance()};
	HashSet<Class<? extends ColumnContents>> data = new HashSet<Class<? extends ColumnContents>>();
	HashMap<Class<? extends ColumnContents>, List<Class<? extends ColumnContents>>> alternatives = 
			new HashMap<Class<? extends ColumnContents>, List<Class<? extends ColumnContents>>>();
	ArrayList<String> conjugations = new ArrayList<String>();
	ArrayList<String> conjugationBase = new ArrayList<String>();
	public List<Class<? extends ColumnContents>> getRequiredColumns() {
		ArrayList<Class<? extends ColumnContents>> requiredTypes = new ArrayList<Class<? extends ColumnContents>>();
		requiredTypes.addAll(data);
		return requiredTypes;
	}
	public List<Class<? extends ColumnContents>> getAllAlternatives(){
		List<Class<? extends ColumnContents>> newList = new ArrayList<Class<? extends ColumnContents>>();
		for (List<Class<? extends ColumnContents>> list : alternatives.values()){
			newList.addAll(list);
		}
		return newList;
	}
	public boolean hasAlternative(Class<? extends ColumnContents> type){
		return alternatives.containsKey(type);
	}
	public List<Class<? extends ColumnContents>> getAlternative(Class<? extends ColumnContents> type){
		return alternatives.get(type);
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
	public static Reaction[] getReactions(){
		return allReactions;
	}
}
