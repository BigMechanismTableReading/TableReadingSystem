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
import utils.Utils;


public class Extractor {
	/**
	 * Begins extraction based on pmcids
	 * @param pmc_ids
	 */
	public static void extractFromList(ArrayList<Integer> pmc_ids){
		for (Integer pmc_id: pmc_ids){
			TableReader.writeToLog("Extracting " + pmc_id);
			extractFromID(pmc_id);
		}
		
	}
	

	


	public static void extractFromID(Integer pmc_id){
		File [] tables = Utils.getFiles(new File(TableReader.tables), pmc_id, ".pb");//protobuf files
		if (tables.length==0){
			File [] html = Utils.getFiles(new File(TableReader.papers), pmc_id, ".html");//protobuf files
				//File [] files = getFiles(new File(TableReader.files), pmc_id, new String[] {".html",".xls", ".xlsx"});
				if (html.length > 0){
					for (File file: html){
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

		
	}



}
