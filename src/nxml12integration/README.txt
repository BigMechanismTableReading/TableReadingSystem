README -nxml12integration

-Package Structure-
This package contains classes that utilize the JSON files produced by Mihai Surdeanu's system.

FriesParser-
Parses the JSON files that Mihai's system produces, storing the grounded participant Bs ("controlled")
in a HashMap<participantB,List<List<String>>> The sublist contains 3 Strings in this order.
1) ParticpantA, 2) TextEvidence, 3) InteractionType