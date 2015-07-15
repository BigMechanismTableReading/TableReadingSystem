package columncontents;

public class PhosphoSequence extends Sequence {
	
	private String cellRegEx = ".*["+"HTPVSKFGDWRNMLYAEIQ"+"]{2,}(ph|[#tspy\\*])["+"HTPVSKFGDWRNMLYAEIQ"+"]{2,}.*";

	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match, cellRegEx);
	}
	
}
