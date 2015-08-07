package extract.index;

/**
 * Container class for participantInfo
 * @author sloates
 *
 */
public class Participant {
	private String grounded;
	private String ungrounded;
	private String type;
	public Participant(String grounded_name,String ungrounded_name, String entity_type){
		grounded = grounded_name;
		ungrounded = ungrounded_name;
		type = entity_type;
	}
	public String getGrounded() {
		return grounded;
	}
	public String getUngrounded() {
		return ungrounded;
	}

	public String getType() {
		return type;
	}
}
