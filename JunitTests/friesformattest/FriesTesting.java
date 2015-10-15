package friesformattest;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import extract.write.FriesFormat;
import extract.write.FriesFormat.FrameType;

public class FriesTesting {

	@Test
	public void testInitial() {
		
		FriesFormat f = new FriesFormat("leidos", "pmc_id", "run_id",
				"Table", Long.toString(System.currentTimeMillis()),
				Long.toString(System.currentTimeMillis()));
		System.out.println(f);
		assertTrue(true);
	}
	@Test
	public void testMakeEntity(){
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("entity_text_b", "name_of_entity");
		values.put("entity_type_b", "protein");
		values.put("row", "Row #");
		values.put("identifier_b", "standardized_name");
		FriesFormat f = new FriesFormat("leidos", "pmc_id", "run_id",
				"Table", Long.toString(System.currentTimeMillis()),
				Long.toString(System.currentTimeMillis()));
		f.makeFrame(FrameType.ENTITY,values);
		System.out.println(f.getJson().toString());
	}
	
	@Test
	public void testMakeEvent(){
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("entity_text_b", "name_of_entity");
		values.put("entity_type_b", "protein");
		values.put("row", "Row #");
		values.put("identifier_b", "standardized_name");
		values.put("modification_type","mod");
		values.put("negative_information","true/false");
		FriesFormat f = new FriesFormat("leidos", "pmc_id", "run_id",
				"Table", Long.toString(System.currentTimeMillis()),
				Long.toString(System.currentTimeMillis()));
		f.makeFrame(FrameType.EVENT,values);
		System.out.println(f.getJson().toString());
	}

}
