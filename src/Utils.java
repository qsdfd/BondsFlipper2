import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Utils {

	// from minutes to millis and then randomize ...
	public static long generateRadomTimestampBetweenMinutes(int min, int max) {
		long secondsToBreak = ThreadLocalRandom.current().nextLong(
				TimeUnit.MINUTES.toSeconds(min),
				TimeUnit.MINUTES.toSeconds(max));
		return TimeUnit.SECONDS.toMillis(secondsToBreak);
	}

	public static String getStacktrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static String readUrl(String urlString) throws Exception {
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

	public static void main(String[] args) {
//		System.out.println(TimeUnit.SECONDS.toMillis(ThreadLocalRandom.current().nextInt((10 * 60), (20 * 60))));
		System.out.println(generateRadomTimestampBetweenMinutes(0,6));
	}
}
