import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.concurrent.TimeUnit;

import org.osbot.rs07.api.GrandExchange.Box;
import org.osbot.rs07.api.GrandExchange.Status;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(name = "BondsFlipper", author = "dokato", version = 1.0, info = "", logo = "")
public class Main extends Script {

	private static final Color standardTxtColor = new Color(255, 255, 255);

	private boolean startb = true;

	private long timeBegan;
	private long timeRan;
	private long timeReset;
	private long timeSinceReset;
	private long timeLoggedIn;
	private long timeOffline;

	private String status;

	private Breaker breaker;
	private SummaryClient sc;

	private static final int BOND = 13190;
	private static final int BOND_UNTRADEBALE = 13192;

	private int cashInInv;

	@Override
	public void onStart() {
		resetTime();

		sc = new SummaryClient();
		breaker = new Breaker(sc);
		breaker.exchangeContext(getBot());
		bot.getRandomExecutor().overrideOSBotRandom(breaker);
	}

	public int onLoop() throws InterruptedException {
		status = "Main loop started";

		if (getClient().isLoggedIn()) {
			if (isConvertingBondsNeeded()) {
				convertBonds();
			} else {
				if (isGEInterfaceOpen()) {
					abortBoxPricesIfNeeded();
					if (isSomeThingBoughtOrSoldOrCancelled()) {
						collectStuff();
					} else if (isSellingBondNeeded()) {
						sell();
					} else if (hasToBuyMore()) {
						buy();
					} else {
						calcProfits();
						breaker.shouldBreak = true;
					}
				} else {
					openGEInterface();
				}
			}
		}

		return 0;
	}

	@Override
	public void onPaint(Graphics2D g1) {

		if (this.startb) {
			this.startb = false;
			this.timeBegan = System.currentTimeMillis();
			this.timeReset = timeBegan;
		}
		this.timeRan = (System.currentTimeMillis() - this.timeBegan);
		this.timeSinceReset = (System.currentTimeMillis() - this.timeReset);
		if (getClient().isLoggedIn()) {
			this.timeLoggedIn = (this.timeSinceReset - this.timeOffline);
		} else {
			this.timeOffline = (this.timeSinceReset - this.timeLoggedIn);
		}

		Graphics2D g = g1;

		int startY = 65;
		int increment = 15;
		int value = (-increment);
		int x = 20;

		g.setFont(new Font("Arial", 0, 13));
		g.setColor(standardTxtColor);
		drawString(g, "Acc: " + getBot().getUsername().substring(0, getBot().getUsername().indexOf('@')), x,
				getY(startY, value += increment));
		drawString(g, "World: " + getWorlds().getCurrentWorld(), x, getY(startY, value += increment));
		value += increment;
		drawString(g, "Version: " + getVersion(), x, getY(startY, value += increment));
		drawString(g, "Runtime: " + ft(this.timeRan), x, getY(startY, value += increment));
		drawString(g, "Time logged in: " + ft(this.timeLoggedIn), x, getY(startY, value += increment));
		drawString(g, "Status: " + status, x, getY(startY, value += increment));
		value += increment;
		drawString(g, "Status breaker: " + breaker.status, x, getY(startY, value += increment));
		drawString(g, "Break for: " + ft(breaker.getBreakAfterTime()), x, getY(startY, value += increment));
		drawString(g, "Status client: " + sc.status, x, getY(startY, value += increment));
		value += increment;
		drawString(g, "Cash in inv: " + cashInInv, x, getY(startY, value += increment));
		
		drawString(g, sc.state, 560, 290);
	}

	private void drawString(Graphics2D g, String text, int x, int y) {
		int lineHeight = g.getFontMetrics().getHeight();
		for (String line : text.split("\n"))
			g.drawString(line, x, y += lineHeight);
	}

	@Override
	public void onMessage(Message message) throws InterruptedException {

	}

	@Override
	public void onExit() {

	}

	private int getY(int startY, int value) {
		return startY + value;
	}

