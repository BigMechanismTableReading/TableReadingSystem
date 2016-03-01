package extract.types;
import tablecontents.*;

/**
 * Possible Reaction
 * Less exact then any other Reaction
 * @author sloates
 *
 */
public class PossibleReaction extends PostTranslationalModification{
	//TODO determine exactly what we want here
	private static PossibleReaction instance = null;

	public static Reaction getInstance(){
		if(instance == null){
			instance = new PossibleReaction();
		}
		return instance;
	}
	@SuppressWarnings("unchecked")
	private PossibleReaction(){
		data.add(Protein.class);
		data.add(Fold.class);
		data.add(Site.class);
		addAlternativeEntry(Fold.class,createEntry(PossibleFold.class));
		addAlternativeEntry(Site.class,createEntry(Sequence.class));
		addAlternativeEntry(Site.class,createEntry(Position.class));
		conjugationBase.add("phosphorylat");
		
	}
}