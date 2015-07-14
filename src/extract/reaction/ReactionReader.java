package extract.reaction;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import extract.analysis.TableType;
import extract.analysis.TableType.ColumnTypes;

;/**
 * Input a tab seperated text file in which each new line is a new reaction.
 * Each column should be seperated like this
 * ex. {"SITE"::"HeaderRegex"::"CellRegEx"}
 * The first column must match one of the listed column types
 * @author sloates
 *
 */
//TODO Possibly switch the format of the file to json or properties
public class ReactionReader {
	
	static ReactionReader reader = null;
	static String reactionTextFile = "";
	private HashMap<String,HashMap<ColumnTypes,String[]>> react = new HashMap<String,HashMap<ColumnTypes,String[]>>();
	
	private ReactionReader(){
		
	}
	
	private ReactionReader(String filename){
		reactionTextFile = filename;
		readReaction(filename);
		//TODO test this tommorow
	}
	
	public static ReactionReader getInstance(String filename){
		if(reader == null || !reactionTextFile.equals(filename)){
			reader = new ReactionReader(filename);
		}
		return reader;
	}

	/**
	 * reads in the properly formated reaction file
	 * @param txtfile
	 */
	private void readReaction(String txtfile){
		File reactions = new File(txtfile);
		Scanner s;
		try{
			s = new Scanner(reactions);
			while(s.hasNextLine()){
				HashMap<ColumnTypes,String[]> subvals = new HashMap<ColumnTypes,String[]>();
				String wholeLine = s.nextLine();
				String [] tabSeperate = wholeLine.split("\\t");
				String reactType = tabSeperate[0];
				ColumnTypes columnIndicator = ColumnTypes.UNKNOWN;
				String[] regExes = new String[]{"UNKNOWN","UNKNOWN"};
				for(int i = 1; i < tabSeperate.length; i++){
					String[] subSplit = tabSeperate[i].split("::");
					System.out.println(Arrays.toString(subSplit));
					regExes = new String[] {subSplit[1],subSplit[2]};
					for(ColumnTypes e : ColumnTypes.values()){
						if(e.toString().equals(subSplit[0]))
							columnIndicator = e;
					}
					subvals.put(columnIndicator, regExes);
				}				
				System.out.println(reactType);
				react.put(reactType, subvals);
			}
			s.close();
		}catch(FileNotFoundException e){
			System.err.println("Wrong File Format");
		}
	}
	
	
	/**
	 * Parses the reaction array.
	 * @param a
	 */
//	private void parseJsonArray(JSONArray a){
//		for(Object s : a.toArray()){
//			JSONObject inArray = (JSONObject) s;
//			System.out.println(inArray.toJSONString());
//			
//			
//		}
//	}
//	
//	private void readJsons(String fileName){
//		JSONObject jsonObject = null;
//		try {
//		
//			JSONParser jsonPars = new JSONParser();
//			File file = new File(fileName);
//			Object obj = jsonPars.parse(new FileReader(file));
//			jsonObject = (JSONObject)obj;
//			JSONArray a = (JSONArray) jsonObject.get("REACTIONS");
//			if(a != null){
//				parseJsonArray(a);
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	public  HashMap<String,HashMap<ColumnTypes,String[]>> getReact(){
		return react;
	}
}
