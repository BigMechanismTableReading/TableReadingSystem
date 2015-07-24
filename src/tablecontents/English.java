package tablecontents;

/**
 * English participantB description class
 * @author sloates
 *
 */
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
			String[] forms = ungrounded.split("(\\(.*\\))");
			ungrounded = ungrounded.replaceAll("\\W+"," ").toUpperCase();
			for (String form : forms){
				form = form.replaceAll("\\W+"," ").toUpperCase().trim();
				if(t.english.containsKey(form)){
					return("Uniprot:" + t.english.get(form).get(0));
				}
			}
			if(t.english.containsKey(ungrounded))
				return("Uniprot:" + t.english.get(ungrounded).get(0));
		}
		return null;
	}
	@Override
	public String cellMatch(String match) {
		String grounded = groundIdentity(match);
		return grounded;
	}

}
