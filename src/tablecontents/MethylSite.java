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
		regEx = "\\blys\\d{2,5}\\b|\\bk\\W{0,1}\\d{2,5}\\b";
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
