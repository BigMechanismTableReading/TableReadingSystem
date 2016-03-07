package extract.types;

import tablecontents.Position;
import tablecontents.Protein;
import tablecontents.SumoSite;
import tablecontents.SumoType;

/**
 * Sumoylation Reaction Class
 * @author sloates
 *
 */
public class Sumoylation extends PostTranslationalModification{
	private static  Sumoylation instance = null;
	
	@SuppressWarnings("unchecked")
	private Sumoylation(){
		//TODO add the neccesary requirements
		data.add(Protein.class);
		data.add(SumoSite.class);
		conjugationBase.add("sumoylat");
		addAlternativeEntry(SumoSite.class, createEntry(Position.class, SumoType.class));
	}
	
	public static Reaction getInstance(){
		if(instance == null){
			instance = new Sumoylation();
		}
		return instance;
	}

}
