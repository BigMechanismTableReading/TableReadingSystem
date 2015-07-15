package extract.types;

import columncontents.*;
import extract.analysis.TableType.ColumnTypes;

public class Phosphorylation extends Reaction{
	private static Phosphorylation instance = null;
	public Phosphorylation(){
		data.add(PhosphoSite.getInstance());
		data.add(Fold.getInstance());
		conjugationBase.add("phosphorylat");
	}
	
	public static Reaction getInstance() {
		if (instance == null){
			instance = new Phosphorylation();
			return instance;
		} else {
			return instance;
		}
	}
}
