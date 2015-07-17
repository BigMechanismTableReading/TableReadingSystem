package extract.write;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.types.Phosphorylation;
import extract.types.Reaction;

public class IndexCardWriter {
	//TODO delete the information that is contained here
	private TableBuf.Table getTable(String fileName){
		
		TableBuf.Table table = null;
		try {
			FileInputStream file = new FileInputStream(fileName);
			 table = TableBuf.Table.parseFrom(file);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return table;
	}
	
	private void basicInfo(JsonObjectBuilder idxBuild,TableBuf.Table t, String readingStart, String readingStop){
		idxBuild.add( "pmc_id", t.getSource().getPmcId());
		idxBuild.add("reading_started", readingStart);
		idxBuild.add("reading_complete", readingStop);
		idxBuild.add("submitter", "Leidos");
		idxBuild.add("reader_type", "machine");
	}
	private void addModelRelation(JsonArrayBuilder elements,  JsonObjectBuilder idxBuilder, IndexCard idx){
		if(idx.getData("model_relation")!= null){
			Iterator<String> elementIter = idx.getData("model_elements").iterator();//TODO fix this
			while(elementIter.hasNext()){
				elements.add(elementIter.next());
			}
			idxBuilder.add("model_relation", elements.build());
		}
	}
	private void buildParticipant(JsonObjectBuilder participant, IndexCard idx, String part){
		//TODO
		participant.add("entity_text", idx.getData("entity_text" + "_" + part));
		participant.add("entity_type", idx.getData("entity_type" + "_" + part));
		participant.add("identifier", idx.getData("identifier" + "_" + part));
		participant.add("in_model", idx.getData("in_model" + "_" + part));
	}
	
	private void addFeatures(JsonObjectBuilder features, JsonObjectBuilder participantB,IndexCard idx) {
		String site = idx.getData("site");
		if(site!= null){
				features.add("site", site);
		}
		String amino = idx.getData("base");
		if(amino!=null){
				features.add("base", amino);
		}
		participantB.add("features", features.build());
	}

	private void addParticipants(JsonObjectBuilder participantA,
			JsonObjectBuilder participantB,	JsonObjectBuilder infoBuilder,IndexCard idx) {
		
		infoBuilder.add("participant_a", participantA.build());
		infoBuilder.add("participant_b", participantB.build());
		infoBuilder.add("interaction_type", idx.getData("interaction_type"));
		//Adds modificationType
		JsonArrayBuilder modifications = Json.createArrayBuilder();
		JsonArrayBuilder positions = Json.createArrayBuilder();
		
		for (String i : idx.getData("site").split("^\\d")){
			positions.add(i);
		}
		//Change it from r to the actual name of the reaction
		modifications.add(Json.createObjectBuilder().add("modification_type", 
				idx.getData("modification_type")).add("position", positions.build()).build());
	
		infoBuilder.add("modifications",modifications);
		
	}
	private void tableEvidence(JsonArrayBuilder evidence,IndexCard idx){
		JsonObjectBuilder tableEvidence= Json.createObjectBuilder();
		JsonArrayBuilder tableArray =Json.createArrayBuilder();
		JsonObjectBuilder interiorTableEv = Json.createObjectBuilder();
		JsonObjectBuilder largeTab = Json.createObjectBuilder();
		interiorTableEv.add("table",table);
		interiorTableEv.add("row", row);
		JsonArrayBuilder headers = Json.createArrayBuilder();
		for(String s : headers){
			headers.add(s);
		}
		interiorTableEv.add("Headers", headers);
		JsonObjectBuilder foldHeader = Json.createObjectBuilder();
		foldHeader.add("fold information used", card.getFoldHeader());
		
		JsonArrayBuilder captions = Json.createArrayBuilder();	
		if(captions != null){
			for(String s : captions){
				captions.add(s);
			}
		}
		
		tableArray.add(interiorTable);
		tableArray.add(foldHeader);
		tableEvidence.add("table_evidence", tableArray);
		tableEvidence.add("captions",captions);
		evidence.add(tableEvidence);
	}
	private void textEvidence(JsonArrayBuilder evidence){
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
	private void createEvidence(JsonObjectBuilder idxBuilder){
		JsonArrayBuilder evidence = Json.createArrayBuilder();
		tableEvidence(evidence);
		textEvidence(evidence);
		idxBuilder.add("evidence", evidence);
	}

	public JsonObject writeIndexCard(String readingStart, String readingStop, TableBuf.Table t, IndexCard idx){
		//TODO decide what to send into here, should send it in all at once not seperately,
		//Why not write a card for each partA and do fold, it doesnt need to be seperate at all.
		JsonObjectBuilder idxBuilder = Json.createObjectBuilder();
		basicInfo(idxBuilder,t,readingStart,readingStop);
		JsonArrayBuilder elements = Json.createArrayBuilder();
		List<String> modelElements = null; //TODO model elements add
		addModelRelation(elements,modelElements,idxBuilder,idx);//TODO figure this out
		JsonObjectBuilder infoBuilder = Json.createObjectBuilder();
		infoBuilder.add("negative_information", idx.getData("negative_information"));
		JsonObjectBuilder participantA = Json.createObjectBuilder();
		buildParticipant(participantA,idx);
		JsonObjectBuilder participantB = Json.createObjectBuilder();
		buildParticipant(participantB,idx);
		JsonObjectBuilder featuresB = Json.createObjectBuilder();
		addFeatures(featuresB,participantB,idx);
		String reactionType;
		addParticipants(participantA,participantB,reactionType,infoBuilder);
		idxBuilder.add("extracted_information", infoBuilder.build());
		createEvidence(idxBuilder);
		JsonObject finishedCard = idxBuilder.build();
		return finishedCard;
	}

}
