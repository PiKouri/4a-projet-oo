package userInterface;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import agent.Agent;

public class PropertiesReader {
	public void getProperties() throws IOException {
		InputStream inputStream = null;
		String propFileName = "config.properties";
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
			InetAddress presenceServerIPAddress = InetAddress.getByName(prop.getProperty("presenceServerIPAddress"));
			String databaseFileName = prop.getProperty("databaseFileName");
			if (debug) System.out.printf("PropertiesReader:\n"
					+ "debug=%b\n"
					+ "timeout=%d\n"
					+ "broadcastPortNumber=%d\n"
					+ "defaultPortNumber=%d\n"
					+ "presenceServerIPAddress=%s\n"
					+ "databaseFileName=%s\n"
					+ "\n", 
					debug,timeout,broadcastPortNumber,defaultPortNumber,presenceServerIPAddress,databaseFileName);
			Agent.debug=debug;
			Agent.timeout=timeout;
			Agent.broadcastPortNumber=broadcastPortNumber;
			Agent.defaultPortNumber=defaultPortNumber;
			Agent.presenceServerIPAddress=presenceServerIPAddress;
			Agent.databaseFileName=databaseFileName;
		} catch (Exception e) {
			System.out.printf("ERROR : Can't read properties file (config.properties) at %s\n", propFileName);
			System.exit(-1);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
}
