package extract.analysis;

import java.util.HashMap;

import columncontents.ColumnContents;
import columncontents.Protein;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;

public class DetermineTable {
	
	private boolean assignB(TableBuf.Table table, ParticipantB partB, HashMap<ColumnContents, Column> labels){
		boolean hasProt = false;
		for(TableBuf.Column col : table.getColumnList()){
			Protein p = partB.hasParticipantB(col);
			if(p != null){
				labels.put(p, col);
				hasProt = true;
			}
		}
		return hasProt;
	}
	public void determine(TableBuf.Table table){
		ParticipantB  partB = new ParticipantB();
		HashMap<ColumnContents,TableBuf.Column> labels = new HashMap<ColumnContents,TableBuf.Column>();
		assignB(table,partB,labels);
	}
}
