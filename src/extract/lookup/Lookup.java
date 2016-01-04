package extract.lookup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Abstract Class for protein lookups
 * @author sloates
 *
 */
public abstract class Lookup {

	public HashMap<String,String> uniprot = new HashMap<String,String>();
	public HashMap<String,String> swisprot = new HashMap<String,String>();
	public HashMap<String,String> genename = new HashMap<String,String>();
	public HashMap<String, LinkedList<String>> english = new HashMap<String,LinkedList<String>>();
	public HashMap<String, Set<String>> uniToGene = new HashMap<String, Set<String>>();
	
}
