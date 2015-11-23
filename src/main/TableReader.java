package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;

import config.Config;

import java.util.regex.Matcher;

import tablecontents.ColumnContents;
import extract.MasterExtractor;
import extract.TextExtractor;
import extract.analysis.DetermineTable;
import extract.analysis.Extraction;
import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.types.PossibleReaction;
import extract.types.Reaction;
import nxml12.ExtractFiles;
import nxml12.ExtractionPipeline;
import nxml12.NxmlTranslator;
import nxml12.PmcTranslator;
/**
 * Used to Extract index cards for a list of PMC IDs
 * @author sloates
 *
 */
public class TableReader {

	private static TableBuf.Table getTable(File fileName){
		TableBuf.Table table = null;
		try {
			FileInputStream file = new FileInputStream(fileName);
			table = TableBuf.Table.parseFrom(file);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return table;
	}

	private static boolean file_is_good(File file, Integer pmc){
		return file.isFile() && !file.getName().toLowerCase().contains("resource") && file.getName().startsWith("PMC"+pmc.toString());
	}


	private static void extractFromList(String file_dir, String table_dir, String paper_dir, String output_file, boolean simple_reaction,
			boolean make_tables,List<Integer> pmc_ids, boolean nxml, String pmc_filename, String nxml_dir) throws IOException {
		Extraction extr = new Extraction();
		FileWriter w = new FileWriter(new File(output_file));
		List<TableBuf.Table> table_list = new LinkedList<TableBuf.Table>();
		File table_use = null;
		if(make_tables){
			table_use = new File(file_dir);
		}else{
			table_use = new File(table_dir);
		}
		if(nxml){
			convertNxml(file_dir,paper_dir,pmc_ids, pmc_filename,nxml_dir);
		}
		
		for(Integer pmc : pmc_ids){
			for (File file : table_use.listFiles()){
				if(file_is_good(file,pmc)){
					String fileName = file.getName();
					if(make_tables){
						table_list = MasterExtractor.buildTable(file, pmc.toString());
						for(TableBuf.Table t : table_list){
							extract(t,w,extr,fileName,simple_reaction);
						}
					}else{
						TableBuf.Table t  = getTable(file);
						extract(t,w,extr,fileName,simple_reaction);
					}
				}
			}
		}
		
	}
	/**
	 * Takes a list of pmc_ids, gets the nxml files and converts them to html files.
	 * @param file_dir
	 * @param paper_dir
	 * @param pmc_ids
	 * @param pmc_filename
	 * @param nxml_dir 
	 * @throws IOException 
	 */
	private static void convertNxml(String file_dir, String paper_dir, List<Integer> pmc_ids,String pmc_filename, String nxml_dir) throws IOException {
		ExtractionPipeline ep= new ExtractionPipeline();
		final TarGZipUnArchiver arc = new TarGZipUnArchiver();
		LinkedList<String> tar_files = ep.get_nxml(pmc_filename, nxml_dir);
		File nxml_d = new File(nxml_dir);
		NxmlTranslator nxm_trans = new NxmlTranslator();
		for(File f : nxml_d.listFiles()){
			for(Integer pmc : pmc_ids){
				if(f.getName().contains(pmc+"")){
					arc.setSourceFile(f);
					File temp_dir = new File("temporary_nxml");
					arc.setDestDirectory(temp_dir);
					arc.extract();
					for(File nxf: temp_dir.listFiles()){
						String ext = FilenameUtils.getExtension(nxf.getName());
						if(ext.equals("nxml")){
							nxm_trans.translateTables(nxf.getName(), file_dir, paper_dir,pmc);
							//TODO test functionality and finish the pipeline
							//All this needs to do is to make sure all the files are in the file_dir and paper_dir, which should happen now
						}
					}
				}
			}
		}
		
	}

	public static void main(String[]args) throws IOException{
		Config config = new Config();
		config.setPropValues();
		String pmc_filename = config.getPmc_file();
		ArrayList<Integer> pmc_ids = new ArrayList<Integer>();
		if(pmc_filename != null){
			try{
				File pmc_file = new File(pmc_filename);
				Scanner reader = new Scanner(pmc_file);
				while(reader.hasNext()){
					pmc_ids.add(reader.nextInt());
				}
				reader.close();
			}catch(FileNotFoundException e){
				System.err.println ("File " + pmc_filename + "not found");
			}
			if(!pmc_ids.isEmpty()){
				String file_dir = config.getFile_dir();
				String table_dir = config.getTable_dir();
				String output_file = config.getOutput_file();
				String paper_dir = config.getPaper_dir();
				boolean simple_reaction = config.isSimple_reaction();
				boolean make_tables = config.isMake_tables();
				boolean nxml = config.isNxml();
				String nxml_dir = config.getNxml_dir();
				TextExtractor.setPaper_dir(paper_dir);
				MasterExtractor.setFile_dir(file_dir);
				MasterExtractor.setTable_dir(table_dir);
				extractFromList(file_dir,table_dir,paper_dir,output_file,simple_reaction,make_tables,pmc_ids,nxml,pmc_filename,nxml_dir);
				
			}else{
				System.err.println("List of PMCIDs was empty or invalid");
			}
			
		}
		
	}
	private static void extract(TableBuf.Table t, FileWriter w, Extraction extr,String fileName,boolean simple_reaction){

		try {
			DetermineTable d = new DetermineTable();
			Pair<Reaction, HashMap<ColumnContents, List<Column>>> r  = d.determine(t,simple_reaction);
			if(r != null){
				System.out.println(r.getA() + " " + r.getB().keySet());
				w.write(fileName + "\n");
				if (r.getA() != PossibleReaction.getInstance()){
					System.out.println(fileName);
					extr.ExtractInfo(r, t,simple_reaction);			
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
