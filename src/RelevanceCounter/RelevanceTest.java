package RelevanceCounter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import tablecontents.ColumnContents;
import extract.MasterExtractor;
import extract.analysis.DetermineTable;
import extract.analysis.Extraction;
import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.types.Reaction;
/**
 * Used to run through a list of PMC IDs
 * @author sloates
 *
 */
public class RelevanceTest {

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
	 * 0 is full
	 * 1 is partial
	 * 2 is html
	 * 3 is excel
	 * default is 1
	 * @param args
	 */
	public static void main(String[] args){
		int relevantCount = 0;
		List<Integer> PMCIDs= new ArrayList<Integer>();
		Scanner reader;
		try {
			File ids = new File(args[0]);
			reader = new Scanner(ids);
			while(reader.hasNext()){
				PMCIDs.add(reader.nextInt());	
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int extractType = 1;
		if(args.length ==2){
			try{
			extractType = Integer.parseInt(args[1]);
			}catch(NumberFormatException e){
				
			}
		}
		
		List<TableBuf.Table> tableList = null;
		File tableDir =  new File("tables");
		if(extractType == 0){
			tableDir = new File("files");
		}else{
			tableDir = new File("tables");
		}
		File markedRelevant = new File("MarkedRelevant.txt");
		FileWriter w;
		Extraction extr = new Extraction();
		System.out.println(extractType);
		try {
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
			for (File file : tableDir.listFiles()){
				for(Integer pmc : PMCIDs){
					if(file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString())){
						if(extractType == 0){
							System.err.println(file.getName());
							tableList = MasterExtractor.buildTable(file, pmc.toString());
							for(TableBuf.Table t : tableList){
								extract(t,w,extr);
							}
						}else if (extractType == 2 ){
							if(!file.getName().contains("Supp")){
								TableBuf.Table t  = getTable(file);
								extract(t,w,extr);
							}

						}else if (extractType == 3 ){
							if(file.getName().contains("Supp")){
								TableBuf.Table t  = getTable(file);
								extract(t,w,extr);
							}
						}else{
							TableBuf.Table t  = getTable(file);
							extract(t,w,extr);
						}
					}
				}
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void extract(TableBuf.Table t, FileWriter w, Extraction extr){

		try {
			DetermineTable d = new DetermineTable();
			Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = d.determine(t);
			if(r != null){
				System.out.println(r.getA() + " " + r.getB().keySet());

				w.write(t.getSource().getPmcId());

				System.out.println(t.getSource().getPmcId());
				extr.ExtractInfo(r, t);					
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
