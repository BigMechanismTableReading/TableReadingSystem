package ReactionReaderTest;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import extract.analysis.TableType.ColumnTypes;
import extract.reaction.ReactionFileCreator;
import extract.reaction.ReactionReader;

public class ReactionWriterTest {

	/**
	 * Tests the reaction writer class (that is used to add new reactions)
	 * By making a new file and testing the Reaction Reader
	 */
	@Test
	public void test() {
		
		try {
			System.setIn(new BufferedInputStream(new FileInputStream(new File("ReactionFileCreatorTest.txt"))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		ReactionFileCreator.main(new String[]{});
		ReactionReader r = ReactionReader.getInstance("ReactionFileTest.txt");
		assertTrue(r.getReact().containsKey("PHOSPHORYLATION"));
		assertTrue(r.getReact().get("PHOSPHORYLATION").containsKey(ColumnTypes.PHOSPHOSITE));
	}

}
