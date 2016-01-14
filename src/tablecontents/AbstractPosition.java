package tablecontents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tableBuilder.TableBuf;
import tableBuilder.TableBuf.Column;

/**
 * Abstract class for position types
 * @author sloates
 *
 */
public abstract class AbstractPosition  implements ColumnContents{
	public String headerRegEx = null;
	public String cellRegEx = null;
	public int confidenceNeeded = 5;
	
	@Override
	public HashMap<String, String> extractData(List<Column> cols, int row) {
		// TODO Auto-generated method stub
		//TODO determine the best position possible by using valid pos or something similar in here.
		HashMap<String,String> position = new HashMap<String, String>();
		for(TableBuf.Column col : cols){
			TableBuf.Cell c = col.getData(row);
			if(c!=null){
				String data = c.getData();
				data = data.replaceAll("\\.0", "");
				String []nums = data.split("[^\\d]");
				List<String> actualNums = new ArrayList<String>();
				boolean badPos = false;
				for(String pos : nums){
					if(pos.length() > 0 && pos.charAt(0) == '0'){
						badPos = true;
					}
					if(pos.length() > 1){
						actualNums.add(pos);
					}
				}
				if(badPos){
					position.put("site", null);
					return position;
				}
				position.put("site", actualNums.toString());
				return position;
			}
		}
		return null;
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
	public int getCellConfNeeded(){
		return confidenceNeeded;
	}
	
	@Override
	public int getPriorityNumber(){
		return 9;
	}
}
