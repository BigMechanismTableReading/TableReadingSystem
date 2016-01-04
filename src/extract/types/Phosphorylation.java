package extract.types;

import tablecontents.*;
/**
 * Phosphorylation Reaction Class
 * @author sloates
 *
 */
public class Phosphorylation extends PostTranslationalModification{
	private static Phosphorylation instance = null;
	@SuppressWarnings("unchecked")
	private Phosphorylation(){
		data.add(Protein.class);
		data.add(PhosphoSite.class);
		data.add(Fold.class);
		addAlternativeEntry(PhosphoSite.class, createEntry(Position.class, PhosphoAmino.class));
		addAlternativeEntry(PhosphoSite.class, createEntry(PhosphoSequence.class));
		addAlternativeEntry(PhosphoSite.class,createEntry(PhosphoPosition.class));
		conjugationBase.add("phosphorylat");
	}
	
	public static Reaction getInstance() {
		if (instance == null){
			instance = new Phosphorylation();
		}
		return instance;
	}
	
}
