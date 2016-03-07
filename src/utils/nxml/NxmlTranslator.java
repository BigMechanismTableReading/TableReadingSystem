package utils.nxml;

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
	 *Translates tables into individual html tables and puts them into the file_dir
	 * @param file_name
	 * @param table_dir
	 * @param pmc 
	 * @throws IOException
	 */
	public static void translateTables(File file,String file_dir,String paper_dir, Integer pmc) throws  IOException{
		String sep = File.separator;
		ExtractFiles ext = new ExtractFiles();
		File html = ext.convertHTML(file ,paper_dir, pmc+"");
		if (html!=null){
			Document doc = Jsoup.parse(html,"UTF-8");
			Elements table_wrap = doc.getElementsByTag("table-wrap");
			int count = 1;
			FileWriter w;
			for(Element e : table_wrap){
				String id = e.attr("id");
				if (id==null){
					id = "T" + count;
				}
				File tab = new File(file_dir + sep + "PMC" + pmc + id +".html");
				tab.canWrite();
				w = new FileWriter(tab);
				w.write(e.outerHtml());
				w.close();
				count++;
			}
		}
		
	}
	
	public static void main(String[]args){
		/*NxmlTranslator test_trans = new NxmlTranslator();
			try {
				test_trans.translateTables("e700m116.nxml","output_test", null, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
	}
}
