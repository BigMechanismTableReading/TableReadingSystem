package tableBuilder.extract;

import java.util.Collections;
import java.util.HashMap;

public class ColumnData {
	private String header;
	HashMap<Integer, String> data = new HashMap<Integer,String>();
	

	public ColumnData(String header){
		this.header = header;
		System.out.println("Col: " + header);
	}
	
	public ColumnData() {
	}

	public String  getHeader() {
		return header;
	}

	public void setHeaders(String header) {
		this.header = header;
	}
	
	public void addData(int row, String add){
		data.put(row, add);
	}
	
	public String [] getData(){
		String [] dataInfo = new String[data.keySet().size()]; //or should i find the max in the key set?
		for (Integer index: data.keySet()){
			dataInfo[index] = data.get(index);
		}
		return dataInfo;
	}
	


}
