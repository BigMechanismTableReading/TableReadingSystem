package extract.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.GeneName;
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
import extract.write.IndexCardWriter;

/**
 * Extracts index card information from a table
 * @author sloates vincenth
 *
 */
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
	
	/**
	 * Returns a columns labeled with a subtype of Fold
	 * @param contents
	 * @return
	 */
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
	
	/**
	 * Writes list of index cards to json files
	 * @param cards
	 * @param readingStart
	 * @param table
	 */
	private void makeIdx(List<IndexCard> cards, String readingStart,TableBuf.Table table){
		String readingEnd = new Date(System.currentTimeMillis()).toString();
		IndexCardWriter w = new IndexCardWriter();
		for (IndexCard card : cards){
			w.writeIndexCard(readingStart, readingEnd, table, card);
		}
	}
	
	/**
	 * Creates s list of index cards
	 * @param r
	 * @param participantACols
	 * @param partB
	 * @param partBuntrans
	 * @param cols
	 * @param contents
	 * @return
	 */
	private List<IndexCard> getCards(Reaction r,List<ParticipantA> participantACols,
			HashMap<Integer, String> partB,
			HashMap<Integer, String> partBuntrans,List<ColumnContents> cols,
			HashMap<ColumnContents,List<TableBuf.Column>> contents){
		
		List<IndexCard> cards = new LinkedList<IndexCard>();
		Iterator<Integer> iter = partB.keySet().iterator();
		while(iter.hasNext()){
			int i = iter.next();
			IndexCard card = new IndexCard(r, partB.get(i), partBuntrans.get(i),i);
			for (ColumnContents content : cols){
				card.addInfo(content.extractData(contents.get(content), i));
			}
			//First get the site/sequence column, then do fold
			for(ParticipantA entry: participantACols){
				IndexCard dupl = new IndexCard(card);
				if (dupl.addPartA(entry,i)){
					cards.add(dupl);
				}
			}
		}
		return cards;
		
	}
	
	/**
	 * Extracts information and writes it to index cards
	 * @param colInfo
	 * @param table
	 */
	public void ExtractInfo(Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> colInfo,
							TableBuf.Table table){
			
		String readingStart = new Date(System.currentTimeMillis()).toString();
		Reaction r = colInfo.getA();
		HashMap<ColumnContents,List<TableBuf.Column>> contents = colInfo.getB();
		Pair<HashMap<Integer, String>, HashMap<Integer, String>> partBinfo = getAllParticipantB(contents);
		HashMap<Integer, String> partB = partBinfo.getA();
		HashMap<Integer, String> partBuntrans = partBinfo.getB();
		ParticipantAExtractor partA = new ParticipantAExtractor();
		List<ParticipantA> participantACols= partA.getParticipantAs(table,partB,partBuntrans,foldContents(contents), r);
		//TODO run the rest of the table, first choosing fold then going through the table
		System.out.println(participantACols.size() + " " + participantACols.get(0).getUntranslatedName());
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
		List<IndexCard> cards = getCards( r, participantACols, partB, partBuntrans, cols, contents);
		makeIdx(cards, readingStart, table);
		
	}
}
