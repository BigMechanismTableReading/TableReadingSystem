package extract.types;

import columncontents.*;
import extract.analysis.TableType.ColumnTypes;

public class Phosphorylation extends Reaction{
	private Phosphorylation instance = null;
	public Phosphorylation(){
		data.add(PhosphoSite.getInstance());
		data.add(Fold.getInstance());
		conjugationBase.add("phosphorylat");
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
