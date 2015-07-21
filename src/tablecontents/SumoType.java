package tablecontents;

public class SumoType extends Amino {

	private String cellRegEx = "Ψ|.K.X.E";
	private static SumoType sumo = null;
	
	public static SumoType getInstance(){
		if(sumo == null)
			sumo = new SumoType();
		return sumo;
	}
	private SumoType(){
		
	}

	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match, cellRegEx);
	}
}
