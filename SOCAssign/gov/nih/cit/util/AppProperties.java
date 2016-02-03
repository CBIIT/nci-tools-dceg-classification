package gov.nih.cit.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class AppProperties {
	File propertyFile;
	Properties currentProperties = new Properties();

	public static AppProperties getDefaultAppProperties(String appName) {
		File f = new File(System.getProperty("user.home"),"." + appName + ".properties");
		return new AppProperties(f);
	}

	public AppProperties(File f) {
		this.propertyFile = f;
		updateProperties();
	}

	public void setProperty(String key,String value) {
		currentProperties.setProperty(key, value);
		save();
	}
	public void setListOfFiles(String key,List<File> files) {
		for (int i = 0;i < files.size();i++) {
			currentProperties.setProperty(key + "." + i, files.get(i).getAbsolutePath());
		}
		save();
	}
	public List<File> getListOfFiles(String key) {
		List<File> list = new ArrayList<File>();

		String defValue = "<N/A>";
		boolean keepGoing = true;
		for (int i = 0;keepGoing;i++) {
			String value = currentProperties.getProperty(key + "." + i,defValue);
			keepGoing = !value.equals(defValue);
			if (keepGoing) list.add(new File(value));
		}

		return list;
	}

	public void setListOfProperties(String key,List<String> values) {
		for (int i = 0;i < values.size();i++) {
			currentProperties.setProperty(key + "." + i, values.get(i));
		}
		save();
	}
	public List<String> getListOfPropertes(String key) {
		List<String> list = new ArrayList<String>();

		String defValue = "<N/A>";
		boolean keepGoing = true;
		for (int i = 0;keepGoing;i++) {
			String value = currentProperties.getProperty(key + "." + i,defValue);
			keepGoing = !value.equals(defValue);
			if (keepGoing) list.add(value);
		}

		return list;
	}

	private void save() {
		try {
			FileWriter writer = new FileWriter(propertyFile);
			currentProperties.store(writer,"Saved Properties - " + new Date());
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

	public void updateProperties() {
		currentProperties.clear();
		try {
			FileReader reader = new FileReader(propertyFile);
			currentProperties.load(reader);
			reader.close();
		} catch (IOException e) {

		}
	}
}
