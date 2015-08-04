package extract.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tablecontents.ColumnContents;

/**
 * Abstract class that contains the structure for reactions.
 * Contains an array of all wanted reactions called allReactions
 * @author vhsiao, sloates
 *
 */
public abstract class Reaction {
	
	public static Reaction[] allReactions = {Phosphorylation.getInstance(),Acetylation.getInstance(),
		Methylation.getInstance(),Sumoylation.getInstance(),PossibleReaction.getInstance()};
	HashSet<Class<? extends ColumnContents>> data = new HashSet<Class<? extends ColumnContents>>();
	HashSet<Class<? extends ColumnContents>> optionalColumns = new HashSet<Class<? extends ColumnContents>>();
	HashMap<Class<? extends ColumnContents>, List<List<Class<? extends ColumnContents>>>> alternatives = 
			new HashMap<Class<? extends ColumnContents>, List<List<Class<? extends ColumnContents>>>>();
	ArrayList<String> conjugations = new ArrayList<String>();
	ArrayList<String> conjugationBase = new ArrayList<String>();
	
	/**
	 * Returns a list of column types required to make an index card for this reaction.
	 * @return
	 */
	public List<Class<? extends ColumnContents>> getRequiredColumns() {
		ArrayList<Class<? extends ColumnContents>> requiredTypes = new ArrayList<Class<? extends ColumnContents>>();
		requiredTypes.addAll(data);
		return requiredTypes;
	}
	
	/**
	 * Returns a list of column types that are not required, but are related to this reaction
	 * @return
	 */
	public List<Class<? extends ColumnContents>> getOptionalColumns() {
		ArrayList<Class<? extends ColumnContents>> optionalTypes = new ArrayList<Class<? extends ColumnContents>>();
		optionalTypes.addAll(optionalColumns);
		return optionalTypes;
	}
	
	/**
	 * Returns a list of alternative column types
	 * @return
	 */
	public List<Class<? extends ColumnContents>> getAllAlternatives(){
		List<Class<? extends ColumnContents>> newList = new ArrayList<Class<? extends ColumnContents>>();
		for (List<List<Class<? extends ColumnContents>>> list : alternatives.values()){
			for (List<Class<? extends ColumnContents>> innerList : list){
				newList.addAll(innerList);
			}
		}
		return newList;
	}
	/**
	 * Returns if a specific type of ColumnContents has a possible alternative
	 * @param type
	 * @return
	 */
	public boolean hasAlternative(Class<? extends ColumnContents> type){
		return alternatives.containsKey(type);
	}
	/**
	 * Returns a list of a list of alternative column contents for an individual type
	 * @param type
	 * @return
	 */
	public List<List<Class<? extends ColumnContents>>> getAlternatives(Class<? extends ColumnContents> type){
		return alternatives.get(type);
	}
	
	/**
	 * Returns a list of conjugations for a reaction
	 * @return
	 */
	public List<String> getConjugationsList(){
		return conjugations;
	}
	
	/**
	 * Gets the base for any conjugations (ex. phosphorylat)
	 * @return
	 */
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
	
	/**
	 * Returns an array of all reactions
	 * @return
	 */
	public static Reaction[] getReactions(){
		return allReactions;
	}
	
	/**
	 * Creates an arrayList of classes
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Class<? extends ColumnContents>> createEntry(Class<? extends ColumnContents> ...list){
		ArrayList<Class<? extends ColumnContents>> newEntry = new ArrayList<Class<? extends ColumnContents>>();
		for (Class<? extends ColumnContents> entry : list){
			newEntry.add(entry);
		}
		return newEntry;
	}
	
	/**
	 * Adds alternatives for an individual column type
	 * @param base
	 * @param alt
	 */
	public void addAlternativeEntry(Class<? extends ColumnContents> base, ArrayList<Class<? extends ColumnContents>> alt){
		if(alternatives.containsKey(base)){
			alternatives.get(base).add(alt);
		} else {
			List<List<Class<? extends ColumnContents>>> newList = new ArrayList<List<Class<? extends ColumnContents>>>();
			newList.add(alt);
			alternatives.put(base, newList);
		}
	}
	
	public String toString(){
		return this.getConjugationBase().get(0) + "ion";
	}
}
