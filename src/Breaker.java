import org.osbot.rs07.script.RandomEvent;
import org.osbot.rs07.script.RandomSolver;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "", info = "", logo = "", name = "Breaker", version = 0)
public class Breaker extends RandomSolver {
	
	private SummaryClient sc;
	
	public boolean shouldBreak;
	public long breakUntil;
	
	public String status;

	public Breaker(SummaryClient sc) {
		super(RandomEvent.BREAK_MANAGER);
		this.sc = sc;
		refreshInterval();
	}

	@Override
	public boolean shouldActivate() {
		status = "Breaker loop started";
		if (timeToUpdate()) {
			refreshInterval();
		}
		return !sc.shouldBot || !sc.isOk || shouldBreak;
	}

	@Override
	public int onLoop() throws InterruptedException {
		logOut();
		return 0;
	}
	
	public long getBreakAfterTime() {
		return breakUntil - System.currentTimeMillis();
	}

	private boolean timeToUpdate() {
		return System.currentTimeMillis() > breakUntil;
	}

	private void refreshInterval() {
		sc.update();
		status = "generating break time";
		breakUntil = System.currentTimeMillis()
				+ Utils.generateRadomTimestampBetweenMinutes(sc.breakMin, sc.breakMax);
		shouldBreak = false;
	}

	private void logOut() throws InterruptedException {
		status = "break loop started";
		if (getClient().isLoggedIn()) {
			status = "checking if logout tab is open";
			if (!logoutTab.isOpen()) {
				status = "opening logout tab";
				logoutTab.open();
			} else {
				status = "logging out";
				logoutTab.logOut();
			}
			status = "sleeping afer check clause";
			sleep(random(0, 10000));
		}
		status = "break loop ended";
	}
}