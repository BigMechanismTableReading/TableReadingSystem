package extract.types;

import tablecontents.PhosphoAmino;
import tablecontents.PhosphoSite;
import tablecontents.Position;
import tablecontents.SumoSite;
import tablecontents.SumoType;

public class Sumoylation extends Reaction{
	private static  Sumoylation instance = null;
	
	@SuppressWarnings("unchecked")
	private Sumoylation(){
		//TODO add the neccesary requirements
		data.add(SumoSite.class);
		conjugationBase.add("sumoylat");
		addAlternativeEntry(PhosphoSite.class, createEntry(Position.class, SumoType.class));
	}
	
	public static Reaction getInstance(){
		if(instance == null){
			instance = new Sumoylation();
		}
		return instance;
	}

}
