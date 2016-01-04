package tablecontents;

/**
 * Interface for linked contents
 * @author sloates
 *
 */
public interface LinkedContents {
	
	//TODO Possibly have an extraction class for each one of these,
	//for example ParticipantAExtractor would implement IndExtract and 
	//the method here would be
	//public Class<? extends IndExtract> getExtractionMethod(HashMap<ColumnContents, List<TableBuf.Column>> labeling);
}
