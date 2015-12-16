package main;

import extract.Extractor;

public class RelevanceTester {

	public static void main(String[] args) {
		TableReader.init(args);
		Extractor.extractFromList(TableReader.pmc_ids);


	}

}
