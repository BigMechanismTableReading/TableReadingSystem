package nxml12integration;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import extract.analysis.Pair;


/**
 * For integration with Mihai Surdeanu's (Arizona) reading system
 * Reads in their JSON files and stores the contents by participantB
 * @author sloates
 *
 */
public class FriesParser {
	String sep = File.separator;
	private HashMap<String,List<List<String>>>  controlled = null;
	private HashMap<String,String> entities = null;
	private HashSet<String> partBMod;
	private HashMap<String,Integer> aCount;

	private FriesParser(){
		controlled = new HashMap<String,List<List<String>>>();
		entities = new HashMap<String,String>();
		partBMod = new HashSet<String>();
		aCount = new HashMap<String,Integer>();
	}
	public FriesParser(String fileName,String entityFile){
		controlled = new HashMap<String,List<List<String>>>();
		entities = new HashMap<String,String>();
		partBMod = new HashSet<String>();
		aCount = new HashMap<String,Integer>();
		parseFries(fileName,entityFile);
	}

	/**
	 * Parses the fries document, putting the entries into a HashMap.
	 * The keys are participantB, while the list order is
	 * 1)partA 2)textEvidence 3)interactionType
	 * @param fileName
	 */
	public void parseFries(String fileName,String entityFile){
		parseEntities(entityFile);
		JSONParser parse = new JSONParser();
		
		try {
			JSONObject json = (JSONObject) parse.parse(new FileReader("fries" + sep + fileName));
			JSONArray frames = (JSONArray) json.get("frames");
			for(Object f: frames){
				JSONObject ind_frame = (JSONObject)f;
				JSONArray arguments = (JSONArray) ind_frame.get("arguments");
				String partB = null;
				String argType = null;
				List<String> partAEV = null;
				String interaction = null;
				String type = (String) ind_frame.get("type");
				for(Object ar : arguments){
					JSONObject ind_arg = (JSONObject)ar;
					if(ind_arg.get("argument-label").equals("controller")){
						partAEV = new LinkedList<String>();
						String partA = (String) ind_arg.get("text");
						partAEV.add(partA);
						String sentence = (String)ind_frame.get("verbose-text");
						partAEV.add(sentence);
						interaction = (String)ind_frame.get("subtype");
						partAEV.add(interaction);
						addCount(partA);
					}else{
						partB = (String) ind_arg.get("text");
						argType = (String) ind_arg.get("argument-type");
					}
					if(type.equals("protein-modification")){
						partB = (String) ind_arg.get("text");
						argType = (String) ind_arg.get("argument-type");
						partBMod.add(partB.toUpperCase());
						partBMod.add(groundEntry(partB,argType));
					}		
				}
				String partBTrans = groundEntry(partB,argType);
				if(true){//TODO use the interaction to determine whether or not this is useful
					addEntry(partAEV,partB.toUpperCase());
					addEntry(partAEV,partBTrans);
				}
			}
		}catch(Exception e){

		}
	}

	private void addCount(String partA) {
		if(aCount.containsKey(partA)){
			Integer temp = aCount.get(partA);
			temp++;
			aCount.put(partA, temp);
		}else{
			aCount.put(partA, 1);
		}
		
	}
	/**
	 * Parses the entity file into a HashMap<String,String> of ungrounded to grounded entries
	 * @param entityFile
	 * @return
	 */
	private HashMap<String,String> parseEntities(String entityFile){
		JSONParser parse = new JSONParser();
		try {
			JSONObject json = (JSONObject) parse.parse(new FileReader("fries" + sep +entityFile));
			JSONArray frames = (JSONArray) json.get("frames");
			for(Object f : frames){
				JSONObject fr = (JSONObject)f;
				String ungr = (String)fr.get("text");
				JSONArray xrefs = (JSONArray) fr.get("xrefs");
				for(Object x :xrefs){
					JSONObject ref = (JSONObject)x;
					String namespace = (String)ref.get("namespace");
					String id = (String)ref.get("id");
					String combo = "";
					if(namespace.equals("uniprot")){
						combo += "Uniprot:";
					}
					combo+= id;
					entities.put(ungr, combo);
				}
			}
		}catch(Exception e){
		}
		return null;
	}

