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
	
	/**
	 * Extract from a File
	 * @param ids
	 * @param extractType 
	 */
	public static void extractFromFile(File ids, int extractType){
		ArrayList<Integer> PMCIDs = new ArrayList<Integer>();
		try {
			Scanner reader = new Scanner(ids);
			while(reader.hasNext()){
				PMCIDs.add(reader.nextInt());	
			}
			reader.close();
			extractFromList(PMCIDs, extractType);
		}
		catch (FileNotFoundException e){
			
		}
		
		
	}
	
	/**
	 * Where extraction begins
	 * @param PMCIDs -an ArrayList of PMCID's
	 * @param extractType -0, 1, 2, or 3 @see documentation
	 * 
	 * 0 is full
	 * 1 is partial
	 * 2 is html
	 * 3 is excel
	 * default is 1
	 *
	 * @throws IOException
	 */
	
	public static void extractFromList (ArrayList<Integer> PMCIDs, int extractType){
		 try{
		List<TableBuf.Table> tableList = new LinkedList<TableBuf.Table>();
		File tableDir =  new File("tables");
		if(extractType == 0){
			tableDir = new File("files");
		}else{
			tableDir = new File("tables");
		}
		
		HashSet<String> has_table = new HashSet<String>();
		
		File table = new File("tables");
		Pattern p = Pattern.compile("PMC([0-9]{6,7})");
		for(File f : table.listFiles()){
			String name = f.getName();
			Matcher m = p.matcher(name);
			if(m.find())
				has_table.add(m.group(1));
		}
		System.out.println(has_table);
		
		File markedRelevant = new File("MarkedRelevant.txt");
		FileWriter w;
		Extraction extr = new Extraction();
		System.out.println(extractType);
			w = new FileWriter(markedRelevant);
			/*for(Integer pmc : PMCIDs){
			 * Set<String> allPartB = new Set<String>();
			 * for (File file : tableDir.listFiles()){
			 * 		if(file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString())){
			 * 			send in allPartB for each thing to use so it doesnt get a participant from a different table
			 * 			allPartB.addAll(have the extraction return a list of partBs so they can be kept)
			 * 		}
			 * }
			 * }
			 * 
			 */
			for(Integer pmc : PMCIDs){
				for (File file : tableDir.listFiles()){
					if(file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString())){
						String fileName = file.getName();
						if(extractType == 0){
							System.err.println(file.getName());
							tableList = MasterExtractor.buildTable(file, pmc.toString());
							for(TableBuf.Table t : tableList){
								extract(t,w,extr,fileName);
							}
						}else if (extractType == 2 ){
							if(!file.getName().contains("Supp")){
								TableBuf.Table t  = getTable(file);
								extract(t,w,extr,fileName);
							}

						}else if (extractType == 3 ){
							if(file.getName().contains("Supp")){
								TableBuf.Table t  = getTable(file);
								extract(t,w,extr,fileName);
							}
						}else{
							TableBuf.Table t  = getTable(file);
							extract(t,w,extr,fileName);
						}
					}
				}
			}
			w.close();
		 }
		 catch (IOException e) {
			System.err.println("Error writing to MarkedRelevant.txt file");
			e.printStackTrace();
		}
		
	}

	/**
	 * 0 is full
	 * 1 is partial
	 * 2 is html
	 * 3 is excel
	 * default is 1
	 * @param args
	 */
	public static void main(String[] args){
		ArrayList<Integer> PMCIDs= new ArrayList<Integer>();
		if (args.length>0){
			try {
				File ids = new File(args[0]);
				Scanner reader = new Scanner(ids);
				while(reader.hasNext()){
					PMCIDs.add(reader.nextInt());	
				}
				reader.close();
			
			
			int extractType = 1; //default
			if (args.length>1){
				Integer.parseInt(args[1]);
			}
			if (extractType < 0 || extractType > 3){
				throw new NumberFormatException();
			}
			if (!PMCIDs.isEmpty()){
				extractFromList(PMCIDs, extractType);
			}
			else{
				System.err.println("List of PMCIDs was empty or invalid");
			}
			
			
			} catch (NumberFormatException e){
				System.err.println("Extract type number invalid");
			} catch (FileNotFoundException e) {
				System.err.println ("File " + args[0] + "not found");
			
			}
		}
		else{
			System.err.println("Please provide a text file containing a list of PMCID's and a number corresponding to usage "
					+ "\n 0: full Creates the tables from the original files, determines \n relevance then extracts information and writes to index cards"
					+ "\n 1: partial Uses already made protobufs and determines relevance \n then extracts information and writes to index cards"
					+ "\n 2: HTML partial For HTML Tables only Uses already made protobufs and determines \n relevance then extracts information \n and writes to index cards"
					+ "\n 3: Excel partial For Excel Tables only Uses already made protobufs and \n determines relevance then extracts information and writes to index cards");
			
		}
		
		
	}
	private static void extract(TableBuf.Table t, FileWriter w, Extraction extr,String fileName){

		try {
			DetermineTable d = new DetermineTable();
			Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = d.determine(t);
			if(r != null){
				System.out.println(r.getA() + " " + r.getB().keySet());
				w.write(fileName + "\n");
				if (r.getA() != PossibleReaction.getInstance()){
					System.out.println(t.getSource().getPmcId());
					extr.ExtractInfo(r, t);			
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
