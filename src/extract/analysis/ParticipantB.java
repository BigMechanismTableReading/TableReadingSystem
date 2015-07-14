package extract.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import columncontents.proteins.English;
import columncontents.proteins.GeneName;
import columncontents.proteins.IPI;
import columncontents.proteins.Protein;
import columncontents.proteins.SwisProt;
import columncontents.proteins.Uniprot;
import extract.analysis.TableType.ColumnTypes;
import extract.buffer.TableBuf;
import extract.lookup.TabLookup;

/**
 * Class used to find the ParticipantBs in different Columns
 * @author sloates
 *
 */
public class ParticipantB {
	Uniprot u = new Uniprot();
	SwisProt s = new SwisProt();
	IPI i = new IPI();
	GeneName g = new GeneName();
	English e = new English();
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
	 * Looks up english words in the database
	 * @param data
	 * @return
	 */
	private boolean LookupEnglish(String data){
		TabLookup t = TabLookup.getInstance();
		data = data.replaceAll("\\W+"," ").toUpperCase();
		if(t.english.containsKey(data))
			return true;
		return false;
	}
	
	/**
	 * Looks up the gene in the database
	 * @param data
	 * @return
	 */
	private boolean LookupGene(String data){
		TabLookup t = TabLookup.getInstance();
		data = data.replaceAll("\\W+"," ").toUpperCase();
		if(t.genename.containsKey(data))
			return true;
		return false;
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
					if(u.matchesFormat(cellData) != null)
						return u;
					if(s.matchesFormat(cellData) != null)
						return s;
					if(i.matchesFormat(cellData)!=null)
						return i;
					if(g.groundIdentity(g.matchesFormat(cellData)) != null)
						return g;//TODO Specify this geneset
					if(e.groundIdentity(cellData) != null)
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
