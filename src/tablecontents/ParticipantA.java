package tablecontents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import extract.buffer.TableBuf;

public class ParticipantA {
	private String name = null;
	HashMap<ColumnContents,List<TableBuf.Column>> foldCols= null;
	
	public ParticipantA(String name){
		this.name = name;
		foldCols = new HashMap<ColumnContents,List<TableBuf.Column>>();
	}
	
	public ParticipantA(String name, HashMap<ColumnContents,List<TableBuf.Column>> cols){
		this.name = name;
		foldCols = cols;
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
	
	public boolean equalString(String otherName){
		return name.equals(otherName);
	}
	public String getName(){
		return name;
	}
}
