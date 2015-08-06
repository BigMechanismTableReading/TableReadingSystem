package extract.postcompletion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class to allow quick correction of the participant A field.
 * Needs a directory name and PMCID
 * @author sloates
 *
 */
public class ParticipantAEditor {
	
	/**
	 * Corrects the participantA on all JSON files in this directory
	 * @param partAUntrans
	 * @param partATrans
	 * @param directory
	 * @param PMCID
	 */
	public void changeA(String partAUntrans, String partATrans,String entity_type, String directory,String PMCID,String tableName){
		String sep = File.separator;
		File direct = null;
		try{
			direct = new File(directory + sep + "PMC" + PMCID);
			if (direct.isDirectory()){
				for(File f : direct.listFiles()){
					if(f.getName().contains(tableName)){
					
						changeJson(f.getPath(), partAUntrans, partATrans, entity_type);
					}
				}
			}			
		}catch(Exception e){
			
		}
	}
	/**
	 * Creates the new JSON file
	 * @param f
	 */
	@SuppressWarnings("unchecked")
	private void changeJson(String fileName,String partAUntrans, String partATrans, String entity_type) {
		JSONParser parse = new JSONParser();
		try {
			JSONObject json = (JSONObject) parse.parse(new FileReader(fileName));
			JSONObject extr_info = (JSONObject)json.get("extracted_information");
			JSONObject partA = (JSONObject) extr_info.get("participant_a");
			partA.put("entity_text",partAUntrans);
			partA.put("entity_type",entity_type);
			partA.put("identifier",partATrans);
			//System.out.println( json);
			System.out.println(fileName);
        	File newFile = new File(fileName);
        
        	FileWriter file = new FileWriter(newFile);
        	
        	file.write(json.toJSONString());
        	file.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main (String[]args){
		ParticipantAEditor aEdit = new ParticipantAEditor();
		aEdit.changeA("test", "Uniprot:test","proteCHemFam","index_cards","2735263","T1");
	}
}
