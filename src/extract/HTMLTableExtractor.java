package extract;

import java.io.File;
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
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import extract.buffer.TableBuf;

/** 
 * Extraction class used to generate protobuf table objects from html based tables.
 * The methods in this class will only work on html tables retrieved from PMC.
 * @author vhsiao
 */
public class HTMLTableExtractor {
	private static String file_dir = "files";
	// Private helper method used to check for single entry rows
	private boolean isValidRow(Element tr){
		int count = 0;
		List<Node> tds = tr.childNodes();
		for (Node td: tds){
			if (td instanceof Element){
				if (!((Element) td).text().trim().equals("")){
					count++;
				}
			}
		}
		return count > 1;
		
	}
	
	/**
	 * Retrieves a data list from an html file by using Jsoup.
	 * 
	 * This method will account for variable row/column spans and will
	 * distribute data accordingly. It will also account for subheaders 
	 * located in the middle of table entries. However it will not
	 * account for implicit row/column spans suggested by empty entries.
	 * 
	 * @param fileName the path to the html file
	 * @return the table data as a 2D List
	 */
	public Collection<List<String>> parseHTMLTable(String fileName){
		File document = new File(fileName);
		
		if (document.exists()) {
			try {
				Document doc = Jsoup.parse(document, "UTF-8", "");
				ArrayList<List<String>> table = new ArrayList<List<String>>();
				
				Elements tables = doc.select("table");
				//was <2
				if(tables.size() < 1){
					return null;
				}
				Element tableElements = tables.get(0);
				Elements tableHeaderRows = tableElements.select("thead tr");
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
								//Adjust the alignment of entries if rowspans and colspans are not 1
								boolean skip = false;
								if (j + headerOffset < table.size()) {
									if (rowspans.get(j + headerOffset) <= 0){
										String originalEntry = table.get(j + headerOffset).get(0);
										table.get(j + headerOffset).set(0, originalEntry + ";" + tableHeaderElements.get(j).text());
										rowspans.set(j + headerOffset, rowspan);
									} else {
										k--;
										skip = true;
									}
								} else {
									ArrayList<String> column = new ArrayList<String>();
									column.add(tableHeaderElements.get(j).text());
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
				
				//TODO: sibling non-empty count > 2
				//ADDING subheaders to caption
		        ArrayList<String> captions = new ArrayList<String>();
		        captions.add("Captions");
		    	//Extract captions
				Elements captionElements = doc.getElementsByClass("caption");
		        for (int i = 0; i < captionElements.size(); i++) {
		        	captions.add(captionElements.get(i).text());
		        }
		        captionElements = doc.getElementsByAttributeValueContaining("id", "fn");
		        for (int i = 0; i < captionElements.size(); i++) {
		        	captions.add(captionElements.get(i).text());
		        }
				
				//Extract table rows
				Elements tableRowElements = tableElements.select(":not(thead) tr");
				
				String prevPartB = null;
				for (int i = 0; i < tableRowElements.size(); i++) {
		            Element row = tableRowElements.get(i);
		            if (!isValidRow(row)){
		            	//adding to the caption!
		            	if (captions.size() > 1){
		            		captions.add(1, captions.get(1) + " ; " + row.text());
		            		captions.remove(2);
		            	}
		            	//captions.add(row.text());
		            	continue;
		            }
		            //System.err.println(row.outerHtml());
		            Elements rowItems = row.select("td");
		            int elementOffset = 0;
		            for (int j = 0; j < rowItems.size(); j++) {
		            	/*IN CASE THERE ARE BLANK LINES INSTEAD OF PARTICIPANT b*/
		            	if (j==0 && rowItems.get(j).html().equals("&nbsp;")){
		            		if (prevPartB!=null){
		            			rowItems.get(j).text(prevPartB);
		            			//System.out.println("SET PARTB to " + prevPartB);
		            		}
		            	}
		            	else if (j==0){
		            		prevPartB = rowItems.get(j).text();
		            	}
		            	//
		               if(rowItems.get(j).attributes().hasKey("colspan")){
		            	   int colspan = Integer.parseInt(rowItems.get(j).attributes().get("colspan"));
		            	   int rowspan = Integer.parseInt(rowItems.get(j).attributes().get("rowspan"));
		            	   String value = rowItems.get(j).text();
		            	   Elements links = rowItems.get(j).select("a[href]"); // a	 with href
		            	   if (links.size() > 0){
		            		   //System.out.println(rowItems.get(j).outerHtml());
		            		  for (Element link: links){
		            			  
		            			  Elements spans = link.select("span");
		            			   if (spans.size() > 0 ){
		            				  // System.out.println("prev value: " + value);
	            					   value = link.ownText();
	            					 //  System.out.println("set value: " + value);	
	            					   break;
		            				 //  System.err.println("LINK: " + link.outerHtml());
		            				   
		            			   }
		            		//	  value =  link.ownText();
		            		   }
		            			
		            	   }
	            		   if(value.trim().length() > 0) {
								//Check for extraneous characters
								if((int)value.charAt(0) == 8722){
									value = "-" + value.substring(1);
								} else if (value.charAt(0) == 8195){
									value = value.substring(1);
								}	
							}
	            		   //System.out.println("VALUE: " + value);
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
			
			
		        table.add(captions);
		        
				return table;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
				columns[i] = builder.addColumnBuilder();
				columns[i].setHeader(TableBuf.Cell.newBuilder().setData(col.get(0)));
				for (int j = 1; j < col.size(); j++) {
					//System.err.println(col.get(j));
					columns[i].addData(TableBuf.Cell.newBuilder().setData(col.get(j)));
				}
				i++;
			}
		}

	}
	
	// Main method used for testing.
	public static void main (String [] args){
		TableBuf.Table.Builder table = TableBuf.Table.newBuilder();
		table.addCaption("Text Extracted from html");
		TableBuf.Source.Builder source = table.getSourceBuilder();
		source.setAuthor("Paul Revere Et Al");
		source.setPmcId("PMC3102680");
		source.setPaperTitle("Biochemical Paper");
		//A		B	C
		//A1	B1 	C1
		//A2 	B2	C2
		
		HTMLTableExtractor extractor = new HTMLTableExtractor();
		//Test Data:
		//PMC3725062Resource5
		//PMC4133982Resource1
		String name = "PMC3725062Resource1";
		Collection<List<String>> data = extractor.parseHTMLTable("files" + File.separator +"PMC3102680pone-0020199-t001" + ".html");
		
		extractor.createTableBuf(table, data);
		
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

	public static String getFile_dir() {
		return file_dir;
	}

	public static void setFile_dir(String file_dir) {
		HTMLTableExtractor.file_dir = file_dir;
	}

}
