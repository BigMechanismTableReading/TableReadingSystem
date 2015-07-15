package columncontents;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import columncontents.ColumnContents;

public abstract class Site implements ColumnContents {
	private String headerRegEx = "site|residue|position|syt|location";//TODO write the header
	/**
	 * Returns cutoff values for positions
	 * @return
	 */
	public int[] validPosition(){
		return new int[]{
			1,20,10000
		};
	}
	
	@Override
	public String headerMatch(String match) {
		Pattern p = Pattern.compile(headerRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
	
	
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

}
