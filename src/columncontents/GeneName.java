package columncontents;

public class GeneName extends Protein {

	public String regEx = "[A-Z[a-z]]{1}[0-9[A-Z[a-z]]]{1,6}";//TODO update this regEx
	private static GeneName gene = null;
	public static GeneName getInstance(){
		if(gene == null)
			gene = new GeneName();
		return gene;
	}
	
	private GeneName(){
		
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
		String grounded = groundIdentity(super.matchesFormat(match, regEx));
		return grounded;
	}
}
