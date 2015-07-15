package columncontents;

import columncontents.ColumnContents;

public class PhosphoSite extends Site implements ColumnContents {
	//TODO fix this up
	private String regEx = "[SYT][A-Z[a-z]]*.{0,1}[0-9]{1,4}.*|p?[SYT].{0,1}\\d{1,4}|(([Ss][Ee][Rr])|([Tt][Yy][Rr])|([Tt][Hh][Rr]))-*.{0,1}\\d{1,4}";
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
