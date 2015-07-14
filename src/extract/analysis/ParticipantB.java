package extract.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.TableType.ColumnTypes;
import extract.buffer.TableBuf;
import extract.lookup.TabLookup;

public class ParticipantB {

	private String uniprotID = "([OPQ][0-9][A-Z0-9]{3}[0-9])|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}";
	private String swisprotID = "[A-Z[0-9]]{1,4}_[A-Z]{0,10}";
	private String IPI = "IPI[0-9]{8}";
	private String geneID = "[A-Z]{3}[0-9[A-Z]]{1,5}";//TODO specify this reg ex
	private String decimal = "\\d+\\.\\d+";
	Pattern uni = Pattern.compile(uniprotID);
	Pattern swisprot  = Pattern.compile(swisprotID);
	Pattern ipi = Pattern.compile(IPI);
	Pattern gene = Pattern.compile(geneID);

	private ColumnTypes matchHelp(Pattern p, String data , ColumnTypes potential){
		Matcher match = p.matcher(data);
		if(match.find()){
			return potential;
		}else{
			return ColumnTypes.NOTPROTEIN;
		}
	}
	private boolean LookupEnglish(String data){
		TabLookup t = TabLookup.getInstance();
		data = data.replaceAll("\\W+"," ").toUpperCase();
		if(t.english.containsKey(data))
			return true;
		return false;
	}
	private boolean LookupGene(String data){
		TabLookup t = TabLookup.getInstance();
		data = data.replaceAll("\\W+"," ").toUpperCase();
		if(t.genename.containsKey(data))
			return true;
		return false;
	}
	/**
	 * Gets if the column returns participantB
	 * @param col
	 * @return
	 */
	private ColumnTypes getCell(TableBuf.Column col){
		int count = 0;
		while(count < 10 && count < col.getDataCount()){
			TableBuf.Cell cell = col.getData(count);
			if(cell != null){
				String cellData = cell.getData();
				if(cell.getData() != null){
					if(count == 0 && Pattern.matches(decimal, cellData))
						count = 10;//TODO verify that this is needed.
					if(matchHelp(uni,cellData,ColumnTypes.UNIPROT) != ColumnTypes.NOTPROTEIN)
						return ColumnTypes.UNIPROT;
					if(matchHelp(swisprot,cellData,ColumnTypes.SWISPROT) != ColumnTypes.NOTPROTEIN)
						return ColumnTypes.SWISPROT;
					if(matchHelp(ipi,cellData,ColumnTypes.IPI) != ColumnTypes.NOTPROTEIN)
						return ColumnTypes.IPI;
					if(matchHelp(gene,cellData,ColumnTypes.GENE) != ColumnTypes.NOTPROTEIN && LookupGene(cellData))
						return ColumnTypes.GENE;//TODO Specify this geneset
					if(LookupEnglish(cellData))
						return ColumnTypes.ENGLISH;
				}
			}
			count++;
		}
		return ColumnTypes.NOTPROTEIN;
	}
	
	/**
	 * Checks to see if there is a participant B column that can be identified,
	 * @param header
	 * @param col
	 * @return
	 */
	public ColumnTypes hasParticipantB(TableBuf.Column col){
		ColumnTypes bType = ColumnTypes.NOTPROTEIN;
		bType = getCell(col);
		return bType;
	}
}
