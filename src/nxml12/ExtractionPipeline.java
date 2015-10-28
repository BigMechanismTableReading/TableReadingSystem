package nxml12;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;

/**
 * Given a list of pmc ids and a directory will extract the .tar files for each ID to the given directory
 * @author sloates
 *
 */
public class ExtractionPipeline {
	
	static final String PMC_URL = "http://www.ncbi.nlm.nih.gov/pmc/?term=";
	
	public void get_nxml(String pmc_list, String directory_name){
		PmcTranslator  pmc = new PmcTranslator();
		ExtractFiles ext = new ExtractFiles();
		String file_name= pmc.translate(pmc_list);
		try {
			ext.getFiles(file_name, directory_name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args){
		String pmc_list = args[0];
		String directory_name = args[1];
		ExtractionPipeline p = new ExtractionPipeline();
		p.get_nxml(pmc_list,directory_name);
	}
}
