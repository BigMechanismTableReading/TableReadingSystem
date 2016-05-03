package tablecontents;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringUtils;

import extract.lookup.GOLookup;
import extract.lookup.Lookup;
import tableBuilder.TableBuf;
import tableBuilder.TableBuf.Column;
import utils.Pair;

public class GOid implements ColumnContents{
	String headerRegEx = "\bGO.*|Process\b|Categories";
	private int confidenceNeeded = 4;
	private GOLookup go_lookup = null;
	private static GOid instance = null;
	private String cellRegEx;
	
	public static GOid getInstance(){
		if(instance == null){
			instance = new GOid();
		}
		return instance;
	}
	
	private GOid(){
		//TODO build the GOLookup using the appropriate text file
		go_lookup = GOLookup.getInstance();
		//TODO update so this works better in the future and can grab a larger variety
		cellRegEx = "(GO:\\d{7,8})|(\\d{7,8})|(([A-Za-z]{2,}\\s*)*)";
	}
	
	@Override
	public boolean needsBoth() {
		return false;
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

	private String groundIdentity(String format_match){
		if(StringUtils.isNumeric(format_match)){
			format_match = "GO:" + format_match;
		}
		if (format_match.contains("GO:")){
			if(go_lookup.lookup_go(format_match) != null){
				return format_match;
			}
		}else{
			if(go_lookup.lookup_process(format_match) != null){
				return go_lookup.lookup_process(format_match);
			}
		}
		return null;
	}
	@Override
	public String cellMatch(String match) {
		Pattern p = Pattern.compile(cellRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		String format_match = "none";
		if(m.find()){
			format_match = m.group();
		}
		String grounded = groundIdentity(format_match);
		return grounded;
	}

	@Override
	public Pair<String, String> bestColumn(HashMap<ColumnContents, List<Column>> cols, int row) {
		//TODO check if needed
		return null;
	}

	@Override
	public HashMap<String, String> extractData(List<Column> cols, int row) {
		HashMap<String, String> goInfo = new HashMap<String,String>();
		TableBuf.Column c = cols.get(0);
		String s = c.getData(row).getData();
		goInfo.put("GO:", cellMatch(s));
		//TODO put the process name here if requested
		return goInfo;
	}

	@Override
	public int getPriorityNumber() {
		return 0;
	}

	public Pair<String, String> get_data(Column go_col, int row) {
		String s = go_col.getData(row).getData();
		String match = cellMatch(s);
		String trans = "unknown";
		if(match != null){
			trans = go_lookup.lookup_go(match);
		}
		return new Pair<String,String>(cellMatch(s),trans);
	}

}
