package ReactionReaderTest;

import static org.junit.Assert.*;

import java.util.HashMap;

import extract.analysis.TableType.ColumnTypes;
import extract.analysis.*;

import org.junit.Test;

import extract.reaction.ReactionReader;

/**
 * Class for testing the Reaction Reader class
 * @author sloates
 *
 */
public class ReactionReaderTest {

	/**
	 * Tests for a single line
	 */
	@Test
	public void SingleLinetest() {
		ReactionReader react = ReactionReader.getInstance("TestReact.txt");
		HashMap<String,HashMap<ColumnTypes,String[]>> reactions= react.getReact();
		//System.out.println(reactions.keySet());
		assertTrue(reactions.containsKey("PHOSPHORYLATION"));
		//System.out.println(reactions.get("PHOSPHORYLATION"));
		assertTrue(reactions.get("PHOSPHORYLATION").containsKey(ColumnTypes.PHOSPHOSITE));
	}
	
	/**
	 * Tests for multiple possible lines
	 */
	@Test
	public void MultipleLinetest(){
		ReactionReader react = ReactionReader.getInstance("TestReact2.txt");
		HashMap<String,HashMap<ColumnTypes,String[]>> reactions= react.getReact();
		//System.out.println(reactions.keySet());
		assertTrue(reactions.containsKey("SUMOYLATION"));
		assertTrue(reactions.containsKey("PHOSPHORYLATION"));
		assertTrue(reactions.get("SUMOYLATION").containsKey(ColumnTypes.SUMOSITE));
		assertTrue(reactions.get("ACETYLATION").containsKey(ColumnTypes.ACETYLSITE));
	}

}
