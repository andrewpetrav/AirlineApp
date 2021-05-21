import java.util.Arrays;
import java.util.Objects;

public class Airport {
	public String IATA;
	public String name;
	public String country;
	public String state;
	public int[] coords;
	
	public Airport(String IATA, String name, String country, String state, int[] coords) {
		this.IATA=IATA;
		this.name=name;
		this.country=country;
		this.state=state;
		this.coords=coords;
	}
	
	public Airport(String IATA, String name, String country, int[] coords) {
		this(IATA,name,country,null, coords);
	}
	public String getIATA() {
		return IATA;
	}
	public void setIATA(String IATA) {
		this.IATA=IATA;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country=country;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state=state; 
	}
	
	public int[] getCoords() {
		return coords;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Airport airport = (Airport) o;
		return Objects.equals(IATA, airport.IATA) && Objects.equals(name, airport.name) && Objects.equals(country, airport.country) && Objects.equals(state, airport.state) && Arrays.equals(coords, airport.coords);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(IATA, name, country, state);
		result = 31 * result + Arrays.hashCode(coords);
		return result;
	}
}
