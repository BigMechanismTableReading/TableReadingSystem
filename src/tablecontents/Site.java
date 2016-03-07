package tablecontents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;
import utils.Pair;
import tableBuilder.TableBuf;

/**
 * Abstract class for site types
 * @author sloates
 *
 */
public abstract class Site implements ColumnContents {
	public String headerRegEx = "site|residue|location|tyrosine";//|position";//TODO write the header
	String extractRegEx = null;
	String regEx = null;
	public int confidenceNeeded = 3;
	
	@Override
	public String headerMatch(String match) {
		Pattern p = Pattern.compile(headerRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find()){
			return m.group();
		}
		return null;
	}
	@Override
	public boolean needsBoth(){
		return false;
	}
	@Override
	public Pair<String,String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	
	/**
	 * Returns if the cell matches the specific site regEx
	 * @param match
	 * @param regEx
	 * @return
	 */
	public String cellMatch(String match, String regEx) {
		String sites = "";
		Pattern p = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		while(m.find())
			sites += "," + m.group();
		sites = sites.replaceAll(",$", "");
		if(sites.equals(""))
			return null;
		return sites;
	}
	
	@Override
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row){
		HashMap<String,String> siteBase = new HashMap<String,String>();
		List<String> sites = new ArrayList<String>();
		List<String> aminos = new ArrayList<String>();
		Pattern site = Pattern.compile(extractRegEx,Pattern.CASE_INSENSITIVE);
		for(TableBuf.Column c : cols){
			if (row < c.getDataCount()){
				TableBuf.Cell cell = c.getData(row);
				if(cell != null){
					String data = cell.getData();
					if(data != null){
						Matcher m = site.matcher(data);
						while(m.find()){
							String s = m.group();
							String [] both = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
							if(both.length== 2){
								sites.add(both[1]);
								aminos.add((both[0].replaceAll("[^A-Z[a-z]]", "")));
							}
						}
					}
				}
			}
		}
		if(!sites.equals("")){
			siteBase.put("site", Arrays.toString(sites.toArray()));
		}
		if(!aminos.equals("")){
			siteBase.put("base",Arrays.toString(aminos.toArray()));
		}
		return siteBase;		
	}	
	@Override
	public int getCellConfNeeded(){
		return confidenceNeeded;
	}
	@Override
	public int getPriorityNumber(){
		return 7;
	}
}
 