package extract.lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

public class ChemicalLookup {
	public static HashMap<String, String> chemicals;
	private static ChemicalLookup instance = null;
	
	public static ChemicalLookup getInstance(){
		if(instance == null)
			instance = new ChemicalLookup();
		return instance;
	}
	
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
				chemicals.put(chem.toUpperCase(),"CHEBI:" + line[1]);
				curr = s.nextLine();
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
}
