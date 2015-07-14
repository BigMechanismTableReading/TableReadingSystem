package columncontents;

import extract.buffer.TableBuf;

public abstract class Fold implements ColumnContents{
	//TODO implement the needed thing for folds
	
	/**
	 * Returns an array of integers that are the min, max, and middle cutoff values
	 * @return
	 */
	public abstract int[] cutoffValues(TableBuf.Column col);
	
	/**
	 * If there is a word indication whether it increases or decreases,
	 * this returns inc, or dec. Else this returns null
	 * If inc or dec, all results are significant
	 * @param title
	 * @return
	 */
	public abstract String wordIndicator(String title);
}
