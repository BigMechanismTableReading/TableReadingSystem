package columncontents;

import columncontents.ColumnContents;

public class PhosphoSite extends Site implements ColumnContents {
	private String regEx = "[SYT][A-Z[a-z]]*[0-9]{1,4}.*|p?[SYT]\\d{1,4}|(([Ss][Ee][Rr])|([Tt][Yy][Rr])|([Tt][Hh][Rr]))-*\\d{1,4}";
	private static PhosphoSite phos = null;
	public static ColumnContents getInstance() {
		if(phos == null)
			phos = new PhosphoSite();
		return null;
	}
	private PhosphoSite(){
		
	}
}
