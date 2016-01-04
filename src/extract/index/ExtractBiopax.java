package extract.index;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class ExtractBiopax {
	String sep = File.separator;
	private HashMap<Participant,Participant> bToA = null;
	private HashMap <Integer,List<Participant>> passageIdB;
	private HashMap<Integer,Participant> passageIdA;
	private HashMap<String,Participant> bToPart;
	
	/**
	 * Takes the fileName and name of the reaction prefix, to search the json for instances of this reaction.
	 * @param fileName
	 * @param entityFile
	 * @param prefix
	 */
	public ExtractBiopax(String fileName,String prefix){
		bToA = new HashMap<Participant,Participant>();
		passageIdB = new HashMap<Integer,List<Participant>>();
		passageIdA = new HashMap<Integer,Participant>();
		bToPart = new HashMap<String,Participant>();
		getBtoA(fileName,prefix);
	}
	
	/**
	 * Creates a participant object
	 * @param ind_participant
	 * @return
	 */
	private Participant getIndParticipant(JSONObject ind_participant){
		String ground_type = (String)ind_participant.get("namespace");
		String ground_id = (String)ind_participant.get("id");
		String entity_type = (String)ind_participant.get("type");
		String text_name = (String)ind_participant.get("text");
		String grounded_entity = ground_type.toUpperCase() + ":" + ground_id.toUpperCase();
		Participant participant = new Participant(grounded_entity,text_name.toUpperCase(),entity_type);
		return participant;//TODO decide on return type
	}
	
	/**
	 * Extracts the info for a participant array
	 * @param participant
	 * @param passage_id
	 * @return
	 */
	private void extractParticipantInfo(JSONArray participant,Integer passage_id){
		for(Object o_ind_participant : participant){
			JSONObject ind_participant = (JSONObject)o_ind_participant;
			Participant p = getIndParticipant(ind_participant);		
			addParticipant(passage_id,p);
		}
	}
	
	/**
	 * Adds a participant to the participant_b list
	 * @param passage_id
	 * @param p
	 */
	private void addParticipant(Integer passage_id, Participant p) {
		if(passageIdB.containsKey(passage_id)){
			passageIdB.get(passage_id).add(p);
		}else{
			LinkedList<Participant> partBs = new LinkedList<Participant>();
			partBs.add(p);
			passageIdB.put(passage_id, partBs);
		}
	}


	/**
	 * Sets this objects participantB to participantA HashMap
	 * @param fileName
	 * @param prefix
	 */
	public void getBtoA(String fileName,String prefix){
		JSONParser parse = new JSONParser();
		try{
			JSONObject idx_cards = (JSONObject)parse.parse(new FileReader("fries" + sep + fileName));
			JSONArray frames = (JSONArray)idx_cards.get("frames");
			for(Object o_idx :frames){
				JSONObject idx = (JSONObject)o_idx;
				Integer passage_id = Integer.parseInt((String) idx.get("passage_id"));
				String reaction_type = (String) idx.get("type");
				Object o_participants = idx.get("participants");
				if(o_participants != null){
					if(reaction_type.contains(prefix.toLowerCase())){
						JSONArray participants = (JSONArray)o_participants;
						extractParticipantInfo(participants,passage_id);
					}
				}
				Object o_controller = idx.get("controller");
				if(o_controller != null){
					JSONObject controller = (JSONObject)o_controller;
					Participant p = getIndParticipant(controller);
					passageIdA.put(passage_id, p);
				}
			}
			getBToA();
		}catch(Exception e){
			System.err.println(e);
		}
	}
	
	/**
	 * Sets the BtoA hashmap
	 */
	private void getBToA(){
		for(Integer passage_id : passageIdA.keySet()){
			if(passageIdB.containsKey(passage_id)){
				for(Participant p : passageIdB.get(passage_id)){
					bToA.put(p, passageIdA.get(passage_id));
					bToPart.put(p.getGrounded(), p);
					bToPart.put(p.getUngrounded(), p);
					bToPart.put(p.getUngrounded().toUpperCase(),p);
				}
			}
		}
	}
	
	/**
	 * Adds a new A or increments the count of one that has been previously found
	 * @param aCount
	 * @param p
	 */
	private void addACount(HashMap<String,Integer> aCount,Participant p){
		Participant a = bToA.get(p);
		if (a != null){
			String ungr = a.getUngrounded();
			if(aCount.containsKey(ungr)){
				Integer temp = aCount.get(ungr);	
				temp++;
				aCount.put(ungr,temp);
			}else{
				aCount.put(ungr, 1);
			}
		}
	}
	/**
	 * Returns the number of times each participant A is mentioned along with phosphorylation
	 * @return
	 */
	public HashMap<String,Integer> getACount(Set<String> participantB){
		HashMap<String,Integer> aCount = new HashMap<String,Integer>();
		for(String b : participantB){
			if(bToPart.containsKey(b)){
				Participant p = bToPart.get(b);
				addACount(aCount,p);
			}else if( bToPart.containsKey(b.toUpperCase())){
				Participant p = bToPart.get(b);
				addACount(aCount,p);
			}
		}
		System.err.println("New Extractor " + aCount);
		return aCount;
	}
}
