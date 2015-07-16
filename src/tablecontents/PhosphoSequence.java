package tablecontents;

public class PhosphoSequence extends Sequence {
	
	private String cellRegEx = ".*["+"HTPVSKFGDWRNMLYAEIQ"+"]{2,}(ph|[#tspy\\*])["+"HTPVSKFGDWRNMLYAEIQ"+"]{2,}.*";
	
	private static PhosphoSequence phosSeq = null;
	public static PhosphoSequence getInstance(){
		if(phosSeq == null)
			phosSeq = new PhosphoSequence();
		return phosSeq;
	}
	private PhosphoSequence(){
		
	}
	
	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match, cellRegEx);
	}
	
}
