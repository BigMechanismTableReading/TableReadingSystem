package tablecontents;

import java.util.HashMap;
import java.util.List;

import utils.Pair;
import tableBuilder.TableBuf.Column;

public class PhosphoPosition extends AbstractPosition{
	
	private static  PhosphoPosition instance = null;
	public static PhosphoPosition getInstance(){
		if(instance == null)
			instance = new PhosphoPosition();
		return instance;
	}
	private PhosphoPosition(){
		headerRegEx = "(site)|(phosphorylated)";
		cellRegEx = "\\b\\d{3,4}\\b";
		confidenceNeeded = 8;
	}
	
	@Override
	public boolean needsBoth() {
		return true;
	}

	@Override
	public Pair<String, String> bestColumn(
			HashMap<ColumnContents, List<Column>> cols, int row) {
		// TODO Auto-generated method stub
		return null;
	}
}
