
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Paths;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SummaryClient {

	private static final String SUMMARY_URL = "http://localhost:3000/summary";

	public static Boolean shouldBot;
	public static Boolean fixed;
	public static Boolean isCalcReliable;
	public static Long convert;
	public static Long buy;
	public static Long sell;

	public static String status;
	public static boolean isOk;

	public static void update() {
		status = "updating ...";

		try {
			String response = Utils.readUrl(SUMMARY_URL);
			JsonObject obj = new JsonParser().parse(response).getAsJsonObject();

			shouldBot = obj.getAsJsonObject("fixed").getAsJsonPrimitive("bot").getAsBoolean();
			if (shouldBot) {
				fixed = obj.getAsJsonObject("fixed").getAsJsonPrimitive("fixed").getAsBoolean();
				isCalcReliable = obj.getAsJsonObject("calc").getAsJsonPrimitive("isCalcReliable").getAsBoolean();
				convert = obj.getAsJsonPrimitive("convert").getAsLong();

				if (fixed) {
					buy = obj.getAsJsonObject("fixed").getAsJsonPrimitive("buy").getAsLong();
					sell = obj.getAsJsonObject("fixed").getAsJsonPrimitive("sell").getAsLong();
				} else if (isCalcReliable) {
					buy = obj.getAsJsonObject("calc").getAsJsonObject("sug").getAsJsonPrimitive("buy").getAsLong();
					sell = obj.getAsJsonObject("calc").getAsJsonObject("sug").getAsJsonPrimitive("sell").getAsLong();
				} else {
					throw new Exception("Err: Not fixed and calc not reliable");
				}
			} else {
				throw new Exception("Should Not bot");
			}

			isOk = true;

		} catch (Exception e) {
			shouldBot = false;
			status = "Err: " + Utils.getStacktrace(e);
			isOk = false;
		}

		status = "done updating";
	}

	public static String getState() {
		return "\nshouldBot: " + shouldBot + "\n" + "isOk: " + isOk + "\n" + "fixed: " + fixed + "\n"
				+ "isCalcReliable: " + isCalcReliable + "\n" + "convert: " + convert + "\n" + "buy: " + buy + "\n"
				+ "sell: " + sell + "\n" + "status: " + status + "\n";
	}

	public static void main(String[] args) throws Exception {
		update();

		System.out.println(getState());

	}
}
