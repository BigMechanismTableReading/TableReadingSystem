package ParticipantBTests;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Test;

import extract.analysis.ParticipantB;
import extract.analysis.TableType.ColumnTypes;
import extract.buffer.TableBuf;

public class ParticipantBTest {

	@Test
	public void uniprotTest() {
		String filename = "ParticipantBTestProtobufs/PMC1459033T1.pb";
		TableBuf.Table table = null;
		try {
			FileInputStream file = new FileInputStream(filename);
			 table = TableBuf.Table.parseFrom(file);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		ParticipantB b = new ParticipantB();
		HashSet<ColumnTypes> colTypesFound = new HashSet<ColumnTypes>();
		if(table != null){
			for(TableBuf.Column col : table.getColumnList()){
				colTypesFound.add(b.hasParticipantB(col));
			}
		}
		assertTrue(colTypesFound.contains(ColumnTypes.UNIPROT));
		assertFalse(colTypesFound.contains(ColumnTypes.SWISPROT));
		assertTrue(colTypesFound.contains(ColumnTypes.GENE));		
	}
	
	/**
	 * Tests a table that has no participantB
	 */
	@Test
	public void irrelevantTableTest(){
		String filename = "ParticipantBTestProtobufs/PMC1794346T1.pb";
		TableBuf.Table table = null;
		try {
			FileInputStream file = new FileInputStream(filename);
			 table = TableBuf.Table.parseFrom(file);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		ParticipantB b = new ParticipantB();
		HashSet<ColumnTypes> colTypesFound = new HashSet<ColumnTypes>();
		if(table != null){
			for(TableBuf.Column col : table.getColumnList()){
				colTypesFound.add(b.hasParticipantB(col));
			}
		}
		
		assertFalse(colTypesFound.contains(ColumnTypes.UNIPROT));
		assertFalse(colTypesFound.contains(ColumnTypes.SWISPROT));
		assertFalse(colTypesFound.contains(ColumnTypes.GENE));	
		assertFalse(colTypesFound.contains(ColumnTypes.ENGLISH));
	}
	
	/**
	 * Tests a table that doesnt have the uniprot ID
	 */
	@Test
	public void noUniTest(){
		
	}

}
