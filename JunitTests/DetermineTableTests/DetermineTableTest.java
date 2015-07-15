package DetermineTableTests;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import columncontents.Protein;
import extract.analysis.DetermineTable;
import extract.buffer.TableBuf;
import extract.types.Reaction;

public class DetermineTableTest {
	private TableBuf.Table getTable(String fileName){
		
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
	
	@Test
	public void tableTest() {
		String filename = "ParticipantBTestProtobufs/PMC1459033T1.pb";
		TableBuf.Table table = getTable(filename);
		DetermineTable dt = new DetermineTable();
		Reaction r = dt.determine(table);
		System.out.println(r);
		assertTrue(true);
	}
}
