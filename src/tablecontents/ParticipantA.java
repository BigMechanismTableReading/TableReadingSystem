package tablecontents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import extract.buffer.TableBuf;

public class ParticipantA {
	private String name = null;
	private String untransName = null;
	private String type = null;
	HashMap<ColumnContents,List<TableBuf.Column>> foldCols= null;
	
	public ParticipantA(String name, String untransName){
		this.name = name;
		this.untransName = untransName;
		entityType(name);
		foldCols = new HashMap<ColumnContents,List<TableBuf.Column>>();
	}
	
	
	public ParticipantA(String name, String untransName, HashMap<ColumnContents,List<TableBuf.Column>> cols){
		this.name = name;
		this.untransName = untransName;
		entityType(name);
		foldCols = cols;
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
	public void addToData(ColumnContents c, TableBuf.Column col){
		if (foldCols.containsKey(c)){
			foldCols.get(c).add(col);
		} else {
			LinkedList<TableBuf.Column> list = new LinkedList<TableBuf.Column>();
			list.add(col);
			foldCols.put(c, list);
		}
	}
	
	public String getType(){
		return type;
	}
	public boolean equalString(String otherName){
		return name.equals(otherName);
	}
	public String getName(){
		return name;
	}
	public String getUntranslatedName(){
		return untransName;
	}
	public HashMap<ColumnContents,List<TableBuf.Column>> getFoldCols(){
		return foldCols;
	}
}
