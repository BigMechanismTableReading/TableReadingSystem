package columncontents;

import java.util.HashMap;
import java.util.List;

import extract.buffer.TableBuf;

public interface ColumnContents {
	public String headerMatch(String match);
	public String cellMatch(String match);
	public String bestColumn (HashMap<ColumnContents,List<TableBuf.Column>> cols, int row);
}
