package tablecontents;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Cell;


public abstract class Sequence implements ColumnContents {
	private String headerRegEx = "sequence";
	
	@Override
	public String headerMatch(String match) {
		Pattern p = Pattern.compile(headerRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
	String cellMatch(String match,String regEx) {
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
	
	@Override
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row){
		HashMap<String, String> seq = new HashMap<String,String>();
		for(TableBuf.Column c : cols){
			Cell cell = c.getData(row);
			if(cell != null){
				seq.put("Sequence", cell.getData());
			}
		}
		
		return seq;
	}
	
	@Override
	public Pair<String, String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	
}
