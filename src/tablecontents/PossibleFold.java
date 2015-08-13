package tablecontents;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.Pair;
import extract.buffer.TableBuf.Column;

public class PossibleFold implements ColumnContents{
	private String cellRegEx =  "(\\b[-+]?[0-9]+\\.[0-9]+)";

	private static PossibleFold instance = null;
	public static PossibleFold getInstance(){
		if(instance == null)
			instance = new PossibleFold();
		return instance;		
	}
	private PossibleFold(){
		
	}
	
	@Override
	public String headerMatch(String match) {
		return null;
	}

	@Override
	public String cellMatch(String match) {
		Pattern p = Pattern.compile(cellRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
	
	@Override
	public boolean needsBoth() {
		return false;
	}
	
	@Override
	public int getCellConfNeeded() {
		return 3;
	}
	
	@Override
	public Pair<String, String> bestColumn(
			HashMap<ColumnContents, List<Column>> cols, int row) {
		return null;
	}
	
	@Override
	public HashMap<String, String> extractData(List<Column> cols, int row) {
		return null;
	}
	
	@Override
	public int getPriorityNumber(){
		return 10;
	}

}
