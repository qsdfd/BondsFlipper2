import java.util.concurrent.TimeUnit;

import org.osbot.rs07.script.RandomEvent;
import org.osbot.rs07.script.RandomSolver;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "", info = "", logo = "", name = "Breaker", version = 0)
public class Breaker extends RandomSolver {

	// tussen 5 en 25 mins random
	
	private long breakUntil;
	
	public Breaker() {
		super(RandomEvent.BREAK_MANAGER);
		refreshInterval();
	}


	@Override
	public boolean shouldActivate() {
		Main.status = "checking if need to break";
		
		if(timeToUpdate()) {
			refreshInterval();
		}
		
		log(SummaryClient.getState());
		
		return !SummaryClient.shouldBot || !SummaryClient.isOk || Main.shouldBreak;
	}


	@Override
	public int onLoop() throws InterruptedException {
		logOut();
		return 0;
	}
	
	private boolean timeToUpdate() {
		return System.currentTimeMillis() > breakUntil;
	}

	private void refreshInterval() {
		SummaryClient.update();
		breakUntil = System.currentTimeMillis() + generateRadomTimestampBetweenMinutes(10, 20);
	}


	
	
	private void logOut() throws InterruptedException {
		Main.status = "break loop started";
		if (getClient().isLoggedIn()) {
			Main.status = "checking if logout tab is open";
			if(!logoutTab.isOpen()) {
				Main.status = "opening logout tab";
				logoutTab.open();
			}else{
				Main.status = "logging out";
				logoutTab.logOut();
			}
			Main.status = "sleeping afer check clause";
			sleep(random(0, 2000));
		}
		Main.status = "break loop ended";
	}

	public static void main(String[] args) {

	}

}