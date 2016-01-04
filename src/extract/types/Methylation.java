package extract.types;

import tablecontents.*;

/**
 * Methylation Reaction class
 * @author sloates
 *
 */
public class Methylation extends PostTranslationalModification{
	private static Methylation instance = null;
	@SuppressWarnings("unchecked")
	private Methylation(){
		data.add(Protein.class);
		data.add(MethylSite.class);
		data.add(Fold.class);
		addAlternativeEntry(MethylSite.class, createEntry(Position.class, MethylAmino.class));
		conjugationBase.add("methylat");
	}
	
	public static Reaction getInstance() {
		if (instance == null){
			instance = new Methylation();
		}
		return instance;
	}
}
