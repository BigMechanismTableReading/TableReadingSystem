package extract.write;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.types.Phosphorylation;
import extract.types.Reaction;

public class IndexCard {
	//TODO delete the information that is contained here
	private static TableBuf.Table getTable(String fileName){
		
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
	
	private static void basicInfo(JsonObjectBuilder idxBuild,TableBuf.Table t, String readingStart, String readingStop){
		idxBuild.add( "pmc_id", t.getSource().getPmcId());
		idxBuild.add("reading_started", readingStart);
		idxBuild.add("reading_complete", readingStop);
		idxBuild.add("submitter", "Leidos");
		idxBuild.add("reader_type", "machine");
	}
	private static void addModelRelation(JsonArrayBuilder elements, List<String> modelElements, JsonObjectBuilder idxBuilder){
		if(modelElements!= null){
			Iterator<String> elementIter = modelElements.iterator();
			while(elementIter.hasNext()){
				elements.add(elementIter.next());
			}
			idxBuilder.add("model_relation", elements.build());
		}
	}
	private static void buildParticipantA(JsonObjectBuilder participant){
		//TODO
		participant.add("entity_text", name);
		participant.add("entity_type", type);
		participant.add("identifier", ground);
		participant.add("in_model", inModel);
	}
	private static void addFeatures(JsonObjectBuilder features, JsonObjectBuilder participantB) {
		
		if(site.length >0){
			if(site[0] > = 0)
				features.add("site", Arrays.toString(site))
		}
		if(amino.length>=1){
			if (amino[0] != "")
				features.add("base", Arrays.toString(amino));
		}
		participantB.add("features", features.build());
	}

	private static void addParticipants(JsonObjectBuilder participantA,
			JsonObjectBuilder participantB, String reactionType,
			JsonObjectBuilder infoBuilder,Reaction r) {
		
		infoBuilder.add("participant_a", participantA.build());
		infoBuilder.add("participant_b", participantB.build());
		infoBuilder.add("interaction_type", reactionType);
		//Adds modificationType
		JsonArrayBuilder modifications = Json.createArrayBuilder();
		JsonArrayBuilder positions = Json.createArrayBuilder();
		for (int i : site){
			positions.add(i);
		}
		//Change it from r to the actual name of the reaction
		modifications.add(Json.createObjectBuilder().add("modification_type", r)
				.add("position", positions.build()).build());
		
		infoBuilder.add("modifications",modifications);
		
	}
	private static void tableEvidence(JsonArrayBuilder evidence){
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
	private static void textEvidence(JsonArrayBuilder evidence){
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
	private static void createEvidence(JsonObjectBuilder idxBuilder){
		JsonArrayBuilder evidence = Json.createArrayBuilder();
		tableEvidence(evidence);
		textEvidence(evidence);
		idxBuilder.add("evidence", evidence);
	}

	public static void writeIndexCard(String readingStart, String readingStop, TableBuf.Table t, Pair<Reaction,?> info){
		//TODO decide what to send into here, should send it in all at once not seperately,
		//Why not write a card for each partA and do fold, it doesnt need to be seperate at all.
		JsonObjectBuilder idxBuilder = Json.createObjectBuilder();
		basicInfo(idxBuilder,t,readingStart,readingStop);
		JsonArrayBuilder elements = Json.createArrayBuilder();
		List<String> modelElements = null; //TODO model elements add
		addModelRelation(elements,modelElements,idxBuilder);//TODO figure this out
		JsonObjectBuilder infoBuilder = Json.createObjectBuilder();
		infoBuilder.add("negative_information", negativeInfo);
		JsonObjectBuilder participantA = Json.createObjectBuilder();
		buildParticipant(participantA);
		JsonObjectBuilder participantB = Json.createObjectBuilder();
		buildParticipant(participantB);
		JsonObjectBuilder featuresB = Json.createObjectBuilder();
		addFeatures(featuresB,participantB);
		String reactionType;
		addParticipants(participantA,participantB,reactionType,infoBuilder);
		idxBuilder.add("extracted_information", infoBuilder.build());
		createEvidence(idxBuilder);
		
	
	}

	//TODO test this then delete the test later
	public static void main(String[]args){
		String filename = "ParticipantBTestProtobufs/PMC1459033T1.pb";
		TableBuf.Table table = getTable(filename);
		writeIndexCard("1","2",table,new Pair<Reaction,List>(Phosphorylation.getInstance(),null));
	}
}
