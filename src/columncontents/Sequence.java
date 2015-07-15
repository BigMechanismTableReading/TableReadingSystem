package columncontents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class Sequence implements ColumnContents {
	private String headerRegEx = "sequence";
	
	@Override
	public String headerMatch(String match) {
		Pattern p = Pattern.compile(headerRegEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
	String cellMatch(String match,String regEx) {
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}
}
