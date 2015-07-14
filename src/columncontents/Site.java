package columncontents;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import columncontents.ColumnContents;

public abstract class Site implements ColumnContents {
	
	public List<String> matchesFormat(String input,String regEx){
		List<String> sites = new ArrayList<String>();
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(input);
		while(m.find())
			sites.add(m.group());
		if(sites.size() > 0 )
			return sites;
		return null;
	}
	/**
	 * Returns cutoff values for positions
	 * @return
	 */
	public int[] validPosition(){
		return new int[]{
			1,20,10000
		};
	}
	
	//TODO
}
