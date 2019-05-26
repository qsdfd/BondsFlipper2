import org.osbot.rs07.script.RandomEvent;
import org.osbot.rs07.script.RandomSolver;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "", info = "", logo = "", name = "Breaker", version = 0)
public class Breaker extends RandomSolver {

	public Breaker() {
		super(RandomEvent.BREAK_MANAGER);
	}

	@Override
	public boolean shouldActivate() {
		Main.status = "checking if to break";
		return false;
	}

	@Override
	public int onLoop() throws InterruptedException {
		logOut();
		return 0;
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