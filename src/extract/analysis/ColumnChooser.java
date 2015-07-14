package extract.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import extract.analysis.TableType.ColumnTypes;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.buffer.TableBuf.Table;

public class ColumnChooser {
	TableBuf.Table table;
	/**
	 * Creates a column chooser object using the current table and columnLabels
	 * @param table
	 * @param columnLabels
	 */
	public ColumnChooser(Table table) {
		this.table = table;
	}
	
	
	
	
	private boolean notBad(String potentialA, List<String> partB) {
		// TODO Make sure A is not in the participantBList
		//Make sure A does not match any words that would lead to a false positive
		return false;
	}
	
	private String checkFoldCols(String colHeader){
		
		//TODO Use the chemical database and protein databases, plus abbreviation lookup
		return null;
	}
	
	/**
	 * Returns a hashmap of partAs and corresponding fold columns
	 * @param columns
	 * @return
	 */
	public HashMap<String,List<TableBuf.Column>> getPartA(HashMap<ColumnTypes,List<TableBuf.Column>> columns,
															List<String> partB){
		HashMap<String, List<TableBuf.Column>> partACols = new HashMap<String, List<TableBuf.Column>>();
		String title = null;
		List<String> textMatch= null;
		//First look in the fold Columns, if not here do other checks
		//TODO Can check against the text here as well in the future
		for(TableBuf.Column col : columns.get(ColumnTypes.FOLD)){
			String potentialA = checkFoldCols(col.getHeader().getData());
			if(potentialA != null){
				if(partACols.containsKey(potentialA) && notBad(potentialA,partB)){
					partACols.get(potentialA).add(col);
				}else{
					List<TableBuf.Column> addCol = new ArrayList<TableBuf.Column>();
					addCol.add(col);
					partACols.put(potentialA,addCol);
				}
			}
		}
		//TODO Add the text matching up here send to checkA
		//text matcher goes here
		if(partACols.size() == 0){
			List<TableBuf.Column> foldCols = getBestFold(columns);
			List<String> possA = new ArrayList<String>();
			int count = 0;
			boolean gotA = false;
			while(count < table.getCaptionCount() && gotA ==false){
				int wordCount = 0;
				String caption[] = table.getCaption(count).split("\\W");
				while(wordCount < caption.length && gotA == false){
					String word = caption[wordCount];
					String partA = checkA(word,textMatch);//TODO groundsA as well
					if(partA != null && notBad(partA,partB)){
						partACols.put(partA,foldCols); //TODO implement this part, do last capital in checkA if no match
						gotA = true;
					}	
					wordCount++;
				}
				count++;
			}
					
			if(gotA == false){
				//TODO ground the text matches of A
			}
		}
		
		return partACols;
	}

	//___________________________________________________________
	//HELPER METHODS IN RETRIEVING PARTICIPANTB
	//_____________________________________________________________
	/**
	 * Returns array of booleans indicating which values are contained
	 * 0 = uni, 1 = swis, 2 = ipi, 3 = gene, 4 = english name
	 * @param protCols
	 * @return
	 */
	private boolean[] protTypes(HashMap<ColumnTypes,List<TableBuf.Column>>  protCols){
		boolean uniprot = false;
		if(protCols.containsKey(ColumnTypes.UNIPROT))
			uniprot = true;
		boolean swisprot = false;
		if(protCols.containsKey(ColumnTypes.SWISPROT))
			swisprot = true;
		boolean ipi = false;
		if(protCols.containsKey(ColumnTypes.IPI))
			ipi = true;
		boolean gene = false;
		if(protCols.containsKey(ColumnTypes.GENE))
			gene = true;
		boolean english = false;
		if(protCols.containsKey(ColumnTypes.ENGLISH))
			english= true;
		
		return new boolean []{uniprot,swisprot,ipi,gene,english};
	}
	
	/**
	 * Grounds participantB, returns null if unable to
	 * @param participantB
	 * @return
	 */
	private String groundB(String participantB){
		//TODO Decide on how to ground the participant
		return null;
	}
	
	/**
	 * Go through every row and identify a grounded protein
	 * Use the size of the protobuf
	 * @param protCols
	 * @return
	 */
	private ArrayList<String> bestMatch(HashMap<ColumnTypes,List<TableBuf.Column>> protCols){
		ArrayList<String> participantB = new ArrayList<String>();
		boolean [] hasProt = protTypes(protCols);
		ColumnTypes [] listProts= new ColumnTypes []{ColumnTypes.UNIPROT,ColumnTypes.SWISPROT,ColumnTypes.IPI,
										ColumnTypes.GENE,ColumnTypes.ENGLISH};
		int size = 0;
		if(table.getColumnCount() > 0)
			size  = table.getColumn(0).getDataCount();
		//TODO get size
		for(int i = 0; i < size; i++){
			String grounded = null;
			int count = 0;
			while(grounded == null && count < listProts.length){
				if(hasProt[count] && protCols.get(listProts[count]).get(0).getDataCount()>i) {
					//TODO null Check here
					String partB =protCols.get(ColumnTypes.UNIPROT).get(0).getData(i).getData();
					grounded = groundB(partB);					
				}
				count++;
			}
			participantB.add(grounded);
		}
		return participantB;
	}

	public List<String> getParticipantB(HashMap<ColumnTypes,List<TableBuf.Column>> cols){
		//MUST GROUND PARTICIPANT B IN THE FIRST PART
		//TODO gradually add in functionality.
		ArrayList<String> partB = bestMatch(cols); 
		return partB;
	}
}
