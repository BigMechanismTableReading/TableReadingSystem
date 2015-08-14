package extract.types;

import tablecontents.ColumnContents;
import tablecontents.Protein;

/**
 * Super class for all PostTranslationalModifications\
 * @author sloates
 *
 */
public abstract class PostTranslationalModification extends Reaction{
	
	@Override
	public Class<? extends ColumnContents> getEssentialClass(){
		return Protein.class;
	}
	
}
