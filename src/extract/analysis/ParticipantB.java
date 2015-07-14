package extract.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.TableType.ColumnTypes;
import extract.buffer.TableBuf;

public class ParticipantB {

	private String uniprotID = "([OPQ][0-9][A-Z0-9]{3}[0-9])|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}";
	private String swisprotID = "[A-Z[0-9]]{1,4}_[A-Z]{0,10}";
	private String IPI = "IPI[0-9]{8}";
	private String geneID = "[A-Z]{3}[0-9]{5}|([A-Z]{2}[A-Z[0-9]]{1,5})|[A-Z[a-z]]{2}[A-Z[a-z[0-9]]]{1,3}";
	
	private boolean getHeader(String header){
		return false;//TODO
	}
	
	private ColumnTypes matchHelp(Pattern p, String data , ColumnTypes potential){
		Matcher match = p.matcher(data);
		if(match.find()){
			return potential;
		}else{
			return ColumnTypes.UNKNOWN;
		}
	}
	private boolean LookupEnglish(String data){
		
		return false;
	}
	/**
	 * Gets if the column returns participantB
	 * @param col
	 * @return
	 */
	private ColumnTypes getCell(TableBuf.Column col){
		int count = 0;
		Pattern uni = Pattern.compile(uniprotID);
		Pattern swisprot  = Pattern.compile(swisprotID);
		Pattern ipi = Pattern.compile(IPI);
		Pattern gene = Pattern.compile(geneID);
		while(count <10 && count <col.getDataCount()){
			TableBuf.Cell cell = col.getData(count);
			if(cell != null){
				String cellData = cell.getData();
				if(cell.getData() != null){
					if(matchHelp(uni,cellData,ColumnTypes.UNIPROT) != ColumnTypes.UNKNOWN)
						return ColumnTypes.UNIPROT;
					if(matchHelp(swisprot,cellData,ColumnTypes.SWISPROT) != ColumnTypes.UNKNOWN)
						return ColumnTypes.SWISPROT;
					if(matchHelp(ipi,cellData,ColumnTypes.IPI) != ColumnTypes.UNKNOWN)
						return ColumnTypes.IPI;
					if(matchHelp(gene,cellData,ColumnTypes.GENE) != ColumnTypes.UNKNOWN)
						return ColumnTypes.GENE;
					if(LookupEnglish(cellData))
						return ColumnTypes.ENGLISH;
				}
			}
			count++;
		}
		return ColumnTypes.UNKNOWN;
	}
	
	public ColumnTypes hasParticipantB(String header, TableBuf.Column col){
		ColumnTypes bType = ColumnTypes.UNKNOWN;
		getHeader(header);
		bType = getCell(col);
		return bType;
	}
}
