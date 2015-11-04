package extract.analysis;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tablecontents.ColumnContents;
import tablecontents.DynamicTyping;
import tablecontents.Protein;
import extract.TextExtractor;
import extract.buffer.TableBuf;
import extract.types.PossibleReaction;
import extract.types.Reaction;
import extract.types.RelaxedReaction;

/**
 * Determines whether or not a table is relevant and labels the columns with possible content type
 * @author sloates, vhsiao
 *
 */
public class DetermineTable {

	/**
	 * Identifies and labels any columns that have potential participantBs
	 * @param table
	 * @param partB
	 * @param labels
	 * @return false if there is no participantB column
	 */
	private boolean assignB(TableBuf.Table table, ParticipantB partB, HashMap<ColumnContents, List<TableBuf.Column>> labels){
		boolean hasProt = false;
		for(TableBuf.Column col : table.getColumnList()){
			ColumnContents p = partB.hasParticipantB(col);
			if(p instanceof Protein){
				addToData(p, col, labels);
				hasProt = true;
			}
		}
		return hasProt;
	}

	/**
	 * Takes in the required classes for the table to be recognized as a reaction type.
	 * Labels the table by columncontents
	 * Returns a HashSet of ColumnContents
	 * @param requiredContents
	 * @param labels
	 * @param table
	 * @return HashSet of column contents found in the table
	 */
	private HashSet<Class<? extends ColumnContents>> getTableColumns(HashSet<Class<? extends ColumnContents>> requiredContents,
			HashMap<ColumnContents,List<TableBuf.Column>> labels,TableBuf.Table table){
		HashSet<Class<? extends ColumnContents>> tableColumns = new HashSet<Class<? extends ColumnContents>>();
		try {
			for (Class<? extends ColumnContents> columnType : requiredContents){
				if (Modifier.isAbstract( columnType.getModifiers())){
					boolean columnTypeExists = false;
					for (Class<? extends ColumnContents> subType : DynamicTyping.getInstance().getSubTypesOf(columnType)){
						Method m = subType.getMethod("getInstance");
						ColumnContents columnContentType = (ColumnContents) m.invoke(null);
						if (labelTable(columnContentType, labels, table)) {
							columnTypeExists = true;
						}
					}
					if(columnTypeExists) {
						tableColumns.add(columnType);
					}
				} else {
					Method m = columnType.getMethod("getInstance");
					ColumnContents columnContentType = (ColumnContents) m.invoke(null);
					if (labelTable(columnContentType, labels, table)) {
						tableColumns.add(columnType);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return tableColumns;
	}


	/**
	 * Gets the reactions mentioned in the table
	 * @param table
	 * @return
	 */
	private Set<Reaction> getTableReactions(TableBuf.Table table,List<Reaction> reactList){
		Set<Reaction> reactions = new HashSet<Reaction>();
		for(String s : table.getCaptionList()){
			for(Reaction r : reactList){
				for(String base : r.getConjugationBase()){
					for(String w : s.split("\\s")){	
						if(w.contains(base)){
							reactions.add(r);
						}
					}
				}
			}
			for(Reaction r : reactions){
				reactList.remove(r);
			}
		}
		return reactions;
	}

	/**
	 * Chooses the reaction most likely in the table if more then one reaction is found.
	 * @param reactionList
	 * @param table
	 * @return
	 */
	private Reaction chooseBestReaction(List<Reaction> reactionList,TableBuf.Table table){
		Reaction choice = reactionList.get(0);
		String header = "";
		if(table.getCaptionList().size() > 0)
			header = table.getCaption(0);
		for(Reaction r : reactionList ){
			for(String word : header.split("\\s")){ 
				for(String cb : r.getConjugationBase()){
					if (word.toLowerCase().contains(cb) && r != PossibleReaction.getInstance())
						return r;
				}
			}
		}
		return choice;
	}

	/**
	 * Checks the caption for key_words indicating interactions
	 * @param table
	 * @param key_words
	 * @return
	 */
	private boolean check_caption(TableBuf.Table table, String [] key_words){
		for(String s: table.getCaptionList()){
			for(String key : key_words)
				if(s.contains(key)){
					return true;
				}
		}
	
	return false;
	}

/**
 * Determines whether a table is relevant and what the table indicates
 * Returns a pair containing the reaction type and a hashmap of ColumnContents to a list of columns with those contents
 * @param table
 */
public Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> determine(TableBuf.Table table,boolean relaxed){
	System.out.println("Starting determine" + table.getSource().getSourceFile());
	ParticipantB  partB = new ParticipantB();
	HashMap<ColumnContents,List<TableBuf.Column>> labels = new HashMap<ColumnContents,List<TableBuf.Column>>();
	List<Reaction> possibleReactions = TextExtractor.getPossibleReactions(table.getSource().getPmcId().substring(3));
	Set<Reaction> tableReactions = getTableReactions(table,possibleReactions);
	if(tableReactions.size() > 0){
		possibleReactions.clear();
		possibleReactions.addAll(tableReactions);
		possibleReactions.add(PossibleReaction.getInstance());
	}

	//Allows relaxed requirements to be used if the user chooses to use them
	if(relaxed){
		Reaction.useRelaxedReactions();
		//key_words can be modified, these are all action words that have the potential for modifications
		String [] key_words = new String[]{"ation","ed","ing","phos","meth","ubiq"};
		if(check_caption(table,key_words)){
			possibleReactions.add(RelaxedReaction.getInstance());
		}
	}

	if(!possibleReactions.isEmpty()){
		System.out.println("Possibly relevant");
		HashSet<Class<? extends ColumnContents>> requiredContents = new HashSet<Class<? extends ColumnContents>>();
		for (Reaction r : possibleReactions) {
			requiredContents.addAll(r.getRequiredColumns());
			requiredContents.addAll(r.getAllAlternatives());
		}
		System.out.println(requiredContents);

		HashSet<Class<? extends ColumnContents>> tableColumns = getTableColumns(requiredContents,labels,table);
		List<Reaction> goodReactions = new LinkedList<Reaction>();
		for (Reaction r : possibleReactions) {
			if (containsAllRequired(r, tableColumns)){
				HashSet<Class<? extends ColumnContents>> optionalContents = new HashSet<Class<? extends ColumnContents>>();
				optionalContents.addAll(r.getOptionalColumns());
				getTableColumns(optionalContents,labels,table);
				goodReactions.add(r);
			}
		}
		System.out.println("DetermineTable Done " + labels.keySet());
		if(goodReactions.size() == 1)
			return new Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>>(goodReactions.get(0),labels);
		else if (goodReactions.size() > 1)
			return new Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>>(chooseBestReaction(goodReactions,table),labels);
	}
	return null;
}

/**
 * Checks to see that all the required contents (or their alternatives) are found in the table
 * @param r
 * @param tc
 * @return
 */
private boolean containsAllRequired(Reaction r, HashSet<Class<? extends ColumnContents>> tc){
	List<Class<? extends ColumnContents>> required = r.getRequiredColumns();
	for (Class<? extends ColumnContents> requiredType : required){
		if (!tc.contains(requiredType)){
			if (r.hasAlternative(requiredType)){
				boolean alternative = false;
				for (List<Class<? extends ColumnContents>> alternativeset : r.getAlternatives(requiredType)){
					if (tc.containsAll(alternativeset)){
						alternative = true;
					}
				}
				if(!alternative){
					return false;
				}
			} else { 
				return false;
			}
		}
	}
	return true;
}

/**
 * Labels the columns of the tables with any of the column contents that are needed by the reactions.
 * @param c
 * @param data
 * @param table
 * @return
 */
private boolean labelTable(ColumnContents c, HashMap<ColumnContents,List<TableBuf.Column>> data, TableBuf.Table table) {
	int confidenceLevel = c.getCellConfNeeded();
	boolean both = c.needsBoth();
	boolean hasCol = false;
	for (TableBuf.Column col : table.getColumnList()){
		int correctCells = 0;
		boolean head = false;
		if(c.headerMatch(col.getHeader().getData()) != null){	

			if(!both){
				addToData(c, col, data);
				hasCol =  true;
			} 
			head = true;
		}
		if ((both && head) || (!both && !hasCol)) {
			boolean over10 = true;
			if(col.getDataCount() < 10)
				over10 = false;
			for (int i = 0; i < 10 && i < col.getDataCount(); i++) {
				if (c.cellMatch(col.getData(i).getData()) != null){
					correctCells++;
					if(correctCells > confidenceLevel){
						addToData(c, col, data);
						hasCol =  true;
						i = 10;
					}else if (over10 == false){
						addToData(c, col, data);
						hasCol =  true;
						i = 10;
					}
				}
			}
		}
	}
	return hasCol;
}

/**
 * Helper method for labeling the columns
 * @param c
 * @param col
 * @param data - ColumnContents to List of Table Buf columns, this labels the table
 */
private void addToData(ColumnContents c, TableBuf.Column col, HashMap<ColumnContents,List<TableBuf.Column>> data){
	if (data.containsKey(c)){
		data.get(c).add(col);
	} else {
		LinkedList<TableBuf.Column> list = new LinkedList<TableBuf.Column>();
		list.add(col);
		data.put(c, list);
	}
}
}
