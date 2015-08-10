package extract.types;

import tablecontents.AcetylSite;
import tablecontents.Fold;

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
	}
	
	public static Reaction getInstance() {
		if (instance == null){
			instance = new Acetylation();
		}
		return instance;
	}
}
