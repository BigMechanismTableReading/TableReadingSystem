package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
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
			boolean make_tables,List<Integer> pmc_ids, boolean nxml, String pmc_filename, String nxml_dir, String resolve_file) throws IOException {
		Extraction extr = new Extraction();
		FileWriter w = new FileWriter(new File(output_file));
		List<TableBuf.Table> table_list = new LinkedList<TableBuf.Table>();
		File table_use = null;
		if(make_tables){
			table_use = new File(file_dir);
		}else{
			table_use = new File(table_dir);
		}
		//if nxml convert nxml to html
		if(nxml){
			convertNxml(file_dir,paper_dir,pmc_ids, pmc_filename,nxml_dir, resolve_file);
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
	public static void convertNxml(String file_dir, String paper_dir, List<Integer> pmc_ids,String pmc_filename, String nxml_dir, String resolve_file) throws IOException {
		ExtractionPipeline ep= new ExtractionPipeline();
		//TODO: targzip archiver and pmc translator should be in extraction pipeline
		TarGZipUnArchiver arc = new TarGZipUnArchiver();
		PmcTranslator pmc = new PmcTranslator(resolve_file);
		File nxml_d = new File(nxml_dir);

		for (Integer pmc_id: pmc_ids){
			String filename = pmc.translate(pmc_id.toString());
			if (filename!=null){
				boolean found = false;
				for (File tar: nxml_d.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File arg0, String name) {
						return name.toLowerCase().endsWith(".tar.gz");}
					})
					){
					

					
					if (tar.getName().contains(filename) || filename.contains(tar.getName())){
						System.out.println("Extracting " + tar.getAbsolutePath() + " ...");
						found = true;					

						BufferedInputStream fin = new BufferedInputStream(new FileInputStream(tar));
						GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fin);
						TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);
						TarArchiveEntry entry = tarIn.getNextTarEntry();
						while (entry!=null){
							String file_in_tar = entry.getName().toLowerCase();
							//System.out.println(file_in_tar);
							//FileUtils.copyFileToDirectory(new File("TEST"), outputDir);
							/*if (entry.getFile() == null){
								System.err.println("NULL FILE: " + entry.getName());
							}*/
							if (file_in_tar.endsWith("nxml")){
								File outputFile = new File("temp_nxml");
								outputFile.mkdir();
								File f = new File(outputFile,  "PMC" + pmc_id + ".nxml");
								f.createNewFile();
					            byte [] btoRead = new byte[1024];
					            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(f));
					            int len = 0;
					            while((len = tarIn.read(btoRead)) != -1){
							                bout.write(btoRead,0,len);
							     }
					            bout.close();
								
								
								NxmlTranslator.translateTables(f, file_dir, paper_dir,pmc_id);

								
							}
							else if (file_in_tar.contains(".xls")){
								File outputFile = new File(file_dir);
								String name =  entry.getName().substring(entry.getName().lastIndexOf("/"));
								File f = new File(outputFile,name);
								f.createNewFile();
					            byte [] btoRead = new byte[1024];
					            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(f));
					            int len = 0;
					            while((len = tarIn.read(btoRead)) != -1){
							                bout.write(btoRead,0,len);
							     }
					            bout.close();
					            Path source = f.toPath();
					            Files.move(source, source.resolveSibling("PMC" + pmc_id + f.getName()), StandardCopyOption.REPLACE_EXISTING);
					           
							}

							entry = tarIn.getNextTarEntry();
						}
						tarIn.close();
						
						System.out.print("Done.");
					}
				
				}
				if (!found){
					System.err.println("Count not find file for " + filename);
				}
				
			}
			else{
				System.err.println("Cant find file for " + pmc_id);
			}

		}
		
	}
	
	

	public static void main(String[]args) throws IOException{
		Config config = new Config();
		config.setPropValues();
		String input_list = config.getInput_List();

		boolean nxml = config.isNxml();
		
		String pmc_filename = input_list;
		
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
				String nxml_dir = config.getNxml_dir();
				String resolve_file = config.getResolve_file();
				TextExtractor.setPaper_dir(paper_dir);
				MasterExtractor.setFile_dir(file_dir);
				MasterExtractor.setTable_dir(table_dir);
				//TODO: if it's not empty then you shouldnt have to translate it
				extractFromList(file_dir,table_dir,paper_dir,output_file,simple_reaction,make_tables,pmc_ids,nxml,pmc_filename,nxml_dir, resolve_file);
				
			}else{
				//HERE IS WHERE YOU SOULD DO THE NXML stuff
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
			else{
				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
