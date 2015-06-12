package gov.nih.cit.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class AppProperties {
	File propertyFile;
	Properties currentProperties=new Properties();

	public static AppProperties getDefaultAppProperties(String appName){
		File f=new File(System.getProperty("user.home"),"."+appName+".properties");
		return new AppProperties(f);
	}

	public AppProperties(File f) {
		this.propertyFile=f;
		updateProperties();
	}
	
	public void setProperty(String key,String value){
		currentProperties.setProperty(key, value);
		save();
	}
	public void setListOfProperties(String key,List<String> values){
		for (int i=0;i<values.size();i++){
			currentProperties.setProperty(key+"."+i, values.get(i));
		}
		save();
	}
	
	private void save(){
		try{
			FileWriter writer=new FileWriter(propertyFile);
			currentProperties.store(writer,"Saved Properties - "+new Date());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void remove(String property) {
		currentProperties.remove(property);
		save();
	}

	public String getProperty(String key) {
		return currentProperties.getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue) {
		return currentProperties.getProperty(key, defaultValue);
	}

	public void updateProperties(){
		currentProperties.clear();
		try {
			FileReader reader=new FileReader(propertyFile);
			currentProperties.load(reader);
			reader.close();			
		} catch (IOException e) {

		}
	}
}
