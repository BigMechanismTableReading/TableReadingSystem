package extract.types;

import tablecontents.AcetylSite;
import tablecontents.Fold;
import tablecontents.Protein;

/**
 * Acetylation Reaction Class
 * @author sloates
 *
 */
public class Acetylation extends Reaction{

	private static Acetylation instance = null;
	
	private Acetylation(){
		conjugationBase.add("acetylat");
		data.add(Fold.class);
		data.add(AcetylSite.class);
		data.add(Protein.class);
	}
	
	public static Reaction getInstance() {
		if (instance == null){
			instance = new Acetylation();
		}
		return instance;
	}
}
