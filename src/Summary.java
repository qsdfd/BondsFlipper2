
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Summary {
	
	private static final String SUMMARY_URL = "http://localhost:3000/summary";
	
	private static Boolean bot;
	private static Boolean fixed;
	private static Boolean isReliable;	
	private static Integer convert;
	private static Integer buy;
	private static Integer sell;
	
	private static JSONParser parser = new JSONParser();
	
	
	public static void update() throws Exception {
		String response = readUrl(SUMMARY_URL);
			
		JSONObject obj = (JSONObject)parser.parse(response);
		
		bot = (boolean)((JSONObject)obj.get("fixed")).get("bot");
		fixed = (boolean)((JSONObject)obj.get("fixed")).get("fixed");
		isReliable = (boolean)((JSONObject)obj.get("calc")).get("isReliable");
		convert = (int)obj.get("convert");
		
		buy = isReliable ? 4 : 2;
	}
	
	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	public static void main(String[] args) throws Exception {
		update();
	}
}
