package tableBuilder.extract;

import java.util.ArrayList;
import java.util.HashMap;

public class HTMLTable {
	private String title;
	private ArrayList<String> captions = new ArrayList<String>();
	private String [] headers;
	
	private ColumnData [] columnData;
	private int currentIndex = -1;
	
	//TODO: THIS IS ORGANIZED POORLY
	//row to data in columns
	private HashMap<Integer, String []> data = new HashMap<Integer,String []>();
	//private String [][] data;
	
	public void addCaption(String caption){
		captions.add(caption);
	}
	
	public void addData(int row, String [] info){
		data.put(row, info);
		for (int i=0; i < info.length; i++){
			addColumnData(i, row, info[i]);
		}
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setHeaderSize(int colSize) {
		headers = new String[colSize];
		columnData = new ColumnData[colSize];
		for (int i=0; i< colSize; i++){
			columnData[i] = new ColumnData(); //now never null
		}
	}
	
	public void addHeader(int i, String col){
		headers[i] = col;
		columnData[i].setHeaders(col);
		currentIndex=i;
	}
	
	public String [] getHeaders(){
		return headers;
	}
	
	public void addNextHeader(String header){
		if (currentIndex+1 < headers.length){
			currentIndex++;
			addHeader(currentIndex, header);
		}
	}

	public ArrayList<String> getCaptions() {
		return captions;
	}
	public String getHeader(int i){
		return headers[i];
	}
	public String [] getData(int i){
		return data.get(i);
	}
	
	public HashMap<Integer, String[]> getData(){
		return data;
	}
	public ColumnData [] getColumnData(){
		return columnData;
	}
	
	public void addColumnData(int colNum, int row, String add){
	//	System.out.println("colDatas: " + columnData.length);
	//	System.out.println("trying to add : " + colNum + "," + row + ", " + add);
		if (columnData[colNum]!=null){ //this shouldnt happen
			columnData[colNum].addData(row, add);
		}
	}

	
	
	
	
	

}
