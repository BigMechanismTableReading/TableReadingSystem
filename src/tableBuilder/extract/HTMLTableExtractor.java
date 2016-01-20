package tableBuilder.extract;

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

import tableBuilder.TableBuf;

/** 
 * Extraction class used to generate protobuf table objects from html based tables.
 * The methods in this class will only work on html tables retrieved from PMC.
 * @author vhsiao
 */
public class HTMLTableExtractor {
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
	
	
	private int getCol(Elements colList){
		int colCount = 0;
		Iterator <Element> cols = colList.iterator();
		while (cols.hasNext()){
			Element col = cols.next();
			int span = 1;
			if (col.hasAttr("span")){
				try {
					span = Integer.parseInt(col.attr("span"));
				}
				catch (NumberFormatException e){
					span = 1;
				}
			}
			colCount+=span;
		}
		return colCount;
	}
	
	/**
	 * There are two ways to determine the number of columns in a table (in order of precedence):

		If the TABLE element contains any COLGROUP or COL elements, user agents should calculate the number of columns by summing the following:
		For each COL element, take the value of its span attribute (default value 1).
		For each COLGROUP element containing at least one COL element, ignore the span attribute for the COLGROUP element. For each COL element, perform the calculation of step 1.
		For each empty COLGROUP element, take the value of its span attribute (default value 1).
		Otherwise, will return -1 and get number of columns during:
		 if the TABLE element contains no COLGROUP or COL elements, user agents should base the number of columns on what is required by the rows. The number of columns is equal to the number of columns required by the row with the most columns, including cells that span multiple columns. For any row that has fewer than this number of columns, the end of that row should be padded with empty cells. The "end" of a row depends on the table directionality.
		It is an error if a table contains COLGROUP or COL elements and the two calculations do not result in the same number of columns.
	 
	 *@return 0 if have to do during calculation
	 */	
	
	//WHAT ABOUT DEALING WITH MULTIPLE COLUNNS
	private int getNumberOfColumns(Document doc){
		int colCount = 0;
		//COL
		Elements cols = doc.select("table > col");
		if (cols.size() > 0){
			colCount+=getCol(cols);
		}
	
		//COLGROUP
		Iterator <Element> colgroups = doc.select("table > colgroup").iterator();
		while (colgroups.hasNext()){
			Element cg = colgroups.next();
			Elements colChildren = cg.select("col");
			if (colChildren.size() > 0){
				colCount+= getCol(colChildren);
			}
			else{
				int span = 1;
				if (cg.hasAttr("span")){
					try {
						span = Integer.parseInt(cg.attr("span"));
					}
					catch (NumberFormatException e){
						span = 1;
					}
				}
				colCount+=span;
			}
		}
		return colCount;
	}
	
