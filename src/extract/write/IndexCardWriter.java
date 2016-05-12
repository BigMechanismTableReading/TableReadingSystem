package extract.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import tableBuilder.TableBuf.Column;
import tableBuilder.TableBuf.Table;
import tablecontents.ColumnContents;
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
		interiorTableEv.add("row", idx.partBData.get("row"));
		JsonArrayBuilder headers = Json.createArrayBuilder();
		for(TableBuf.Column c : t.getColumnList()){
			headers.add(c.getHeader().getData());
		}
		interiorTableEv.add("Headers", headers);
		JsonObjectBuilder foldHeader = Json.createObjectBuilder();
		if(idx.getData("fold_information_used") != null){
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

	
	private JsonObjectBuilder writeData(HashMap<String,String> data){
		JsonObjectBuilder output_data = Json.createObjectBuilder();
		for(String s : data.keySet()){
			String val = data.get(s);
			if(val != null)
				output_data.add(s, val);
		}
		return output_data;
	}
	
	private JsonArrayBuilder writeML(HashMap<ColumnContents, List<Column>> contents,String row){
		int cell = -1;
		try{
			cell = Integer.parseInt(row);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		JsonArrayBuilder ml_data = Json.createArrayBuilder();
		for(ColumnContents c : contents.keySet()){
			for(TableBuf.Column col : contents.get(c)){
				JsonObjectBuilder ml_temp = Json.createObjectBuilder();
				ml_temp.add("Purpose",c.toString().split("\\.")[1].split("@")[0]);
				ml_temp.add("ColumnName",col.getHeader().getData());
				if(cell >= 0 && col.getData(cell) != null && col.getData(cell).getData()!= null){
					ml_temp.add("Cells", col.getData(cell).getData());
				}
				ml_data.add(ml_temp);
			}
		}
		return ml_data;
	}
	
	
	public JsonObject newWriteIndexCard(String readingStart, String readingStop, TableBuf.Table t,
			IndexCard idx, HashMap<String, String> possibleA, HashMap<ColumnContents, List<Column>> contents){
		JsonObjectBuilder idxBuilder = Json.createObjectBuilder();
		basicInfo(idxBuilder,t,readingStart,readingStop);
		JsonObjectBuilder infoBuilder = Json.createObjectBuilder();
		infoBuilder.add("confidence_level", idx.partAData.get("confidence_level"));
		infoBuilder.add("list_position",idx.partAData.get("list_position"));
		
		//Add Participant Info
		JsonObjectBuilder extracted_info = writeData(idx.extractedInfoData);
		extracted_info.add("participant_a", writeData(idx.partAData).build());
		extracted_info.add("participant_b", writeData(idx.partBData).build());
		infoBuilder.add("extracted_information", extracted_info.build());
		
		//Add evidence info
		JsonArrayBuilder evidence = Json.createArrayBuilder();
		tableEvidence(evidence, idx,t, TableReader.simple_reaction);
		textEvidence(evidence, idx);
		idxBuilder.add("evidence", evidence);
		idxBuilder.add("info", infoBuilder.build());
		
		idxBuilder.add("ML", writeML(contents,idx.partBData.get("row")).build());
		//Output card
		JsonObject finishedCard = idxBuilder.build();
		String partA = idx.getData("entity_text_a");
		jsonToFile(finishedCard,"index_cards",t,idx.partBData.get("row"), partA);//TODO dont hardcode in index_cards
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

		writeToDir(TableReader.index_cards,fileSubStr,card, row,t, partA);
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
