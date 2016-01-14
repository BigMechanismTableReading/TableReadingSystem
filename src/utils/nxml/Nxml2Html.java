package utils.nxml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.codehaus.plexus.archiver.commonscompress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.archiver.commonscompress.archivers.tar.TarArchiveInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Nxml2Html {

	public static void main(String[] args) {
		//args[2] is the .tar.gz directory
		//args[1] is pmcid list
		if (args.length > 1){
			File f = new File(args[1]);
			if (!f.exists()){
				System.err.println(args[1] + " does not exist");
			}
			else{
				try {
					ArrayList<String> pmc_ids = new ArrayList<String>();
					BufferedReader r  = new BufferedReader(new FileReader(f));
					String line = r.readLine();
					while (line!=null){
						//if (line.startsWith("PMC")){
							//line = line.substring(3);
							
						//}
						pmc_ids.add(line);
						line = r.readLine();
						//Integer pmc_id = Integer.parseInt(line);
					}
					r.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
		else{
			//just unwrap all of the .tar.gz
			File tarDir = new File(args[0]);
			File [] tars = tarDir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File arg0) {
					return arg0.getName().endsWith(".tar.gz");
						
					
				}
				
			});
			if (tars.length ==0){
				System.err.println(args[0] + " did not have tars in it");
			}
			else{
				try{
					for (File tar: tars){
						System.out.println("extracting file: " + tar.getName());
						extractFiles(tar);
					}
				}
				catch (IOException e){
					e.printStackTrace();
				}
			}
			
		}

	}
	
	
	public static void toHTML(File file) throws  IOException{
		String sep = File.separator;
		ExtractFiles ext = new ExtractFiles();
		File html = ext.convertHTML(file ,"papers", file.getName().replace(".utils.nxml", ""));

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
				File tab = new File("files" + sep + file.getName().replace(".utils.nxml", "") + id +".html");
				tab.canWrite();
				w = new FileWriter(tab);
				w.write(e.outerHtml());
				w.close();
				count++;
			}
		}
		
	}

	private static void extractFiles(File tar) throws IOException {
		BufferedInputStream fin = new BufferedInputStream(new FileInputStream(tar));
		GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fin);
		TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);
		TarArchiveEntry entry = tarIn.getNextTarEntry();
		while (entry!=null){
			String file_in_tar = entry.getName().toLowerCase();
			//System.out.println(file_in_tar);
			//FileUtils.copyFileToDirectory(new File("TEST"), outputDir);
			/*if (entry.getFile() == null){
				System.err.println("NULL FILE: " + entry.getName());
			}*/
			if (file_in_tar.endsWith("utils.nxml")){
				File outputFile = new File("temp_nxml");
				outputFile.mkdir();
				File f = new File(outputFile, tar.getName().replace(".tar.gz", ".utils.nxml"));
				f.createNewFile();
				System.out.println("writing utils.nxml: " + f.getAbsolutePath());
	            byte [] btoRead = new byte[1024];
	            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(f));
	            int len = 0;
	            while((len = tarIn.read(btoRead)) != -1){
			                bout.write(btoRead,0,len);
			     }
	            bout.close();
				
	            //feeding it a .utils.nxml name
				toHTML(f);

				
			}
			else if (file_in_tar.contains(".xls")){
				File outputFile = new File("files");
				String name =  entry.getName().substring(entry.getName().lastIndexOf("/"));
				File f = new File(outputFile,name);
				f.createNewFile();
	            byte [] btoRead = new byte[1024];
	            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(f));
	            int len = 0;
	            while((len = tarIn.read(btoRead)) != -1){
			                bout.write(btoRead,0,len);
			     }
	            bout.flush();
	            bout.close();
	            Path source = f.toPath();
	           // toHTML(new File(outputFile, tar.getName().replace(".tar.gz", "") + f.getName()));
	            Files.move(source, source.resolveSibling(tar.getName().replace(".tar.gz", "") + f.getName()), StandardCopyOption.REPLACE_EXISTING);

			}
			try {
			entry = tarIn.getNextTarEntry();
			} catch (EOFException e){
				break;
			}
		
		}
		tarIn.close();
		fin.close();
		gzIn.close();
		
		System.out.print("Done.");
	}
		// TODO Auto-generated method stub
		
	

}
