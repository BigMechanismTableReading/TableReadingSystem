package columncontents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.buffer.TableBuf;

//TODO Decide on best implementation, abstract or not
public abstract class Fold implements ColumnContents{
	private HashSet<String> INCREASINGTERMS = set("INCREAS");
	private HashSet<String> DECREASINGTERMS = set("DECREAS");
	private static String[] conjugations = new String[]{"E","ES","ING","ED"};
	
	/**
	 * Calculates and returns cutoffValues as array/HASH?
	 * @param col
	 * @return
	 */
	public abstract double[] cutoffValues(TableBuf.Column col);//TODO Determine best way to do this
	
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
				return null;//TODO what should be returned? ENUM?
			}else if (DECREASINGTERMS.contains(word)){
				return null; //TODO what should be returned? ENUM?
			}
		}
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
	public String bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	
}
