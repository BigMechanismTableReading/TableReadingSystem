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
		File papers = new File("papers");
		if (!papers.exists() || !papers.isDirectory()){
			papers.mkdir();
		}
		File files = new File("files");
		if (!files.exists() || !files.isDirectory()){
			files.mkdir();
		}
		if (args.length==1){
		
			File tarDir = new File(args[0]);
			if (tarDir.isDirectory()){ 	//just unwrap all of the .tar.gz
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
			else if (tarDir.getName().endsWith(".tar.gz")){
				try {
					extractFiles(tarDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			System.err.println("Requires one input (directory of .tar.gz files or a single .tar.gz file)");
		}

	}
	
	
	public static void toHTML(File file) throws  IOException{
		String sep = File.separator;
		ExtractFiles ext = new ExtractFiles();
		File html = ext.convertHTML(file ,"papers", file.getName().replace(".nxml", ""));

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
				File tab = new File("files" + sep + file.getName().replace(".nxml", "") + id +".html");
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
			if (file_in_tar.endsWith(".nxml")){
				File outputFile = new File("temp_nxml");
				outputFile.mkdir();
				File f = new File(outputFile, tar.getName().replace(".tar.gz", ".nxml"));
				f.createNewFile();
				System.out.println("writing nxml: " + f.getAbsolutePath());
	            byte [] btoRead = new byte[1024];
	            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(f));
	            int len = 0;
	            while((len = tarIn.read(btoRead)) != -1){
			                bout.write(btoRead,0,len);
			     }
	            bout.close();
				
	            //feeding it a .nxml name
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
		
		tar.delete();
		System.out.print("Done.");
	}
		// TODO Auto-generated method stub
		
	

}
