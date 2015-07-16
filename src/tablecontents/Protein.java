package tablecontents;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;
import extract.buffer.TableBuf;
import extract.lookup.TabLookup;

public abstract class Protein implements ColumnContents{
	
	private static Protein prot = null;
	static Uniprot u = Uniprot.getInstance();
	static  SwisProt s = SwisProt.getInstance();
	static IPI i = IPI.getInstance();
	static GeneName g = GeneName.getInstance();
	static English e = English.getInstance();
	public static Protein[] protList = {u,s,i,g,e};
	
	public static ColumnContents getInstance(){
		return prot;
	}

	TabLookup t = TabLookup.getInstance();
	
	/**
	 * Checks that the regEx matches the input and returns the 1st match
	 * @param input
	 * @param regEx
	 * @return
	 */
	public String matchesFormat(String input,String regEx) {
		System.out.println(input);
		Pattern p = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(input);
		if(m.find()){
			return m.group();
		}
		return null;
	}
	
	@Override
	public String headerMatch(String match) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getGrounded(Protein p,HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		String data;
		if(cols.containsKey(p)){
			TableBuf.Column col = cols.get(p).get(0);
			if(col.getDataCount() > row && col.getData(row) != null){
				 data = col.getData(row).getData();
				 System.out.println(data);
				 String s = p.groundIdentity(data);
				 
				 if(s != null)
					 return s;
				 else if (p instanceof Uniprot)
					 return "Uniprot:" + data;
				 else return null;
			}
		}
		return null;
	}
	
	@Override 
	public String bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		for(Protein p : protList){
			String s = getGrounded(p,cols,row);
			if(s!= null)
				return s;
		}
		return null;
	}
	
	/**
	 * Returns the grounded ID if found, else returns null
	 * @param ungrounded
	 * @return
	 */
	public abstract String groundIdentity(String ungrounded);
}
