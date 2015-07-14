package columncontents.site;

import columncontents.ColumnContents;

public class PhosphoSite extends Site {
	private String regEx = "[SYT][A-Z[a-z]]*[0-9]{1,4}.*|p?[SYT]\\d{1,4}|(([Ss][Ee][Rr])|([Tt][Yy][Rr])|([Tt][Hh][Rr]))-*\\d{1,4}";
	private static PhosphoSite phos = null;
	@Override
	public static ColumnContents getInstance() {
		if(phos == null)
			phos = new PhosphoSite();
		return null;
	}
	private PhosphoSite(){
		
	}
}
