TabLookup and YeastLookup both have 5 hashmap that convert to uniprot, and one that converts uniprot to geneNames
IPI Lookup and Chemical Lookup have a single hashmap that ground the entities
AbbreviationLookup is called statically with AbbreviationLookup.lookupAbbr(String abbr), returns grounded name or null
ChemblLookup is called statically with ChEMBLLookup.lookupChemical(String chemical), returns grounded name or null
SpeciesChecker is currently unused.