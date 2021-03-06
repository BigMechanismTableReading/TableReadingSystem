package tablecontents;

import tableBuilder.TableBuf.Column;

public class FoldChange extends Fold{
	private String headerRegEx = ".*change.*";	
	private static FoldChange cha = null;
	
	public static FoldChange getInstance(){
		if(cha == null)
			cha = new FoldChange();
		return cha;	
	}
	private FoldChange(){
		cutOffs = new double[]{-1.5,1.5,1};
	}
	
	@Override
	public String headerMatch(String match) {
		return super.match(match, headerRegEx);
	}

	@Override
	public String cellMatch(String match) {
		return null;
	}


	@Override
	public double[] cutoffValues(Column col) {
		//TODO see if a negative check is needed here
		return new double[]{-1.5,1.5,1};
	}
}
