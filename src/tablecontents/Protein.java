package tablecontents;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tablecontents.ColumnContents;
import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.lookup.Lookup;
import extract.lookup.TabLookup;
import extract.lookup.YeastLookup;

public abstract class Protein implements ColumnContents{
	
	private static Protein prot = null;
	public int confidenceNeeded = 5;
	static Uniprot u = Uniprot.getInstance();
	static  SwisProt s = SwisProt.getInstance();
	static IPI i = IPI.getInstance();
	static GeneName g = GeneName.getInstance();
	static English e = English.getInstance();
	public static Protein[] protList = {u,s,i,g,e};
	public static boolean yeast = false;
	public String regEx = null;
	private static TabLookup t = TabLookup.getInstance();
	private static YeastLookup y = YeastLookup.getInstance();
	
	/**
	 * Checks that the regEx matches the input and returns the 1st match
	 * @param input
	 * @param regEx
	 * @return
	 */
	public String matchesFormat(String input,String regEx,boolean caseSensitive) {
		Pattern p = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
		if (caseSensitive){
			p = Pattern.compile(regEx);
		}
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
	@Override
	public boolean needsBoth(){
		return false;
	}
	
	private Pair<String, String> getGrounded(Protein p,HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		String data;
		if(cols.containsKey(p)){
			for (TableBuf.Column col :cols.get(p)){
				if(col.getDataCount() > row && col.getData(row) != null){
					data = col.getData(row).getData().toUpperCase();
					String s = p.cellMatch(data);
					if(s != null){
						return new Pair<String,String>(data, s);
					} else if (p instanceof Uniprot){
						return  new Pair<String,String>(data, "Uniprot:" + data);
					} else {
						return  new Pair<String,String>(data, null);
					}
				}
			}
		}
		return null;
	}
	
	@Override 
	public Pair<String, String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		Pair<String,String> s = null;
		for(Protein p : protList){
			Pair<String,String> a = getGrounded(p,cols,row);
			if (a != null)
				s = a;
			if (s!= null && s.getB() != null)
				return s;
		}
		return s;
	}
	
	public String getRegEx(){
		return regEx;
	}
	
	/**
	 * returns null since this method will never be used
	 */
	@Override
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row){
		return null;
	}
	
	@Override
	public int getCellConfNeeded(){
		return confidenceNeeded;
	}
	/**
	 * Returns the grounded ID if found, else returns null
	 * @param ungrounded
	 * @return
	 */
	public abstract String groundIdentity(String ungrounded);

	public static Lookup getT() {
//		if(!yeast)
		return t;
//		else 
//			return y;
	}

}
