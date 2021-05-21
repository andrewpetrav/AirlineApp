
public class Price {
	private Flight flight;
	private double firstClassPrice;
	private double economyClassPrice;
	
	public Price(Flight flight, double firstClassPrice, double economyClassPrice) {
		this.flight=flight;
		this.firstClassPrice=firstClassPrice;
		this.economyClassPrice=economyClassPrice;
	}
	
	public Flight getFlight() {
		return flight;
	}
	public void setFlight(Flight flight) {
		this.flight=flight;
	}
	
	public double getFirstClassPrice() {
		return firstClassPrice;
	}
	public void setFirstClassPrice(double firstClassPrice) {
		this.firstClassPrice=firstClassPrice;
	}
	
	public double getEconomyClassPrice() {
		return economyClassPrice;
	}
	public void setEconomyClassPrice(double economyClassPrice) {
		this.economyClassPrice=economyClassPrice;
	}
}
