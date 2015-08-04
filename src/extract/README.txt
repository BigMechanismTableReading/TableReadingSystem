README - Extract

-Package Structure-
This package contains several utility classes that are used to process tables retrieved from various sources and
convert them into a specifications used in our TableBuf class. This package also contains the TextExtractor class
which is used for information retrieval from text. The recommended main method for this package in a runnable jar 
is MasterExtractor which functions as a interactive table retrieval and extraction tool.

Web-based Retrieval:
WebScraper

ProtoBuf Generation:
HTMLTableExtractor
XMLTableExtractor
TableExtractor

Pipeline:
MasterExtractor

Text Reading:
TextExtractor


-Format Specifications-
The WebScraper class will download papers, tables and any supplementary webpages that can be retrieved
from the PMC id it is given. PMC ids are required to be integers.

Required Directories: 
tables (.pb): Serialized TableBuf objects
files (.html, .xls, .xlsx, .xml): Raw table files, also includes .xml papers
papers (.html): Papers in html format
webpages (.html): Supplemental webpages

Papers retrieved from the internet are named as PMC<id>.html. 
Ex: PMC1459033.html
Tables retrieved from the internet are named as PMC<id>(Supp)?<link>.html 
Ex: PMC2834543T1.html or PMC3293634Suppmsb20124-s2.xls
The generated TableBuf objects will be serialized as the same file names with a .pb extension
EX: PMC2834543T1.pb


-Running MasterExtractor-
The MasterExtractor class can be run as an independent application. It has three run configurations:
PMCIDs file - Asks for a file containing PMC ids
PMCID - Asks for a single PMCID
Skip Web Scraping - Runs only ProtoBuf generation

In all cases it will run ProtoBuf generation on every file in the files directory.