package columncontents.proteins;

public class SwisProt extends Protein{
	private String regEx = "([A-Z[0-9]]{1,4}_[A-Z]{0,10})";
	
	public String groundIdentity(String ungrounded) {
		ungrounded = ungrounded.toUpperCase();
		if(t.swisprot.containsKey(ungrounded))
			return("Uniprot:" + t.swisprot.get(ungrounded));
		return null;
	}
	public String matchesFormat(String input) {
		return super.matchesFormat(input, regEx);
	}	

}