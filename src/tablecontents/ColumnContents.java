package tablecontents;

import java.util.HashMap;
import java.util.List;

import extract.buffer.TableBuf;

public interface ColumnContents {
	public String headerMatch(String match);
	public String cellMatch(String match);
	public String bestColumn (HashMap<ColumnContents,List<TableBuf.Column>> cols, int row);
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row);
}
