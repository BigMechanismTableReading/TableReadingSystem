package extract.lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Dictionary {
	public HashSet<String> words;
	private static Dictionary instance = null;
	
	public static Dictionary getInstance(){
		if(instance == null)
			instance = new Dictionary();
		return instance;
	}
	
	private Dictionary(){
		words = new HashSet<String>();
		File chemicalList = new File("dictionary.txt");
		Scanner s;
		try {
			s = new Scanner(chemicalList);
			String curr =  s.nextLine();
			while(s.hasNext()){
				words.add(curr.trim());
				curr = s.nextLine();
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
