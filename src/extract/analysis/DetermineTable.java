package extract.analysis;

import java.util.HashMap;

import columncontents.ColumnContents;
import columncontents.Protein;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;

/**
 * Determines whether or not a table is relevant
 * @author sloates
 *
 */
public class DetermineTable {
	
	/**
	 * Identifies any columns that have potential participantB
	 * @param table
	 * @param partB
	 * @param labels
	 * @return
	 */
	private boolean assignB(TableBuf.Table table, ParticipantB partB, HashMap<ColumnContents, Column> labels){
		boolean hasProt = false;
		for(TableBuf.Column col : table.getColumnList()){
			Protein p = partB.hasParticipantB(col);
			if(p instanceof Protein){
				labels.put(p, col);
				hasProt = true;
			}
		}
		return hasProt;
	}
	
	/**
	 * Pipeline that determines whether a table is relevant and what the table indicates
	 * @param table
	 */
	public void determine(TableBuf.Table table){
		ParticipantB  partB = new ParticipantB();
		HashMap<ColumnContents,TableBuf.Column> labels = new HashMap<ColumnContents,TableBuf.Column>();
		if(assignB(table,partB,labels)){
			//TODO Determine the reaction type and relevance
		}else{
			//TODO Decide what to determine when the table is not relevant
		}
	}
}
