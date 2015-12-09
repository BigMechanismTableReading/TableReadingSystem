package extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Workbook;

import extract.buffer.TableBuf;

/**
 * Pipeline class used for general extraction of table documents (html, xls, xml)
 * @author hsiaov
 *
 */
public class MasterExtractor {
	private static String file_dir = "files";
	private static String table_dir = "tables";
	// Used for general scraping and extraction purposes
	public static void main (String args[]) {
		System.out.println("Please choose input type:");
		System.out.println("1 - File with PMCIDs");
		System.out.println("2 - PMCID");
		System.out.println("3 - Skip Web Scraping");
		Scanner s = new Scanner(System.in);
		
		int type = s.nextInt();
		
		File files = new File("files");
		
		if(type == 1){
			System.out.println("Please input path to file:");
			String path = s.next();
			
			Scanner reader;
			try {
				File ids = new File(path);
				reader = new Scanner(ids);
				System.out.println("Downloading Files...");
				while(reader.hasNext()){
					int PMCID = reader.nextInt();	
					WebScraper.extractPaper(PMCID);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if (type == 2){
			System.out.println("Please input a PMCID:");
			int PMCID = s.nextInt();	
			System.out.println("Downloading Files...");
			WebScraper.extractPaper(PMCID);
		}
		s.close();
		
		System.out.println("Extracting Files...");
		File[] list = files.listFiles();
		for(int i = 0; i < files.listFiles().length; i++){
			System.out.println(list[i].getName());
			buildTable(list[i], list[i].getName().substring(3, 10));
		}
		System.out.println("Done");
	}
	
	/**
	 * Extract TableBuf objects from html/xls/xml files. Also writes them to .pb files.
	 * @param target target Excel/html/xml
	 * @param PMCID PMCID of paper
	 * @return list of tables extracted
	 */
	public static List<TableBuf.Table> buildTable(File target, String PMCID){
		ArrayList<TableBuf.Table> tables = new ArrayList<TableBuf.Table>();
		if(target.getName().endsWith(".html")){
			TableBuf.Table.Builder table = TableBuf.Table.newBuilder();
			TableBuf.Source.Builder source = table.getSourceBuilder();
			source.setAuthor("Unknown");		
			source.setPmcId("PMC" + PMCID);
			source.setPaperTitle("Unknown");
			source.setSourceFile(target.getName());
			boolean humanMarkupRequired = false;
			String extra = "";
			HTMLTableExtractor.setFile_dir(file_dir);
			HTMLTableExtractor extractor = new HTMLTableExtractor();
			System.out.println("parsing html file: " + target.getAbsolutePath());
			Collection<List<String>> data = extractor.parseHTMLTable(target.getPath());
			if(data == null) {
				System.out.println("Human markup required on: " + target.getName());
				humanMarkupRequired = true;
			} else {
				extractor.createTableBuf(table, data);
			}
			if(!humanMarkupRequired){
				TableBuf.Table tb = table.build();
				writeToFile(target, tb, extra);
				tables.add(tb);
			}
		} else if(target.getName().endsWith(".xml")){
			tables.addAll(XMLTableExtractor.extractXMLPaper(target.getName(), PMCID));
		} else {
			//int sheetNum = 0;
			
			TableExtractor extractor = new TableExtractor();
			Workbook wb = extractor.openExcelDocument(target.getPath());
			
			if(wb != null) {
				int numOfSheets = wb.getNumberOfSheets();
				for (int sheetNum = 0; sheetNum < numOfSheets; sheetNum++){
					TableBuf.Table.Builder table = TableBuf.Table.newBuilder();
					TableBuf.Source.Builder source = table.getSourceBuilder();
					source.setAuthor("Unknown");		
					source.setPmcId("PMC" + PMCID);
					source.setPaperTitle("Unknown");
					source.setSourceFile(target.getName());
					source.setSheetNo(""+sheetNum);
					boolean humanMarkupRequired = false;
					String extra = "";
					Collection<List<String>> data = null;
					try{
						data = extractor.parseExcelTable(target.getPath(), sheetNum);
					}catch(IllegalArgumentException e){
						e.printStackTrace();
					}catch(IllegalStateException a){
						a.printStackTrace();
					}
					if(data == null) {
						if (wb.getSheetAt(sheetNum).getPhysicalNumberOfRows() > 0) {
							System.out.println("Human markup required on: " + target.getName() + " Sheet" + (sheetNum + 1));
						}
						humanMarkupRequired = true;
					} else {
						extractor.createTableBuf(table, data);
						extra = "Sheet" + (sheetNum + 1);
					}
					if(!humanMarkupRequired){
						TableBuf.Table tb = table.build();
						writeToFile(target, tb, extra);
						tables.add(tb);
					}
					
				}
				
			}
		}
		System.out.println("Here");
		return tables;
	}
	
	//private helper method for writing .pb files
	private static void writeToFile(File target, TableBuf.Table table, String extra){
		String name = target.getName().substring(0, target.getName().lastIndexOf("."));
		
		FileOutputStream output;
		try {
			output = new FileOutputStream(table_dir + File.separator + name + extra + ".pb");
			table.writeTo(output);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getTable_dir() {
		return table_dir;
	}

	public static void setTable_dir(String table_dir) {
		MasterExtractor.table_dir = table_dir;
	}

	public static String getFile_dir() {
		return file_dir;
	}

	public static void setFile_dir(String file_dir) {
		MasterExtractor.file_dir = file_dir;
	}
}
