package tableBuilder;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import main.TableReader;
import tableBuilder.TableBuf.Table;
import tableBuilder.extract.ExcelOpener;
import tableBuilder.extract.HTMLTable;
import tableBuilder.extract.HTMLTableExtractor;
import tableBuilder.extract.TableExtractor;
import tableBuilder.extract.XMLTableExtractor;

import org.apache.poi.ss.usermodel.Workbook;

import com.jcabi.aspects.Timeable;


public class TableBuilder {
	/**
	 * Extract TableBuf objects from html/xls/xml files. Also writes them to .pb files.
	 * @param target target Excel/html/xml
	 * @param PMCID PMCID of paper
	 * @return list of tables extracted
	 */
	
	public static List<Table> buildTable(File target, String PMCID){
		
		
		ArrayList<Table> tables = new ArrayList<Table>();
		if(target.getName().endsWith(".html")){
			Table.Builder table = Table.newBuilder();
		
			TableBuf.Source.Builder source = table.getSourceBuilder();
			source.setAuthor("Unknown");		
			source.setPmcId("PMC" + PMCID);
			source.setPaperTitle("Unknown");
			source.setSourceFile(target.getName());
			boolean humanMarkupRequired = false;
			HTMLTableExtractor extractor = new HTMLTableExtractor();
			TableReader.writeToLog("Parsing html file: " + target.getAbsolutePath());
			/*Collection<List<String>> data = extractor.parseHTMLTable(target.getPath());
			if(data == null) {
				TableReader.writeToLog("Human markup required on: " + target.getName());
				humanMarkupRequired = true;
			} else {
				extractor.createTableBuf(table, data);
			}*/
			ArrayList<HTMLTable> htmlInfo = extractor.parseHTML(target.getAbsolutePath());
			if (htmlInfo.isEmpty()){
				TableReader.writeToLog("Human markup required on: " + target.getName());
				humanMarkupRequired = true;
			}
			else{
				for (HTMLTable tbl: htmlInfo){
					try {
						extractor.createTableBuf(table, tbl);
						Table tb = table.build();
						writeToFile(target, tb, "");
						tables.add(tb);
					}
					catch (NullPointerException e){
						System.err.println("Error building tableBufs for " + target.getName());
						TableReader.writeToLog("Error building tableBufs for " + target.getName());
					}

				}
			}
			
		} else if(target.getName().endsWith(".xml")){
			tables.addAll(XMLTableExtractor.extractXMLPaper(target.getName(), PMCID));
		} else if (target.getName().trim().endsWith(".xls") || target.getName().trim().endsWith(".xlsx")){
			//int sheetNum = 0;
			TableExtractor extractor = new TableExtractor();
			Workbook wb =extractor.openExcelDocument(target.getPath());			
		//	ExcelOpener eo = new ExcelOpener();
		//	eo.setFileName(target.getPath());
		//	Future future = executor.submit(eo);
			
		//	try {
				
			//	Workbook wb = (Workbook) future.get(10, TimeUnit.SECONDS);
	
				if(wb != null) {
					int numOfSheets = wb.getNumberOfSheets();
					//System.out.println(numOfSheets);
					for (int sheetNum = 0; sheetNum < numOfSheets; sheetNum++){
						Table.Builder table = Table.newBuilder();
						TableBuf.Source.Builder source = table.getSourceBuilder();
						source.setAuthor("Unknown");		
						source.setPmcId("PMC" + PMCID);
						source.setPaperTitle("Unknown");
						String name = wb.getSheetAt(sheetNum).getSheetName();
						if (name!=null && !name.trim().equals("")){
							source.setPaperTitle(name);
						}
						source.setSourceFile(target.getName());
						source.setSheetNo(""+sheetNum);
						boolean humanMarkupRequired = false;
						String extra = "";
						Collection<List<String>> data = null;
						try{
							data = extractor.parseExcelTable(wb, sheetNum);
						}catch(IllegalArgumentException e){
							e.printStackTrace();
						}catch(IllegalStateException a){
							a.printStackTrace();
						}
						if(data == null) {
							if (wb.getSheetAt(sheetNum).getPhysicalNumberOfRows() > 0) {
								TableReader.writeToLog("Human markup required on: " + target.getName() + " Sheet" + (sheetNum + 1));
							}
							humanMarkupRequired = true;
						} else {
							extractor.createTableBuf(table, data);
							extra = "Sheet" + (sheetNum + 1);
						}
						if(!humanMarkupRequired){
							Table tb = table.build();
							writeToFile(target, tb, extra);
							tables.add(tb);
						}
						
					}
					
				}
			//	System.out.println("done: " + future.isDone());
			/*}
			catch (TimeoutException e){
				System.err.println("Took too long: " + target.getName());
				TableReader.writeToLog("Took too long: " + target.getName());
				//Thread.currentThread().interrupt();
				//future.cancel(true);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
			//	future.cancel(true);
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				//future.cancel(true);
				e1.printStackTrace();
			}*/
			
		}
	
		return tables;
	}
	
	//private helper method for writing .pb files
	private static void writeToFile(File target, Table table, String extra){
		String name = target.getName().substring(0, target.getName().lastIndexOf("."));
		
		try {
			File f = new File(TableReader.tables + File.separator + name + extra + ".pb");
			if (f.exists()){
				f.delete();
			}
			FileOutputStream output = new FileOutputStream(new File(TableReader.tables + File.separator + name + extra + ".pb"), false);
			table.writeTo(output);
			output.close();
		} catch (FileNotFoundException e) {
			//access denied to overwrite?
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		File dir = new File(args[0]);
		try {
			File [] files = dir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File arg0) {
					String name = arg0.getName();
					return name.endsWith(".xlsx") || name.endsWith(".xls");
				}
				
			});
			for (File file: files){
				TableExtractor.openExcelFile(file);
			}
		}	catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	
		
	
	}
}
