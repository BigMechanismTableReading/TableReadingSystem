package RelevanceCounter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;
import extract.analysis.DetermineTable;
import extract.analysis.Extraction;
import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.types.PossibleReaction;
import extract.types.Reaction;

public class RelevanceOnly {

	HashSet<String> relevant_pmc_ids = new HashSet<String>();
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

	public List<Integer> getIds(String file_name){
		List<Integer> pmc_ids= new ArrayList<Integer>();
		Scanner reader;
		try {
			File ids = new File(file_name);
			reader = new Scanner(ids);
			while(reader.hasNext()){
				pmc_ids.add(reader.nextInt());	
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return pmc_ids;
	}
	public void determineRelevancy(TableBuf.Table t, FileWriter w,String file_name){
		try {
			DetermineTable d = new DetermineTable();
			Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = d.determine(t);
			if(r != null && !relevant_pmc_ids.contains(file_name)){
				w.write(file_name + "\n");
				relevant_pmc_ids.add(file_name);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getRelevancy(String file_name,File table_dir) throws IOException{
		List<Integer> pmc_ids = getIds(file_name);
		FileWriter w = new FileWriter("relevant_pmc_ids.txt");
		for(Integer pmc : pmc_ids){
			for (File file : table_dir.listFiles()){
				if(file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString())){
					TableBuf.Table t  = getTable(file);
					determineRelevancy(t,w,pmc.toString());
				}
			}
		}
		w.close();

	}
	public static void main(String[] args){
		String file_name = args[0];
		RelevanceOnly relevance = new RelevanceOnly();
		File table_dir = new File("tables");
		try {
			relevance.getRelevancy(file_name, table_dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
