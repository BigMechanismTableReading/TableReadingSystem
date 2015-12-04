package nxml12;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class PmcTranslator {

	
	public HashMap<String,String> pmcToName;
	
	public PmcTranslator(String resolve_file){
		pmcToName = new HashMap<String,String>();
		makeHashMap(resolve_file);
	}
	/**Hashmap is PMCID, tar filename**/
	
	public void makeHashMap(String resolve_file){
		System.out.println("Creating map for pmc id resolution");
		File pmcName = new File(resolve_file);
		try {
			BufferedReader r = new BufferedReader(new FileReader(pmcName));
			String line = r.readLine();
			int count=0;
			while (line!=null){
				count++;
				String [] ids = line.split("\t");
				if (ids.length >=2){
					
					pmcToName.put(ids[1], ids[0]); //pmcid to filename
					if (count%10000==0){
						System.out.println(".." + count + "..");
					}
				}
				line = r.readLine();
			}
			System.out.println("Done.");
			r.close();
		} catch (FileNotFoundException e) {
			System.err.println("Cant find resolve file: " + resolve_file + ". Make sure you have.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public HashMap<String,String> getHashMap(){	
		return pmcToName;
	}
	
	public String translate(String pmcID){
		System.out.println("getting: " + pmcID);
		String result = pmcToName.get(pmcID);
		if (result==null){
			//if it doesnt start with PMC try that
			if (!pmcID.startsWith("PMC")){
				pmcID = "PMC" + pmcID;
				result = pmcToName.get(pmcID);
			}
			else if (pmcID.startsWith("PMC")){
				pmcID = pmcID.replace("PMC", "");
				result = pmcToName.get(pmcID);
			}
		}
		return result;
	}
	
	public String translate_file(String file_name){
		Scanner r;
		File file = new File(file_name);
		try {
			r = new Scanner(file);
			PrintWriter file_writer = new PrintWriter("temporary_paper_names.txt","UTF-8");
			while(r.hasNextLine()){
				String pmc = r.nextLine().trim();
				String translated =  translate("PMC" +pmc);
				file_writer.println(translated);
			}
			file_writer.close();
			r.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "temporary_paper_names.txt";
	}
	public static void main(String[]args){
		/*PmcTranslator translator = new PmcTranslator();
		File all_pmc = new File("All.txt");
		
		Scanner r;
		try {
			r = new Scanner(all_pmc);
			PrintWriter file_writer = new PrintWriter("translated_corpus.txt","UTF-8");
			while(r.hasNextLine()){
				String pmc = r.nextLine().trim();
				String translated =  translator.translate("PMC" +pmc);
				file_writer.println(translated);
			}
			file_writer.close();
			r.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
