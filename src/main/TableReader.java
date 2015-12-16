package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import config.Config;
import extract.Extractor;

public class TableReader {
	protected static ArrayList<Integer> pmc_ids = new ArrayList<Integer>();
	private static File log = new File("log.txt"); //default
	public static String home = ".";
	public static String tables = "tables";
	public static String files = "files";
	public static String papers = "papers";

//	public static String getHome(){
	//	return home;
	//}
	
	public static void init(String[] args){
		if (args.length!=1){
			System.err.println("Requires location of config file argument");
		}
		else{
			Config config = new Config();
			//give config file
			try {
				config.setPropValues(args[0]);
				String input_list = config.getInput_List();
				home = config.getHome_dir();
				System.out.println("Home is: " + home);
				tables = home + File.separator + "tables";
				mkdir(tables);
				files = home + File.separator + "files";
				mkdir(files);
				papers = home + File.separator + "papers";
				mkdir(papers);

				//TODO: set log file
				getPMCS(input_list);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}
	
	public static void main(String[] args) {
		init(args);
		Extractor.extractFromList(pmc_ids);

	}
	
	private static void mkdir(String file){
		File newDir = new File(file);
		if (!newDir.exists()){
			newDir.mkdir();
		}
	}
	
	/**writes to logfile**/
	public static void writeToLog(String toWrite){
		try {
			Files.write(log.toPath(), toWrite.getBytes());
		} catch (IOException e) {
			System.err.println("Error writing to log");
		}
		
	}
	
	/**
	 * returns the pmc list given the filename/path input_list
	 * @param input_list
	 */
	private static void getPMCS(String input_list) {
		File input = new File(input_list);
		if (!input.exists()){
			System.err.println(input_list + " does not exist");
		}
		else{
			try {
				try (Stream<String> lines = Files.lines(input.toPath())){
					Iterator<String> it = lines.iterator();
					while (it.hasNext()){
						String curr = it.next();
						if (curr.startsWith("PMC")){
							curr = curr.substring(3);
						}
						if (curr.matches("\\d+")){
							pmc_ids.add(Integer.parseInt(curr));
						}
						else{
							writeToLog("ID error: " + curr);
						}
					}
				}

			} catch (IOException e) {
				System.err.println("Error opening stream with " + input.getAbsolutePath());
			}
			
		}
		
		
	}

}
