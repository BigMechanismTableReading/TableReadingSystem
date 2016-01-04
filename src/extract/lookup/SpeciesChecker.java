package extract.lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This class is not currently used, but it can create a lookup for species deemed invalid.
 * @author sloates
 *
 */
public class SpeciesChecker {
	private static HashSet<String> wrongSpecies = null;
	
	
	private HashSet<String> makeWrongSpecies(){
		HashSet<String> wrong = new HashSet<String>();
		File species = new File("invalidSpecies.txt");
		Scanner s;
		try {
			s = new Scanner(species);
			while(s.hasNextLine()){
				wrong.add(s.nextLine().toUpperCase());
			}
			s.close();
		}catch(FileNotFoundException e){
			
		}
		return wrong;
		
	}
	
	public SpeciesChecker(){
		setWrongSpecies(makeWrongSpecies());
	}

	public HashSet<String> getWrongSpecies() {
		return wrongSpecies;
	}

	private void setWrongSpecies(HashSet<String> wrong) {
		wrongSpecies = wrong;
	}
}
