package extract.types;

import columncontents.*;
import extract.analysis.TableType.ColumnTypes;

public class Phosphorylation extends Reaction{
	private String[] phosphositeRegex = {"a","cell"};
	private String[] foldRegex = {"b", "cell"};
	private Phosphorylation instance = null;
	public Phosphorylation(){
		data.put(English.getInstance(), phosphositeRegex);
		data.put(ColumnTypes.FOLD, foldRegex);
		conjugations.add("phosphorylat");
	}
	
	@Override
	public Reaction getInstance() {
		if (instance == null){
			instance = new Phosphorylation();
			return instance;
		} else {
			return instance;
		}
	}
}
