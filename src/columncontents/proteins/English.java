package columncontents.proteins;

public class English extends Protein{

	public String groundIdentity(String ungrounded) {
		ungrounded = ungrounded.replaceAll("\\W+"," ");
		if(t.english.containsKey(ungrounded))
			return("Uniprot:" + t.english.get(ungrounded));
		return null;
	}

}
