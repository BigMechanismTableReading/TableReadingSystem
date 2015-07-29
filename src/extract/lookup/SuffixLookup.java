package extract.lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

public class SuffixLookup {
	private static SuffixLookup instance=null;
	public HashMap<Integer,HashSet<String>> suffixList = new HashMap<Integer,HashSet<String>>();
	public static SuffixLookup getInstance(){
	      if(instance == null) {
	          instance = new SuffixLookup();
	       }
	       return instance;
	}
	public SuffixLookup(){
		File proteins = new File("drugsuffixes.txt");
		Scanner s;
		String uni ="";
		try {
			s = new Scanner(proteins);
			s.nextLine();
			while(s.hasNextLine()){
				String line = s.nextLine().toUpperCase();
				if(suffixList.get(line.length()) != null){
					suffixList.get(line.length()).add(line);
				} else {
					HashSet<String> newSet = new HashSet<String>();
					newSet.add(line);
					suffixList.put(line.length(), newSet);
				}
			}
			s.close();
			//System.out.println(uniprot.keySet().toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
