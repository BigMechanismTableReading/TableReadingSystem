package tableBuilder.extract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jcabi.aspects.Timeable;

import main.TableReader;
import tableBuilder.TableBuf;

/** 
 * Extraction class used to generate protobuf table objects from Excel based tables.
 * The methods in this class will only work on Excel spreadsheets.
 * @author vhsiao
 */
public class TableExtractor {
	
	/**
	 * Opens an Excel file (supports both .xls and .xlsx formats)
	 * @param excelFileName the path to the Excel file
	 * @return the Workbook object from Apache POI that represents an Excel Workbook
	 */
	public Workbook openExcelDocument(String excelFileName) {
		File excel_document = new File(excelFileName);
		
		long length = excel_document.length();
		
		if (excel_document.exists() && length > 0 ) {
			double kb = length / 1024;
			if (kb <=100){
				if (excelFileName.endsWith(".xls")) {
					try {
						HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excel_document));
						return wb;
					/*} catch (OfficeXmlFileException e){
						try {
							XSSFWorkbook xb = new XSSFWorkbook(new FileInputStream(excel_document));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
*/
						
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				} else if (excelFileName.endsWith(".xlsx")) {
					try {
						XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excel_document));
						return wb;
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
			else{
				TableReader.writeToLog("Excel too large: " + excelFileName);
			}
		}
		
		return null;
	}
	
	/**
	 * Overloaded version of parseExcelTable
	 * @param excelFileName the path to the excel file
	 * @return the data as a 2D List
	 */
	public Collection<List<String>> parseModelFile(String filename){
		return parseExcelTable(filename, 0);
	}
	
