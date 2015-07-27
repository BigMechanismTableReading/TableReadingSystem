package tablecontents;

public class Uniprot extends Protein{

	private static Uniprot uni = null;
	
	public static Uniprot getInstance(){
		if(uni == null){
			uni = new Uniprot();
		}
		return uni;
	}
	private Uniprot(){
		//TODO word breaks
		regEx = "\\b([OPQ][0-9][A-Z0-9]{3}[0-9])\\b|\\b([A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})\\b";
	}
	
	public String groundIdentity(String ungrounded) {
		if(ungrounded != null){
			if(getT().uniprot.containsKey(ungrounded))
				return("Uniprot:" + getT().uniprot.get(ungrounded));
		}
		return null;		
	}
	
	@Override
	public String cellMatch(String match) {
		String grounded = groundIdentity(super.matchesFormat(match, regEx,false));
		return grounded;
	}
	
}
