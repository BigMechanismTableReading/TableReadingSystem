package extract.analysis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.Protein;
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

	private void addPartA(HashMap<String, HashMap<ColumnContents, List<TableBuf.Column>>> participantAs,
			String partA, ColumnContents f, Column col) {
		//TODO make this structure into a class.
		if(participantAs.containsKey(partA)){
			addToData(f, col, participantAs.get(partA));
		} else {
			HashMap<ColumnContents, List<TableBuf.Column>> map = new HashMap<ColumnContents, List<TableBuf.Column>>();
			addToData(f, col, map);
			participantAs.put(partA, map);
		}
	}
	
	private void addToData(ColumnContents c, TableBuf.Column col, HashMap<ColumnContents,List<TableBuf.Column>> data){
		if (data.containsKey(c)){
			data.get(c).add(col);
		} else {
			LinkedList<TableBuf.Column> list = new LinkedList<TableBuf.Column>();
			list.add(col);
			data.put(c, list);
		}
	}
	
	private HashMap<String,HashMap<ColumnContents,TableBuf.Column>> getParticipantAs(
			HashMap<Integer,String> partB, HashMap<ColumnContents,List<TableBuf.Column>> contents){
		HashMap<String,HashMap<ColumnContents,TableBuf.Column>> participantAs = new HashMap<String,HashMap<ColumnContents,TableBuf.Column>>();
		for(ColumnContents f : contents.keySet()){
			if(f instanceof Fold){
				for (TableBuf.Column col : contents.get(f)){
					String partA = checkPartA(col);
					if (partA != null){
						addPartA(participantAs, partA, f, col);
					}
				}
			}
		}
		
		return null;
	}
	


	public void ExtractInfo(Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> colInfo){
		//TODO add the reaction to the index card; idx.setReaction, colInfo.getA().toString
		Reaction r = colInfo.getA();
		HashMap<ColumnContents,List<TableBuf.Column>> contents = colInfo.getB();
		HashMap<Integer, String> partB = getAllParticipantB(contents);
		getParticipantAs(partB);
	}
}
