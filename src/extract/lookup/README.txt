README - Extract Lookup

-Package Structure-
This package contains utility classes used to ground entities. These classes typically contain a HashMap(s) that holds data
retrieved from database files.

TabLookup
Database: UniprotKB (mammal, reviewed subset)
Conversions:
uniprot - Uniprot to Uniprot
swisprot - Swissprot to Uniprot
genename - Gene name to Uniprot
english - English description to Uniprot
uniToGene - Uniprot to Gene name
Access method: singleton with public HashMaps

YeastLookup
Database: UniprotKB (yeast, reviewed subset)
Conversions:
uniprot - Uniprot to Uniprot
swisprot - Swissprot to Uniprot
genename - Gene name to Uniprot
english - English description to Uniprot
uniToGene - Uniprot to Gene name
Access method: singleton with public HashMaps

IPILookup
Database: IPI last release Uniprot cross reference
Conversions:
IPItoUNI - IPI to Uniprot
Access method: singleton with public HashMap

ChemicalLookup
Database: ChEBI
Conversions:
chemicals - Chemical name to ChEBI id
Access method: singleton with public HashMap

AbbreviationLookup
Database: Allie
Conversions:
translatedAbreviations - Abbreviations to Uniprot
Access method: AbbreviationLookup.lookupAbbr(String abbr)

ChEMBLLookup
Database: ChEMBL subset
Conversions:
translatedChemicals - Chemical name to ChEMBL id
Access method: ChEMBLLookup.lookupChemical(String chemical)

SpeciesChecker is currently unused.