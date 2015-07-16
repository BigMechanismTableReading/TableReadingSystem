package extract.analysis;

import java.util.HashMap;
import java.util.List;

import columncontents.ColumnContents;
import columncontents.Protein;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.types.Reaction;

public class Extraction {
	
	/**
	 * Gets all participantB 
	 * @param contents
	 * @return
	 */
	private HashMap<Integer,String> getAllParticipantB(HashMap<ColumnContents, List<TableBuf.Column>> contents){
		TableBuf.Column col = null;
		ColumnContents protein = null;
		for(ColumnContents c : contents.keySet()){
			if(c instanceof Protein){
				col = contents.get(c).get(0);
				protein = c;
			}
		}
		HashMap<Integer,String> partB = new HashMap<Integer,String>();
		int row = 0;
		if(col != null){
			while(row < col.getDataCount()){
				String ground = protein.bestColumn(contents, row);
				if(ground != null){
					partB.put(row, ground);
				}
				row++;
			}
		}
		
		return partB;
	}

	public void ExtractInfo(Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> colInfo){
		//TODO add the reaction to the index card; idx.setReaction, colInfo.getA().toString
		Reaction r = colInfo.getA();
		HashMap<ColumnContents,List<TableBuf.Column>> contents = colInfo.getB();
		HashMap<Integer, String> partB = getAllParticipantB(contents);
	}
}
