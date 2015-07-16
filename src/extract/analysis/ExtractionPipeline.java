package extract.analysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import columncontents.ColumnContents;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.types.Reaction;

public class ExtractionPipeline {
	private static TableBuf.Table getTable(String fileName){
		TableBuf.Table table = null;
		try {
			FileInputStream file = new FileInputStream(fileName);
			 table = TableBuf.Table.parseFrom(file);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return table;
	}
	public static void main(String[]args){
		String filename = null;
		TableBuf.Table t = getTable(filename);
		DetermineTable d = new DetermineTable();
		Pair<Reaction, HashMap<ColumnContents, List<Column>>> p = d.determine(t);
		//TODO determine all participantBs
	}
}
