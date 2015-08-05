package nxml12integration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
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
	//Stored by ParticipantB 
	private HashMap<String,List<List<String>>>  controlled = new HashMap<String,List<List<String>>>();
	private HashMap<String,String> entities = new HashMap<String,String>();
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
				String partA = null;
				String partB = null;
				String textEvidence = null;
				String subtype = null;
				List<String> partAEV = null;
				for(Object ar : arguments){
					JSONObject ind_arg = (JSONObject)ar;
					if(ind_arg.get("argument-label").equals("controller")){
						partAEV = new LinkedList<String>();
						partAEV.add((String) ind_arg.get("text"));
						partAEV.add((String) ind_frame.get("verbose-text"));
						partAEV.add( (String)ind_frame.get("subtype"));
					}else{
						partB = (String) ind_arg.get("text");
					}
				}
				String partBTrans = groundEntry(partB);//TODO ground the entry (Uniprot only seems easiest??
				addEntry(partAEV,partBTrans);
			}
		}catch(Exception e){
			
		}
		System.out.println(controlled);
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
	 * Used to ground the particpantB, to make searching easier in our system
	 * @return
	 */
	private String groundEntry(String partB){
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
	private List<String> possA(String key){
		List<String> possA = new LinkedList<String>();
		for(List<String> detList: controlled.get(key)){
			String a = detList.get(0);
			if (a != null){//TODO check interaction
				possA.add(a);
			}
		}
		return possA;
	}
	/**
	 * Returns a list of potential participant As that control anything from a list of participantBs found in the text.
	 * @param participantB
	 * @return
	 */
	public List<String> getPossA(Set<String> participantB){
		List<String> possibleA = new LinkedList<String>();
		for(String b : participantB){
			if(controlled.containsKey(b)){
				possibleA.addAll(possA(b));
			}
		}
		if(possibleA.size() > 0)
			return possibleA;
		
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
