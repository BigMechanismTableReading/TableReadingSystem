package extract.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import columncontents.ColumnContents;
import columncontents.Protein;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.lookup.SpeciesChecker;
import extract.types.Reaction;

/**
 * Determines whether or not a table is relevant
 * @author sloates
 *
 */
public class DetermineTable {
	
	/**
	 * Checks the captions for any obvious signs of a wrong species
	 * @param captionList
	 * @return
	 */
	private boolean invalidSpecies(List<String> captionList){
		SpeciesChecker spec = new SpeciesChecker();
		HashSet<String> specSet = spec.getWrongSpecies();
		for(String sentence : captionList){
			for(String word : sentence.split("\\W")){
				if(specSet.contains(word.toUpperCase()))
					return true;
			}
	
		}
		
		return false;
	}
	
	/**
	 * Identifies any columns that have potential participantB
	 * @param table
	 * @param partB
	 * @param labels
	 * @return
	 */
	private boolean assignB(TableBuf.Table table, ParticipantB partB, HashMap<ColumnContents, Column> labels){
		boolean hasProt = false;
		for(TableBuf.Column col : table.getColumnList()){
			Protein p = partB.hasParticipantB(col);
			if(p instanceof Protein){
				labels.put(p, col);
				hasProt = true;
			}
		}
		return hasProt;
	}
	
	/**
	 * Pipeline that determines whether a table is relevant and what the table indicates
	 * @param table
	 */
	public Reaction determine(TableBuf.Table table){
		//First Checks whether the species is invalid 
		if(invalidSpecies(table.getCaptionList()))
			return null;
		//Checks to make sure that participantB is in the table, setting columnLabels as it iterates through
		ParticipantB  partB = new ParticipantB();
		HashMap<ColumnContents,TableBuf.Column> labels = new HashMap<ColumnContents,TableBuf.Column>();
		if(assignB(table,partB,labels)){
			//TODO Determine the reaction type and relevance
		}
		
		return null;
	}
}
