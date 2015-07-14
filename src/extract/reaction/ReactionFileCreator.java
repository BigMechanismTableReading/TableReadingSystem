package extract.reaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;

import extract.analysis.TableType.ColumnTypes;

/**
 * Used to create a reaction file, or edit a current one to add reactions.
 * @author sloates
 *
 */

public class ReactionFileCreator {
	static BufferedReader br;
	private void makeNew(){
		System.out.println("What do you want to name this reaction file?");
	
		String fileName = null;
		try{
			fileName = br.readLine();
		}catch(IOException e){
			
		}
		if(fileName != null){
			Writer writer;
			
			try {
				writer = new BufferedWriter(new OutputStreamWriter(
				        new FileOutputStream(fileName)));
				writeLines(writer);
				writer.close();
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void editOld(){
		System.out.println("Which file do you want to write to?");
		String fileName = null;
		try{
			fileName = br.readLine();
		}catch(IOException e){
			
		}
		if(fileName != null){
			File file = new File(fileName);
			if(file.exists() && !file.isDirectory()){
				  Writer writer;
				try {
					writer = new BufferedWriter(new OutputStreamWriter(
						        new FileOutputStream(file, true)));
					
					writeLines(writer);
					writer.close();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
				
		}
	}
	
	private void writeLines(Writer w){
		System.out.println("What is the name of the new reaction? Put in -10 when done");
		try {	
			String line = "";
			String reaction = br.readLine();
			
			while(!reaction.equals("-10")){
				line = "";
				line = line + reaction.toUpperCase() + "\t";
				System.out.println("Type 0 to keep writing lines\n1 to be done with it");
				int done = Integer.parseInt(br.readLine());
				while(done == 0 ){
					System.out.println("Put -1 to be done");
					System.out.println("Put the Name of the Column Type, It must match one of these\n"
							+ Arrays.toString(ColumnTypes.values()));
					String colType = br.readLine();
					if(colType.equals("-1")){
						line +="UNKNOWN::-1::-1";
					}else{
						line+= colType.toUpperCase() + "::";
						System.out.println("Put the Header regex of the Column Type");
						String headRegEx = br.readLine();
						if(headRegEx.equals("-1")){
							line+="-1::-1";
						}else{
							line+=headRegEx+ "::";
							System.out.println("Put the Cell regex of the Column Type");
							String cellRegEx = br.readLine();
							if(cellRegEx.equals("-1")){
								line+="-1";
							}else{
								line+=cellRegEx;
							}
						}
					}
					System.out.println("Type 0 to keep writing lines\n1to be done with it");
					done = Integer.parseInt(br.readLine());
					if(done == 0)
						line+="\t";
				}
				//TODO check new line
				w.write(line + "\n");
				System.out.println("What is the name of the new reaction? Put in -10 when done");
				reaction = br.readLine();
			}
		} catch (IOException|NumberFormatException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[]args){
		ReactionFileCreator r = new ReactionFileCreator();
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("0 to make a new file \n1 to add to a current file\n2 to quit");
		int choose = -1;
		while(choose == -1){
			try{
				choose = Integer.parseInt(br.readLine());
				if(choose != 0 && choose != 1&& choose != 2){
					System.err.println("InvalidChoice");
					choose = -1;
				}
			}catch(IOException|NumberFormatException a){
			
			}
		}
		if(choose == 0)
			r.makeNew();
		else if(choose == 1)
			r.editOld();
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
