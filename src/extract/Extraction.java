package extract;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.ParticipantA;
import tablecontents.Protein;
import utils.Pair;
import tableBuilder.TableBuf;
import tableBuilder.TableWrapper;
import tableBuilder.TableBuf.Table;
import extract.types.Reaction;
import extract.write.IndexCard;
import extract.write.IndexCardWriter;
import main.TableReader;

/**
 * Extracts index card information from a table
 * @author sloates, vhsiao
 *
 */
public class Extraction {
	
	/**
	 * Gets all participantBs from the table
	 * Returns a pair of hashmaps, 
	 * pair.getA has translated B
	 * pair.getB has untranslated B
	 * @param contents
	 * @return
	 */
	private Pair<HashMap<Integer,String>, HashMap<Integer,String>> getAllParticipantB(HashMap<ColumnContents, List<TableBuf.Column>> contents){
		TableBuf.Column col = null;
		ColumnContents protein = null;
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
					if(ground.getB() != null) {
						partB.put(row, ground.getB());
					}
					partBuntrans.put(row, ground.getA());
				}
				row++;
			}
		}
		return new Pair<HashMap<Integer,String>, HashMap<Integer,String>>(partB, partBuntrans);
	}
	
	/**
	 * Returns any columns labeled Fold
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
	 * @param possibleA 
	 */
	private void makeIdx(List<IndexCard> cards, String readingStart,Table table, HashMap<String, String> possibleA){
		String readingEnd = new Date(System.currentTimeMillis()).toString();
		IndexCardWriter w = new IndexCardWriter();
		for (IndexCard card : cards){
			w.writeIndexCard(readingStart, readingEnd, table, card, possibleA);
		}
	}
	
	/**
	 * Creates s list of index cards from the labeled columns
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
		int count = 0;
		boolean simple_reaction = TableReader.simple_reaction;
		while(iter.hasNext()){
			int i = iter.next();
			IndexCard card = new IndexCard(r, partB.get(i), partBuntrans.get(i),i);
			for (ColumnContents content : cols){
				if(!(content instanceof Protein)){
					card.addInfo(content.extractData(contents.get(content), i));
				}
			}
			
			//First get the site/sequence column, then do fold
			for(ParticipantA entry: participantACols){	
				IndexCard dupl = new IndexCard(card);
				if (dupl.addPartA(entry,i)){
					cards.add(dupl);
				}else if(simple_reaction){
					IndexCard new_card = new IndexCard(card);
					new_card.addPartA(entry,i);
					cards.add(new_card);
				}
			}
			
		}
		System.err.println(cards.size() + " cards");
		return cards;
		
	}
	
	/**
	 * Extracts information from the table and text 
	 * and writes the information to index cards
	 * @param colInfo
	 * @param table
	 * @param simple_reaction 
	 */
	public void ExtractInfo(Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> colInfo,
							TableWrapper tableWrap){
		String readingStart = new Date(System.currentTimeMillis()).toString();

		Table table = tableWrap.getTable();
		Reaction r = colInfo.getA();
		HashMap<ColumnContents,List<TableBuf.Column>> contents = colInfo.getB();
		//First HashMap is the translated participant B and the Second HashMap is the untranslated participant B
		Pair<HashMap<Integer, String>, HashMap<Integer, String>> partBinfo = getAllParticipantB(contents);
		HashMap<Integer, String> partB = partBinfo.getA();
		HashMap<Integer, String> partBuntrans = partBinfo.getB();
		ParticipantAExtractor partA = new ParticipantAExtractor();
		List<ParticipantA> participantACols= partA.getParticipantAs(tableWrap,partB,partBuntrans,foldContents(contents), r);
		//TODO run the rest of the table, first choosing fold then going through the table
		//System.out.println(participantACols.size() + " " + participantACols.get(0).getUntranslatedName());
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
		System.out.println("Printing index cards");
		List<IndexCard> cards = getCards( r, participantACols, partB, partBuntrans, cols, contents);
		makeIdx(cards, readingStart, table, partA.possibleA);
		
	}
}
