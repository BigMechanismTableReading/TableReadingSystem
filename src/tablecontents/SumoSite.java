package tablecontents;

public class SumoSite extends Site{
	//TODO fix this up
	private static SumoSite phos = null;
	public static ColumnContents getInstance() {
		if(phos == null)
			phos = new SumoSite();
		return phos;
	}
	private SumoSite(){
		//TODO fix the regEx
		regEx = "\\b((KX|E.D)\\d{1,4})\\b";
	}

	@Override
	public String headerMatch(String match) {
		return null;
	}
	
	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match,regEx);
	}
}
