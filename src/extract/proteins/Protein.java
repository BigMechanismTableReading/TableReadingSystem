package extract.proteins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ColumnContents.ColumnContents;
import extract.lookup.TabLookup;

public abstract class Protein implements ColumnContents{
	
	TabLookup t = TabLookup.getInstance();
	
	public String matchesFormat(String input,String regEx) {
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(input);
		if(m.find())
			return m.group();
		return null;
	}
	
	/**
	 * Returns the grounded ID if found, else returns null
	 * @param ungrounded
	 * @return
	 */
	public abstract String groundIdentity(String ungrounded);
}
