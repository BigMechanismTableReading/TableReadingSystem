package extract.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import columncontents.*;
import extract.analysis.TableType.ColumnTypes;
import extract.buffer.TableBuf;
import extract.lookup.TabLookup;

/**
 * Class used to find the ParticipantBs in different Columns
 * @author sloates
 *
 */
public class ParticipantB {
	Uniprot u = Uniprot.getInstance();
	SwisProt s = SwisProt.getInstance();
	IPI i = IPI.getInstance();
	GeneName g = GeneName.getInstance();
	English e = English.getInstance();
	/**
	 * Helper method that handles matching
	 * @param pattern
	 * @param data
	 * @param potential
	 * @return
	 */
	private ColumnTypes matchHelp(Pattern pattern, String data , ColumnTypes potential){
		Matcher match = pattern.matcher(data);
		if(match.find()){
			return potential;
		}else{
			return ColumnTypes.NOTPROTEIN;
		}
	}
	
	
	/**
	 * Sees if the column has potential participantBs
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
					if(u.cellMatch(cellData) != null)
						return u;
					if(s.cellMatch(cellData) != null)
						return s;
					if(i.cellMatch(cellData)!=null)
						return i;
					if(g.groundIdentity(g.cellMatch(cellData)) != null)
						return g;//TODO Specify this geneset
					if(e.cellMatch(cellData) != null)
						return e;
				}
			}
			count++;
		}
		return null;
	}
	
	/**
	 * Checks to see if the current Column is a participantB column
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
