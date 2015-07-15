package extract.types;

import columncontents.*;

public class Phosphorylation extends Reaction{
	private static Phosphorylation instance = null;
	@SuppressWarnings("unchecked")
	public Phosphorylation(){
		data.add(PhosphoSite.class);
		data.add(Fold.class);
		addAlternativeEntry(PhosphoSite.class, createEntry(Position.class, PhosphoAmino.class));
		addAlternativeEntry(PhosphoSite.class, createEntry(PhosphoSequence.class));
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
