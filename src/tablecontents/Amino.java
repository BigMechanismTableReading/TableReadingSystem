package tablecontents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Cell;


/**
 * Abstract class for seperated amino acids
 * @author sloates
 *
 */
public abstract class Amino implements ColumnContents{
	String headerRegEx = "\bamino.*|\bbase\b|syt";
	public int confidenceNeeded = 5;
	@Override
	public String headerMatch(String match) {
		Pattern p = Pattern.compile(headerRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
	String cellMatch(String match,String regEx) {
		Pattern p = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
	
	@Override
	public boolean needsBoth(){
		return false;
	}
	@Override
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row){
		HashMap<String,String> amino = new HashMap<String,String>();
		for(TableBuf.Column c : cols){
			Cell cell = c.getData(row);
			if(cell != null){
				String base = cell.getData();
				amino.put("base", Arrays.toString(base.split("\\W")));
			}
		}
		return amino;
	}
	
	@Override
	public Pair<String,String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	@Override
	public int getCellConfNeeded(){
		return confidenceNeeded;
	}
	
}
