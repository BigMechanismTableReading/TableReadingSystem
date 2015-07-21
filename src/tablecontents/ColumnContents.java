package tablecontents;

import java.util.HashMap;
import java.util.List;

import extract.analysis.Pair;
import extract.buffer.TableBuf;

public interface ColumnContents {
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
	 * Returns the information from the specific row for the bestColumn for the row
	 * @param cols
	 * @param row
	 * @return
	 */
	public Pair<String, String> bestColumn (HashMap<ColumnContents,List<TableBuf.Column>> cols, int row);
	
	/**
	 * Extracts data from the current row
	 * @param cols
	 * @param row
	 * @return
	 */
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row);
}
