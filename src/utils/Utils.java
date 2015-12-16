package utils;

import java.io.File;
import java.io.FileFilter;

public class Utils {

	/**Returns files in directory that match*/
	public static File [] getFiles(File dir, Integer pmc_id, String[] extensions){
		File [] files = dir.listFiles(new FileFilter(){
			@Override
			public boolean accept(File arg0) {
				boolean pmc = arg0.getName().startsWith("PMC" + pmc_id);
				if (pmc){
					for (String ext: extensions){
						if (arg0.getName().endsWith(ext)){
							return true;
						}
					}
				}
				return false;
			}	
		});
		return files;
	}
	
	public static File [] getFiles(File dir, Integer pmc_id, String extension){
		return getFiles(dir, pmc_id, new String[] {extension});
	}
}
