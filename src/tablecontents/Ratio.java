package tablecontents;

import extract.buffer.TableBuf.Column;

public class Ratio extends Fold{
	private String headerRegEx = ".*ratio.*|.*silac.*|.*phospho.{0,2}rate|"
			+ ".*induction.|.*mean.*|.*K[OD]/WT.*";
	private String cellRegEx = null;
	
	private static Ratio rat = null;
	
	public static Ratio getInstance(){
		if(rat == null)
			rat = new Ratio();
		return rat;	
	}
	private Ratio(){
		cutOffs = new double[]{.67,1.5,1};
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
		return new double[]{.67,1.5,1};
	}
}
