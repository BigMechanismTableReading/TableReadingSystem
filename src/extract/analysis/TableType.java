package extract.analysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import extract.analysis.TableType.ColumnTypes;
import extract.analysis.TableType.ReactionType;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.buffer.TableBuf.Table;
import extract.reaction.ReactionReader;

public class TableType {
	
	private HashMap<ColumnTypes, List<TableBuf.Column>> columnMapping = new HashMap<ColumnTypes, List<TableBuf.Column>>();
	private TableBuf.Table table = null;
	
	//TODO possible enum subclassing
	public enum ColumnTypes{
		UNIPROT,SWISPROT,IPI,GENE,ENGLISH,
		MULTISITE,PHOSPHOSITE,METHYLSITE,SUMOSITE,ACETYLSITE,
		FOLD,PHOSAMINO,METHAMINO,MULTAMINO,POSITION, UNKNOWN,NOTPROTEIN
	}
	public enum ReactionType{
		MULTIPLE,PHOSPHORYLATION,ACETYLATION,FARNESYLATION,GLYCOSYLATION,HYDROXYLATION,
		METHYLATION,RIBOSYLATION,SUMOYLATION,UBIQUITINATION,OXIDATION,CARBIDOMETHYL,
		INHIBITION,ACTIVATION,UNKNOWN
	}
	public enum proteinSubType{
		UNIPROT,SWISPROT,IPI,GENE,ENGLISH
	}
	

	/**
	 * Takes in the column and matches contents to a columnType
	 * @param col
	 * @return
	 */
	private List<ColumnTypes> matchCell(TableBuf.Column col){
		List<ColumnTypes> types = new LinkedList<ColumnTypes>();
		int count = 0;
		while(count < col.getDataCount() && count < 50 ){
			TableBuf.Cell currCell = col.getData(count);
			if(currCell != null){
				String data = currCell.getData();
				//TODO check against cell types to see if there is a match
			}
			count++;
		}
		
		return types;
	}
	/**
	 * Takes in the header string and returns possible columnTypes
	 * @param header
	 * @return
	 */
	private List<ColumnTypes> matchHeader(String header){
		List<ColumnTypes> types = new LinkedList<ColumnTypes>();
		//TODO check against header types for a possible match
		return types;
	}

	private ColumnTypes mergeNames(ColumnTypes nameOne, ColumnTypes nameTwo){
		
		//TODO add flexibility with some way of calculating this.
		return ColumnTypes.PHOSPHOSITE;
	}
	
	/**
	 * Takes in list of size two and merges together with the other list
	 * @param cols
	 * @return
	 */
	private TableBuf.Column.Builder mergeColumns(TableBuf.Column c1, TableBuf.Column c2, ColumnTypes newName){
		TableBuf.Column.Builder newColumn = TableBuf.Column.newBuilder();
		newColumn.setHeader(TableBuf.Cell.newBuilder().setData(newName.toString()));
		
		for(int i = 0; i < c1.getDataCount(); i ++){
			String combined = "";
			if(c2.getDataCount() >i && c2.getData(i)!= null && c1.getData(i) != null){
				combined = c1.getData(i).getData() + c2.getData(i).getData();
			}
			newColumn.addData(TableBuf.Cell.newBuilder().setData(combined));
		}
		
		return newColumn;
	}
	/**
	 * Merges any columns that need to be merged together
	 * Must have subtypes of amino to indicate which reaction it matches with
	 * @param table
	 * @param columnMapping 
	 * @return
	 */
	private TableBuf.Table determineMerge(ColumnTypes firstCol, ColumnTypes secondCol,ColumnTypes newName) {
		//TODO build subtypes of amino to determine matching
		TableBuf.Table.Builder tableBuilder = table.toBuilder();
		if(columnMapping.containsKey(firstCol) && columnMapping.containsKey(secondCol)){
			List<TableBuf.Column> cols = new LinkedList<TableBuf.Column>();
			if(columnMapping.get(firstCol).size() > 0 &&  columnMapping.get(secondCol).size() >0){
				TableBuf.Column c1 = columnMapping.get(firstCol).get(0);
				TableBuf.Column c2 = columnMapping.get(secondCol).get(0);
				tableBuilder.addColumn(mergeColumns(c1,c2,newName));
				table = tableBuilder.build();
			}
		}
		return table;
	}
	
