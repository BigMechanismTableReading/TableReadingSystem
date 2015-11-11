package extract.types;
import tablecontents.*;

public class SimpleReaction extends Reaction{

	private static SimpleReaction instance = null;

	public static Reaction getInstance(){
		if(instance == null){
			instance = new SimpleReaction();
		}
		return instance;
	}
	@SuppressWarnings("unchecked")
	private SimpleReaction(){
		data.add(Protein.class);
		conjugationBase.add("none");
	}
	@Override
	public Class<? extends ColumnContents> getEssentialClass() {
		// TODO Auto-generated method stub
		return null;
	}
}