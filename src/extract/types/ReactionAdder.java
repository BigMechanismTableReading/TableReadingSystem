package extract.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.reflections.Reflections;

import tablecontents.ColumnContents;

/**
 * Utility for easily adding new reactions 
 * @author sloates
 *
 */
public class ReactionAdder {

	private static int closingCount = 0;
	private static void createSingleton(StringBuilder react, String name){
		react.append("\tprivate static " + name + " instance = null;\n\n");
		react.append("\tpublic static Reaction getInstance(){\n");
		closingCount++;
		react.append("\t\tif(instance == null){\n");
		closingCount++;
		react.append("\t\t\tinstance = new " + name + "();\n\t\t}\n");
		closingCount--;
		react.append("\t\treturn instance;\n\t}");
		closingCount--;
	}

	private static void makeTemplate(StringBuilder react,String name){
		react.append("package extract.types" + "\n");
		react.append("public class " + name + " extends Reaction{\n\n");
		closingCount++;
		createSingleton(react,name);
	} 
	
	private static void addAlternateEntries(StringBuilder react,Set<Class<? extends ColumnContents>> choosen,
			HashMap<Class<? extends ColumnContents>, List<Class<? extends ColumnContents>>> alternates){
		for(Class<? extends ColumnContents> c  : alternates.keySet()){
			react.append("\t\taddAlternativeEntry(" +  c.getName().split("\\.")[1] + ".class, creatEntry(");
			for(Class<? extends ColumnContents> alt : alternates.get(c)){
				react.append(alt.getName().split("\\.")[1] + ".class,");
			}
			react.deleteCharAt(react.length()-1);
			react.append("));\n");
		}
	}
	
	private static void addContents(StringBuilder react,Set<Class<? extends ColumnContents>> choosen,
			String name, HashMap<Integer, Class<? extends ColumnContents>> mapped,Scanner s){
		
		react.append("\n");
		react.append("\t@SuppressWarnings(\"unchecked\")\n");
		react.append("\tprivate " + name + "(){\n");
		closingCount++;
		HashMap<Class<? extends ColumnContents>, List<Class<? extends ColumnContents>>> alternates =
				new HashMap<Class<? extends ColumnContents>, List<Class<? extends ColumnContents>>>();
		for(Class<? extends ColumnContents> c : choosen){
			react.append("\t\tdata.add(" + c.getName().split("\\.")[1] + ".class" + ");\n");
			System.out.println("Do you want alternative classes for this class?" +
					c.getName().split("\\.")[1]  + "\n 0 for no\n 1 for yes");
			int num = s.nextInt();
			if(num == 1){
				System.out.println("What are the alternate entries for " + c);
				System.out.println("Put the number of the class -1 when done\n" + mapped.toString());
				num = s.nextInt();
				while(num != -1){
					if(alternates.containsKey(c)){
						alternates.get(c).add(mapped.get(num));	
					}else{
						List<Class<? extends ColumnContents>> alts = new LinkedList<Class<? extends ColumnContents>>();
						alts.add(mapped.get(num));
						alternates.put(c, alts);
					}
					num = s.nextInt();
				}
			}
			
		}
		System.out.println(alternates);
		addAlternateEntries(react,choosen,alternates);
		System.out.println("what is your conjugation base");
		String base = s.next();
		react.append("\t\tconjugationBase.add(\"" + base + "\");\n");
		react.append("\t}");
	}
	
	private static void getContents(StringBuilder react,Scanner s,String name){
		Reflections reflections = new Reflections("tablecontents");
		Set<Class<? extends ColumnContents>> subTypes = reflections.getSubTypesOf(ColumnContents.class);
		HashMap<Integer,Class< ? extends ColumnContents>> mapped = new HashMap<Integer,Class<? extends ColumnContents>>();
		int count = 0;
		for(Class<? extends ColumnContents> contents : subTypes){
			mapped.put(count,contents);
			count++;
		}
		System.out.println("Write the number of the contents that this type must have, -1 when done");
		System.out.println(mapped);
		int num = s.nextInt();
		Set<Class<? extends ColumnContents>> choosen = new HashSet<Class<? extends ColumnContents>>();
		while(num != -1 ){
			if(mapped.containsKey(num))
				choosen.add(mapped.get(num));
			else 
				System.err.println("Invalid Num");
			System.out.println("Next number, -1 when done");
			num = s.nextInt();
		}
		System.out.println(choosen);
		addContents(react, choosen,name,mapped,s);
	}
	public static void main (String [] args){
		System.out.println("Make a reaction,\nPress 1 to continue\n0 to quit");
		Scanner s = new Scanner(System.in);
		int go = s.nextInt();
		if(go == 1){
			StringBuilder reactionBuilder = new StringBuilder();
			System.out.println("What is your reaction named?");
			String name = s.next();
			if(name != ""){
				makeTemplate(reactionBuilder,name);
				getContents(reactionBuilder, s,name);
				System.out.println(reactionBuilder);
			}
			reactionBuilder.append("}");
		}
	
	}
}
