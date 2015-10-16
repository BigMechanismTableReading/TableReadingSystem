package nxml12;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class PmcTranslator {

	
	public HashMap<String,String> pmcToName;
	
	public PmcTranslator(){
		pmcToName = new HashMap<String,String>();
	}
	public void makeHashMap(Set<String> pmcIds){
	
		File pmcName = new File("file_list.csv");
		Scanner s;
		try {
			s = new Scanner(pmcName);
			s.nextLine();
			String curr = s.nextLine();
			while(s.hasNext()){
				String[]line = curr.split(",");
				String pmc = line[2].substring(2, line[2].length());
				if(pmcIds.contains(pmc)){
					pmcToName.put(pmc.substring(2, pmc.length()),line[0]);
				}
				curr = s.nextLine();
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public HashMap<String,String> getHashMap(){
		
		return pmcToName;
	}
}
