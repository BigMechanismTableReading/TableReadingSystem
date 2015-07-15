package columncontents;

import columncontents.ColumnContents;

public class MethylSite extends Site implements ColumnContents {
	private String regEx = "[SYT][A-Z[a-z]]*[0-9]{1,4}.*|p?[SYT]\\d{1,4}|(([Ss][Ee][Rr])|([Tt][Yy][Rr])|([Tt][Hh][Rr]))-*\\d{1,4}";
	private static MethylSite meth = null;
	public static ColumnContents getInstance() {
		if(meth == null)
			meth =  new MethylSite();
		return meth;
	}
	private MethylSite(){
		
	}
	@Override
	public String headerMatch(String match) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match,regEx);
	}
}
