package beans;

public class Transistor {
	private int id;
	private String Name;
	private String Vgsth;
	private String Gm;
	private String Cgd;
	private String Cgs;
	private String Cds;
	private String ROn;
	private String Ls;
	
	public Transistor(int id, String name, String vgsth, String gm, String cgd,
			String cgs, String cds, String ron, String ls) {
		super();
		this.id = id;
		Name = name;
		Vgsth = vgsth;
		Gm = gm;
		Cgd = cgd;
		Cgs = cgs;
		Cds = cds;
		ROn = ron;
		Ls = ls;
	}
	
	public Transistor() {
		
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getVgsth() {
		return Vgsth;
	}
	public void setVgsth(String vgsth) {
		Vgsth = vgsth;
	}
	public String getGm() {
		return Gm;
	}
	public void setGm(String gm) {
		Gm = gm;
	}
	public String getCgd() {
		return Cgd;
	}
	public void setCgd(String cgd) {
		Cgd = cgd;
	}
	public String getCgs() {
		return Cgs;
	}
	public void setCgs(String cgs) {
		Cgs = cgs;
	}
	public String getCds() {
		return Cds;
	}
	public void setCds(String cds) {
		Cds = cds;
	}
	public String getROn() {
		return ROn;
	}
	public void setROn(String ron) {
		ROn = ron;
	}
	public String getLs() {
		return Ls;
	}
	public void setLs(String ls) {
		Ls = ls;
	}
	
	
}