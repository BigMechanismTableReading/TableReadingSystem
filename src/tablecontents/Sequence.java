package tablecontents;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extract.analysis.Pair;
import extract.buffer.TableBuf;
import extract.buffer.TableBuf.Cell;
import extract.lookup.Dictionary;


public abstract class Sequence implements ColumnContents {
	public int confidenceNeeded = 3;
	private Dictionary dictionary = Dictionary.getInstance();
	@Override
	public String headerMatch(String match) {
		return null;
	}
	
	private boolean consonantChecker(String sequence){
		String vowels = "AEIOUaeiou";
		for(int i = 0; i < sequence.length()-5; i++){
			String sub = sequence.substring(i, i+5);
			boolean all_consonants = true;
			for(char c: vowels.toCharArray()){
				if(sub.contains(c+"")){
					all_consonants = false;
					break;
				}
			}
			if( all_consonants == true){
				return true;
			}
		}
		return false;
	}
	
	String cellMatch(String match,String regEx) {
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(match);
		if(m.find()){
			String found = m.group();
			if(!dictionary.words.contains(found)){
				if(consonantChecker(found)){
					return found;
				}
			}
		}
		return null;
	}
	@Override
	public boolean needsBoth(){
		return false;
	}
	
	@Override
	public HashMap<String, String> extractData (List<TableBuf.Column> cols, int row){
		HashMap<String, String> seq = new HashMap<String,String>();
		for(TableBuf.Column c : cols){
			Cell cell = c.getData(row);
			if(cell != null){
				seq.put("Sequence", cell.getData());
			}
		}
		
		return seq;
	}
	
	@Override
	public Pair<String, String> bestColumn(HashMap<ColumnContents,List<TableBuf.Column>> cols, int row){
		return null;
	}
	@Override
	public int getCellConfNeeded(){
		return confidenceNeeded;
	}
	
	@Override
	public int getPriorityNumber(){
		return 10;
	}
}
