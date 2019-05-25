import org.osbot.rs07.script.RandomEvent;
import org.osbot.rs07.script.RandomSolver;
import org.osbot.rs07.script.ScriptManifest;

//Written by Vilius, all praise to him
//Change the name variable to whatever you want to be displayed when its executed
@ScriptManifest(author = "Vilius", info = "", logo = "", name = "Breaker", version = 0)
public class Breaker extends RandomSolver {
	public Breaker() {
		super(RandomEvent.BREAK_MANAGER);
	}

	@Override
	public boolean shouldActivate() {
		//Condition for the break manager to be activated
		log("checking");
		return false;
	}

	@Override
	public int onLoop() throws InterruptedException {
		//Code which should be executed while the break manager is activated
		//example
		log("hi");
		return 0;
	}

}