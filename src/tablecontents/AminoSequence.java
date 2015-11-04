package tablecontents;

public class AminoSequence extends Sequence {
	private String cellRegEx = ".*["+"HTPVSKFGDWRNMLYAEIQ"+"]{5,}.*";
	
	private static AminoSequence aSeq = null;
	public static AminoSequence getInstance(){
		if(aSeq == null)
			aSeq = new AminoSequence();
		return aSeq;
	}
	private AminoSequence(){
		
	}
	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match, cellRegEx);
	}
}
