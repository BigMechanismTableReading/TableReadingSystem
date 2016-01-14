package utils.nxml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
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

	public File convertHTML(File xml, String paper_dir,String pmc){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//Needed to comment out so that it does not resolve DTDs and ENTs
		//dbf.setNamespaceAware(true);
		//dbf.setValidating(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			//entity resolver needs to return empty input source so that it does not try to resolve DTDs and ENT
			db.setEntityResolver(new EntityResolver(){

				@Override
				public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
					// TODO Auto-generated method stub
					return new InputSource(new StringReader(""));
				}
				
			});
			Document doc = db.parse(xml);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.METHOD,"html");
			Source source = new DOMSource(doc);
			File f = new File(paper_dir, pmc + ".html");
			StreamResult result = new StreamResult(pmc + ".html");
			t.transform(source, result);
			File resultFile = new File(result.getSystemId());
			
			File paperDir = new File(paper_dir);
		    Path returnFile = Files.move(resultFile.toPath(), paperDir.toPath().resolve(resultFile.toPath().getFileName()), StandardCopyOption.REPLACE_EXISTING);

			return returnFile.toFile();
		} catch (FileNotFoundException e){
			System.err.println("Cannot transform to html: " + xml.getAbsolutePath());
			System.err.println("Missing: " + e.getMessage());
			//e.printStackTrace();

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
	
	
	public LinkedList<String> getFiles(String file_name,String directory_name, String resolve_file) throws IOException, URISyntaxException{
		String ftp_site = "ftp://ftp.ncbi.nlm.nih.gov/pub/pmc";
		PmcTranslator pmc = new PmcTranslator(resolve_file);
		String separator = "/";
		URL curr_url = null;
		File dir = new File(directory_name);
		LinkedList<String> file_list = new LinkedList<String>();
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
				file_list.add(dir + File.separator + pmc_curr + ".tar.gz");
			}
		}
		return file_list;
	}
	public static void main (String[]args){
		/*ExtractFiles file_extractor = new ExtractFiles();
		try {
			file_extractor.getFiles("translated_corpus.txt","temp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}

