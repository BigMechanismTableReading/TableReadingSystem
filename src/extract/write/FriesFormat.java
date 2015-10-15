package extract.write;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
		JsonArrayBuilder frames = null;
		if(json_type == JsonType.COLLECTION){
			object_type = "frame-collection";
			frames = Json.createArrayBuilder();

		}else if(json_type == JsonType.FRAME){
			object_type = "frame";
		}

		object.add("object_type", object_type);
		object.add("object_meta", meta);
		if(frames != null){
			object.add("frames", frames);
		}
	}
	private JsonObjectBuilder makeObject(JsonType json_type){
		JsonObjectBuilder  object= Json.createObjectBuilder();
		addType(json_type,object);
		if(json_type == JsonType.FRAME){
			//TODO for frames add to the main object
		}
		return object;
	}

	/**
	 * Builds the outer framework that contains all the frames collected from a table
	 * @return
	 */
	private JsonObjectBuilder makeFrameCollection(){
		JsonObjectBuilder fries_builder= Json.createObjectBuilder();
		makeObject(JsonType.COLLECTION);
		return fries_builder;
	}
}