	public ArrayList<HTMLTable> parseHTML(String filename){
	//	System.out.println("Doing own parsing: " + filename);
		File document = new File(filename);
		ArrayList<HTMLTable> tableResults = new ArrayList<HTMLTable>();
		if (document.exists()){
			try {
				Document doc = Jsoup.parse(document, "UTF-8", "");
				//System.out.println(doc.outerHtml());
				Elements tables = doc.select("table-wrap");
				Iterator <Element> it = tables.iterator();
				while (it.hasNext()){
					HTMLTable t = new HTMLTable();
					Element e = it.next();
					//title
					Elements titles = e.select("title , caption"); //TODO: caption?
					if (titles.size() > 0){
						//TODO: ignore title importance for now
						Iterator <Element> txtIterator = titles.iterator();
						while (txtIterator.hasNext()){
							String title = txtIterator.next().text();	
							title = title.replaceAll("<xref.*?>.*?</xref>", "");
							//title = title.replaceAll("(?<=<xref).*?(?=</xref>)", "");
							//title = title.replaceAll("<xref</xref>", "");
						//	System.out.println("title: " + title);
							t.addCaption(title);
						}
						//TODO: can we use the information from col align that it will have certain numbers
					}
					int colSize = getNumberOfColumns(doc);
					if (colSize > 0){
						System.out.println("number of columns: " + colSize);
						t.setHeaderSize(colSize);
						
						//headers -- theres only one thead but can be multiple thead tr sets
						Elements theads = e.select("thead > tr");
						if (theads.size() > 0){
							//TODO: if there is more than one thead then we should treat as another table?
							Iterator <Element> itTr = theads.iterator();
							while (itTr.hasNext()){
								Elements th =  itTr.next().select("th , td");
							//	String [] headers = new String[th.size()];
								Iterator <Element> itTh = th.iterator();
								while (itTh.hasNext()){
									Element head = itTh.next();
									int colspan=1; //default to 1
									if (head.hasAttr("colspan")){ //TODO: "rowspan"
										colspan = Integer.parseInt(head.attr("colspan"));
									}
									int i=0;
									while (i < colspan){
										String header = head.ownText();
										t.addNextHeader(header);
										i++;
									}
								}
								
							}
						}
						else{
							Elements heads = e.select("tr > th");
							Iterator <Element> headIt = heads.iterator();
	
							while (headIt.hasNext()){
								t.addNextHeader(headIt.next().ownText());
							
							}
							
						}
						//there can be multiple tbodys
						Elements tbodys = e.select("tbody"); 
						if (tbodys.size() > 0){
							
							Iterator <Element> tbs = tbodys.iterator(); //tbody
							while (tbs.hasNext()){
								Iterator <Element> trs = tbs.next().select("tr").iterator(); //tbody tr
								int row = 0;
								while (trs.hasNext()){
									Iterator <Element> tds = trs.next().select("td").iterator(); //tbody tr td
									String [] rowInfo = new String[colSize];
									
									//ROW
									int i=0;
									while (tds.hasNext()){ //TODO: if td colspan > 1 then add to caption
										Element td = tds.next();
										if (td.hasAttr("colspan")){
											try{
												//if it spans more than one than it will cause issues
												if (Integer.parseInt(td.attr("colspan")) > 1){
													t.addCaption(td.ownText()); //TODO: split into groups? 
													continue;
												}
											}
											catch (NumberFormatException ne){
												//do nothing
											}
										}
										
										String txt = td.ownText();
										if (i >= rowInfo.length){
											System.out.println("Info went beyond number of columns");
											break;
										}
										if (txt.equals("&nbsp;")){
											txt = ""; //set to nothing
										}
									//	System.out.println(row + "," + txt);
										rowInfo[i] = txt;
										i++;
									}
									
									t.addData(row, rowInfo);
									row++;

								}
	
							}
							tableResults.add(t);

						}
						else{ //look for just tr
							System.err.println("No tbody");
						}
					}
					else{
						System.err.println("Can't determine number of columns");
					}
					
					
				
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tableResults;
		
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
	
	//TODO: colspan = number of tr th in thead?
	//TODO: rowspan = number of tr td in tbody?
	public Collection<List<String>> parseHTMLTable(String fileName){
		parseHTML(fileName);
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
						
						if(tableHeaderElements.get(j).attributes().hasKey("colspan") && tableHeaderElements.get(j).attributes().hasKey("rowspan")){
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
	
	
	public void createTableBuf(TableBuf.Table.Builder builder, HTMLTable table){
		TableBuf.Column.Builder[] columns = new TableBuf.Column.Builder[table.getHeaders().length];
		builder.addAllCaption(table.getCaptions());
		for (int i=0; i < columns.length; i++){
			ColumnData columnData = table.getColumnData()[i];
			columns[i] = builder.addColumnBuilder();
			columns[i].setHeader(TableBuf.Cell.newBuilder().setData(columnData.getHeader()));
			String [] data = columnData.getData();
				
			for (int j=0; j < data.length; j++){
				if (data[j]!=null){
					
					columns[i].addData(TableBuf.Cell.newBuilder().setData(data[j]));
				}
				else{
					System.out.println("Data in " + j + " , " + columnData.getHeader() + " is null");
				}
			}
			
		}
	}
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

	

}
