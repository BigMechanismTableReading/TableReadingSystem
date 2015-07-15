package RelevanceCounter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import extract.analysis.DetermineTable;
import extract.buffer.TableBuf;
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
		File tableDir = new File("tables");
		for (File file : tableDir.listFiles()){
			for(Integer pmc : PMCIDs){
				if(file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString())){
						TableBuf.Table t = getTable(file);
						if(t!=null){
							DetermineTable d = new DetermineTable();
							Reaction r  = d.determine(t).getA();
							if(r != null){
								System.out.println(file.getName());
								relevantCount++;							
							}
						}
				}
			}
		}
		System.out.println(relevantCount);
	}
}
