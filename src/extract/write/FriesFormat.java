package extract.write;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Writes information to new file format
 * @author sloates
 *
 */
public class FriesFormat {

	private String org_name;
	private String pmc_id;
	private String run_id;
	private String frame_id;
	private String table_name;
	private String start;
	private String end;
	private JsonObjectBuilder fries_builder;
	private JsonArrayBuilder frame_builder;
	
	/**
	 * Takes in all the info for a logical naming conventrion that follows the format
	 * <type-prefix>-<pmc>-<table_name>-<org>-<run_id>-<frame_id>
	 * @param org_name
	 * @param pmc_id
	 * @param run_id
	 * @param table_name
	 * @param start
	 * @param end
	 */
	public FriesFormat(String org_name, String pmc_id, String run_id,String table_name,String start,String end){
		this.org_name = org_name;
		this.pmc_id = pmc_id;
		this.run_id = run_id;
		this.table_name = table_name;
		this.start = start;
		this.end = end;
		this.fries_builder = makeFrameCollection();
	}

	private enum JsonType{
		COLLECTION,FRAME,META
	}
	public enum FrameType{
		TABLE_EVENT,ENTITY,PASSAGE,SENTENCES
	}
	private JsonObjectBuilder createMeta(JsonType type){
		JsonObjectBuilder meta = Json.createObjectBuilder();
		meta.add("object_type","meta-info");
		String component = "TRS";//TODO figure out what this means for us specifically
		switch(type){
		case COLLECTION: component = "TRS";
			meta.add("component",component);
			meta.add("organization", this.org_name);
			meta.add("doc_id", this.pmc_id);
			meta.add("processing-start", this.start);
			meta.add("processing-end", this.end);
			break;
		case FRAME:
			meta.add("component", component);
			break;
		default:
			break;

		}
		
		return meta;
	}
	private void addType(JsonType json_type,JsonObjectBuilder object){
		String object_type = "";
		String object_meta = "";
		JsonObjectBuilder meta =  createMeta(json_type);
		if(json_type == JsonType.COLLECTION){
			object_type = "frame-collection";
			frame_builder = Json.createArrayBuilder();

		}else if(json_type == JsonType.FRAME){
			object_type = "frame";
		}

		object.add("object_type", object_type);
		object.add("object_meta", meta);
		
	}
	
	private void make_entity(String mid_id,JsonObjectBuilder object, HashMap<String, String> values) {
		
		//TODO decide if second one is needed for participant A
		object.add("entity_text_b", values.get("entity_text_b"));
		object.add("entity_type_b", values.get("entity_type_b"));	
		object.add("row", values.get("row"));
		JsonArrayBuilder xrefs_array = Json.createArrayBuilder();
		JsonObjectBuilder xrefs = Json.createObjectBuilder();
		//TODO determination of what to add to xrefs, currently just adding the database reference
		xrefs.add("object_type", "db-reference");
		String namespace = "";
		if( values.get("entity_type_b").equals("protein")){
			namespace = "Uniprot";   
		}else{
			namespace = "Chembl";
		}
		xrefs.add("namespace", namespace);
		xrefs.add("id", values.get("identifier_b"));
		xrefs_array.add(xrefs);
		object.add("xrefs",	xrefs_array);
		
		
	}
	private void build_frame(JsonObjectBuilder object, FrameType frame_type, HashMap<String, String> values) {
		String frame_id = "";
		String type = "";
		String mid_id = pmc_id + "-" + org_name;
		if(frame_type == FrameType.ENTITY){
			frame_id = "ment-"+mid_id + "-" + table_name+"-";//TODO add the row
			type = "entitity_mention";
			make_entity(mid_id,object,values);
		}
		object.add("frame-id", frame_id);
		object.add("frame-type", type);
	}
	
	
	private JsonObjectBuilder makeObject(JsonType json_type,FrameType frame_type,HashMap<String,String> values){
		JsonObjectBuilder  object= Json.createObjectBuilder();
		addType(json_type,object);
		if(json_type == JsonType.FRAME){
			//TODO for frames add to the main object
			build_frame(object,frame_type,values);
		}
		return object;
	}
	public JsonObject getJson(){
		if(fries_builder != null && frame_builder != null){
			fries_builder.add("frames", frame_builder);
		}
		return fries_builder.build();
	}
	
	/**
	 * Builds frame from frame_type and values needed for that frame type
	 * @param frame_type
	 * @param values
	 */
	public void makeFrame(FrameType frame_type,HashMap<String,String> values){
		//TODO make a frame and add this to the main frame array need to type the frames
		JsonObjectBuilder frame = makeObject(JsonType.FRAME,frame_type,values);
		frame_builder.add(frame);
	
	}
	/**
	 * Builds the outer framework that contains all the frames collected from a table
	 * @return
	 */
	private JsonObjectBuilder makeFrameCollection(){
		JsonObjectBuilder fries_builder= Json.createObjectBuilder();
		fries_builder = makeObject(JsonType.COLLECTION,null,null);
		return fries_builder;
	}
}
