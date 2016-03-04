package tablecontents;

/**
 * Interface to be implemented by possible essential columns
 * @author sloates
 *
 */
public interface EssentialColumn {
	public Class<? extends LinkedContents> getLinkedInfo();
}
