// Salah Malik
// JP Morgan Java Technical Test - Daily Trade Reporting Engine
// DailyTradeReportingEngine.java

import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DailyTradeReportingEngine {
	
	public static enum TradeType {   B,   S }
	public static enum Currency  { SGP, AED, SAR }
	
	public static class Entity implements Comparable<Entity> {
		private String name;
		private TradeType type;
		private float agreedFx;
		private Currency currency;
		private Date instructionDate;
		private Date settlementDate;
		private int numUnits;
		private float pricePerUnit;
		private float tradeAmount;
		
		public Entity(String name, TradeType type, float fx, Currency curr, String instDateStr, String settDateStr, int num, float ppu) 
			throws ParseException {
			this.name = new String(name);
			this.type = type;
			this.agreedFx = fx;
			this.currency = curr;
			this.instructionDate = new SimpleDateFormat("yyyy-MM-dd").parse(instDateStr);
			this.settlementDate  = new SimpleDateFormat("yyyy-MM-dd").parse(settDateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.settlementDate);
			if (this.currency == Currency.AED || this.currency == Currency.SAR) {
				if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
					calendar.add(Calendar.DATE, 2);
				else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					calendar.add(Calendar.DATE, 1);
			} else {
				if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					calendar.add(Calendar.DATE, 2);
				else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					calendar.add(Calendar.DATE, 1);
			}
			this.settlementDate = calendar.getTime();
			this.numUnits = num;
			this.pricePerUnit = ppu;
			this.tradeAmount = setTradeAmount();
		}
		
		public TradeType getTradeType() {
			return type;
		}
	
		public float getTradeAmount() {
			return tradeAmount;
		}
		
		private float setTradeAmount() {
			return pricePerUnit * numUnits * agreedFx;
		}
		
		@Override
		public int compareTo(Entity e) {
			return Float.compare(this.tradeAmount, e.tradeAmount);
		}
		
		public void printInfo(boolean printHeader) {
			if (printHeader) {
				System.out.println("Entity | Buy/Sell | Agreed Fx | Currency | Instruction Date | Settlement Date | Units | Price per unit | Trade Amount");
				System.out.println("---------------------------------------------------------------------------------------------------------------------");
			}
			SimpleDateFormat tradeDateFormat = new SimpleDateFormat("dd MMM yyyy");
			System.out.print("  " + name + "  |    " + type + "     |   ");
			System.out.printf("%.02f    |    ", agreedFx);
			System.out.print(currency + "   |    " + tradeDateFormat.format(instructionDate) + "   |    " + 
							 tradeDateFormat.format(settlementDate) + "  |  " + numUnits  + "  |    ");
			System.out.printf("$%.02f     |  $%.02f", pricePerUnit, tradeAmount);
		}
	}
	
	private static ArrayList<Entity> entityList = new ArrayList<Entity>();
	
	public DailyTradeReportingEngine() {}
	
	public static Entity getEntity(int index) {
		return entityList.get(index);
	}
	
	public static void addEntity(Entity entity) {
		entityList.add(entity);
	}
	
	public static void displayDailySettledIncoming() {
		float amount = 0.0f;
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			if (e.getTradeType() == TradeType.S) {
				amount += e.getTradeAmount();
			}
		}
		System.out.printf("Daily Settled Incoming Amount: $%.02f USD\n\n", amount);
	}
	
	public static void displayDailySettledOutgoing() {
		float amount = 0.0f;
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			if (e.getTradeType() == TradeType.B) {
				amount += e.getTradeAmount();
			}
		}
		System.out.printf("Daily Settled Outgoing Amount: $%.02f USD\n\n", amount);
	}
	
	public static void displayEntityRankLists() {
		displayEntityIncomingList();
		System.out.println();
		displayEntityOutgoingList();
		System.out.println();
	}
	
	public static void displayEntityIncomingList() {
		float amount = 0.0f;
		Collections.sort(entityList, Collections.reverseOrder());
		System.out.println("Incoming Amount Entity Rank List");
		System.out.println("Entity | Buy/Sell | Agreed Fx | Currency | Instruction Date | Settlement Date | Units | Price per unit | Trade Amount");
		System.out.println("---------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			if (e.getTradeType() == TradeType.S) {
				e.printInfo(false);
				System.out.println();
			}
		}
	}
	
	public static void displayEntityOutgoingList() {
		float amount = 0.0f;
		Collections.sort(entityList, Collections.reverseOrder());
		System.out.println("Outgoing Amount Entity Rank List");
		System.out.println("Entity | Buy/Sell | Agreed Fx | Currency | Instruction Date | Settlement Date | Units | Price per unit | Trade Amount");
		System.out.println("---------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			if (e.getTradeType() == TradeType.B) {
				e.printInfo(false);
				System.out.println();
			}
		}
	}
	
	public static void main(String args[]) {
		System.out.println("Daily Trade Reporting Engine Report\n");
		try {
			Entity foo = new Entity("foo", TradeType.B, 0.50f, Currency.SGP, "2016-01-01", "2016-01-02", 200, 100.25f);
			Entity bar = new Entity("bar", TradeType.S, 0.22f, Currency.AED, "2016-01-05", "2016-01-07", 450, 150.50f);
			Entity xyz = new Entity("xyz", TradeType.S, 0.32f, Currency.SAR, "2016-01-03", "2016-01-08", 300, 215.50f);
			DailyTradeReportingEngine.addEntity(foo);
			DailyTradeReportingEngine.addEntity(bar);
			DailyTradeReportingEngine.addEntity(xyz);
			DailyTradeReportingEngine.displayDailySettledIncoming();
			DailyTradeReportingEngine.displayDailySettledOutgoing();
			DailyTradeReportingEngine.displayEntityRankLists();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage() + "\n");
		} finally {
			System.out.println("End Report");
		}
	}
}
