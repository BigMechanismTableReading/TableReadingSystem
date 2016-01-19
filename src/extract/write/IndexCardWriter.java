package extract.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import main.TableReader;
import tableBuilder.TableBuf;
import tableBuilder.TableBuf.Table;
/**
 * Writes index cards to proper json format
 * @author sloates
 *
 */
public class IndexCardWriter {

	/**
	 * Writes the basic information 
	 * @param idxBuild
	 * @param t
	 * @param readingStart
	 * @param readingStop
	 */
	private void basicInfo(JsonObjectBuilder idxBuild, Table t, String readingStart, String readingStop){
		idxBuild.add( "pmc_id", t.getSource().getPmcId().replace("PMC", ""));
		idxBuild.add("reading_started", readingStart);
		idxBuild.add("reading_complete", readingStop);
		idxBuild.add("submitter", "Leidos");
		idxBuild.add("reader_type", "machine");
	}

	/**
	 * Adds participant Information
	 * @param participant
	 * @param idx
	 * @param part
	 * @param possibleA 
	 * @param simple_reaction 
	 */
	private void buildParticipant(JsonObjectBuilder participant, IndexCard idx, String part, boolean simple_reaction, HashMap<String, String> possibleA){
		if(!simple_reaction  || part.equals("b")){
			participant.add("entity_text", idx.getData("entity_text" + "_" + part));
			participant.add("entity_type", idx.getData("entity_type" + "_" + part));
			participant.add("identifier", idx.getData("identifier" + "_" + part));
			participant.add("in_model", "false");
		}else{
			JsonArrayBuilder poss_a = Json.createArrayBuilder();
			for(String part_a: possibleA.keySet()){
				String trans_a = possibleA.get(part_a);
				JsonObjectBuilder a_obj= Json.createObjectBuilder();
				a_obj.add("entity_text", part_a);
				if(trans_a.charAt(0) == 'C'){
					a_obj.add("entity_type", "chemical");
				}else{
					a_obj.add("entity_type", "protein");
				}
				a_obj.add("identifier", trans_a);
				poss_a.add(a_obj);
			}
			participant.add("possible_a", poss_a);
		}
	}

	/**
	 * Adds the feature section
	 * @param features
	 * @param participantB
	 * @param idx
	 * @return
	 */
	private boolean addFeatures(JsonObjectBuilder features, JsonObjectBuilder participantB,IndexCard idx) {
		String site = idx.getData("site");
		if(site!= null){
			features.add("site", site);
		}else{
			return false;
		}
		String amino = idx.getData("base");
		if(amino!=null){
			features.add("base", amino);
		}
		participantB.add("features", features.build());
		return true;
	}

	/**
	 * Adds the participant detains and interaction type
	 * @param participantA
	 * @param participantB
	 * @param infoBuilder
	 * @param idx
	 * @return
	 */
	private boolean addParticipants(JsonObjectBuilder participantA,
			JsonObjectBuilder participantB,	JsonObjectBuilder infoBuilder,
			IndexCard idx,boolean simple_reaction) {
		
		infoBuilder.add("participant_a", participantA.build());
		
		infoBuilder.add("participant_b", participantB.build());
		if(!simple_reaction){
			infoBuilder.add("interaction_type", idx.getData("interaction_type"));
		}
		//Adds modificationType
		JsonArrayBuilder modifications = Json.createArrayBuilder();
		JsonArrayBuilder positions = Json.createArrayBuilder();

		String site = idx.getData("site");
		if (site != null){
			for (String i : idx.getData("site").split("^\\d")){
				positions.add(i);
			}
		}else if(!simple_reaction){
			return false;
		}
		//Change it from r to the actual name of the reaction
		if(!simple_reaction){
		modifications.add(Json.createObjectBuilder().add("modification_type", 
				idx.getData("modification_type")).add("position", positions.build()).build());

		infoBuilder.add("modifications",modifications);
		}
		return true;

	}
	/**
	 * Adds table evidence
	 * @param evidence
	 * @param idx
	 * @param t
	 */
	private void tableEvidence(JsonArrayBuilder evidence,IndexCard idx,TableBuf.Table t,boolean simple_reaction){
		JsonObjectBuilder tableEvidence= Json.createObjectBuilder();
		JsonArrayBuilder tableArray =Json.createArrayBuilder();
		JsonObjectBuilder interiorTableEv = Json.createObjectBuilder();
		interiorTableEv.add("table",t.getSource().getSourceFile()+ "sheet" + t.getSource().getSheetNo());
		interiorTableEv.add("row", idx.getData("row"));
		JsonArrayBuilder headers = Json.createArrayBuilder();
		for(TableBuf.Column c : t.getColumnList()){
			headers.add(c.getHeader().getData());
		}
		interiorTableEv.add("Headers", headers);
		JsonObjectBuilder foldHeader = Json.createObjectBuilder();
		if(!simple_reaction){
			foldHeader.add("fold_information_used", idx.getData("fold_information_used"));
		}
		JsonArrayBuilder captions = Json.createArrayBuilder();	
		if(captions != null){
			for(String s : t.getCaptionList()){
				captions.add(s);
			}
		}

		tableArray.add(interiorTableEv);
		tableArray.add(foldHeader);
		tableEvidence.add("table_evidence", tableArray);
		tableEvidence.add("captions",captions);
		evidence.add(tableEvidence);
	}
	/**
	 * Adds text evidence
	 * @param evidence
	 * @param idx
	 */
	private void textEvidence(JsonArrayBuilder evidence,IndexCard idx){
		JsonObjectBuilder textEvidence = Json.createObjectBuilder();
		JsonArrayBuilder textArray = Json.createArrayBuilder();
		JsonObjectBuilder interiorText = Json.createObjectBuilder();
		interiorText.add("section", "");
		interiorText.add("paragraph", "");
		interiorText.add("text", "");
		textArray.add(interiorText);
		textEvidence.add("text_evidence",textArray);
		evidence.add(textEvidence);
	}
	private void createEvidence(JsonObjectBuilder idxBuilder,IndexCard idx,TableBuf.Table t,boolean simple_reaction){
		JsonArrayBuilder evidence = Json.createArrayBuilder();
		tableEvidence(evidence, idx,t, simple_reaction);
		textEvidence(evidence, idx);
		idxBuilder.add("evidence", evidence);
	}

