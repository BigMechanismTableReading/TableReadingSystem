package extract.analysis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import columncontents.ColumnContents;
import columncontents.DynamicTyping;
import columncontents.Protein;
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
			Protein p = partB.hasParticipantB(col);
			if(p instanceof Protein){
				addToData(p, col, labels);
				hasProt = true;
			}
		}
		return hasProt;
	}
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
	 * Pipeline that determines whether a table is relevant and what the table indicates
	 * Returns a pair containing the interaction type and the ColumnTypes mapped to a list of columns
	 * @param table
	 */
	public Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>> determine(TableBuf.Table table){
		//Checks to make sure that participantB is in the table, setting columnLabels as it iterates through
		ParticipantB  partB = new ParticipantB();
		HashMap<ColumnContents,List<TableBuf.Column>> labels = new HashMap<ColumnContents,List<TableBuf.Column>>();
		if(assignB(table,partB,labels)){
			List<Reaction> possibleReactions = TextExtractor.getPossibleReactions(table.getSource().getPmcId().substring(3));
			HashSet<Class<? extends ColumnContents>> requiredContents = new HashSet<Class<? extends ColumnContents>>();
			for (Reaction r : possibleReactions) {
				requiredContents.addAll(r.getRequiredColumns());
				requiredContents.addAll(r.getAllAlternatives());
			}
			HashSet<Class<? extends ColumnContents>> tableColumns = getTableColumns(requiredContents,labels,table);
			for (Reaction r : possibleReactions) {
				if (containsAllRequired(r, tableColumns)){
					return new Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>>(r,labels);
				}
			}
		}
		
		return null;
	}
	
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
	
	private boolean labelTable(ColumnContents c, HashMap<ColumnContents,List<TableBuf.Column>> data, TableBuf.Table table) {
		for (TableBuf.Column col : table.getColumnList()){
			if(c.headerMatch(col.getHeader().getData()) != null){
				addToData(c, col, data);
				return true;
			} else {
				for (int i = 0; i < 10 && i < col.getDataCount(); i++) {
					if (c.cellMatch(col.getData(i).getData()) != null){
						addToData(c, col, data);
						return true;
					}
				}
			}
		}
		return false;
	}
	
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
