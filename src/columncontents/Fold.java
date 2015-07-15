package columncontents;

import java.util.HashSet;

import extract.buffer.TableBuf;

//TODO Decide on best implementation, abstract or not
public abstract class Fold implements ColumnContents{
	private final HashSet<String> INCREASINGTERMS = set("INCREAS");
	private final HashSet<String> DECREASINGTERMS = set("DECREAS");
	private final String[] conjugations = new String[]{"E","ES","ING","ED"};
	
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
}