	/**
	 * Finds the entity within the event.
	 * @param partB
	 * @return
	 */
	private String findEntity(String partB){
		for(String s : partB.split(" ")){
			if(entities.containsKey(s)){
				return s;
			}
		}
		return partB;
	}
	/**
	 * Used to ground the particpantB, to make searching easier in our system
	 * @param argType 
	 * @return
	 */
	private String groundEntry(String partB, String argType){
		if(argType.equals("event")){
			partB = findEntity(partB);
		}
		if(entities.containsKey(partB)){
			return entities.get(partB);
		}
		return null;
	}

	/**
	 * Returns a list of possible participantA for a valid participantB
	 * @param key
	 * @return
	 */
	private HashMap<Pair<String,String>,Integer> possA(String key){
		HashMap<Pair<String,String>,Integer> possA = new HashMap<Pair<String,String>,Integer>();
	
		for(List<String> detList: controlled.get(key)){
			String a = detList.get(0);
			if (a != null){//TODO check interaction
				if(aCount.containsKey(a)){
					Pair<String,String> p = new Pair<String,String>(a,detList.get(1));
					possA.put(p,aCount.get(a));
				}
			}
		}
		return possA;
	}
	/**
	 * Combines a Pair<String,String>, Integer map with a String,Integer Map
	 * Combined into the String,Integer Map
	 * @param pairMap
	 * @param addTo
	 */
	private void combineMaps(HashMap<Pair<String,String>,Integer> pairMap, HashMap<String,Integer> addTo){
		for(Pair<String,String> pair : pairMap.keySet()){
			String a = pair.getA();
			addTo.put(a,aCount.get(a));
		}
	}
	
	/**
	 * Checks the sentences to see if they are a good indication that this participantA is a good canidate
	 * @return
	 */
	private boolean check_sentence(String sentence){
		//TODO implement checks in this method
		return true;
	}
	/**
	 * Returns a list of potential participant As that control anything from a list of participantBs found in the text.
	 * @param participantB
	 * @return
	 */
	public HashMap<String,Integer> getPossA(Set<String> participantB){
		HashMap<String,Integer> numberedA = new HashMap<String,Integer>();
		for(String b : participantB){
			if(controlled.containsKey(b)){
				if(partBMod.contains(b)){
					for(Pair<String,String> pair : possA(b).keySet()){
						String a = pair.getA();
						String sentence = pair.getB();
						if(!participantB.contains(a.toUpperCase())){
							if(check_sentence(sentence)){
								combineMaps(possA(b),numberedA);
							}
						}
					}
				}
			}
		}
		return numberedA;
	}

	/**
	 * Lookup for single particpantB
	 * @param participantB
	 * @return
	 */
	public HashMap<String,Integer> getPartA(String participantB){
		HashMap<String,Integer> base = new HashMap<String,Integer>();
		if(controlled.containsKey(participantB)){
			if(partBMod.contains(participantB)){
				combineMaps(possA(participantB),base);
				return base;
			}
		}
		return null;
	}
	/**
	 * Adds an entry to controlled
	 * @param partAEV
	 * @param partB
	 */
	private void addEntry(List<String> partAEV,String partB){
		if(partAEV != null){
			if(controlled.containsKey(partB)){
				controlled.get(partB).add(partAEV);
			}else{
				List<List<String>> largeList = new LinkedList<List<String>>();
				largeList.add(partAEV);
				controlled.put(partB, largeList);
			}
		}
	}

	/**
	 * For testing
	 * @param args
	 */
	public static void main(String[]args){
		FriesParser testParser = new FriesParser();
		testParser.parseFries("zjw621.uaz.events.json","zjw621.uaz.entities.json");
	}
}
