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
		regEx = "lys\\d{2,5}|k\\d{2,5}";
	}

	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match,regEx);
	}
}
