package tablecontents;

import tablecontents.ColumnContents;

public class PhosphoSite extends Site implements ColumnContents {
	//TODO fix this up
	private String regEx = "\bp?[SYT].{0,1}\\d{1,4}\b|\\b(([Ss][Ee][Rr])|([Tt][Yy][Rr])|([Tt][Hh][Rr]))-*.{0,1}\\d{1,4}\\b";
	private static PhosphoSite phos = null;
	public static ColumnContents getInstance() {
		if(phos == null)
			phos = new PhosphoSite();
		return phos;
	}
	private PhosphoSite(){
		
	}

	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match,regEx);
	}
}
