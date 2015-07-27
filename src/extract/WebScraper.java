package extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WebScraper {
	private static String NIH = "http://www.ncbi.nlm.nih.gov";
	
	public static void main(String[] args) {
		//Test Data:
		//PMCids
		//SinglePMCidTest
		File ids = new File("PMCids1-6");
		Scanner s;
		try {
			s = new Scanner(ids);
			while(s.hasNext()){
				int PMCID = s.nextInt();	
				extractPaper(PMCID);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Extracts all tables from a paper
	 * @param PMCID the pmc id of the paper
	 * @return list of all files that would have been downloaded
	 */
	public static List<File> extractPaper(int PMCID){
		URL urlObj;
		
		String url = NIH + "/pmc/articles/PMC" + PMCID + "/";
		
		InputStream is = null;
		BufferedReader dis;
		URLConnection urlConnection;
		String line;
		String lowercaseLine;
		int numOfTables = 0;
		ArrayList<File> filesDownloaded = new ArrayList<File>();
		File download;
		
		try {

			
			File target = new File("papers" + File.separator + "PMC" + PMCID + ".html");
			Path p = target.toPath();
			if(!target.exists()){
				urlObj = new URL(url);
				urlConnection = urlObj.openConnection();
				urlConnection.addRequestProperty("User-Agent", 
					        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
				is = urlConnection.getInputStream();
				Files.copy(is, p, StandardCopyOption.REPLACE_EXISTING);
			} 
			is = new FileInputStream(target);
			if (target.exists()){
				//FileInputStream is = new FileInputStream(target);
						
				InputStreamReader isr = new InputStreamReader(is);
				dis = new BufferedReader(isr);
				while((line = dis.readLine()) != null){
				//	System.out.println("reading line: " + line);
					lowercaseLine = line.toLowerCase();
					if(lowercaseLine.lastIndexOf("sup-box half_rhythm") != -1){
						int counter = 0;
						int location = 0;
						while((location = lowercaseLine.indexOf("<a href=", counter)) != -1) {
							counter = lowercaseLine.indexOf("</a>", location);
							String hyperlink = null;
							if (counter + 4 < line.length()){
								hyperlink = line.substring(location, counter + 4);
							}
							else{
								hyperlink = line.substring(location);
							}
							if (hyperlink.indexOf(".xlsx") != -1){
								System.out.println(hyperlink);
								int linkStart = hyperlink.indexOf('\"');
								numOfTables++;
								String excel_url = NIH + hyperlink.substring(linkStart + 1, hyperlink.indexOf('\"', linkStart + 1));
								download = DownloadFile(excel_url, numOfTables, ".xlsx", PMCID);
								filesDownloaded.add(download);
							} else if (hyperlink.indexOf(".xls") != -1){
								System.out.println(hyperlink);
								int linkStart = hyperlink.indexOf('\"');
								numOfTables++;
								String excel_url = NIH + hyperlink.substring(linkStart + 1, hyperlink.indexOf('\"', linkStart + 1));
								download = DownloadFile(excel_url, numOfTables, ".xls", PMCID);
								filesDownloaded.add(download);
							} else if (hyperlink.indexOf("index.html") != -1){
								System.out.println(hyperlink);
								int linkStart = hyperlink.indexOf('\"');
								String new_url = hyperlink.substring(linkStart + 1, hyperlink.indexOf('\"', linkStart + 1));
								if(new_url.indexOf("http") == -1){
									new_url = NIH + new_url;
								} 
								numOfTables = getExcelFiles(new_url, numOfTables, PMCID, filesDownloaded);
							}
						}
					} 
					if(lowercaseLine.indexOf("<a class=\"figpopup\" href=") != -1){
						int counter = 0;
						int location = 0;
						while((location = lowercaseLine.indexOf("<a class=\"figpopup\" href=", counter)) != -1) {
							counter = lowercaseLine.indexOf("</a>", location);
							String hyperlink = null;
							if (counter + 4 < line.length()){
								hyperlink = line.substring(location, counter + 4);
							}
							else{
								hyperlink = line.substring(location);
							}
							if(hyperlink.indexOf("table") != -1){
								System.out.println(hyperlink);
								int linkStart = hyperlink.indexOf("href=\"");
								numOfTables++;
								String internalTable = NIH + hyperlink.substring(linkStart + 6, hyperlink.indexOf('\"', linkStart + 6));
								download = DownloadFile(internalTable, numOfTables, ".html", PMCID);
								filesDownloaded.add(download);
							}
						}
					}
				}
				is.close();
				dis.close();
			}
			else{
				System.out.println("File " + target.getName() + " does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filesDownloaded;
	}
	/**
	 * Retrieves Excel documents from a supplementary webpage
	 * @param path url for webpage
	 * @param table_num table number to start saving tables at
	 * @param PMCID pmc id of paper
	 * @param filesDownloaded list to add files to
	 * @return
	 */
	private static int getExcelFiles(String path, int table_num, int PMCID, List<File> filesDownloaded){
		String relativePath = path.substring(0, path.lastIndexOf("/") + 1);
		URL urlObj;
		InputStream is = null;
		BufferedReader dis;
		URLConnection urlConnection;
		String line;
		String lowercaseLine;
		File download;
		
		try {
			File target = new File("webpages" + "/" + "PMC" + PMCID + "suppPage" + table_num + ".html");
			
			Path p = target.toPath();
			if(!target.exists()){
				urlObj = new URL(path);
				urlConnection = urlObj.openConnection();
				urlConnection.addRequestProperty("User-Agent", 
					        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
				is = urlConnection.getInputStream();
				Files.copy(is, p, StandardCopyOption.REPLACE_EXISTING);
			}
			is = new FileInputStream(target);
			
			InputStreamReader isr = new InputStreamReader(is);
			
			dis = new BufferedReader(isr);
			while((line = dis.readLine()) != null){
				lowercaseLine = line.toLowerCase();
				int counter = 0;
				int location = 0;
				//<a href =" link" ></a>
				while((location = lowercaseLine.indexOf("<a href=", counter)) != -1) {
					counter = lowercaseLine.indexOf("</a>", location);
					String hyperlink = line.substring(location, counter + 4);
					if (hyperlink.indexOf(".xlsx") != -1){
						System.out.println(hyperlink);
						int linkStart = hyperlink.indexOf('\"');
						table_num++;
						String excel_url = relativePath + hyperlink.substring(linkStart + 1, hyperlink.indexOf('\"', linkStart + 1));
						download = DownloadFile(excel_url, table_num, ".xlsx", PMCID);
						filesDownloaded.add(download);
					} else if (hyperlink.indexOf(".xls") != -1){
						System.out.println(hyperlink);
						int linkStart = hyperlink.indexOf('\"');
						table_num++;
						String excel_url = relativePath + hyperlink.substring(linkStart + 1, hyperlink.indexOf('\"', linkStart + 1));
						download = DownloadFile(excel_url, table_num, ".xls", PMCID);
						filesDownloaded.add(download);
					} else if (hyperlink.indexOf("index.html") != -1){
						//TODO extra recursion
					}
				}
			}
			isr.close();
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return table_num;
	}
	
	/**
	 * Downloads a file
	 * @param path the url for the file
	 * @param table_num the table number to save the file as
	 * @param type the type of file (for example .xls or .xlsx)
	 * @param PMCID the pmc id of the paper
	 */
	private static File DownloadFile(String path, int table_num, String type, int PMCID){
		try {

			
			String table_name = "";
			//System.out.println(path);
			if (path.lastIndexOf(".xls") != -1) {
				table_name = "Supp" + path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
			} else {
				int last = path.lastIndexOf("/");
				table_name = path.substring(path.lastIndexOf("/", last - 1) + 1, last);
			}
		
			File target = new File("files"+ File.separator + "PMC" + PMCID + table_name + type); //+ "Resource" + table_num + type);
			System.out.println(target.getAbsolutePath());
			Path p = target.toPath();
			if(!target.exists()){
				URL excel_file = new URL(path);
				URLConnection excelConnection = excel_file.openConnection();
				excelConnection.addRequestProperty("User-Agent", 
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
				InputStream excelStream = excelConnection.getInputStream();
				Files.copy(excelStream, p, StandardCopyOption.REPLACE_EXISTING);
			}
			return target;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
