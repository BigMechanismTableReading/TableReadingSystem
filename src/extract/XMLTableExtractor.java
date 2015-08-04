package extract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import extract.buffer.TableBuf;

/** 
 * Extraction class used to generate protobuf table objects from xml files.
 * The methods in this class will only work on xml files.
 * @author vhsiao
 */
public class XMLTableExtractor {
	/**
	 * Retrieve a table from an xml file by using Jsoup
	 * @param the Jsoup element representing an xml table
	 * @return the data as a 2D List
	 */
	public Collection<List<String>> parseXMLTable(Element xmltable){
			try {
				ArrayList<List<String>> table = new ArrayList<List<String>>();
				
				Elements tableHeaderRows = xmltable.select("thead tr");
				
				ArrayList<Integer> rowspans = new ArrayList<Integer>();
				
				//Extract headers
				for (int i = 0; i < tableHeaderRows.size(); i++) {
					Elements tableHeaderElements = tableHeaderRows.get(i).select("th");
					tableHeaderElements.addAll(tableHeaderRows.get(i).select("td"));
					
					int headerOffset = 0;
					for (int j = 0; j < tableHeaderElements.size(); j++) {
						
						if(tableHeaderElements.get(j).attributes().hasKey("colspan")){
							int colspan = Integer.parseInt(tableHeaderElements.get(j).attributes().get("colspan"));
							int rowspan = Integer.parseInt(tableHeaderElements.get(j).attributes().get("rowspan"));
							for (int k = 0; k < colspan; k++){
								//Adjust the alignment of entries if rowspan is not 1
								boolean skip = false;
								if (j + headerOffset < table.size()) {
									if (rowspans.get(j + headerOffset) <= 0){
										String originalEntry = table.get(j + headerOffset).get(0);
										table.get(j + headerOffset).set(0, originalEntry + ";" + getText(tableHeaderElements.get(j)));
										rowspans.set(j + headerOffset, rowspan);
									} else {
										k--;
										skip = true;
									}
								} else {
									ArrayList<String> column = new ArrayList<String>();
									column.add(getText(tableHeaderElements.get(j)));
									table.add(column);
									rowspans.add(rowspan);
								}
								if(colspan > 1 || skip)
								headerOffset++;
							} 
						}
					}
					for (int j = 0; j < rowspans.size(); j++) {
						rowspans.set(j, rowspans.get(j)-1);
					}
		        }
				
				//Extract table rows
				Elements tableRowElements = xmltable.select("tbody tr");
				for (int i = 0; i < tableRowElements.size(); i++) {
		            Element row = tableRowElements.get(i);
		            Elements rowItems = row.select("td");
		            int elementOffset = 0;
		            for (int j = 0; j < rowItems.size(); j++) {
		               if(rowItems.get(j).attributes().hasKey("colspan")){
		            	   int colspan = Integer.parseInt(rowItems.get(j).attributes().get("colspan"));
		            	   int rowspan = Integer.parseInt(rowItems.get(j).attributes().get("rowspan"));
		            	   String value = getText(rowItems.get(j));
	            		   if(value.length() > 0) {
								//Check for extraneous characters
								if((int)value.charAt(0) == 8722){
									value = "-" + value.substring(1);
								} else if (value.charAt(0) == 8195){
									value = value.substring(1);
								}	
							}
		            		for (int k = 0; k < colspan; k++){
		            			   //Adjust the alignment of entries if rowspans and colspans are not 1
		            			   boolean skip = false;
		            			   if (j + elementOffset < table.size()) {
		            				   if (rowspans.get(j + elementOffset) <= 0){
		            					   if (colspan > 1){
		            						   String originalEntry = table.get(j + elementOffset).get(0);
		            						   table.get(j + elementOffset).set(0, originalEntry + ";" + value);
		            					   } else {
		            						   table.get(j + elementOffset).add(value);
		            					   }
		            					   rowspans.set(j + elementOffset, rowspan);
		            				   } else {
		            					   k--;
		            					   String lastElement = table.get(j + elementOffset).get(table.get(j + elementOffset).size()-1);
		            					   table.get(j + elementOffset).add(lastElement);
		            					   skip = true;
		            				   }
		            			   } else {
		            				   ArrayList<String> column = new ArrayList<String>();
		           					   column.add(value);
		           					   table.add(column);
		           					   rowspans.add(rowspan);
		            			   }
		            			   if(colspan > 1 || skip){
		            			   elementOffset++;
		            		   }
		            	   } 
		               } 
		               
		            }
		            for (int j = 0; j < rowspans.size(); j++) {
						rowspans.set(j, rowspans.get(j)-1);
					}
		           
		         }
				
				
				//Extract captions
				Elements captionElements = xmltable.getElementsByClass("caption");
		        ArrayList<String> captions = new ArrayList<String>();
		        captions.add("Captions");
		        for (int i = 0; i < captionElements.size(); i++) {
		        	captions.add(getText(captionElements.get(i)));
		        }
		        captionElements = xmltable.select("fn");
		        for (int i = 0; i < captionElements.size(); i++) {
		        	captions.add(getText(captionElements.get(i)));
		        }
		        table.add(captions);
		        
				return table;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	//Private helper method used for retrieving xml text
	private String getText(Element xmlEntry){
		Document simple = Jsoup.parse(xmlEntry.text());
		String s = simple.text();
		return s;
	}
	
	/**
	 * Take the raw table data and put it in the TableBuf protocol for storage
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
				columns[i] = builder.addColumnBuilder();
				columns[i].setHeader(TableBuf.Cell.newBuilder().setData(col.get(0)));
				for (int j = 1; j < col.size(); j++) {
					columns[i].addData(TableBuf.Cell.newBuilder().setData(col.get(j)));
				}
				i++;
			}
		}

	}
	
	/**
	 * Extract xml tables from a xml paper
	 * @param paper the path to the xml file
	 * @return list of tables extracted
	 */
	public static List<TableBuf.Table> extractXMLPaper (String paper, String PMCID){
		File document = new File(paper);
		
		ArrayList<TableBuf.Table> extractedTables = new ArrayList<TableBuf.Table>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(document);
			Document doc = Jsoup.parse(fis, "UTF-8", "", Parser.xmlParser());
			Elements tables = doc.select("table-wrap");
			
			for(int i = 0; i < tables.size(); i++){
				Element xmltable = tables.get(i);
				
				TableBuf.Table.Builder table = TableBuf.Table.newBuilder();
				table.addCaption("Text Extracted from xml");
				TableBuf.Source.Builder source = table.getSourceBuilder();
				source.setAuthor("Unknown");
				source.setPmcId("PMC" + PMCID);
				source.setPaperTitle("Unknown");
				source.setSourceFile(paper);
				source.setSheetNo(""+i);
				XMLTableExtractor extractor = new XMLTableExtractor();
				String name = "PMC" + PMCID + "Resource" + (i + 1);
				Collection<List<String>> data = extractor.parseXMLTable(xmltable);
				
				extractor.createTableBuf(table, data);
				
				TableBuf.Table t = table.build();
				
				File outputFile = new File(name + ".pb");
				FileOutputStream output;
				output = new FileOutputStream(outputFile);
				table.build().writeTo(output);
				output.close();
				extractedTables.add(t);
			}
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return extractedTables;
	}
	
	// Main method used for testing
	public static void main(String[] args){
		extractXMLPaper("PMC3358292.xml", "3358292");
	}
}
