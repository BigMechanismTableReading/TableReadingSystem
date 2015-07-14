package columncontents.proteins;

public class IPI extends Protein {
	private String regEx = "IPI[0-9]{8}";
	private static IPI ipi = null;
	public static IPI getInstance(){
		if(ipi == null)
			ipi = new IPI();
		return ipi;
	}
	private IPI(){
		
	}
	@Override
	public String groundIdentity(String ungrounded) {
		// TODO GROUND IN THE IPI LOOKUP
		return null;
	}
	public String matchesFormat(String input) {
		return super.matchesFormat(input, regEx);
	}	

}
