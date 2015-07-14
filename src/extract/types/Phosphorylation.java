package extract.types;

import extract.analysis.TableType.ColumnTypes;

public class Phosphorylation extends Reaction{
	private String[] phosphositeRegex = {"a","cell"};
	private String[] foldRegex = {"b", "cell"};
	public Phosphorylation(){
		data.put(ColumnTypes.PHOSPHOSITE, phosphositeRegex);
		data.put(ColumnTypes.FOLD, foldRegex);
		conjugations.add("phosphorylat");
	}
}
