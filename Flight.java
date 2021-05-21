import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

public class Flight {
	public int flightNo;
	public Timestamp deptDate;
	public Timestamp arrDate;
	public int seats;
	public int firstClassSeats;
	public int economyClassSeats;
	public int maxFirstClassSeats;
	public int maxEconomyClassSeats;
	public Airport departureAirport;
	public Airport destinationAirport;
	public String airlineCode;
	public Airline airline;
	public double distance; //in miles


	public Flight(int flightNo,Timestamp deptDate, Timestamp arrDate, int maxFirstClassSeats, int maxEconomyClassSeats, int firstClassSeats,int economyClassSeats, Airport departureAirport, Airport destinationAirport, Airline airline) {
		this.flightNo=flightNo;
		this.deptDate=deptDate;
		this.arrDate=arrDate;
		this.firstClassSeats=firstClassSeats;
		this.economyClassSeats=economyClassSeats;
		seats=this.firstClassSeats+this.economyClassSeats;
		this.departureAirport=departureAirport;
		this.destinationAirport=destinationAirport;
		this.maxEconomyClassSeats = maxEconomyClassSeats;
		this.maxFirstClassSeats = maxFirstClassSeats;
		this.airlineCode=airline.getCode();
		this.airline=airline;
		int[] deptCoords=departureAirport.getCoords();
		int[] destCoords=destinationAirport.getCoords();
		this.distance=Math.sqrt(Math.pow(deptCoords[0]-destCoords[0],2)+Math.pow(deptCoords[1]-destCoords[1],2));
	}
	
	public int[] getSeats() {
		int[] arr= {firstClassSeats,economyClassSeats};
		return (arr); //returns an array. first index=how many firstClassSeats available, second index=economy seats available
	}
	
	public void reserveSeat(String classID, String action) { //used for reserving and unreserving
		if(action=="reserve") {
			if(classID=="firstClass") {
				firstClassSeats--;
			}
			else if(classID=="economyClass") {
				economyClassSeats--;
			}
		}
		else if(action=="unreserve") {
			if(classID=="firstClass") {
				firstClassSeats++;
			}
			else if(classID=="economyClass") {
				economyClassSeats++;
			}
		}
		
		
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Flight flight = (Flight) o;
		return flightNo == flight.flightNo && seats == flight.seats && firstClassSeats == flight.firstClassSeats && economyClassSeats == flight.economyClassSeats && maxFirstClassSeats == flight.maxFirstClassSeats && maxEconomyClassSeats == flight.maxEconomyClassSeats && Double.compare(flight.distance, distance) == 0 && Objects.equals(deptDate, flight.deptDate) && Objects.equals(arrDate, flight.arrDate) && Objects.equals(departureAirport, flight.departureAirport) && Objects.equals(destinationAirport, flight.destinationAirport) && Objects.equals(airlineCode, flight.airlineCode) && Objects.equals(airline, flight.airline);
	}

	@Override
	public int hashCode() {
		return Objects.hash(flightNo, deptDate, arrDate, seats, firstClassSeats, economyClassSeats, maxFirstClassSeats, maxEconomyClassSeats, departureAirport, destinationAirport, airlineCode, airline, distance);
	}
}
