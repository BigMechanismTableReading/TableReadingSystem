package extract.postcompletion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

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
		Gson gson = new GsonBuilder().setPrettyPrinting().create();//
		
		JsonParser jp = new JsonParser();		//
		try {
			FileReader f = new FileReader(fileName);
			JsonObject je = gson.fromJson(f, JsonElement.class).getAsJsonObject();
			//JSONObject json = (JSONObject) parse.parse(new FileReader(fileName));
			JsonObject extr_info = je.getAsJsonObject("extracted_information");
			JsonObject partA = extr_info.getAsJsonObject("participant_a");
			partA.add("entity_text",new JsonPrimitive(partAUntrans));
			partA.add("entity_type",new JsonPrimitive(entity_type));
			partA.add("identifier",new JsonPrimitive(partATrans));
			File newFile = new File(fileName);
        	FileWriter file = new FileWriter(newFile);
        	file.write(gson.toJson(je));
        	file.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main (String[]args){
		ParticipantAEditor aEdit = new ParticipantAEditor();
		aEdit.changeA("test", "Uniprot:test","proteCHemFam","index_cards","2808269","TI");
	}
}
 