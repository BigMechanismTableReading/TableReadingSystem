package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
	public static String index_cards = "index_cards";
	public static boolean simple_reaction = false;
	public static boolean make_tables = false;
//	public static String getHome(){
	//	return home;
	//}
	
	
	public static void init(){
		String [] args = new String[0];
		init(args);
	}
	
	public static void init(String [] args){
		File config = null;
		if (args.length!=1){
			System.err.println("Requires location of config file argument. Trying default.");
			config = new File("table.config");			
		}
		else if (args.length==1){
			config = new File(args[0]);		
		}
		
		if (config!=null && config.exists()){
			System.out.println("table.config at " + config.getAbsolutePath());
			init(config.getAbsolutePath());
		}
		else{
			System.err.println("Requires location of config file argument");
		}
	}
	
	public static void init(String configFile){
		Config config = new Config();
		//give config file
		try {
			config.setPropValues(configFile);
			String input_list = config.getInput_List();
			home = config.getHome_dir();
			System.out.println("Home is: " + home);
			tables = home + File.separator + "tables";
			mkdir(tables);
			files = home + File.separator + "files";
			mkdir(files);
			papers = home + File.separator + "papers";
			mkdir(papers);
			index_cards = home + File.separator + "index_cards";
			mkdir(index_cards);
			simple_reaction = config.isSimple_reaction();
			make_tables = config.isMake_tables();
			String log_file = config.getLog_file();
			if (!log_file.trim().equals("")){
				log = new File(log_file);
			}

			if (log.exists()){
				FileWriter clearFile = new FileWriter(log);
				clearFile.write("");
				clearFile.flush();
				clearFile.close();
			}
			else{
				log.createNewFile();
			}
			if (!config.printOutput()){
				System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("system.output.txt"))));
				System.setErr(new PrintStream(new BufferedOutputStream(new FileOutputStream("system.error.txt"))));
			}
			getPMCS(input_list); //sets the pmc_ids
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		Long startTime = System.currentTimeMillis();
		init(args);
		writeToLog("Start time: " + startTime + "ms");
		writeToLog("pmc ids: " + pmc_ids.size());
		Extractor.extractFromList(pmc_ids);
		writeToLog("Start time: " + startTime + "ms");
		Long endTime = System.currentTimeMillis();
		writeToLog("End time: " + endTime + "ms");
		writeToLog("Difference: " + (endTime - startTime) + "ms");
		writeToLog("pmc ids: " + pmc_ids.size());

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
			toWrite += "\n";
			if (!log.exists()){
				log.createNewFile();
			}
			Files.write(log.toPath(), toWrite.getBytes(), StandardOpenOption.APPEND);
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

	public static void writeToLog(Exception e) {
		StringWriter error = new StringWriter();
		e.printStackTrace(new PrintWriter(error));
		writeToLog(error.toString());
		
		
	}

}
