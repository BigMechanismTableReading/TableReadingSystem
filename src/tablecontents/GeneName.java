package tablecontents;

public class GeneName extends Protein {

	 //TODO update this regEx Look at this
	private static GeneName gene = null;
	public static GeneName getInstance(){
		if(gene == null)
			gene = new GeneName();
		return gene;
	}
	
	private GeneName(){
		regEx = "([A-Z]{1}[0-9[A-Z[a-z]]]{1,7})";
	}
	
	public String groundIdentity(String ungrounded) {
		if(ungrounded != null){
			ungrounded = ungrounded.toUpperCase();
			if(t.genename.containsKey(ungrounded))
				return("Uniprot:" + t.genename.get(ungrounded));
		}
		return null;
	}

	@Override
	public String cellMatch(String match) {
		match = match.replaceAll("-", "");
		String grounded = groundIdentity(super.matchesFormat(match, regEx,true));
		return grounded;
	}
}
