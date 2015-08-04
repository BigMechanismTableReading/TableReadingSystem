Relevance Test is a premade pipeline.
Set as your main class if you choose to export this as a jar file.
java -jar RelevanceTest.jar [FileName] [OPTION]
 0: full Creates the tables from the original files, determines relevance 
 	then extracts information and writes to index cards
 1: partial Uses already made protobufs and determines relevance 
 	then extracts information and writes to index cards
 2: HTML partial For HTML Tables only Uses already made protobufs and determines 
 	relevance then extracts information and writes to index cards
 3: Excel partial For Excel Tables only Uses already made protobufs and 
 	determines relevance then extracts information and writes to index cards