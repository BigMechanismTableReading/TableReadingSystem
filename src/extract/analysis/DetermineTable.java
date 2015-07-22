package extract.analysis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import tablecontents.ColumnContents;
import tablecontents.DynamicTyping;
import tablecontents.Protein;
import extract.TextExtractor;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Column;
import extract.lookup.SpeciesChecker;
import extract.types.Reaction;

/**
 * Determines whether or not a table is relevant
 * @author sloates
 *
 */
public class DetermineTable {
	
	/**
	 * Identifies any columns that have potential participantB
	 * @param table
	 * @param partB
	 * @param labels
	 * @return
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
	 * @return
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
	 * Pipeline that determines whether a table is relevant and what the table indicates
	 * Returns a pair containing the interaction type and the ColumnTypes mapped to a list of columns
	 * @param table
	 */
	public Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> determine(TableBuf.Table table){
		ParticipantB  partB = new ParticipantB();
		HashMap<ColumnContents,List<TableBuf.Column>> labels = new HashMap<ColumnContents,List<TableBuf.Column>>();
		List<Reaction> possibleReactions = TextExtractor.getPossibleReactions(table.getSource().getPmcId().substring(3));
		Set<Reaction> tableReactions = getTableReactions(table,possibleReactions);
		if(tableReactions.size() > 0){
			possibleReactions.clear();
			possibleReactions.addAll(tableReactions);
		}
		
		if(!possibleReactions.isEmpty() && assignB(table,partB,labels)){	
			HashSet<Class<? extends ColumnContents>> requiredContents = new HashSet<Class<? extends ColumnContents>>();
			for (Reaction r : possibleReactions) {
				requiredContents.addAll(r.getRequiredColumns());
				requiredContents.addAll(r.getAllAlternatives());
			}
			HashSet<Class<? extends ColumnContents>> tableColumns = getTableColumns(requiredContents,labels,table);
			for (Reaction r : possibleReactions) {
				if (containsAllRequired(r, tableColumns)){
					HashSet<Class<? extends ColumnContents>> optionalContents = new HashSet<Class<? extends ColumnContents>>();
					optionalContents.addAll(r.getOptionalColumns());
					getTableColumns(optionalContents,labels,table);
					return new Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>>(r,labels);
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks to see that all the required contents (or their alternatives) are their for a specific reaction
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
	 * Labels the columns of the tables
	 * @param c
	 * @param data
	 * @param table
	 * @return
	 */
	private boolean labelTable(ColumnContents c, HashMap<ColumnContents,List<TableBuf.Column>> data, TableBuf.Table table) {
		int confidenceLevel = c.getCellConfNeeded();
		boolean both = c.needsBoth();
		boolean head = false;
		for (TableBuf.Column col : table.getColumnList()){
			int correctCells = 0;
			if(c.headerMatch(col.getHeader().getData()) != null){
				if(!both){
					addToData(c, col, data);
					return true;
				}
				head = true;
			}
			if (!both || head) {
				for (int i = 0; i < 10 && i < col.getDataCount(); i++) {
					if (c.cellMatch(col.getData(i).getData()) != null){
						correctCells++;
						System.out.println(c + "  " + correctCells);
						if(correctCells > confidenceLevel){
							addToData(c, col, data);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Helper method for labeling the columns
	 * @param c
	 * @param col
	 * @param data
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
