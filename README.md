#Welcome to the TableReadingSystem

The TableReadingSystem extracts relevant information from tables in (or supplmentary to) documents. The system takes a list of PMCIDs either by text file or `ArrayList<Integer>` and generates index cards (json files). The PMCIDs must correspond to the papers located in the papers directory (required at the top level).

##To compile and run: 

1. `git clone https://github.com/BigMechanismTableReading/TableReadingSystem.git`  
2. `mvn clean compile assembly:single`   
3. `java ([optional but may be necessary] -Xmx16384m) -jar BigMechanismTableReader-0.0.1-jar.with-dependencies.jar "(filename)" (0,1,2 or 3 depending on configuration)`


###Please visit the [wiki](https://github.com/BigMechanismTableReading/TableReadingSystem/wiki) for more information and options