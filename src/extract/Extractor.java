package extract;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import com.google.protobuf.InvalidProtocolBufferException;

import relevance.DetermineTable;
//import extract.analysis.Extraction;
import utils.Pair;
import tableBuilder.TableBuf.Column;
import extract.types.PossibleReaction;
import extract.types.Reaction;
import tableBuilder.TableBuf;
import main.TableReader;
import tableBuilder.TableBuf.Table;
import tablecontents.ColumnContents;
import tableBuilder.TableBuilder;
import utils.Utils;


public class Extractor {
	/**
	 * Begins extraction based on pmcids
	 * @param pmc_ids
	 */
	public static void extractFromList(ArrayList<Integer> pmc_ids){
		for (Integer pmc_id: pmc_ids){
			TableReader.writeToLog("Extracting " + pmc_id);
			extractFromID(pmc_id);
		}
		
	}
	
	
	/*private void extractFromTable(Table t, String fileName, boolean simple_reaction){

		DetermineTable dt = new DetermineTable();
		Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = dt.determine(t,simple_reaction);
		if(r != null){
			System.out.println("A: " + r.getA() + " " + r.getB().keySet());
			if (r.getA() != PossibleReaction.getInstance()){
				//extr.ExtractInfo(r, t,simple_reaction);			
			}
		}
		else{
			
		}
	}*/
	
	public void determineRelevance(Table t){
		DetermineTable dt = new DetermineTable();
		Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = dt.determine(t);
		
	}
	
	/*
	private static void extract(TableBuf.Table t, FileWriter w, Extraction extr,String fileName,boolean simple_reaction){

		try {
			DetermineTable d = new DetermineTable();
			Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = d.determine(t,simple_reaction);
			if(r != null){
				System.out.println("A: " + r.getA() + " " + r.getB().keySet());
				w.write(fileName + "\n");
				if (r.getA() != PossibleReaction.getInstance()){
					System.out.println(fileName);
					extr.ExtractInfo(r, t,simple_reaction);			
				}
			}
			else{
				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/



	public static void extractFromID(Integer pmc_id){
		//GOING TO ASSUME THAT ALL OF THE TABLES WERE BUILT IF AT LEAST ONE IS THERE
		File [] tables = Utils.getFiles(new File(TableReader.tables), pmc_id, ".pb");//protobuf files
		if (tables.length==0){
			File [] html = Utils.getFiles(new File(TableReader.papers), pmc_id, ".html");//protobuf files
				//File [] files = getFiles(new File(TableReader.files), pmc_id, new String[] {".html",".xls", ".xlsx"});
				if (html.length > 0){
					for (File file: html){
						List<Table> table_list= TableBuilder.buildTable(file, pmc_id.toString());
						for(Table t : table_list){
							if (t!=null){
							//extract(t,w,extr,fileName,simple_reaction);
							}
						}
					}
				}
				else{
					System.err.println("Error: can't find an html for " + pmc_id);
					TableReader.writeToLog("Error: can't find an html for " + pmc_id);
				}

		}
		else{
			for (File table: tables){
				Table t = Utils.getTable(table);
			}
		}

		
	}



}
