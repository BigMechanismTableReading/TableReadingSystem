package extract.types;

import tablecontents.*;

public class Methylation extends Reaction{
	private static Methylation instance = null;
	public Methylation(){
		data.add(MethylSite.class);
		data.add(Fold.class);
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
