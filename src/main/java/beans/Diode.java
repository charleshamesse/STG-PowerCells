package beans;

public class Diode {
	private int id;
	private String name;
	private String Cd;
	private String Vd;
	
	public Diode(int id, String name, String cd, String vd) {
		super();
		this.id = id;
		this.name = name;
		Cd = cd;
		Vd = vd;
	}
	
	public Diode() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCd() {
		return Cd;
	}

	public void setCd(String cd) {
		Cd = cd;
	}

	public String getVd() {
		return Vd;
	}

	public void setVd(String vd) {
		Vd = vd;
	}
	
}