	public JsonObject writeIndexCard(String readingStart, String readingStop, TableBuf.Table t,
			IndexCard idx, HashMap<String, String> possibleA){
		//TODO decide what to send into here, should send it in all at once not seperately,
		//Why not write a card for each partA and do fold, it doesnt need to be seperate at all.
		JsonObjectBuilder idxBuilder = Json.createObjectBuilder();
		basicInfo(idxBuilder,t,readingStart,readingStop);
		JsonObjectBuilder infoBuilder = Json.createObjectBuilder();

		infoBuilder.add("confidence_level", idx.getData("confidence_level"));
		infoBuilder.add("list_position",idx.getData("list_position"));
		boolean simple_reaction = TableReader.simple_reaction;
		if(!simple_reaction){
			infoBuilder.add("negative_information", idx.getData("negative_information"));
		}
		JsonObjectBuilder participantA = Json.createObjectBuilder();
		buildParticipant(participantA,idx, "a",simple_reaction,possibleA);
		JsonObjectBuilder participantB = Json.createObjectBuilder();
		buildParticipant(participantB,idx,"b",simple_reaction,possibleA);
		JsonObjectBuilder featuresB = Json.createObjectBuilder();
		if(!simple_reaction){
			addFeatures(featuresB,participantB,idx);
		}
		addParticipants(participantA,participantB,infoBuilder,idx, simple_reaction);

		idxBuilder.add("extracted_information", infoBuilder.build());
		createEvidence(idxBuilder,idx,t, simple_reaction);
		JsonObject finishedCard = idxBuilder.build();
		String partA = idx.getData("entity_text_a");
		jsonToFile(finishedCard,"index_cards",t,idx.getData("row"), partA);//TODO dont hardcode in index_cards
		return finishedCard;
	}
	/**
	 * Writes the built json info to a file
	 * @param card
	 * @param directory
	 * @param t
	 * @param row
	 * @param partA
	 */
	public void jsonToFile(JsonObject card,String directory,TableBuf.Table t,String row, String partA){
		String fileSubStr = t.getSource().getPmcId()+"/";

		writeToDir("index_cards",fileSubStr,card, row,t, partA);
	}

	/**
	 * Helper method for writing to files
	 * @param directory
	 * @param fileSubStr
	 * @param indexcard
	 * @param row
	 * @param t
	 * @param partA
	 */
	private static void writeToDir(String directory, String fileSubStr,JsonObject indexcard,String row,TableBuf.Table t, String partA){
		try {

			File root = new File(directory + File.separator);
			if (!root.exists()) {
				root.mkdir();	
			}
			File f = new File(directory + File.separator+fileSubStr);
			if (!f.exists()) {
				f.mkdir();
			}
			Map<String, Object> properties = new HashMap<String, Object>(1);
			properties.put(JsonGenerator.PRETTY_PRINTING, true);
			JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
			String tbl = t.getSource().getSourceFile() + "sheet"+ t.getSource().getSheetNo();
			FileOutputStream fis = new FileOutputStream(new File(directory + File.separator + fileSubStr + tbl +"Row"+ row + partA + ".json"));
			JsonWriter writer = writerFactory.createWriter(fis);
			writer.write(indexcard);
			fis.close();
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
