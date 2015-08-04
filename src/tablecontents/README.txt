README - tablecontents

ColumnContents interface- 
	Any new content type must implement the ColumnContents interface.
 	Once a type is added it can be used in any reaction class.
 	Contains the methods needed for extraction and labeling
 
ParticipantA-
	Holds potential participant A and their fold columns.
	Used to determine the significance of a piece of and interaction 
	Does it increase or decrease? Is it negative information?

DynamicTyping- 
	Used to get list of subtypes, which is useful when determining table relevancy.	
	Makes use of Reflections.