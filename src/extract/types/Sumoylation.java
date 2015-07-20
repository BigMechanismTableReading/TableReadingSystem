package extract.types;

import tablecontents.SumoSite;

public class Sumoylation extends Reaction{
	private static  Sumoylation instance = null;
	
	@SuppressWarnings("unchecked")
	private Sumoylation(){
		//TODO add the neccesary requirements
		data.add(SumoSite.class);
		conjugationBase.add("sumoylat");
	}
	public static Reaction getInstance(){
		if(instance == null){
			instance = new Sumoylation();
		}
		return instance;
	}

}
