README - tablecontents

-Package Structure-
This package contains all classes that are column types, those classes that implement the ColumnContents interface,
In addition the ParticipantA and Dynamic typing classes are contained here as well.

ColumnContents interface- 
	Any new content type must implement the ColumnContents interface.
 	Once a type is added it can be used in any reaction class.
 	Contains the methods needed for extraction and labeling.
 	All ColumnContents types should be singleton classes.
 
ParticipantA-
	Holds potential participant A and their fold columns.
	Used to determine the significance of a piece of and interaction 
	(Ex. Does the ptm increase or decrease? Is it significant information?)

DynamicTyping- 
	Used to get list of subtypes, which is useful when determining table relevance.	
	Makes use of Reflections.