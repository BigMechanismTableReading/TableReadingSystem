package tablecontents;

import java.util.HashMap;
import java.util.List;

import extract.analysis.Pair;
import extract.buffer.TableBuf;

/**
 * Interface that must be implemented by all column types
 * @author sloates
 */
public interface ColumnContents {
	
	/**
	 * Must match header and cell regular expression
	 * @return
	 */
	public boolean needsBoth();
	/**
	 * Returns the confidence level needed for this type to be labeled
	 * @return
	 */
	public int getCellConfNeeded();
	/**
	 * Attempts to match the header with a known type, returning null if no match
	 * @param match
	 * @return
	 */
	public String headerMatch(String match);
	
	/**
	 * Attemps to match the cell contents with a known type, returning null if no match
	 * @param match
	 * @return
	 */
	public String cellMatch(String match);
	
	/**
	 * Returns the information extracted from the best column for that particular type and row.
	 * @param cols
	 * @param row
	 * @return
	 */
	public Pair<String, String> bestColumn (HashMap<ColumnContents,List<TableBuf.Column>> cols, int row);
	
	/**
	 * Extracts data from the current row, utilizing type specific checks
	 * @param cols
	 * @param row
	 * @return
	 */
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row);
}