	private void fillRect(Graphics2D g, Rectangle rect) {
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	private void resetTime() {
		this.timeReset = System.currentTimeMillis();
		this.timeLoggedIn = 0;
		this.timeOffline = 0;
	}

	private String ft(long duration) {
		String res = "---";

		if (duration > 0) {
			long days = TimeUnit.MILLISECONDS.toDays(duration);
			long hours = TimeUnit.MILLISECONDS.toHours(duration)
					- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
			long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
					- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
					- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
			if (days == 0L) {
				res = hours + ":" + minutes + ":" + seconds;
			} else {
				res = days + ":" + hours + ":" + minutes + ":" + seconds;
			}
		}

		return res;
	}

	private boolean isConvertingBondsNeeded() {
		status = "checking if bonds need to be converted";
		return !hasAnyBoxStatus(Status.FINISHED_BUY) && getInventory().contains(BOND_UNTRADEBALE)
				&& hasEnoughToConvert();
	}

	private boolean hasAnyBoxStatus(Status status) {

		boolean isInMemWorld = getWorlds().isMembersWorld();

		return getGrandExchange().getStatus(Box.BOX_1).equals(status)
				|| getGrandExchange().getStatus(Box.BOX_2).equals(status)
				|| getGrandExchange().getStatus(Box.BOX_3).equals(status)
				|| (isInMemWorld && getGrandExchange().getStatus(Box.BOX_4).equals(status))
				|| (isInMemWorld && getGrandExchange().getStatus(Box.BOX_5).equals(status))
				|| (isInMemWorld && getGrandExchange().getStatus(Box.BOX_6).equals(status))
				|| (isInMemWorld && getGrandExchange().getStatus(Box.BOX_7).equals(status))
				|| (isInMemWorld && getGrandExchange().getStatus(Box.BOX_8).equals(status));
	}

	private boolean hasEnoughToConvert() {
		return getInventory().contains("Coins") && getInventory().getItem("Coins").getAmount() >= sc.convert;
	}

	private void convertBonds() throws InterruptedException {
		if (isBondsInterfaceOpen()) {
			status = "Bonds interface is open";
			clickMouse(getWidgets().get(64, 41, 0));
			sleep(random(940, 1740));
		} else {
			status = "Need to open bonds interface";
			if (getWidgets().closeOpenInterface()) {
				sleep(random(750, 1284));
				getInventory().getItem(BOND_UNTRADEBALE).interact("Convert");
				sleep(random(9, 1741));
			}
		}
	}

	private boolean isBondsInterfaceOpen() {
		status = "checking if bonds interface is open";
		return getWidgets().isVisible(64, 3);
	}

	private void clickMouse(RS2Widget rs2Widget) {
		status = "Clicking confirm button to convert";
		getMouse().click(
				random((int) rs2Widget.getRectangleIgnoreIsHidden(true).getMinX(),
						(int) rs2Widget.getRectangleIgnoreIsHidden(true).getMaxX()),
				random((int) rs2Widget.getRectangleIgnoreIsHidden(true).getMinY(),
						(int) rs2Widget.getRectangleIgnoreIsHidden(true).getMaxY()),
				false);
	}

	private boolean isGEInterfaceOpen() {
		return getGrandExchange().isOpen();
	}

	private boolean isSomeThingBoughtOrSoldOrCancelled() {
		status = "Checking if any box is finished buying";
		return hasAnyBoxStatus(Status.FINISHED_BUY) || hasAnyBoxStatus(Status.FINISHED_SALE)
				|| hasAnyBoxStatus(Status.CANCELLING_BUY) || hasAnyBoxStatus(Status.CANCELLING_SALE);
	}

	private void collectStuff() throws InterruptedException {
		status = "collecting stuff";
		if (getGrandExchange().collect()) {
			sleep(random(1912, 2901));
		}
	}

	private boolean isSellingBondNeeded() {
		status = "checking if bonds need to be sold";
		return getInventory().contains(BOND) && isOpenBox();
	}

	private boolean isOpenBox() {
		status = "Checking if any available box is open";
		return hasAnyBoxStatus(Status.EMPTY);
	}

	private void sell() throws InterruptedException {
		status = "selling";
		getGrandExchange().sellItem(BOND, sc.sell, 1);
		sleep(random(1540, 2300));
	}

	private void buy() throws InterruptedException {
		status = "Buying";
		getGrandExchange().buyItem(BOND, "bond", sc.buy, 1);
		sleep(random(1189, 4300));
	}

	private boolean hasToBuyMore() {
		status = "Checking if has to buy more";
		return !getInventory().contains(BOND_UNTRADEBALE) && hasEnoughMoney() && isOpenBox();
	}

	private boolean hasEnoughMoney() {
		status = "Checking if has enough money to buy more";
		int minimalCashNeeded = (getAmountOfPendingBuys() * sc.convert) + (sc.buy + sc.convert);
		return getInventory().contains("Coins") && getInventory().getItem("Coins").getAmount() > minimalCashNeeded;
	}

	private int getAmountOfPendingBuys() {
		int amount = 0;
		Box[] boxes = new Box[] { Box.BOX_1, Box.BOX_2, Box.BOX_3, Box.BOX_4, Box.BOX_5, Box.BOX_6, Box.BOX_7,
				Box.BOX_8 };
		for (Box box : boxes)
			if (getGrandExchange().getStatus(box).equals(Status.PENDING_BUY))
				amount++;
		return amount;
	}

	private void openGEInterface() throws InterruptedException {
		status = "GE interface isnt openend, opening GE";
		getObjects().closest(10061, 10060).interact("Exchange");
		sleep(random(1741, 2156));
	}

	private void abortBoxPricesIfNeeded() throws InterruptedException {
		status = "getting perticular box that needs abort";

		if ((getGrandExchange().getStatus(Box.BOX_1).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_1).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_1) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_1) != sc.sell) {
			abortBox(getWidgets().get(465, 7, 2), "1");
		}
		if ((getGrandExchange().getStatus(Box.BOX_2).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_2).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_2) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_2) != sc.sell) {
			abortBox(getWidgets().get(465, 8, 2), "2");
		}
		if ((getGrandExchange().getStatus(Box.BOX_3).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_3).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_3) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_3) != sc.sell) {
			abortBox(getWidgets().get(465, 9, 2), "3");
		}
		if ((getGrandExchange().getStatus(Box.BOX_4).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_4).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_4) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_4) != sc.sell) {
			abortBox(getWidgets().get(465, 10, 2), "4");
		}
		if ((getGrandExchange().getStatus(Box.BOX_5).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_5).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_5) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_5) != sc.sell) {
			abortBox(getWidgets().get(465, 11, 2), "5");
		}
		if ((getGrandExchange().getStatus(Box.BOX_6).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_6).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_6) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_6) != sc.sell) {
			abortBox(getWidgets().get(465, 12, 2), "6");
		}
		if ((getGrandExchange().getStatus(Box.BOX_7).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_7).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_7) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_7) != sc.sell) {
			abortBox(getWidgets().get(465, 13, 2), "7");
		}
		if ((getGrandExchange().getStatus(Box.BOX_8).equals(Status.PENDING_BUY)
				|| getGrandExchange().getStatus(Box.BOX_8).equals(Status.PENDING_SALE))
				&& getGrandExchange().getItemPrice(Box.BOX_8) != sc.buy
				&& getGrandExchange().getItemPrice(Box.BOX_8) != sc.sell) {
			abortBox(getWidgets().get(465, 14, 2), "8");
		}

	}

	private void abortBox(RS2Widget rs2Widget, String box) throws InterruptedException {
		status = "aborting box " + box;
		rs2Widget.interact("Abort offer");
		sleep(random(1890, 2941));
	}

	private void calcProfits() {
		status = "getting cash in inv";
		if (getInventory().contains("Coins")) {
			cashInInv = getInventory().getItem("Coins").getAmount();
		}
	}
}