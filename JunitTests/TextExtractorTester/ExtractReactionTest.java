package TextExtractorTester;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import extract.TextExtractor;
import extract.types.Methylation;
import extract.types.Phosphorylation;
import extract.types.Reaction;

public class ExtractReactionTest {

	private HashSet<Reaction>getStats(String id){
		List<Reaction> react = TextExtractor.getPossibleReactions(id);
		HashSet<Reaction>  reactions = new HashSet<Reaction>();
		Phosphorylation p = (Phosphorylation) Phosphorylation.getInstance();
		Methylation m = (Methylation) Methylation.getInstance();
		for(Reaction r : react ){
			if(r instanceof Phosphorylation)
				reactions.add(p);
			if(r instanceof Methylation)
				reactions.add(m);
		}
		
	
		return reactions;
	}
	@Test
	public void extractPhosphorylation() {
		String pmc = "1459033";
		HashSet<Reaction>  reactions = getStats(pmc);
		assertTrue(reactions.contains(Phosphorylation.getInstance()));
	}
	/**
	 * 
	 */
	@Test
	public void extractMethylation(){
		String pmc = "4023394";
		HashSet<Reaction>  reactions = getStats(pmc);
		System.out.println(reactions);
		assertTrue(reactions.contains(Methylation.getInstance()));
	}

}
