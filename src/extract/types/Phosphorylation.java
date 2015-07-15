package extract.types;

import java.util.ArrayList;

import columncontents.*;

public class Phosphorylation extends Reaction{
	private static Phosphorylation instance = null;
	public Phosphorylation(){
		data.add(PhosphoSite.class);
		data.add(Fold.class);
		ArrayList<Class<? extends ColumnContents>> phosphositeAlt = new ArrayList<Class<? extends ColumnContents>>();
		
		conjugationBase.add("phosphorylat");
	}
	
	public static Reaction getInstance() {
		if (instance == null){
			instance = new Phosphorylation();
			return instance;
		} else {
			return instance;
		}
	}
}
