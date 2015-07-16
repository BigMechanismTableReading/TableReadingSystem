package columncontents;

import columncontents.ColumnContents;

public class MethylSite extends Site implements ColumnContents {
	private String regEx = "lys\\d{2,5}|k\\d{2,5}";
	private static MethylSite meth = null;
	public static ColumnContents getInstance() {
		if(meth == null)
			meth =  new MethylSite();
		return meth;
	}
	private MethylSite(){
		
	}

	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match,regEx);
	}
}
