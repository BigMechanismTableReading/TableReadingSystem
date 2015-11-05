package extract.types;
import extract.buffer.TableBuf;
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
		conjugationBase.add("protein_modificat");
		//TODO add other needed bases and figure out column contents
	}
	public void setReactionType(TableBuf.Table table){
		boolean conj_found = false;
		for(Reaction r : Reaction.getReactions()){
			for(String c: table.getCaptionList()){
				for(String conj: r.getConjugationBase()){
					if(c.contains(conj)){
						this.conjugationBase.clear();
						this.conjugationBase.add(conj);
						conj_found = true;
						break;
					}
				}
				if(conj_found){
					break;
				}
			}
			if(conj_found){
				break;
			}
		}
		
	}
	@Override
	public Class<? extends ColumnContents> getEssentialClass() {
		// TODO Auto-generated method stub
		return null;
	}
}