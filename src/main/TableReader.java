package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import config.Config;

import java.util.regex.Matcher;

import tablecontents.ColumnContents;
import extract.MasterExtractor;
import extract.analysis.DetermineTable;
import extract.analysis.Extraction;
import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.types.PossibleReaction;
import extract.types.Reaction;
/**
 * Used to Extract index cards for a list of PMC IDs
 * @author sloates
 *
 */
public class TableReader {

	private static TableBuf.Table getTable(File fileName){
		TableBuf.Table table = null;
		try {
			FileInputStream file = new FileInputStream(fileName);
			table = TableBuf.Table.parseFrom(file);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return table;
	}

	private static boolean file_is_good(File file, Integer pmc){
		return file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString());
	}


	private static void extractFromList(String file_dir, String table_dir, String paper_dir, String output_file, boolean simple_reaction,
			boolean make_tables,List<Integer> pmc_ids) throws IOException {
		Extraction extr = new Extraction();
		FileWriter w = new FileWriter(new File(output_file));
		List<TableBuf.Table> table_list = new LinkedList<TableBuf.Table>();
		File table_use = null;
		if(make_tables){
			table_use = new File(file_dir);
		}else{
			table_use = new File(table_dir);
		}
		
		for(Integer pmc : pmc_ids){
			for (File file : table_use.listFiles()){
				if(file_is_good(file,pmc)){
					String fileName = file.getName();
					if(make_tables){
						table_list = MasterExtractor.buildTable(file, pmc.toString());
						for(TableBuf.Table t : table_list){
							extract(t,w,extr,fileName,simple_reaction);
						}
					}else{
						TableBuf.Table t  = getTable(file);
						extract(t,w,extr,fileName,simple_reaction);
					}
				}
			}
		}
		
	}
	public static void main(String[]args) throws IOException{
		Config config = new Config();
		config.setPropValues();
		String pmc_filename = config.getPmc_file();
		ArrayList<Integer> pmc_ids = new ArrayList<Integer>();
		if(pmc_filename != null){
			try{
				File pmc_file = new File(pmc_filename);
				Scanner reader = new Scanner(pmc_file);
				while(reader.hasNext()){
					pmc_ids.add(reader.nextInt());
				}
				reader.close();
			}catch(FileNotFoundException e){
				System.err.println ("File " + pmc_filename + "not found");
			}
			if(!pmc_ids.isEmpty()){
				String file_dir = config.getFile_dir();
				String table_dir = config.getTable_dir();
				String output_file = config.getOutput_file();
				String paper_dir = config.getPaper_dir();
				boolean simple_reaction = config.isSimple_reaction();
				boolean make_tables = config.isMake_tables();
				extractFromList(file_dir,table_dir,paper_dir,output_file,simple_reaction,make_tables,pmc_ids);
				
			}else{
				System.err.println("List of PMCIDs was empty or invalid");
			}
			
		}
		
	}
	private static void extract(TableBuf.Table t, FileWriter w, Extraction extr,String fileName,boolean simple_reaction){

		try {
			DetermineTable d = new DetermineTable();
			Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = d.determine(t,simple_reaction);
			if(r != null){
				System.out.println(r.getA() + " " + r.getB().keySet());
				w.write(fileName + "\n");
				if (r.getA() != PossibleReaction.getInstance()){
					System.out.println(fileName);
					extr.ExtractInfo(r, t,simple_reaction);			
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
