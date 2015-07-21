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
	
	public static void main(String[] args){
		int relevantCount = 0;
		List<Integer> PMCIDs= new ArrayList<Integer>();
		Scanner reader;
		System.out.println(args[0]);
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
		File tableDir = new File("files");
		File markedRelevant = new File("MarkedRelevant.txt");
		FileWriter w;
		Extraction extr = new Extraction();
		try {
			w = new FileWriter(markedRelevant);
			for (File file : tableDir.listFiles()){
				for(Integer pmc : PMCIDs){
					if(file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString())){
						List<TableBuf.Table> tableList = MasterExtractor.buildTable(file, pmc.toString());
						for (TableBuf.Table t : tableList){
							//TableBuf.Table t = getTable(file);
							DetermineTable d = new DetermineTable();
							Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = d.determine(t);
							if(r != null){
								System.out.println(r.getA() + " " + r.getB().keySet());
								w.write(t.getSource().getPmcId());
								System.out.println(t.getSource().getPmcId());
								extr.ExtractInfo(r, t);					
							}
						}
						
					}
				}
			}
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
