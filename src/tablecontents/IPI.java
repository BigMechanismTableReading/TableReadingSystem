package tablecontents;

import extract.lookup.IPILookup;

public class IPI extends Protein {
	
	private static IPI ipi = null;
	public static IPI getInstance(){
		if(ipi == null)
			ipi = new IPI();
		return ipi;
	}
	private IPI(){
		regEx = "(IPI[0-9]{8})";
	}
	@Override
	public String groundIdentity(String ungrounded) {
		if(ungrounded ==null)
			return null;
		if(IPILookup.getInstance().IPItoUNI.containsKey(ungrounded)){
			return "Uniprot:" + IPILookup.getInstance().IPItoUNI.get(ungrounded);
		} else {
			return null;
		}
	}
	@Override
	public String cellMatch(String match) {
		//TODO fix when IPI is added in
		//String grounded = groundIdentity(super.matchesFormat(match, regEx));
		return groundIdentity(super.matchesFormat(match, regEx,false));
	}
}
