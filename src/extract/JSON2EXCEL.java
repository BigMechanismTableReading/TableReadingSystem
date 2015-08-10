package extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
/**
 * @author charnessn
 *
 */
public class JSON2EXCEL {
	private static WritableWorkbook output = null;
	private static JSONParser parser = new JSONParser();
	private static int currRow = 1;
	private static WritableSheet sheet = null;

	
	private static WritableSheet initExcelDoc(String outputFilename){
		
		try {
			output = Workbook.createWorkbook(new File(outputFilename));
			WritableSheet sheet = output.createSheet("results", 0);
			return initSheet(sheet);

		} catch (IOException e) {
			System.err.println("Error writing output. Most likely file is open elsewhere....");
			//e.printStackTrace();
		}
		return null;

		
	}
	
	private static WritableSheet initSheet(WritableSheet sheet){
		String [] columns = {"PMC_ID", "Table", "Row", "Negative information", "Participant A Text", 
				"Type", "Identifier", "In model",
				 "Participant B Text", "Type", "Identifier", "In model", "Site", "Interaction Type", "Modification Type",
				 "confidence_level"
		};
		try {
			for (int i=0; i < columns.length; i++){
				sheet.addCell(new Label(i,0, columns[i]));
			}
			currRow=1;
		} catch (RowsExceededException e) {
			System.err.println("Rows exceeded exception.");
		} catch (WriteException e) {
			System.err.println("Error writing output. Most likely file is open elsewhere....");
		}
		return sheet;
		
		
	}

	
	private static void addToExcel(File jsonFile){
		try {
			BufferedReader r = new BufferedReader(new FileReader(jsonFile));
			JSONObject obj = (JSONObject) parser.parse(r);
			r.close();
			String pmc_id = (String) obj.get("pmc_id");
			Iterator<?> i = ((JSONArray) obj.get("evidence")).iterator();
			while (i.hasNext()){
				Object o = i.next();
				if (o instanceof JSONObject){
					JSONArray evidence = (JSONArray) ((JSONObject) o).get("table_evidence");
					if (evidence!=null){
						//System.out.println(evidence);
						JSONObject tableInfo = (JSONObject) evidence.get(0);
						//GET TABLE ID
						String table = (String) tableInfo.get("table");
						String rowNum = (String) tableInfo.get("row");
						sheet.addCell(new Label(0, currRow, pmc_id));
						sheet.addCell(new Label(1, currRow, table));
						sheet.addCell(new Label(2, currRow, rowNum));
						break;
					}
				}
			}
			//TODO: null checks
			JSONObject extracted_info= (JSONObject) obj.get("extracted_information");
			JSONObject part_a = (JSONObject) extracted_info.get("participant_a");
			//NEGATIVE INFORMATION
			String negative_info = ((String) extracted_info.get("negative_information"));
			if (negative_info!=null){
				sheet.addCell(new Label(3,currRow, negative_info));
			}
			sheet.addCell(new Label(4,currRow, (String) part_a.get("entity_text")));
			sheet.addCell(new Label(5,currRow,(String) part_a.get("entity_type")));
			sheet.addCell(new Label(6,currRow,(String) part_a.get("identifier")));
			sheet.addCell(new Label(7,currRow, (String) part_a.get("in_model")));
			JSONObject part_b = (JSONObject) extracted_info.get("participant_b");
			sheet.addCell(new Label(8,currRow,(String) part_b.get("entity_text")));
			sheet.addCell(new Label(9,currRow,(String) part_b.get("entity_type")));
			sheet.addCell(new Label(10,currRow,(String) part_b.get("identifier")));
			sheet.addCell(new Label(11,currRow,(String) part_b.get("in_model")));
			String site = (String) ((JSONObject) part_b.get("features")).get("site");
			if (site!=null){
				sheet.addCell(new Label(12,currRow,site));
			}
			else{
				System.err.println("null site: " + pmc_id );
				System.err.println("currRow: " + currRow);
			}
			//Interaction type
			String interaction_type = (String) extracted_info.get("interaction_type");
			if (interaction_type!=null){
				sheet.addCell(new Label(13,currRow,interaction_type));
			}
			//Modification type
			if(extracted_info.get("modifications")!=null){
				JSONArray modifications = (JSONArray) extracted_info.get("modifications");
				if (modifications.size() > 0){
					JSONObject mods = (JSONObject) modifications.get(0);
					String modification_type = (String) mods.get("modification_type");
					if (modification_type!=null){
						sheet.addCell(new Label(14,currRow,modification_type));
					}
					
				}
			}
			sheet.addCell(new Label(15,currRow,(String)extracted_info.get("confidence_level")));
			currRow++;
			

		} catch (ParseException e) {
			System.err.println("Error reading json file " + jsonFile.getAbsolutePath());
			e.printStackTrace();
		} catch (RowsExceededException e) {
			System.err.println("Rows Exceeded...Writing new Sheet");
			//output.write();
			//output.close();
			if (output!=null){
				int sheetNum = output.getNumberOfSheets();
				sheet = output.createSheet("results " + sheetNum, sheetNum);
				initSheet(sheet);
			}
			
		} catch (WriteException e) {
			System.err.println("Error writing output. Most likely file is open elsewhere....");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length!=1){
			System.err.println("Input must be a single file or a directory of directories of json files");
		}
		else{
			File input = new File(args[0]);
			if (input.isDirectory()){
				File [] dirs = input.listFiles(new FileFilter(){

					@Override
					public boolean accept(File arg0) {
						return arg0.isDirectory();
					}
					
				});
				System.out.println("Output will be " + input.getAbsolutePath() + File.separator + "json_output.xls");
				sheet = initExcelDoc(input.getAbsolutePath() + File.separator + "json_output.xls");
				for (File dir: dirs){
					File [] files = dir.listFiles(new FileFilter(){

						@Override
						public boolean accept(File arg0) {
							return arg0.getName().endsWith(".json");
						}
						
					});
					for (File f: files){
						addToExcel(f);
					}
				}
				
				
			}
			else if (!input.getName().endsWith(".json")){
				System.err.println("Input must be a json file");
			}
			else{
				System.out.println("Output will be " + input.getParentFile().getAbsolutePath() + File.separator + "json_output.xls");
				sheet = initExcelDoc(input.getParentFile().getAbsolutePath() + File.separator + "json_output.xls");
				addToExcel(input);
			}
			if (output!=null){
				
				try {
					output.write();
					output.close();
				} catch (WriteException | IOException e) {
					System.err.println("Error writing output. Most likely file is open elsewhere....");
				}

			}
		}

	}

}
