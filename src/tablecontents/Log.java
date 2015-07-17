package tablecontents;

import extract.buffer.TableBuf.Column;

/**
 * Subclass of Fold for Log Measurements
 * @author sloates
 *
 */
public class Log extends Fold{

	private String headerRegEx = "\\blog";
	private String cellRegEx = null;
	private double[] cutOffs = new double[]{-.5,.5,0};
	private static Log log = null;
	
	public static Log getInstance(){
		if(log == null)
			log = new Log();
		return log;	
	}
	private Log(){
		
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
		return new double[]{-.5,.5,0};
	}

}
