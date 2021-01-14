package userInterface;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import agent.Agent;

public class PropertiesReader {
	public void getProperties() {
		InputStream inputStream = null;
		String propFileName = Agent.dir+"config.properties";
		try {
			Properties prop = new Properties();	
			//inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			inputStream = new FileInputStream(propFileName);
			prop.load(inputStream);
			// Get the property values
			boolean debug = Boolean.valueOf(prop.getProperty("debug"));
			int timeout = Integer.valueOf(prop.getProperty("timeout"));
			int broadcastPortNumber = Integer.valueOf(prop.getProperty("broadcastPortNumber"));
			int defaultPortNumber = Integer.valueOf(prop.getProperty("defaultPortNumber"));
			String presenceServerIPAddress = prop.getProperty("presenceServerIPAddress");
			String databaseFileName = prop.getProperty("databaseFileName");
			String logFileName = prop.getProperty("logFileName");
			if (debug) System.out.printf("PropertiesReader:\n"
					+ "debug=%b\n"
					+ "timeout=%d\n"
					+ "broadcastPortNumber=%d\n"
					+ "defaultPortNumber=%d\n"
					+ "presenceServerIPAddress=%s\n"
					+ "databaseFileName=%s\n"
					+ "logFileName=%s\n"
					+ "\n", 
					debug,timeout,broadcastPortNumber,defaultPortNumber,presenceServerIPAddress,databaseFileName,logFileName);
			Agent.debug=debug;
			Agent.timeout=timeout;
			Agent.broadcastPortNumber=broadcastPortNumber;
			Agent.defaultPortNumber=defaultPortNumber;
			Agent.presenceServerIPAddress=presenceServerIPAddress;
			Agent.databaseFileName=databaseFileName;
			Agent.logFileName=logFileName;
		} catch (Exception e) {
        	Agent.errorMessage(
					String.format("ERROR Can't read properties file (config.properties) at %s\n", propFileName), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
}
