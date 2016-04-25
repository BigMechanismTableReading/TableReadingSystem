package config;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

public class Config{
	private String user;
	private String table_dir;
	private String file_dir;
	private String paper_dir;
	private boolean make_tables;
	private boolean simple_reaction;
	private String reaction_class;
	private String input_list;
	private String output_file;
	private boolean nxml;
	private String nxml_dir;
	private String resolve_file;
	private String home_dir;
	private String log_file;
	private boolean printOutput;
	private Boolean go_only;
	public String getResolve_file() {
		return resolve_file;
	}
	public void setResolve_file(String resolve_file) {
		this.resolve_file = resolve_file;
	}
	public String getOutput_file(){
		return output_file;
	}
	public String getUser() {
		return user;
	}

	public String getTable_dir() {
		return table_dir;
	}

	public String getFile_dir() {
		return file_dir;
	}

	public String getPaper_dir() {
		return paper_dir;
	}

	public boolean isMake_tables() {
		return make_tables;
	}

	public boolean isSimple_reaction() {
		return simple_reaction;
	}

	public String getReaction_class() {
		return reaction_class;
	}

	public String getInput_List() {
		return input_list;
	}
	public boolean isNxml() {
		return nxml;
	}
	public String getNxml_dir() {
		return nxml_dir;
	}

	public void setPropValues(String configFile) throws IOException{
		Properties prop = new Properties();
		//String config_file = configFile;
		FileInputStream in = new FileInputStream(configFile);
		prop.load(in);
		in.close();
		/*inputStream = getClass().getResourceAsStream(config_file);
		if(inputStream != null){
			prop.load(inputStream);
		}else{
			throw new FileNotFoundException(config_file + " not found");
		}*/
		// Gets the property values
		user = prop.getProperty("user");
		input_list = prop.getProperty("input_list");
		//output_file = prop.getProperty("output_file");
		//table_dir = prop.getProperty("table_dir");
		//file_dir = prop.getProperty("file_dir");
		//paper_dir = prop.getProperty("paper_dir");
		make_tables = Boolean.parseBoolean(prop.getProperty("make_tables"));
		simple_reaction = Boolean.parseBoolean(prop.getProperty("simple_reaction"));
		reaction_class = prop.getProperty("reaction_class");
		//nxml = Boolean.parseBoolean(prop.getProperty("utils.nxml"));
		//nxml_dir = prop.getProperty("nxml_dir");
		//resolve_file = prop.getProperty("resolve_file");
		home_dir = prop.getProperty("home_dir");
		home_dir = Paths.get(home_dir).toAbsolutePath().normalize().toString();
		log_file = prop.getProperty("log_file");
		printOutput = Boolean.parseBoolean(prop.getProperty("print_output"));
		go_only = Boolean.parseBoolean(prop.getProperty("go_only"));
	}
	public String getHome_dir() {	
		return home_dir;
	}
	public void setHome_dir(String home_dir) {
		this.home_dir = home_dir;
	}
	public String getLog_file() {
		return log_file;
	}
	public void setLog_file(String log_file) {
		this.log_file = log_file;
	}
	public boolean printOutput() {
		return printOutput;
	}
	public void setPrintOutput(boolean printOutput) {
		this.printOutput = printOutput;
	}
	public Boolean getGo_only() {
		return go_only;
	}
	public void setGo_only(Boolean go_only) {
		this.go_only = go_only;
	}



}
