package extract.analysis;

import java.util.HashMap;
import java.util.List;

import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.analysis.TableType.ReactionType;
import extract.analysis.TableType.ColumnTypes;


public class TablePipeline {

	/**
	 * Pipeline that first determines relevance and neccesary columns,
	 * then selects the best columns to use, and then writes the info to a card.
	 * @param table
	 */
	public boolean runPipeline(TableBuf.Table table){
		TableType getReaction = new TableType();
		Pair<ReactionType,List<ColumnTypes>> pair = getReaction.tableType(table);
		table = getReaction.getTable();
		HashMap<ColumnTypes, List<TableBuf.Column>> columnLabels = getReaction.getColumnMapping();
		if(pair.getA() == ReactionType.UNKNOWN){
			return false;
		}else{
			ColumnChooser choose = new ColumnChooser(table);
			List<String> participantB = choose.getParticipantB(columnLabels);
			//TODO uses the participantBs to check for participantA
			//First check the fold column, if there are multipleAs in the fold column return those columns
			//and the participantAs, Gets the fold As well
			HashMap<String,TableBuf.Column> partAFold = choose.getPartA();
			//Once all participantAs have been retrieved along with fold, retrieve remaining info dependent on reaction
			//If there are mulitple participantAs then, info must be written for both one (two index cards)
			//Do a for each loop over the HashMap of partAFold, create list of idx to write to (only fold differs)
			List<IndexCards> = cr.writeColumns(pair,table,columnLabels);
			//TODO go through columns and write the info to index cards for each column
			
			
			return true;
		}
	}
}
