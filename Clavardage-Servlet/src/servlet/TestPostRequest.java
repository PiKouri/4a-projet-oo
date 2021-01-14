package servlet;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Deprecated
public class TestPostRequest {
	private static HttpURLConnection con;
	private static String baseURL;
	
	public static void main(String[] args) throws Exception{
		TestPostRequest.baseURL = "http://"+"192.168.56.1:8080/Clavardage-Servlet/";
		TestPostRequest.sendPost("checkIfExtern","");	
	}
	
	private static void sendPost(String action, String username) throws Exception {
		var url = baseURL;
        var urlParameters = "action="+action+"&username="+username;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        try {
            var myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            try (var wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }
            StringBuilder content;
            try (var br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String line;
                content = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            System.out.println(content.toString());
        } finally {
            con.disconnect();
        }
    }
	
	@SuppressWarnings("unused")
	@Deprecated
	private static void sendGet() throws Exception {
		String query = String.format("param1=%s&param2=%s", 
	             URLEncoder.encode("param1Value", "UTF-8"), 
	             URLEncoder.encode("param1Value", "UTF-8"));

	    URL url = new URL("http://"+"192.168.56.1:8080" + "?" + query);
	    
	    HttpClient client = HttpClient.newHttpClient();
       HttpRequest request = HttpRequest.newBuilder()
               .uri(url.toURI())
               .build();

       HttpResponse<String> response = client.send(request,
               HttpResponse.BodyHandlers.ofString());

       System.out.println(response.body());
	}
}
