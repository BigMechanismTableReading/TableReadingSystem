package extract.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.ParticipantA;
import tablecontents.Protein;
import extract.TextExtractor;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.lookup.AbbreviationLookup;
import extract.lookup.ChemicalLookup;
import extract.lookup.TabLookup;
import extract.types.Reaction;
import extract.write.IndexCard;

public class Extraction {
	
	/**
	 * Gets all participantB 
	 * @param contents
	 * @return
	 */
	private Pair<HashMap<Integer,String>, HashMap<Integer,String>> getAllParticipantB(HashMap<ColumnContents, List<TableBuf.Column>> contents){
		TableBuf.Column col = null;
		ColumnContents protein = null;//TODO error is here
		for(ColumnContents c : contents.keySet()){
			if(c instanceof Protein && protein == null){
				col = contents.get(c).get(0);
				protein = c;
			}
		}
		HashMap<Integer,String> partB = new HashMap<Integer,String>();
		HashMap<Integer,String> partBuntrans = new HashMap<Integer,String>();
		int row = 0;
		if(col != null){
			while(row < col.getDataCount()){
				Pair<String, String> ground = protein.bestColumn(contents, row);
				if(ground != null){
					partB.put(row, ground.getB());
					partBuntrans.put(row, ground.getA());
				}
				row++;
			}
		}
		return new Pair<HashMap<Integer,String>, HashMap<Integer,String>>(partB, partBuntrans);
	}
	
	private HashMap<ColumnContents, List<TableBuf.Column>> foldContents(
			HashMap<ColumnContents, List<TableBuf.Column>> contents) {
		HashMap<ColumnContents, List<TableBuf.Column>> foldCols = new HashMap<ColumnContents, List<TableBuf.Column>>();
		for (ColumnContents f : contents.keySet()) {
			if (f instanceof Fold) {
				foldCols.put(f, contents.get(f));
			}
		}
		return foldCols;
	}
	public void ExtractInfo(Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> colInfo,
							TableBuf.Table table){
			
		Reaction r = colInfo.getA();
		HashMap<ColumnContents,List<TableBuf.Column>> contents = colInfo.getB();
		Pair<HashMap<Integer, String>, HashMap<Integer, String>> partBinfo = getAllParticipantB(contents);
		HashMap<Integer, String> partB = partBinfo.getA();
		HashMap<Integer, String> partBuntrans = partBinfo.getB();
		ParticipantAExtractor partA = new ParticipantAExtractor();
		List<ParticipantA> participantACols= partA.getParticipantAs(table,partB,foldContents(contents), r);
		//TODO run the rest of the table, first choosing fold then going through the table
		List<ColumnContents> cols = new ArrayList<ColumnContents>();
		for(Class<? extends ColumnContents> c : r.getRequiredColumns()){
			if (!(c == Fold.class)) {
				for (ColumnContents a : contents.keySet()){
					if (c.isAssignableFrom(a.getClass())){
						cols.add(a);
					}
				}
				if(cols.isEmpty()){
					for(List<Class<? extends ColumnContents>> list : r.getAlternatives(c)){
						for(Class<? extends ColumnContents> alts : list){
							for (ColumnContents a : contents.keySet()){
								if (alts.isAssignableFrom(a.getClass())){
									cols.add(a);
								}
							}
						}
					}
				}
			}
		}
		//Go through the other columns storing all the information
		List<IndexCard> cards = new LinkedList<IndexCard>();
		Iterator<Integer> iter = partB.keySet().iterator();
		while(iter.hasNext()){
			int i = iter.next();
			IndexCard card = new IndexCard(r, partB.get(i), partBuntrans.get(i));
			for (ColumnContents content : cols){
				card.addInfo(content.extractData(contents.get(content), i));
			}
			//First get the site/sequence column, then do fold
			for(ParticipantA entry: participantACols){
				IndexCard dupl = new IndexCard(card);
				dupl.addPartA(entry);
				cards.add(dupl);
			}
		}
		
		for (IndexCard card : cards){
			printIndexCard(card);
		}
		
	}
}
