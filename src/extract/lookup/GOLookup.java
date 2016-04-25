package extract.lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class GOLookup {

	private static GOLookup instance=null;
	HashMap<String,String> go_ids;
	HashMap<String,String> processes;
	public static GOLookup getInstance(){
		if(instance == null) {
			instance = new GOLookup();
		}
		return instance;
	}

	private GOLookup(){
		go_ids = new HashMap<String,String>();
		processes = new HashMap<String,String>();
		File go_file = new File("");
		Scanner s;
		String go;
		String english;
		try {
			s = new Scanner(go_file);
			String curr = s.nextLine();
			while(s.hasNextLine()){
				String[]line = curr.split(",");
				go = line[0];
				english = line[1];
				processes.put(english, go);
				go_ids.put(go, english);
				curr = s.nextLine();
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String lookup_process(String s){
		if(processes.containsKey(s.toUpperCase())){
			return processes.get(s.toUpperCase());
		}
		return null;
	}
	public String lookup_go(String s){
		if(go_ids.containsKey(s.toUpperCase())){
			return go_ids.get(s.toUpperCase());
		}
		return null;
	}
	
}