	/**
	 * Retrieve a table from an excel file, supports xls and xlsx files
	 * @param excelFileName the path to the excel file
	 * @return the data as a 2D List
	 */
	public Collection<List<String>> parseExcelTable(String excelFileName, int sheetNum){
		File excel_document = new File(excelFileName);
		//check if file is under 10MB
		if (excel_document.exists() && excel_document.length() < 10485760) {
				//	String extension = excelFileName.substring(period_index);
					if (excelFileName.toLowerCase().endsWith(".xls")) {
						try {
							POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
									excel_document));
							HSSFWorkbook wb = new HSSFWorkbook(fs);
							HSSFSheet sheet = wb.getSheetAt(sheetNum);
							List<int[]> regions = markupTable(sheet);
							if (regions.size()>0){
								return getDataFromExcelFile(sheet,regions);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					} else if (excelFileName.toLowerCase().endsWith(".xlsx")) {
						try {
							XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excel_document));
							XSSFSheet sheet = wb.getSheetAt(sheetNum);
							List<int[]> regions = markupTable(sheet);
							if (regions.size()>0){
								return getDataFromExcelFile(sheet,regions);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
		}
		else if (!excel_document.exists()){
			System.err.println("File does not exist: " + excelFileName);
		}
		
		//System.err.println("returning null here");
		return null;
	}
	
	/**
	 * Takes raw table data (in list format) and converts it into 
	 * the TableBuf protocol for storage.
	 * 
	 * @param builder the Table protobuffer to add the data to
	 * @param rawTable the data to be added
	 */
	public void createTableBuf(TableBuf.Table.Builder builder, Collection<List<String>> rawTable){
		
		TableBuf.Column.Builder[] columns = new TableBuf.Column.Builder[rawTable.size() - 1];
		
		Iterator<List<String>> iter = rawTable.iterator();
		
		int i = 0;
		while(iter.hasNext()){
			List<String> col = iter.next();
			if(col.get(0).equals("Captions")){
				for (int j = 1; j < col.size(); j++) {
					builder.addCaption(col.get(j));
				}
			} else {
				boolean empty = true;
				for (int j = 0; j < col.size(); j++) {
					if(!col.get(j).equals("")){
						empty = false;
					}
				}
				if (!empty){
					columns[i] = builder.addColumnBuilder();
					columns[i].setHeader(TableBuf.Cell.newBuilder().setData(col.get(0)));
					for (int j = 1; j < col.size(); j++) {
						columns[i].addData(TableBuf.Cell.newBuilder().setData(col.get(j)));
					}
					i++;
				}
			}
		}

	}
	
	// Private helper method for retrieving data from a cell
	private static String getCellValue(Cell c){
		if(c != null){
			int type = c.getCellType();
			String value;
			if (type == Cell.CELL_TYPE_NUMERIC || type == Cell.CELL_TYPE_FORMULA || type == Cell.CELL_TYPE_ERROR){
				try {
					value = "" + c.getNumericCellValue();
				} catch (java.lang.IllegalStateException ise){
					if (type == Cell.CELL_TYPE_ERROR){
						value = "" + FormulaError.forInt(c.getErrorCellValue()).toString();
					}
					else{
						value = "";
					}
				}
			
			} else if (type == Cell.CELL_TYPE_BOOLEAN){
				value = Boolean.toString(c.getBooleanCellValue());
			} else {
				value = c.getStringCellValue();
			}
		
			return value;
		}else 
			return "";
	}
	
	/**
	 * Finds valid table regions found in the excel sheet.
	 * 
	 * The method also fills in null cells inside the excel sheet
	 * if they lie within the region found. The list returned will
	 * have the largest region as its first entry.
	 * 
	 * @param sheet The sheet to mark-up
	 * @return list of table regions
	 */
	public static List<int[]> markupTable(Sheet sheet){
		int rows = sheet.getLastRowNum();
		int counter = 0;
		
		int table_regions = 0;
		
		ArrayList<int[]> regions = new ArrayList<int[]>();
		
		while (counter < rows && table_regions < 100) {
			Row row = sheet.getRow(counter);
			if(row != null){
				int start = row.getFirstCellNum();
				int end = row.getLastCellNum();
				while(start < end){
					if(!checkWithinRegions(regions, start, counter)){
						int width = 0;
						int height = 0;
						while (row.getCell(start + width) != null){
							width++;
						}
						int emptyrows = 0;
						while (sheet.getRow(counter + height) != null && sheet.getRow(counter + height).getCell(start) != null && emptyrows < 200){
							//TODO fix it
							if(getCellValue(sheet.getRow(counter + height).getCell(start)).equals("")){
								emptyrows++;
							}else{
								emptyrows = 0;
							}
							if(emptyrows ==199){
								System.out.println(getCellValue(sheet.getRow(counter + height).getCell(start)));
							}
							height++;
						}
						if(width + height > 6 && width > 1 ) {
							table_regions++;
							int[] region = {start, start + width, counter, counter + height};
							System.out.println(region[0] + " " + region[1] + " " + region[2] + " " + region[3]);
							if(table_regions > 1){
								//System.err.println("Multiple table regions : " + sheet.getSheetName());
								//return false;
								if( width * height > (regions.get(0)[1] - regions.get(0)[0]) 
										* (regions.get(0)[3] - regions.get(0)[2]) && width < 100){
									Random rand = new Random();
									int emptycells = 0;
									for (int i = 0; i < 25; i++){
										int randX = rand.nextInt(width);
										int randY = rand.nextInt(height);
										if(sheet.getRow(counter + randY) != null){
											Cell c = sheet.getRow(counter + randY).getCell(start + randX);
											if (getCellValue(c).equals("")){
												emptycells++;
											}
										}
									}
									if(emptycells < 5){
										regions.add(0,region);
									} else {
										regions.add(region);
									}
								} else {
									//TODO
									regions.add(region);
								}
							} else {
								regions.add(region);
							}
						} 
						start += width;
						if(width <= 0){
							start++;
						}
						if(emptyrows >= 200){
							counter = rows;
							System.err.println("EMPTY ROWS");
						}
					} else {
						start++;
					}
				}
			}
			counter++;
		}
		//Fill in empty cells
		if(table_regions > 0) {
			int[] a = regions.get(0);
			for(int i = a[2]; i < a[3]; i++){
				Row row = sheet.getRow(i);
				for(int j = a[0]; j < a[1]; j++){
					Cell cell = row.getCell(j);
					if (cell == null) {
						cell = row.createCell(j);
					}
				}
			}
			return regions;
		} else {
			return regions;
		}
	}
	
	//private helper method to check region overlaps
	private static boolean checkWithinRegions(ArrayList<int[]> regions, int x, int y){
		for(int[] region : regions){
			if (x >= region[0] && x <= region[1]){
				if (y >= region[2] && y <= region[3]){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the data from the current xlsx or xls file, including headers
	 * @param sheet the object that represents a sheet in an Excel file
	 * @return the data as a 2D List
	 */
	private Collection<List<String>> getDataFromExcelFile(Sheet sheet, List<int []> regions){
		int rows = regions.get(0)[3];
		//int counter = 0;
		
		//Un-merge all merged regions
		int merged_regions = sheet.getNumMergedRegions();
		for (int i = 0; i < merged_regions; i++){
			int start = sheet.getMergedRegion(i).getFirstColumn();
			int end = sheet.getMergedRegion(i).getLastColumn();
			int row = sheet.getMergedRegion(i).getFirstRow();
			
			//Copy merged data over merged cells
			Cell cell = sheet.getRow(row).getCell(start);
			for (int j = start + 1; j <= end; j++){
				Cell following_cell = sheet.getRow(row).getCell(j);
				if (following_cell == null) {
					following_cell = sheet.getRow(row).createCell(j);
				}
				int type = cell.getCellType();
				following_cell.setCellType(type);
				if (type == Cell.CELL_TYPE_NUMERIC || type == Cell.CELL_TYPE_FORMULA || type == Cell.CELL_TYPE_ERROR){
					following_cell.setCellValue(cell.getNumericCellValue());
				} else if (type==Cell.CELL_TYPE_BOOLEAN){
					following_cell.setCellValue(cell.getBooleanCellValue());
				} else {
					following_cell.setCellValue(cell.getStringCellValue());
				}
				following_cell.setCellStyle(cell.getCellStyle());
			}
		}
		
		//initialize the table where we will extract the 
		//data from the excel sheet
		HashMap<Integer, List<String>> table = new HashMap<Integer, List<String>>();
		//extract the data from the spreadsheet
		
		//Add a caption list
		ArrayList<String> captions = new ArrayList<String>();
		captions.add("Captions");
		table.put(-1, captions); //TODO: -1
		String sheet_name = sheet.getSheetName();
		if (sheet_name!=null && !sheet_name.trim().equals("")){
			captions.add(sheet_name);
		}
		
		int cols = regions.get(0)[1];
		
		for (int counter= regions.get(0)[2]; counter < rows; counter++){
			Row row = sheet.getRow(counter);
		
			if (row != null && row.getFirstCellNum()!=-1) {
				int cell_counter = regions.get(0)[0];
				Cell following_cell;
				//Iterate through row
				while(cell_counter < cols) {
					if((following_cell = row.getCell(cell_counter)) != null){
						
						String value = getCellValue(following_cell);
						if(value.length() > 0) {
							//Check for extraneous characters
							if((int)value.charAt(0) == 8722){
								value = "-" + value.substring(1);
							} else if (value.charAt(0) == 8195){
								value = value.substring(1);
							}	
						}
						
						List<String> values = table.get(cell_counter);
						if (values == null){
							values = new ArrayList<String>();
						}
						values.add(value);
						table.put(cell_counter, values);
					}
					
					cell_counter++;
				}
			
			}
			
		}
		return table.values();
	}
	
	// Main method used for testing
	public static void main (String [] args){
		TableBuf.Table.Builder table = TableBuf.Table.newBuilder();
		table.addCaption("Text Extracted from excel");
		TableBuf.Source.Builder source = table.getSourceBuilder();
		source.setAuthor("Paul Revere Et Al");
		source.setPmcId("PMC4335977");
		source.setPaperTitle("Biochemical Paper");
		//A		B	C
		//A1	B1 	C1
		//A2 	B2	C2
		
		TableExtractor extractor = new TableExtractor();
		//Test Data:
		//PMC3725062Table1
		//PMC3404884TableS1
		//PMC3643591TableS2
		//PMC2711022Resource1
		String name = /*"files"+File.separator +*/  "PMC2984231Suppsupp_M110.002113_mcp.M110.002113-1.xls";
		Collection<List<String>> data = extractor.parseExcelTable(name,0);
		
		extractor.createTableBuf(table, data);
		
		/*for (TableBuf.Column col : table.getColumnList()){
			System.out.println(col.getHeader().getData() + " " + col.getDataCount());
		}*/
		
		/*TableBuf.Column.Builder colA = table.addColumnBuilder();
		colA.setHeader(TableBuf.Cell.newBuilder().setData("A"));
		colA.addData(TableBuf.Cell.newBuilder().setData("A1"));
		colA.addData(TableBuf.Cell.newBuilder().setData("A2"));
		
		TableBuf.Column.Builder colB = table.addColumnBuilder();
		colB.setHeader(TableBuf.Cell.newBuilder().setData("B"));
		colB.addData(TableBuf.Cell.newBuilder().setData("B1"));
		colB.addData(TableBuf.Cell.newBuilder().setData("B2"));*/
		
		 FileOutputStream output;
		try {
			output = new FileOutputStream(name + ".pb");
			table.build().writeTo(output);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    

	}

}
