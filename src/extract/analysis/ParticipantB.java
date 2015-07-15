package extract.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import columncontents.*;
import extract.buffer.TableBuf;
import extract.lookup.TabLookup;

/**
 * Identifies if column contains participantB and returns which subtype
 * @author sloates
 *
 */
public class ParticipantB {
	
	//Gets instances of different protein subtypes
	Uniprot u = Uniprot.getInstance();
	SwisProt s = SwisProt.getInstance();
	IPI i = IPI.getInstance();
	GeneName g = GeneName.getInstance();
	English e = English.getInstance();
	
	/**
	 * Checks through the columns, matching for participantB
	 * @param col
	 * @return
	 */
	private Protein getCell(TableBuf.Column col){
		int count = 0;
		while(count < 10 && count < col.getDataCount()){
			TableBuf.Cell cell = col.getData(count);
			if(cell != null){
				String cellData = cell.getData();
				if(cell.getData() != null){
					//TODO verify that this is needed.
					//This way gets better F1 but not grounded here.
					if(u.matchesFormat(cellData,u.regEx) != null)
						return u;
					if(s.matchesFormat(cellData,s.regEx) != null)
						return s;
					if(i.matchesFormat(cellData,i.regEx) !=null)
						return i;
					if(g.matchesFormat(cellData,s.regEx) != null)
						return g;//TODO Specify this geneset
					if(e.cellMatch(cellData) != null)
						return e;
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
