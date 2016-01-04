package extract.lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Lookup for terms in the Chebi database.
 * @author sloates
 *
 */
public class ChemicalLookup {
	public HashMap<String, String> chemicals;
	private static ChemicalLookup instance = null;
	
	public static ChemicalLookup getInstance(){
		if(instance == null)
			instance = new ChemicalLookup();
		return instance;
	}
	
	/**
	 * Creates a lookup that contains most entities in the chebi database.
	 */
	private ChemicalLookup(){
		chemicals = new HashMap<String, String>();
		File chemicalList = new File("names.tsv");
		Scanner s;
		try {
			s = new Scanner(chemicalList);
			s.nextLine();
			String curr =  s.nextLine();
			while(s.hasNext()){
				String[]line = curr.split("\t");
				String chem = line[4];
				//TODO possibly filter out by the identifier type
				//if(!line[3].toLowerCase().equals("uniprot")) TODO figure out what to filter out choose from
				//(KEGG COMPOUND,UNI,DRUGBANK,CHEBI,IUPAC,CHEMIDPLUS, PDBeChem)
				chemicals.put(chem.toUpperCase(),"CHEBI:" + line[1]);
				curr = s.nextLine();
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
}
