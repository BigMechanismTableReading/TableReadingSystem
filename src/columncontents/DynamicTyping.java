package columncontents;

import java.util.Set;

import org.reflections.Reflections;

public class DynamicTyping {
	private static DynamicTyping dt = null;
	private Reflections typeList;
	public static DynamicTyping getInstance(){
		if(dt == null)
			dt = new DynamicTyping();
		return dt;
	}
	private DynamicTyping(){
		typeList = Reflections.collect();
		
	}
	public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type){
		return typeList.getSubTypesOf(type);
	}
}
