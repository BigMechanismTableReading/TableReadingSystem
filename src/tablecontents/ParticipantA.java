package tablecontents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import tableBuilder.TableBuf;
import tableBuilder.TableBuf.Column;

/**
 * Stores participantA and its fold columns
 * @author sloates
 *
 */
public class ParticipantA implements LinkedContents{
	private String name = null;
	private String untransName = null;
	private String type = null;
	private double confidenceLevel = -1.0;
	private int listPosition = -1;
	HashMap<ColumnContents,List<TableBuf.Column>> foldCols= null;
	
	/**
	 * Creates participantA without known fold Columns
	 * @param name
	 * @param untransName
	 */
	public ParticipantA(String name, String untransName,double confidenceLevel){
		this.name = name;
		this.untransName = untransName;
		entityType(name);
		foldCols = new HashMap<ColumnContents,List<TableBuf.Column>>();
		this.confidenceLevel = confidenceLevel;
	}
	
	/**
	 * Creates participant A when best fold column for the participant is already known
	 * @param name
	 * @param untransName
	 * @param cols
	 */
	public ParticipantA(String name, String untransName, HashMap<ColumnContents,List<TableBuf.Column>> cols,double confidenceLevel){
		this.name = name;
		this.untransName = untransName;
		entityType(name);
		foldCols = cols;
		this.confidenceLevel = confidenceLevel;
	}
	public ParticipantA(String aTrans, String a,
			HashMap<ColumnContents, List<Column>> contents,
			double confidenceLevel, int listPos) {
		this.name = aTrans;
		this.untransName = a;
		entityType(name);
		foldCols = contents;
		this.confidenceLevel = confidenceLevel;
		this.listPosition = listPos;
	}

	private void entityType(String name){
		if(name.charAt(0) == 'U'){
			type = "protein";
		}else if (name.charAt(0) == 'C'){
			type = "chemical";
		}else{
			type = "unknown";
		}
	}
	
	/**
	 * Adds to the fold column list
	 * @param c
	 * @param col
	 */
	public void addToData(ColumnContents c, TableBuf.Column col){
		if (foldCols.containsKey(c)){
			foldCols.get(c).add(col);
		} else {
			LinkedList<TableBuf.Column> list = new LinkedList<TableBuf.Column>();
			list.add(col);
			foldCols.put(c, list);
		}
	}
	
	/**
	 * Returns type as a string
	 * (chemical,protein)
	 * @return
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Returns if the name of this participantA is equal to this string.
	 * @param otherName
	 * @return
	 */
	public boolean equalString(String otherName){
		return name.equals(otherName);
	}
	/**
	 * Returns the translated name of this participant A
	 * @return
	 */
	public String getName(){
		return name;
	}
	/**
	 * Returns the untranslated name of this participant A
	 * @return
	 */
	public String getUntranslatedName(){
		return untransName;
	}
	/**
	 * Returns the fold contents for this participant A
	 * @return
	 */
	public HashMap<ColumnContents,List<TableBuf.Column>> getFoldCols(){
		return foldCols;
	}

	/**
	 * Returns the confidence level of this participant A
	 * @return
	 */
	public String getConfidenceLevel() {
		return confidenceLevel  + "";
	}

	/**
	 * Returns the position of the text list that this was extracted from
	 * @return
	 */
	public String getListPosition() {
		return listPosition + "";
	}

}
