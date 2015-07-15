package columncontents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import columncontents.ColumnContents;
import extract.lookup.TabLookup;

public abstract class Protein implements ColumnContents{
	
	private static Protein prot = null;
	
	public static ColumnContents getInstance(){
		return prot;
	}

	TabLookup t = TabLookup.getInstance();
	
	public String matchesFormat(String input,String regEx) {
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(input);
		if(m.find())
			return m.group();
		return null;
	}
	
	@Override
	public String headerMatch(String match) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Returns the grounded ID if found, else returns null
	 * @param ungrounded
	 * @return
	 */
	public abstract String groundIdentity(String ungrounded);
}