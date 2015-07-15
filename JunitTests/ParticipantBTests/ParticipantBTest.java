package ParticipantBTests;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import columncontents.English;
import columncontents.GeneName;
import columncontents.IPI;
import columncontents.Protein;
import columncontents.SwisProt;
import columncontents.Uniprot;
import extract.analysis.ParticipantB;
import extract.buffer.TableBuf;

public class ParticipantBTest {

	Uniprot u = Uniprot.getInstance();
	SwisProt s = SwisProt.getInstance();
	IPI i = IPI.getInstance();
	GeneName g = GeneName.getInstance();
	English e = English.getInstance();
	
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
	private HashMap<Protein, Integer> getStats(TableBuf.Table table){
		HashMap<Protein, Integer> prots = new HashMap<Protein,Integer>();
		ParticipantB b = new ParticipantB();
		int uniprot = 0;
		int gene = 0;
		int swisprot = 0;
		int ipi = 0;
		int english = 0;
		int protein = 0;
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
				if(p instanceof GeneName){
					protein++;
				}
			}
		}
		prots.put(u,uniprot);
		prots.put(g,gene);
		prots.put(s, swisprot);
		prots.put(i,ipi);
		prots.put(e,english);
		return prots;
	}
	/**
	 * Tests for Uniprot ID
	 */
	@Test
	public void uniprotTest() {
		String filename = "ParticipantBTestProtobufs/PMC1459033T1.pb";
		TableBuf.Table table = getTable(filename);
		HashMap<Protein, Integer> prot = getStats(table);
		assertTrue(prot.get(u) > 0);
		assertTrue(prot.get(i)== 0 && prot.get(s) == 0);
			
	}
	
	/**
	 * Tests a table that has no participantB
	 */
	@Test
	public void irrelevantTableTest(){
		String filename = "ParticipantBTestProtobufs/PMC1794346T1.pb";
		TableBuf.Table table = getTable(filename);
		HashMap<Protein, Integer> prot = getStats(table);
		assertTrue(prot.get(u) == 0 &&prot.get(e) == 0 && prot.get(s) == 0 &&prot.get(g) == 0 );
	}
	
	/**
	 * Tests a table with IPI and possibly gene as well
	 */
	@Test
	public void IPITest(){
		String filename = "ParticipantBTestProtobufs/PMC4146493SuppNIHMS615059-supplement-ST4Sheet1.pb";
		TableBuf.Table table = getTable(filename);
		HashMap<Protein, Integer> prot = getStats(table);
		assertFalse(prot.get(i) == 0);
		assertFalse(prot.get(u) > 0);
		assertFalse(prot.get(s)>0);
		System.out.println(prot.get(g) + " "  + prot.get(e));
		
	}
	
	/**
	 * Tests a table with IPI and possibly gene as well
	 */
	@Test
	public void EnglishTest(){
		/*String filename = "ParticipantBTestProtobufs/";
		TableBuf.Table table = getTable(filename);
		HashMap<Protein, Integer> prot = getStats(table);
		assertTrue(prot.get(e) > 0);*/
		
	}

}
