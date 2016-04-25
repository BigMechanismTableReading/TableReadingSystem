package extract.types;

import org.apache.poi.ss.formula.functions.Value;

import tablecontents.ColumnContents;
import tablecontents.Fold;
import tablecontents.GOid;

public class BiologicalProcess extends Regulation{
	private static BiologicalProcess instance = null;
	public static Reaction getInstance() {
		if (instance == null){
			instance = new BiologicalProcess();
		}
		return instance;
	}
	
	private BiologicalProcess(){
		//TODO add the needed classes here, keep it basic for now
		conjugationBase.add("GO:");
		conjugationBase.add("process");
		data.add(GOid.class);
		data.add(Fold.class);
		//addAlternativeEntry(Fold.class, createEntry(Pvalue.class));
	}
	@Override
	public Class<? extends ColumnContents> getEssentialClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
