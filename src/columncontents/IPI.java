package columncontents;

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
		if(ungrounded ==null)
			return null;
		// TODO GROUND IN THE IPI LOOKUP
		return null;
	}
	@Override
	public String cellMatch(String match) {
		//TODO fix when IPI is added in
		//String grounded = groundIdentity(super.matchesFormat(match, regEx));
		return super.matchesFormat(match, regEx);
	}
}
