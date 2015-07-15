package columncontents;

public class PhosphoAmino extends Amino {

	private String cellRegEx = "\b[SYT]|\bser|\btyr|\bthr";
	private static PhosphoAmino phosAmino = null;
	public static PhosphoAmino getInstance(){
		if(phosAmino == null)
			phosAmino = new PhosphoAmino();
		return phosAmino;
	}
	private PhosphoAmino(){
		
	}

	@Override
	public String cellMatch(String match) {
		return super.cellMatch(match, cellRegEx);
	}
}
