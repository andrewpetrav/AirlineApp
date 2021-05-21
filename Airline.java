import java.util.Objects;

public class Airline {
	private String code;
	private String name;
	private String country;
	
	public Airline(String code, String name, String country) {
		this.code=code;
		this.name=name;
		this.country=country;
	}
	
	public String getCode() {
		return code;
	}
	
	public String toString() {
		return(code+": "+name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Airline airline = (Airline) o;
		return Objects.equals(code, airline.code) && Objects.equals(name, airline.name) && Objects.equals(country, airline.country);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name, country);
	}
}
