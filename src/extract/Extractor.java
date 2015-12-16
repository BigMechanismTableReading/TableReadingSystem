package extract;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import main.TableReader;
import tableBuilder.TableBuf.Table;
import tableBuilder.TableBuilder;


public class Extractor {
	/**
	 * Begins extraction based on pmcids
	 * @param pmc_ids
	 */
	public void extractFromList(ArrayList<Integer> pmc_ids){
		File table_dir = new File(TableReader.files);
		for (Integer pmc_id: pmc_ids){
			TableReader.writeToLog("Extracting " + pmc_id);
			extractFromID(pmc_id);
		}
		
	}
	
	public File [] getFiles(File dir, Integer pmc_id, String[] extensions){
		File [] files = dir.listFiles(new FileFilter(){
			@Override
			public boolean accept(File arg0) {
				boolean pmc = arg0.getName().startsWith("PMC" + pmc_id);
				if (pmc){
					for (String ext: extensions){
						if (arg0.getName().endsWith(ext)){
							return true;
						}
					}
				}
				return false;
			}	
		});
		return files;
	}
	
	public File [] getFiles(File dir, Integer pmc_id, String extension){
		return getFiles(dir, pmc_id, new String[] {extension});
	}
	
	
	protected boolean test(File arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void extractFromID(Integer pmc_id){
		try {
			File [] tables = getFiles(new File(TableReader.tables), pmc_id, ".pb");//protobuf files
			if (tables.length==0){
					File [] files = getFiles(new File(TableReader.files), pmc_id, new String[] {".html",".xls", ".xlsx"});
					if (files.length > 0){
						for (File file: files){
							List<Table> table_list= TableBuilder.buildTable(file, pmc_id.toString());
							for(Table t : table_list){
								if (t!=null){
								//extract(t,w,extr,fileName,simple_reaction);
								}
							}
						}
					}
					else{
						System.err.println("Error: can't find an html for " + pmc_id);
						TableReader.writeToLog("Error: can't find an html for " + pmc_id);
					}

			}
			else{
				//tables.forEach(table -> );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}



}
