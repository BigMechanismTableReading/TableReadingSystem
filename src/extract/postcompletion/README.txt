README - Extract Postcompletion

-Package Structure-
This contains a single class that is used for editing participant A information and fold information 
after the index cards have been generated.

ParticipantAEditor
	Can use the current JOP interface
	Otherwise create an instance of this object, and call the changeA method
	changeA(String partAUntrans,  -untranslated new A
			String partATrans,	  -translated new A	
			String entity_type,   -type of new A
			String directory,	  -directory name
			String PMCID,		  -PMCID 
			String tableName,	  -Name of the table
			boolean flip)		  -flips the type of activation (adds or inhibits mod)