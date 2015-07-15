package columncontents;

import extract.buffer.TableBuf.Column;

/**
 * Subclass of Fold for Log Measurements
 * @author sloates
 *
 */
public class Log extends Fold{

	private String headerRegEx = ".*[^A-Z[a-z]]log.*";
	private String cellRegEx = null;
	
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
		System.out.println(super.match(match, headerRegEx));
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
