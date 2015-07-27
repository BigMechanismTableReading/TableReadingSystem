package tablecontents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Cell;

/**
 * Abstract class for different fold measurements
 * @author sloates
 *
 */
public abstract class Fold implements ColumnContents{
	private HashSet<String> INCREASINGTERMS = set("INCREAS");
	private HashSet<String> DECREASINGTERMS = set("DECREAS");
	private static String[] conjugations = new String[]{"E","ES","ING","ED"};
	double[] cutOffs = null;
	public int confidenceNeeded = 3;
	public String cellRegEx = "";
	
	/**
	 * Calculates and returns cutoffValues as array/HASH?
	 * @param col
	 * @return
	 */
	public abstract double[] cutoffValues(TableBuf.Column col);//TODO Determine best way to do this
	
	public Fold bestFold(HashMap<ColumnContents,List<TableBuf.Column>> foldCols){
		Log l = Log.getInstance();
		FoldChange c = FoldChange.getInstance();
		Ratio r = Ratio.getInstance();
		
		if(foldCols.containsKey(l)){
			return l;
		}else if (foldCols.containsKey(c)){
			return c;
		}else if(foldCols.containsKey(r)){
			return r;
		}
		return null;
		
	}

	public String [] determineMod(double d){
		if(d < cutOffs[0]){
			return new String [] {"inhibits modification", "false"};
		}else if(d > cutOffs[1]){
			return new String [] {"adds modification","false"};
		}else if (d < cutOffs[2]){
			return new String [] {"inhibits modification","true"};
		}else if(d > cutOffs[2]){
			return new String [] {"adds modification","true"};
		}
		return null;
	}
	
	private TableBuf.Column bestSubColumn(List<TableBuf.Column> cols){
		String headerReg = "avg|average";//TODO add to this list after looking at more data
		Pattern p = Pattern.compile(headerReg,Pattern.CASE_INSENSITIVE);
		for(TableBuf.Column c : cols){
			Matcher m = p.matcher(c.getHeader().getData());
			if(m.find()){
				return c;
			}
		}
		if(cols.size() > 0){
			return cols.get(0);
		}
		return null;
	}
	
	@Override
	public int getCellConfNeeded(){
		return confidenceNeeded;
	}
	@Override
	public boolean needsBoth(){
		return false;
	}
	
	@Override
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row){
		//TODO work on other types of tables with time stuff (not that many tables)
		TableBuf.Column c = bestSubColumn(cols);
		HashMap<String, String> modifs = new HashMap<String,String>();
		Pattern p = Pattern.compile("\\b(\\d{1,3}\\.\\d{1,10})\\b");
		TableBuf.Cell cell = c.getData(row);
		if(cell != null){
			String data = cell.getData();
			Matcher m = p.matcher(data);
			boolean neg = false;
			if(data.contains("-"))
				neg = true;
			double num = Double.POSITIVE_INFINITY;
			if(m.find()){
				try{
					num = Double.parseDouble(m.group());
					if(neg)
						num *= -1;
				}catch(NumberFormatException e){

				}
			}
			if(num != Double.POSITIVE_INFINITY){
				String [] mods = determineMod(num);
				if(mods != null){
					modifs.put("interaction_type",mods[0]);
					modifs.put("negative_information", mods[1]);
					modifs.put("fold_information_used",c.getHeader().getData());
				}
			}

		}
		return modifs;
	}
	
	/**
	 * If there is a word indication whether it increases or decreases,
	 * this returns inc, or dec. Else this returns null
	 * If inc or dec, all results are significant
	 * @param title
	 * @return
	 */
	public String wordIndicator(String title){
		for(String word : title.split("\\W")){
			word = word.toUpperCase();
			if(INCREASINGTERMS.contains(word)){
				return null;
			}else if (DECREASINGTERMS.contains(word)){
				return null; 
			}
		}
		//TODO what should be returned? ENUM?
		return null;
	}
	
	
	String match(String match,String regEx) {
		Pattern p = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(match);
		if(m.find())
			return m.group();
		return null;
	}

	/**
	 * Used to create inc and dec hashsets by adding conjucations to the bases
	 * @param base
	 * @return
	 */
	private HashSet<String> set(String base) {
		HashSet<String> set = new HashSet<String>();
		base = base.toUpperCase();
		for(String s : conjugations){
			set.add(base + s.toUpperCase());
		}
		return set;
	}
	public Pair<String, String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	
}
