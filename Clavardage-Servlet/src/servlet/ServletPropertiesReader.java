package servlet;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Deprecated
public class ServletPropertiesReader {
	public void getProperties() throws IOException {
		InputStream inputStream = null;
		String propFileName = "realPath"+"/WEB-INF/classes/servlet_config.properties";
		try {
			Properties prop = new Properties();	
			//inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			inputStream = new FileInputStream(propFileName);
			prop.load(inputStream);
			// Get the property values
			int timeout = Integer.valueOf(prop.getProperty("timeout"));
			int broadcastPortNumber = Integer.valueOf(prop.getProperty("broadcastPortNumber"));
			int defaultPortNumber = Integer.valueOf(prop.getProperty("defaultPortNumber"));
			String presenceServerSubnet = prop.getProperty("presenceServerSubnet");
			MyServlet.printAndLog(String.format("PropertiesReader:\n"
					+ "timeout=%d\n"
					+ "broadcastPortNumber=%d\n"
					+ "defaultPortNumber=%d\n"
					+ "presenceServerSubnet=%s\n"
					+ "\n",timeout,broadcastPortNumber,defaultPortNumber,presenceServerSubnet));
			MyServlet.timeout=timeout;
			MyServlet.broadcastPortNumber=broadcastPortNumber;
			MyServlet.defaultPortNumber=defaultPortNumber;
			MyServlet.presenceServerSubnet=presenceServerSubnet;
		} catch (Exception e) {
			MyServlet.errorMessage(
					String.format("ERROR Can't read properties file (config.properties) at %s\n", propFileName), e);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
}
