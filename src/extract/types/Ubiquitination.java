package extract.types;

import tablecontents.*;

/**
 * Ubiquitination Reaction Class
 * @author sloates
 *
 */
public class Ubiquitination extends Reaction{
	private static  Ubiquitination instance = null;
	
	private Ubiquitination(){
		//TODO add the neccesary requirements
		data.add(Protein.class);
		conjugationBase.add("ubiquitinat");
	}
	public static Reaction getInstance(){
		if(instance == null){
			instance = new Ubiquitination();
		}
		return instance;
	}

}
