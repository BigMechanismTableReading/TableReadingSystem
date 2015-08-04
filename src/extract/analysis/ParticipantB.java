package extract.analysis;

import tablecontents.*;
import extract.buffer.TableBuf;

/**
 * Identifies if column contains participantB and returns which subtype
 * @author sloates
 *
 */
public class ParticipantB {
	//Gets instances of different protein subtypes
	
	/**
	 * Checks through the columns, matching for participantB types
	 * @param col
	 * @return
	 */
	private Protein getCell(TableBuf.Column col){
		int count = 0;
		Protein p[] = Protein.protList;
		while(count < 10 && count < col.getDataCount()){
			TableBuf.Cell cell = col.getData(count);
			if(cell != null){
				String cellData = cell.getData();
				if(cellData != null && !(cellData.trim().length() == 0)){
					//TODO verify that this is needed.
					//This way gets better F1 but not grounded here.
					if(p[0].matchesFormat(cellData,p[0].getRegEx(),false) != null)
						return p[0];
					if(p[1].matchesFormat(cellData,p[1].getRegEx(),false)  != null)
						return p[1];
					if(p[2].matchesFormat(cellData,p[2].getRegEx(),false)  !=null)
						return p[2];
					if(p[3].cellMatch(cellData) != null)
						return p[3];
					if(p[4].cellMatch(cellData) != null)
						return p[4];
					//System.out.println(cellData);
					//This way gets actual grounding
//					if(u.cellMatch(cellData) != null)
//						return u;
//					if(s.cellMatch(cellData) != null)
//						return s;
//					if(i.cellMatch(cellData)!=null)
//						return i;
//					if(g.cellMatch(cellData) != null)
//						return g;//TODO Specify this geneset
//					if(e.cellMatch(cellData) != null)
//						return e;
				}
			}
			count++;
		}
		return null;
	}
	
	/**
	 * Returns a protein subtype if this column contains participantB
	 * else returns null
	 * @param header
	 * @param col
	 * @return
	 */
	public Protein hasParticipantB(TableBuf.Column col){
		Protein p = null;
		p = getCell(col);
		return p;
	}
}
