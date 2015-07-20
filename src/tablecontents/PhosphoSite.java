package tablecontents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;

public class PhosphoSite extends Site implements ColumnContents {
	//TODO fix this up
	private static PhosphoSite phos = null;
	public static ColumnContents getInstance() {
		if(phos == null)
			phos = new PhosphoSite();
		return phos;
	}
	private PhosphoSite(){
		//TODO change regex
		//Need to account for being in a sequence
		regEx = "\\b(p?[SYT].{0,1}\\d{2,5})\\b|\\b(([Ss][Ee][Rr])|([Tt][Yy][Rr])|([Tt][Hh][Rr]))-*.{0,1}\\d{2,5}\\b";
	}
	@Override
	public String headerMatch(String match) {
		return null;
	}
	
	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match,regEx);
	}
}
