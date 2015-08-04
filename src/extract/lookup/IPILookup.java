package extract.lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Used to lookup proteins that are given in the old IPI format
 * @author sloates
 *
 */
public class IPILookup {
	private static IPILookup instance=null;
	public HashMap<String,String> IPItoUNI = new HashMap<String,String>();
	public static IPILookup getInstance(){
	      if(instance == null) {
	          instance = new IPILookup();
	       }
	       return instance;
	}
	
	/**
	 * Creates the lookup that contains some IPIs to uniprot IDs
	 */
	private IPILookup(){
		File proteins = new File("ipi.HUMAN.xrefs");
		Scanner s;
		String uni ="";
		try {
			s = new Scanner(proteins);
			s.nextLine();
			while(s.hasNextLine()){
				String line = s.nextLine();
				String [] ls = line.split("\\s");
				IPItoUNI.put(ls[2],ls[1].replaceAll("\\-\\d",""));
			}
			s.close();
			//System.out.println(uniprot.keySet().toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
