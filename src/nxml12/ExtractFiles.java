package nxml12;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ExtractFiles {


	/**
	 * Returns the text from the body and abstract tag of an nxml file.
	 * @param fileName
	 * @return
	 */
	public String getBodyText(String fileName){
		File file = new File(fileName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		String body_text = "";
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			Element art = (Element) doc.getElementsByTagName("article");
			Element body = (Element) art.getElementsByTagName("body");
			Element abs = (Element) art.getElementsByTagName("abstract");
			body_text = body.getTextContent()+abs.getTextContent();

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return body_text;
	}

	public File convertHTML(String fileName){
		File xml = new File(fileName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(xml);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.METHOD,"html");
			Source source = new DOMSource(doc);
			StreamResult result = new StreamResult(fileName.replaceAll("nxml","html"));
			t.transform(source, result);
			return new File(result.getSystemId());
			


		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Return a NodeList of tables. Returns null if not found.
	 * @param fileName
	 * @return
	 */
	public NodeList getTables(String fileName){
		File file = new File(fileName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		String []tables = null;
		try{
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			
			Element article = (Element)doc.getChildNodes().item(1);
			NodeList table_elements = article.getElementsByTagName("table");
			return table_elements;

		}catch(SAXException e){

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null ;
	}

	public List<String> getFileNames(String fileName){
		List<String> fileNames = new LinkedList<String>();

		File f = new File(fileName);
		Scanner s;

		try {
			s = new Scanner(f);
			while(s.hasNextLine()){
				fileNames.add(s.nextLine().trim());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileNames;
	}


//	public void getFiles(String fileName,String file_directory,String output_directory) throws IOException{
//		final int BUFFER = 2048;
//		List<String> fileNames = getFileNames(fileName);
//		FileInputStream tar_dir = new FileInputStream(file_directory);
//		BufferedInputStream buf = new BufferedInputStream(tar_dir);
//		GZIPInputStream gz = new GZIPInputStream(buf);
//		TarArchiveInputStream tar_in  = new TarArchiveInputStream(gz);
//		TarArchiveEntry entry = null;
//		while((entry = (TarArchiveEntry)tar_in.getNextEntry()) != null){
//			System.err.println(entry.getName());
//			if(entry.isDirectory() && fileNames.contains(entry.getName())){
//				File tar = new File(output_directory + entry.getName());
//
//			}else if (fileNames.contains(entry.getName())){
//				System.err.println("here");
//				int count;
//				byte data[] = new byte[BUFFER];
//				FileOutputStream fos = new FileOutputStream(output_directory);
//				BufferedOutputStream dest = new BufferedOutputStream(fos,
//						BUFFER);
//				while ((count = tar_in.read(data, 0, BUFFER)) != -1) {
//					dest.write(data, 0, count);
//				}
//				dest.close();
//			}
//		}
//		tar_in.close();
//	}


	public void getFiles(String file_name,String directory_name) throws IOException, URISyntaxException{
		String ftp_site = "ftp://ftp.ncbi.nlm.nih.gov/pub/pmc";
		PmcTranslator pmc = new PmcTranslator();
		String separator = "/";
		URL curr_url = null;
		URLConnection connect = null;
		BufferedReader input;
		File dir = new File(directory_name);
		dir.mkdir();
		String curr_file = null;
		for(String pmc_curr : pmc.pmcToName.keySet() ){
			curr_file = pmc.translate(pmc_curr);
			if(curr_file != null){
				System.out.println(curr_file);
				String file_site = ftp_site+separator+curr_file;
				System.out.println(file_site);
				curr_url = new URL(file_site);
				FileUtils.copyURLToFile(curr_url,new File(dir + File.separator + pmc_curr + ".tar.gz"));

			}
		}

	}
	public static void main (String[]args){
		ExtractFiles file_extractor = new ExtractFiles();
		try {
			file_extractor.getFiles("translated_corpus.txt","temp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

