package tablecontents;

import java.util.HashMap;
import java.util.List;


import extract.analysis.Pair;
import extract.buffer.TableBuf;

public class Position extends AbstractPosition{
	//TODO determine matching for this	
	private static Position pos = null;
	public static Position getInstance(){
		if(pos == null)
			pos = new Position();
		return pos;
	}
	private Position(){
		cellRegEx = "\\b\\d{3,5}\\b";
		headerRegEx = "residue|location|position|site|tyrosine|serine|lysine";
	}

	@Override
	public boolean needsBoth(){
		return true;
	}
	public Pair<String, String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	
}
