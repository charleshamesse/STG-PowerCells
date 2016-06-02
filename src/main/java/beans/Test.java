package beans;

public class Test {
	private int id;
	private String type;
	private String component1;
	private String component2;
	private String voltage;
	private String current;
	private String dutycycle;
	private String frequency;
	private String losses;
	private String date;
	
	public Test(int id, String type, String component1, String component2,
			String voltage, String current, String dutycyle, String losses, String frequency, 
			String date) {
		super();
		this.id = id;
		this.type = type;
		this.component1 = component1;
		this.component2 = component2;
		this.voltage = voltage;
		this.current = current;
		this.dutycycle = dutycyle;
		this.frequency = frequency;
		this.losses = losses;
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getComponent1() {
		return component1;
	}
	public void setComponent1(String component1) {
		this.component1 = component1;
	}
	public String getComponent2() {
		return component2;
	}
	public void setComponent2(String component2) {
		this.component2 = component2;
	}
	public String getVoltage() {
		return voltage;
	}
	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}
	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
	public String getDutyCycle() {
		return dutycycle;
	}
	public void setDutyCycle(String dutycyle) {
		this.dutycycle = dutycyle;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getLosses() {
		return losses;
	}
	public void setLosses(String losses) {
		this.losses = losses;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
}
