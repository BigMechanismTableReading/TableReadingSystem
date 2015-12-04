package nxml12;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Given a list of pmc ids and a directory will extract the .tar files for each ID to the given directory
 * @author sloates
 *
 */
public class ExtractionPipeline {
	
	static final String PMC_URL = "http://www.ncbi.nlm.nih.gov/pmc/?term=";
	
	public LinkedList<String> get_nxml(String pmc_list, String directory_name, String resolve_file){
		System.out.println("pmc list: " + pmc_list);
		PmcTranslator  pmc = new PmcTranslator(resolve_file);
		ExtractFiles ext = new ExtractFiles();
		String file_name= pmc.translate(pmc_list);
		try {
			LinkedList<String> files = ext.getFiles(file_name, directory_name, resolve_file);
			return files;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	

	
	public static void main(String[] args){
		String pmc_list = args[0];
		String directory_name = args[1];
		ExtractionPipeline p = new ExtractionPipeline();
		//p.get_nxml(pmc_list,directory_name);
	}
}
