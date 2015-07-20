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
		regEx = "([OPQ][0-9][A-Z0-9]{3}[0-9])|([A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})";
	}
	
	public String groundIdentity(String ungrounded) {
		if(ungrounded != null){
			if(t.uniprot.containsKey(ungrounded))
				return("Uniprot:" + t.uniprot.get(ungrounded));
		}
		return null;		
	}
	
	@Override
	public String cellMatch(String match) {
		String grounded = groundIdentity(super.matchesFormat(match, regEx));
		return grounded;
	}
	
}