	private List<List<ColumnTypes>> mergeCols(){
		//TODO, find all possible combinations and return them.
		return null;
	}
	/**
	 * Adds all the merge columns to the table and the HashMap
	 * @param columnMapping
	 * @param table
	 * @return
	 */
	private TableBuf.Table addMergeCols() {
		for(List<ColumnTypes> types: mergeCols()){
			ColumnTypes newType = types.get(2);
			int add = table.getColumnCount();
			table = determineMerge(types.get(0),types.get(1),newType);
			if(table.getColumnCount() > add){
				TableBuf.Column newCol = table.getColumn(table.getColumnList().size()-1);
				//TODO make sure this adds at the end of each column
				if(columnMapping.containsKey(newType)){
					columnMapping.get(newType).add(0, newCol);
				}else{
					List<TableBuf.Column> c = new LinkedList<TableBuf.Column>();
					c.add(newCol);
					columnMapping.put(newType,c);
				}
			}
		}
		return table;
	}
	
	
	private void addColumnMapEntry(ColumnTypes t, TableBuf.Column col){
		if(columnMapping.containsKey(t)){
			columnMapping.get(t).add(col);
		}else{
			ArrayList<TableBuf.Column> list = new ArrayList<TableBuf.Column>();
			list.add(col);
			columnMapping.put(t,col);
		}
	}
	
	/**
	 * Returns whether this table has a participantB Columns
	 * @return
	 */
	private boolean getPartB(){
		ParticipantB bCheck = new ParticipantB();
		for(TableBuf.Column col : table.getColumnList()){
			ColumnTypes t = bCheck.hasParticipantB(col);
			addColumnMapEntry(t,col);
		}
		if(columnMapping.containsKey(ColumnTypes.NOTPROTEIN)){
			if(columnMapping.size()>1)
				return true;
			else 
				return false;
		}else if(columnMapping.size() > 0){
			return true;
		}
		return false;
	}
	/**
	 * Gets the possible column types indicated by the headers
	 * @param reactionHash 
	 * @param table
	 * @return
	 */
	private void getColumnLabels(HashMap<String, HashMap<ColumnTypes, String[]>> reactionHash){
		//Identify ParticipantB Before other columns
		for(TableBuf.Column col : table.getColumnList()){
			String header = "";
			if(col.getHeader() != null){
				header = col.getHeader().getData();
				//TODO efficent way to do the header
				System.out.println(matchHeader(header));
				
				//List<ColumnTypes> cellTypes = matchCell(col);
				//TODO compare the two lists to decide which column types are correct
				//Add to columnMapping here
			}
		}
		addMergeCols();
	}
	
	private List<ColumnTypes> getNeededColumnTypes(ReactionType r){
		//TODO gets the needed column types
		return null;
	}
	
	private ReactionType getReactionType(Set<ColumnTypes> colTypes){
		//TODO Use identified columns to find the reaction type occuring inside the table
		String title = "";
		if(table.getCaptionList().size() > 0)
			title = table.getCaption(0);
		return null;
	}
	
	/**
	 * Returns what the reaction type of the table is, in addition to getting what each column type is
	 * @param table
	 * @return
	 */
	public Pair<ReactionType,List<ColumnTypes>> tableType(TableBuf.Table table,String reactionFile){
		this.table = table;
		//Determines whether there is a participantB and maps the columns that have participantB
		boolean hasPartB = getPartB();
		if(hasPartB == true){
			//Gets the reactions from a formatted file.
			ReactionReader r = ReactionReader.getInstance(reactionFile);
			HashMap<String, HashMap<ColumnTypes,String[]>> reactionHash = r.getReact();
			//TODO Uses these Hashmaps on the headers first, then inside the columns(looks through first 10 or so rows
			getColumnLabels(reactionHash);
			ReactionType reaction = getReactionType(columnMapping.keySet());
			List<ColumnTypes> colsNeeded = getNeededColumnTypes(reaction);
			Pair<ReactionType,List<ColumnTypes>> pair = new Pair<ReactionType, List<ColumnTypes>>(reaction,colsNeeded);
			return pair;
		}else{
			System.err.println("No Participant B Column Found");
			return null;
		}
	}
	
	
	
	public HashMap<ColumnTypes, List<TableBuf.Column>> getColumnMapping() {
		return columnMapping;
	}
	public void setColumnMapping(HashMap<ColumnTypes, List<TableBuf.Column>> columnMapping) {
		this.columnMapping = columnMapping;
	}
	public TableBuf.Table getTable() {
		return table;
	}
	public void setTable(TableBuf.Table updatedTable) {
		this.table = updatedTable;
	}
}
