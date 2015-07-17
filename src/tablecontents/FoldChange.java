package tablecontents;

import extract.buffer.TableBuf.Column;

public class FoldChange extends Fold{
	private String headerRegEx = ".*change.*";
	private String cellRegEx = null;
	
	private static FoldChange cha = null;
	
	public static FoldChange getInstance(){
		if(cha == null)
			cha = new FoldChange();
		return cha;	
	}
	private FoldChange(){
		
	}
	
	@Override
	public String headerMatch(String match) {
		return super.match(match, headerRegEx);
	}

	@Override
	public String cellMatch(String match) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double[] cutoffValues(Column col) {
		//TODO see if a negative check is needed here
		return new double[]{-1.5,1.5,1};
	}
}
