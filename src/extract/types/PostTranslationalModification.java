package extract.types;

import tablecontents.ColumnContents;
import tablecontents.Protein;

/**
 * Class for all PostTranslationalModifications,
 * This will store anything that is similar between them
 * @author sloates
 *
 */
public abstract class PostTranslationalModification extends Reaction{
	
	public Class<? extends ColumnContents> getEssentialClass(){
		return Protein.class;
	}
	
	
}
