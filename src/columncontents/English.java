package columncontents;

public class English extends Protein{

	
	private static English eng = null;
	public static English getInstance(){
		if(eng == null)
			eng = new English();
		return eng;
	}
	private English(){
		
	}
	public String groundIdentity(String ungrounded) {
		if(ungrounded != null){
			ungrounded = ungrounded.replaceAll("\\W+"," ");
			if(t.english.containsKey(ungrounded))
				return("Uniprot:" + t.english.get(ungrounded));
		}
		return null;
	}
	@Override
	public String cellMatch(String match) {
		String grounded = groundIdentity(match);
		return grounded;
	}

}
