package tablecontents;

import tablecontents.ColumnContents;

public class MethylSite extends Site implements ColumnContents {
	private static MethylSite meth = null;
	public static ColumnContents getInstance() {
		if(meth == null)
			meth =  new MethylSite();
		return meth;
	}
	private MethylSite(){
		regEx = "lys\\d{2,5}|\\bk\\d{2,5}";
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
