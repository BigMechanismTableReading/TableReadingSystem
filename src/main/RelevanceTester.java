package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import extract.Extractor;
import extract.types.Reaction;
import tableBuilder.TableBuf.Column;
import tableBuilder.TableBuf.Table;
import tableBuilder.TableWrapper;
import tablecontents.ColumnContents;
import utils.Pair;

//TODO: MAKE THIS A QUICK RELEVANCE TEST

public class RelevanceTester {

	public static void main(String[] args) {
		TableReader.init(args);
		for (Integer pmc_id: TableReader.pmc_ids){
			TableReader.writeToLog("Testing relevance " + pmc_id);
			//ArrayList<Table> tables = Extractor.getTables(pmc_id);
			System.out.println(pmc_id + ": " + isRelevant(pmc_id));
		}


	}
		
	//TODO: redo so you just go by table
	public static boolean isRelevant(Integer pmc_id){
		ArrayList<TableWrapper> tables = Extractor.getTables(pmc_id);
		for (TableWrapper table: tables){
			Pair<Reaction, HashMap<ColumnContents, List<Column>>> r = Extractor.determineRelevance(table);
			if (r!=null){
				return true;
			}
		}
		return false;
	}
		
	

}
