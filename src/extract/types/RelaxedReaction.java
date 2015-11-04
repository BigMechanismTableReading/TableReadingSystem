package extract.types;
import tablecontents.*;

public class RelaxedReaction extends Reaction{

	private static RelaxedReaction instance = null;

	public static Reaction getInstance(){
		if(instance == null){
			instance = new RelaxedReaction();
		}
		return instance;
	}
	@SuppressWarnings("unchecked")
	private RelaxedReaction(){
		data.add(Site.class);
		data.add(Protein.class);
		addAlternativeEntry(Site.class, createEntry(Amino.class,Position.class));
		addAlternativeEntry(Site.class,createEntry(AminoSequence.class));
		addAlternativeEntry(Site.class,createEntry(Fold.class));
		conjugationBase.add("protein_modification");
		//TODO add other needed bases and figure out column contents
	}
	@Override
	public Class<? extends ColumnContents> getEssentialClass() {
		// TODO Auto-generated method stub
		return null;
	}
}