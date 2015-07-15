package extract.types;

import columncontents.*;

public class Methylation extends Reaction{
	private static Methylation instance = null;
	public Methylation(){
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
