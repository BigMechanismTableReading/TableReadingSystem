package columncontents;

import extract.buffer.TableBuf.Column;

public class Ratio extends Fold{
	private String headerRegEx = null;//TODO These RegExp
	private String cellRegEx = null;
	
	private Ratio rat = null;
	
	public Ratio getInstance(){
		if(rat == null)
			rat = new Ratio();
		return rat;	
	}
	private Ratio(){
		
	}
	
	@Override
	public String headerMatch(String match) {
		// TODO Auto-generated method stub
		return null;
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
