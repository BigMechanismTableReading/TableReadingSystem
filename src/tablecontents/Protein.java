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

/**
 * Abstract class for Protein Types
 * @author sloates
 *
 */
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
		if(regEx !=  null){
			Pattern p = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
			if (caseSensitive){
				p = Pattern.compile(regEx);
			}
			Matcher m = p.matcher(input);
			if(m.find()){
				return m.group();
			}
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
	
	/**
	 * When there are multiple uniprot names, this iterates through finding one that can be ground
	 * @param input
	 * @param uni
	 * @return
	 */
	private String findUni(String input, Protein uni){
		Pattern p = Pattern.compile(uni.regEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(input);
		while(m.find()){
			String prot = m.group();
			if(uni.groundIdentity(prot) != null)
				return prot;
		}
		return null;
	}
	/**
	 * Returns the grounded version of the protein, along with the ungrounded version
	 * Pair<ungrounded, grounded>
	 * @param p
	 * @param cols
	 * @param row
	 * @return
	 */
	private Pair<String, String> getGrounded(Protein p,HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		String data;
		if(cols.containsKey(p)){
			for (TableBuf.Column col :cols.get(p)){
				if(checkEmpty(col, row)){
					data = col.getData(row).getData().toUpperCase();
					String temp_data = matchesFormat(data,p.regEx,true);
					if(temp_data != null)
						data = temp_data;
					String s = p.cellMatch(data);
					if(s != null){
						return new Pair<String,String>(data, s);
					} else if (p instanceof Uniprot && data.trim().length() >= 5){
						String untrans = data;
						String untransMatch = matchesFormat(data,p.regEx,false);
						if(untransMatch != null)
							untrans = untransMatch;
						String uni = findUni(data,p);
						if(uni != null)
							data = uni;
						if (cols.containsKey(g)){
							if(checkEmpty(cols.get(g).get(0), row)){
								untrans = cols.get(g).get(0).getData(row).getData();
							}
						}
						return  new Pair<String,String>(untrans, "Uniprot:" + data);
					} else {
						return  new Pair<String,String>(data, null);
					}
				}
			}
		}
		return null;
	}
	
	private boolean checkEmpty(TableBuf.Column col, int row){
		return col.getDataCount() > row && col.getData(row) != null && !(col.getData(row).getData().trim().isEmpty());
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

	/**
	 * Returns the lookup being used
	 * Currently always returns mammal lookup, can be changed
	 * @return
	 */
	public static Lookup getT() {
//		if(!yeast)
		return t;
//		else 
//			return y;
	}

}
