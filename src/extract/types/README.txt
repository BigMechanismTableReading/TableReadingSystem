README -  extract.types


-Package Structure-
This package contains the main reaction class, any specific reactions, and the ReactionAdder Utility class

Reaction
	Every specific reaction must extend this class. This abstract class has all the methods that a reaction class needs
	to be constructed.
	data.add(Class)-can be used to add an entry that is needed to a reaction
	
	addAlternativeEntry(Class, createEntry(AlternateClass))-Can be used to create alternate entries there can be more then 
	one alternate class required.
	
	conjugationBase.add(String)-adds the conjugation base such as "phosphorylat"
	
	
ReactionAdder
	ReactionAdder can be used to add new reactions, using any ColumnContents that have already been created.
	Makes a new class in the format of a Reaction class, with all the needed information. Uses system in
	If a reaction is added and you want to use it, add the reaction instance to the allReaction array in Reaction.java