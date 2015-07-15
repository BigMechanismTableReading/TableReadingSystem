package columncontents;

import java.util.HashSet;

import extract.buffer.TableBuf;

//TODO Decide on best implementation, abstract or not
public abstract class Fold implements ColumnContents{
	private final HashSet<String> INCREASINGTERMS;
	private final HashSet<String> DECREASINGTERMS;
	private final String[] conjugations = new String[]{"E","ES","ING","ED"};
	
	public Fold(){
		INCREASINGTERMS = set("INCREAS");
		DECREASINGTERMS = set("DECREAS");
	}
	/**
	 * Returns an array of integers that are the min, max, and middle cutoff values
	 * @return
	 */
	public int[] cutoffValues(TableBuf.Column col){return null;};
	
	/**
	 * If there is a word indication whether it increases or decreases,
	 * this returns inc, or dec. Else this returns null
	 * If inc or dec, all results are significant
	 * @param title
	 * @return
	 */
	public String wordIndicator(String title){
		//TODO checks the title for words like increasing or decreasing
		for(String word : title.split("\\W")){
			word = word.toUpperCase();
			if(INCREASINGTERMS.contains(word)){
				return null;//TODO
			}else if (DECREASINGTERMS.contains(word)){
				return null; //TODO
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
