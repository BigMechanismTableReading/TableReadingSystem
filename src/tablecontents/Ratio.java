package tablecontents;

import extract.buffer.TableBuf.Column;

public class Ratio extends Fold{
	private String headerRegEx = ".*ratio.*|.*silac.*|.*phospho.{0,2}rate|"
			+ ".*induction.|.*K[OD]/WT.*|mean|\\bvs\\b|/control|\\bover\\b";
	private String cellRegEx = "(\\d{1,2}\\.\\d{1,3}\\s?±\\s?\\d{1,2}\\.\\d{1,3})";
	
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
		return super.match(match, cellRegEx);
	}


	@Override
	public double[] cutoffValues(Column col) {
		//TODO see if a negative check is needed here
		return new double[]{.67,1.5,1};
	}
}
