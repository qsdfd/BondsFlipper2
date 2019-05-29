
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SummaryClient {

	private static final String SUMMARY_URL = "http://localhost:3000/summary";

	public Boolean shouldBot;
	public Boolean fixed;
	public Boolean isCalcReliable;
	public Integer convert;
	public Integer buy;
	public Integer sell;
	public Integer breakMin;
	public Integer breakMax;

	public String status;
	public boolean isOk;
	
	public String state;

	public void update() {
		this.status = "updating ...";

		try {
			String response = Utils.readUrl(SUMMARY_URL);
			JsonObject obj = new JsonParser().parse(response).getAsJsonObject();

			shouldBot = obj.getAsJsonObject("fixed").getAsJsonPrimitive("bot").getAsBoolean();
			if (shouldBot) {
				fixed = obj.getAsJsonObject("fixed").getAsJsonPrimitive("fixed").getAsBoolean();
				breakMin = obj.getAsJsonObject("fixed").getAsJsonPrimitive("break_min").getAsInt();
				breakMax = obj.getAsJsonObject("fixed").getAsJsonPrimitive("break_max").getAsInt();

				isCalcReliable = obj.getAsJsonObject("calc").getAsJsonPrimitive("isCalcReliable").getAsBoolean();
				convert = obj.getAsJsonPrimitive("convert").getAsInt();

				if (fixed) {
					buy = obj.getAsJsonObject("fixed").getAsJsonPrimitive("buy").getAsInt();
					sell = obj.getAsJsonObject("fixed").getAsJsonPrimitive("sell").getAsInt();
				} else if (isCalcReliable) {
					buy = obj.getAsJsonObject("calc").getAsJsonObject("sug").getAsJsonPrimitive("buy").getAsInt();
					sell = obj.getAsJsonObject("calc").getAsJsonObject("sug").getAsJsonPrimitive("sell").getAsInt();
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

		state = updateState();
		status = "done updating";
	}

	public String updateState() {
		return "\nshouldBot: " + shouldBot + "\n" + "isOk: " + isOk + "\n" + "fixed: " + fixed + "\n"
				+ "isCalcReliable: " + isCalcReliable + "\n" + "convert: " + convert + "\n" + "buy: " + buy + "\n"
				+ "sell: " + sell + "\n" + "breakMin: " + breakMin + "\n" + "breakMax: " + breakMax + "\n";
	}
}
