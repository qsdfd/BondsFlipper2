import org.osbot.rs07.script.RandomEvent;
import org.osbot.rs07.script.RandomSolver;
import org.osbot.rs07.script.ScriptManifest;

//Written by Vilius, all praise to him
//Change the name variable to whatever you want to be displayed when its executed
@ScriptManifest(author = "Vilius", info = "", logo = "", name = "Breaker", version = 0)
public class Breaker extends RandomSolver {
	
	private Utils utils;

	public Breaker() {
		super(RandomEvent.BREAK_MANAGER);
		this.utils = new Utils();
	}

	@Override
	public boolean shouldActivate() {
		Main.status = "checking if to break";
		return true;
	}

	@Override
	public int onLoop() throws InterruptedException {
		utils.logOut();
		return 0;
	}

}