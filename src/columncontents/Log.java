package columncontents;

import extract.buffer.TableBuf.Column;

/**
 * Subclass of Fold for Log Measurements
 * @author sloates
 *
 */
public class Log extends Fold{

	private String headerRegEx = null;//TODO These RegExp
	private String cellRegEx = null;
	
	private Log log = null;
	
	public Log getInstance(){
		if(log == null)
			log = new Log();
		return log;	
	}
	private Log(){
		
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
		return new double[]{-.5,.5,0};
	}

}
