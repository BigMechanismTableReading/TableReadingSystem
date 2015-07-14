package columncontents.proteins;

public class English extends Protein{

	
	private English eng = null;
	public English getInstance(){
		if(eng == null)
			eng = new English();
		return eng;
	}
	private English(){
		
	}
	public String groundIdentity(String ungrounded) {
		ungrounded = ungrounded.replaceAll("\\W+"," ");
		if(t.english.containsKey(ungrounded))
			return("Uniprot:" + t.english.get(ungrounded));
		return null;
	}

}
