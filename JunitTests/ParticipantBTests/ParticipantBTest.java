package ParticipantBTests;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Test;

import columncontents.English;
import columncontents.GeneName;
import columncontents.IPI;
import columncontents.Protein;
import columncontents.SwisProt;
import columncontents.Uniprot;
import extract.analysis.ParticipantB;
import extract.analysis.TableType.ColumnTypes;
import extract.buffer.TableBuf;

public class ParticipantBTest {

	/**
	 * Tests for Uniprot ID
	 */
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
		HashSet<Protein> colTypesFound = new HashSet<Protein>();
		int uniprot = 0;
		int gene = 0;
		int swisprot = 0;
		int ipi = 0;
		int english = 0;
		if(table != null){
			for(TableBuf.Column col : table.getColumnList()){
				Protein p = b.hasParticipantB(col);
				if(p instanceof Uniprot){
					uniprot++;
				}
				if(p instanceof GeneName){
					gene++;
				}
				if(p instanceof SwisProt){
					swisprot++;
				}
				if(p instanceof IPI){
					ipi++;
				}
				if(p instanceof English){
					english++;
				}
			}
		}
		assertTrue(uniprot > 0);
		assertTrue(ipi == 0 && swisprot == 0);
			
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
		int protein = 0;
		if(table != null){
			for(TableBuf.Column col : table.getColumnList()){
				Protein p = b.hasParticipantB(col);
				if(p instanceof Protein){
					System.out.println(p);
					protein++;
				}
			}
		}
		assertFalse(protein > 0);
	}
	
	/**
	 * Tests a table that doesnt have the uniprot ID
	 */
	@Test
	public void noUniTest(){
		
	}

}
