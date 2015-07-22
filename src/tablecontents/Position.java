package tablecontents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;

public class Position implements ColumnContents{
	//TODO determine matching for this
	private String headerRegEx = "residue|location|position|site|tyrosine|serine|lysine";
	//private String cellRegEx = "^\\d{3,5}$";//TODO figure out good position regex
	public int confidenceNeeded = 5;
	
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
		/*Pattern p = Pattern.compile(cellRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();*/
		return null;
	}
	
	public Pair<String, String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	@Override
	public HashMap<String, String> extractData(List<TableBuf.Column> cols, int row) {
		// TODO Auto-generated method stub
		//TODO determine the best position possible by using valid pos or something similar in here.
		HashMap<String,String> position = new HashMap<String, String>();
		for(TableBuf.Column col : cols){
			TableBuf.Cell c = col.getData(row);
			if(c!=null){
				String data = c.getData();
				data = data.replaceAll("\\.0", "");
				String []nums = data.split("[^\\d]");
				boolean badPos = false;
				for(String pos : nums){
					if(pos.length() > 0 && pos.charAt(0) == '0'){
						badPos = true;
					}
				}
				if(badPos){
					position.put("site", null);
					return position;
				}
				position.put("site", Arrays.toString(nums));
				return position;
			}
		}
		return null;
	}
	
	@Override
	public int getCellConfNeeded(){
		return confidenceNeeded;
	}
	

}
