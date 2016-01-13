package tableBuilder;

import java.io.File;

import tableBuilder.TableBuf.Table;

public class TableWrapper {
	
	private Table table;
	private File file;
	
	public TableWrapper(Table t){
		this.table = t;
	}
	public TableWrapper(Table t, File file){
		this.setFile(file);
		this.table = t;
	}

	public Table getTable() {
		return table;
	}
	public void setTable(Table table) {
		this.table = table;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public boolean hasFile(){
		return file!=null;
	}
	
	

}
