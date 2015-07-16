package tablecontents;

public class SwisProt extends Protein{
	private static SwisProt swis = null;
	public static SwisProt getInstance(){
		if(swis == null)
			swis = new SwisProt();
		return swis;
	}
	private SwisProt(){
		 regEx = "([A-Z[0-9]]{1,4}_[A-Z]{0,10})";
	}
	public String groundIdentity(String ungrounded) {
		if(ungrounded != null){
			ungrounded = ungrounded.toUpperCase();
			if(t.swisprot.containsKey(ungrounded))
				return("Uniprot:" + t.swisprot.get(ungrounded));
		}
		return null;
	}
	@Override
	public String cellMatch(String match) {
		String grounded = groundIdentity(super.matchesFormat(match, regEx));
		return grounded;
	}
	

}
