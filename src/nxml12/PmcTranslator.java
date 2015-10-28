package nxml12;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class PmcTranslator {

	
	public HashMap<String,String> pmcToName;
	
	public PmcTranslator(){
		pmcToName = new HashMap<String,String>();
		makeHashMap();
	}
	public void makeHashMap(){
	
		File pmcName = new File("file_list.csv");
		Scanner s;
		try {
			s = new Scanner(pmcName);
			s.nextLine();
			String curr = s.nextLine();
			while(s.hasNext()){
				String[]line = curr.split(",");
				
				String pmc = line[2];
				
				pmcToName.put(pmc,line[0]);
				System.out.println(pmc);
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
	
	public String translate(String pmc){
		return pmcToName.get(pmc);
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
		PmcTranslator translator = new PmcTranslator();
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
		}
	}
}
