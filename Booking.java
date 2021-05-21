
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class Booking {
	private String name;
	private String classID; // first class or economy
	private CreditCard creditCard;
	Flight flight;
	public String email; // how to ID
	private int id;
	// TODO

	public Booking(String classID, CreditCard card, Flight flight, String email, int id) {
		this.classID = classID;
		this.creditCard = card;
		this.flight = flight;
		this.email = email;
		this.id = id;
	}

	public Booking(String bookingName, String classID, CreditCard card, Flight flight, String email, int id) {
		this.name = bookingName;
		this.classID = classID;
		this.creditCard = card;
		this.flight = flight;
		this.email = email;
		this.id = id;
	}

	public String getClassID() {
		return classID;
	}

	public CreditCard creditCard() {
		return creditCard;
	}

	public Flight getFlight() {
		return flight;
	}

	public String getName() {
		return this.name;
	}

	public String getEmail() {
		return email;
	}

	public String toString() {
		// return("Booking from")
		return ("Booking key: " + name + " -> " + classID + " booking for Flight "+flight.airlineCode+"#" + flight.flightNo + " at " + flight.deptDate + " from "
				+ flight.departureAirport.name + " to " + flight.destinationAirport.name);
	}
	
	public static String pathString(ArrayList<Booking> p) { //used for flight searching
		//airport->airport->airport->...	Leave At	Arrive At	Total Time	Total Price(assuming all economy)
		String pathString=p.get(0).getFlight().departureAirport.IATA+"->";
		double price=0;
		int j=0;
		for(Booking e: p) {
			price+=DB.GetPriceForFlight(e.getFlight()).getEconomyClassPrice();
        	if(j++==p.size()-1) { //last iteration
	        	pathString+=e.getFlight().destinationAirport.IATA;
        	}
        	else {
	        	pathString+=e.getFlight().destinationAirport.IATA+"->";
        	}
		}
		pathString+=" ";
		java.sql.Timestamp aTime=p.get(p.size()-1).getFlight().arrDate;
		java.sql.Timestamp dTime=p.get(0).getFlight().deptDate;
		long alTime=aTime.getTime();
		long dlTime=dTime.getTime();
		long timeDiff=alTime-dlTime;
		long minDiff=(timeDiff/(1000*60))%60;
        long hoursDiff=(timeDiff/(1000*3600))%24;
        pathString+=hoursDiff+"hrs"+minDiff+"mins";
        
        pathString+=" $"+price;
		
		return pathString;
	}

	public int getId() {
		return id;
	}


	public static String generateRandomBookingName() {
		int count = 5;
		String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

}
