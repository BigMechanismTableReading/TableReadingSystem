Extract Analysis ReadMe

1.) The first step in our system is determining whether a table is relevant or not.
		a) To do this first convert the tables to a TableBuf format using the extract package.
		b) Once the table is in tableBuf format create an instance of the DetermineTable class
		c) Call the determine(table) method. This will return a 
			Pair<Reaction,HashMap<ColumnContents,List<TableBuf.Column>>>. 
2.) Once the relevance of a table is determined the extraction class can be called.
		a)Make an instance of the Extraction class.
		b)Check if the reaction is null or PossibleReaction, if neither
		  then call the extract(pair,table) method. This takes the
		  pair from determine and the tableBuf for the table.
		c)The extraction class will handle the rest of the process including 
		  making the index cards in json format.
		  	i) The extraction process begins by finding the best participantB column
		  		extracting all participant Bs which are stored in a pair of hashmaps of 
		  		row to participantBs.
		  	ii) Both HashMaps in the pair are converted to sets (one translated and one untranslated)
		  		that are to be used in participant A.
		  	iii) A ParticipantAExtractor instance is created. The 
		  		 getParticipantAs(table,partB,partBuntrans,foldContents(contents), r) method
		  		 is called. This method returns a List<ParticipantA> objects
		  		 	+ The TextExtractor returns a List of potential participant As
		  		 	+ First all fold columns are looked through for potential participantAs
		  		 		and any potential participantAs are compared to the list gotten from
		  		 		the TextExtractor.
		  		 	+ If there are no ParticipantAs found in the fold then the caption is looked at next
		  		 		and any possibleAs are compared to potential participantAs in TextExtractor
		  		 	+ The last resort is using the best case found from the TextExtractor
		  		 	+ This has the most potential for integration with text systems.
		  	iv) Columns that are to be used are chosen and alternates chosen if the normal columns are not found.
		  	v) The Columns are iterated through row by row, the HashMap of rows to participantBs is iterated through
		  		and the rest of the neccesary info is written to an index card.
		  	vi) The list of index cards are iterated through and written to Json files.
		  	vii) The JSon files are then written to an index card folder under the table name and row number.

		