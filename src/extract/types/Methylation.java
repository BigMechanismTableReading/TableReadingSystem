package extract.types;

import tablecontents.*;

public class Methylation extends Reaction{
	private static Methylation instance = null;
	@SuppressWarnings("unchecked")
	public Methylation(){
		data.add(MethylSite.class);
		data.add(Fold.class);
		addAlternativeEntry(MethylSite.class, createEntry(Position.class, MethylAmino.class));
		conjugationBase.add("methylat");
	}
	
	public static Reaction getInstance() {
		if (instance == null){
			instance = new Methylation();
			return instance;
		} else {
			return instance;
		}
	}
}
