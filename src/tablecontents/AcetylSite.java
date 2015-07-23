package tablecontents;

/**
 * Acetylation Site
 * @author sloates
 *
 */
public class AcetylSite extends Site{
	private static AcetylSite acetyl = null;
	public static ColumnContents getInstance() {
		if(acetyl == null)
			acetyl = new AcetylSite();
		return acetyl;
	}
	private AcetylSite(){
		//TODO how to match something like this
		headerRegEx = "acetyl|acetyl\\s*site|acetyl\\s*residue|acetyl\\s*location|acetyl\\s*position";//TODO look at this
		regEx = "\\b((k|lys)\\d{2,4})\\b";
		confidenceNeeded = 7;
		
	}
	@Override
	public boolean needsBoth(){
		return true;
	}
	@Override
	public String headerMatch(String match) {
		return super.headerMatch(match);
	}
	
	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match,regEx);
	}
}
