package nxml12;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NxmlTranslator {
	
	/**
	 *Translates tables into individual html tables
	 * @param file_name
	 * @param table_dir
	 * @param pmc 
	 * @throws IOException
	 */
	public void translateTables(String file_name,String table_dir,String paper_dir, Integer pmc) throws  IOException{
		String sep = File.separator;
		ExtractFiles ext = new ExtractFiles();
		File html = ext.convertHTML(file_name,paper_dir, pmc+"");
		Document doc = Jsoup.parse(html,"UTF-8");
		Elements table_wrap = doc.getElementsByTag("table-wrap");
		int count = 1;
		FileWriter w;
		for(Element e : table_wrap){
			File tab = new File(table_dir + sep + pmc +count +".html");
			tab.canWrite();
			w = new FileWriter(tab);
			w.write(e.outerHtml());
			w.close();
			count++;
		}
		
	}
	
	public static void main(String[]args){
		NxmlTranslator test_trans = new NxmlTranslator();
			try {
				test_trans.translateTables("e700m116.nxml","output_test", null, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
