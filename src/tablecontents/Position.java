package tablecontents;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.buffer.TableBuf;

public class Position implements ColumnContents{
	//TODO determine matching for this
	private String headerRegEx = "residue|location|position";
	private String cellRegEx = "^\\d{1,5}$";//TODO figure out good position regex
	
	private static Position pos = null;
	public static Position getInstance(){
		if(pos == null)
			pos = new Position();
		return pos;
	}
	private Position(){
		
	}
	
	@Override
	public String headerMatch(String match) {
		Pattern p = Pattern.compile(headerRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
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
	
	public String bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	

}
