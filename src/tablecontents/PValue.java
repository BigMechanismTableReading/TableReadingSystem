package tablecontents;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tableBuilder.TableBuf;
import tableBuilder.TableBuf.Column;
import utils.Pair;

public class PValue implements ColumnContents {
	
	private static PValue p;
	private String cellRegEx;
	private String headerRegEx;
	private int confidenceNeeded;
	public static PValue getInstance(){
		if(p == null){
			p = new PValue();
		}
		return p;
	}
	
	private PValue(){
		//cellRegEx = "(\\d\\.\\d{0,12}(E-\\d{0,2}){0,1})";
		cellRegEx = "(\\d\\.\\d{0,12}(E-\\d{0,2}){0,1})|(0\\.\\d{0,8})";
		headerRegEx = "\\b(p.*val).*|\\b(e.*val).*";
	}
	@Override
	public boolean needsBoth() {
		return true;
	}
	@Override
	public int getCellConfNeeded() {
		return confidenceNeeded;
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
	@Override
	public Pair<String, String> bestColumn(HashMap<ColumnContents, List<Column>> cols, int row) {
		// TODO check if needed
		return null;
	}
	@Override
	public HashMap<String, String> extractData(List<Column> cols, int row) {
		HashMap<String,String> data = new HashMap<String,String>();
		if(cols.size()>0){
			TableBuf.Column c = cols.get(0);
			String s = cellMatch(c.getData(row).getData());
			data.put("PValue", s);
		}
		return data;
	}
	@Override
	public int getPriorityNumber() {
		// TODO figure out what is best here
		return 0;
	}
	
}
