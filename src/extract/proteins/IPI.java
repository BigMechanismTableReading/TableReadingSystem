package extract.proteins;

public class IPI extends Protein {
	private String regEx = "IPI[0-9]{8}";
	@Override
	public String groundIdentity(String ungrounded) {
		// TODO Auto-generated method stub
		return null;
	}
	public String matchesFormat(String input) {
		return super.matchesFormat(input, regEx);
	}	

}
