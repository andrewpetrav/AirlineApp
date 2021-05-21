import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataHolder {
	static ArrayList<Flight> flights = new ArrayList<Flight>();
	static ArrayList<Customer> customers = new ArrayList<Customer>();
	static ArrayList<Airline> airlines = new ArrayList<Airline>();
	private static ArrayList<Airport> airports = new ArrayList<Airport>();
	static boolean useDatabase = true;
	static DB db = new DB();

	public static void main(String[] args) {

		SimpleDateFormat sdfD = new SimpleDateFormat("MM/DD/YY HH:mm"); // this format includes day (for bookings)
		SimpleDateFormat sdf = new SimpleDateFormat("MM/YY");

		String date0String = ("04/19/2021 00:00");
		String date1String = ("04/19/2021 01:00");
		String date2String = ("04/19/2021 02:00");
		String date3String = ("04/19/2021 03:00");
		String date4String = ("04/19/2021 04:00");
		String date5String = ("04/19/2021 04:30");
		String date6String = ("04/19/2021 05:00");
		String date7String = ("04/19/2021 05:30");
		String date8String = ("04/19/2021 06:00");
		String date9String = ("04/19/2021 07:00");
		String date10String = ("04/19/2021 09:00");

		Timestamp date0 = null;
		Timestamp date1 = null;
		Timestamp date2 = null;
		Timestamp date3 = null;
		Timestamp date4 = null;
		Timestamp date5 = null;
		Timestamp date6 = null;
		Timestamp date7 = null;
		Timestamp date8 = null;
		Timestamp date9 = null;
		Timestamp date10 = null;
		try {
			date0 = (Timestamp) sdfD.parse(date0String);
			date1 = (Timestamp) sdfD.parse(date1String);
			date2 = (Timestamp) sdfD.parse(date2String);
			date3 = (Timestamp) sdfD.parse(date3String);
			date4 = (Timestamp) sdfD.parse(date4String);
			date5 = (Timestamp) sdfD.parse(date5String);
			date6 = (Timestamp) sdfD.parse(date6String);
			date7 = (Timestamp) sdfD.parse(date7String);
			date8 = (Timestamp) sdfD.parse(date8String);
			date9 = (Timestamp) sdfD.parse(date9String);
			date10 = (Timestamp) sdfD.parse(date10String);
		} catch (Exception e) {

		}

		// Coordinates
		int[] BHMCoords = { 100, 5 };
		int[] ATLCoords = { 90, 10 };
		int[] DFWCoords = { 60, 0 };
		int[] DENCoords = { 10, 80 };
		int[] ORDCoords = { 100, 100 };
		int[] LAXCoords = { 0, 50 };
		int[] CLTCoords = { 95, 55 };
		int[] IAHCoords = { 30, 10 };
		int[] LASCoords = { 20, 40 };
		int[] PHXCoords = { 30, 30 };

		// Airports
		Airport BHM = new Airport("BHM", "Birmingham�Shuttlesworth International Airport", "USA", "AL", BHMCoords);
		Airport ATL = new Airport("ATL", "Hartsfield-Jackson International Airport", "USA", "GA", ATLCoords);
		Airport DFW = new Airport("DFW", "Dallas/Fort Worth International Airport", "USA", "TX", DFWCoords);
		Airport DEN = new Airport("DEN", "Denver International Airport", "USA", "CO", DENCoords);
		Airport ORD = new Airport("ORD", "O'Hare International Airport", "USA", "IL", ORDCoords);
		Airport LAX = new Airport("LAX", "Los Angeles International Airport", "USA", "CA", LAXCoords);
		Airport CLT = new Airport("CLT", "Charlotte Douglas International Airport", "USA", "NC", CLTCoords);
		Airport IAH = new Airport("IAH", "George Bush Intercontinental Airport", "USA", "TX", IAHCoords);
		Airport LAS = new Airport("LAS", "McCarran International Airport", "USA", "NV", LASCoords);
		Airport PHX = new Airport("PHX", "Phoenix Sky Harbor International Airport", "USA", "AZ", PHXCoords);

		airports.add(BHM);
		airports.add(ATL);
		airports.add(DFW);
		airports.add(DEN);
		airports.add(ORD);
		airports.add(LAX);
		airports.add(CLT);
		airports.add(IAH);
		airports.add(LAS);
		airports.add(PHX);

		// Airlines
		Airline AA = new Airline("AA", "American Airlines", "USA");
		Airline DL = new Airline("DL", "Delta Airlines", "USA");
		Airline WN = new Airline("WN", "Southwest Airlines", "USA");
		Airline UA = new Airline("UA", "United Airlines", "USA");
		Airline AC = new Airline("AC", "Air Canada", "CAN");
		Airline AS = new Airline("AS", "Alaska Airlines", "USA");

		airlines.add(AA);
		airlines.add(DL);
		airlines.add(WN);
		airlines.add(UA);
		airlines.add(AC);
		airlines.add(AS);


		// Flights
		Flight Flight1 = new Flight(100, date0, date1, 50, 70, 50, 70, BHM, DFW, AA);
		Flight Flight2 = new Flight(101, date4, date6, 50, 70, 50, 70, ATL, DFW, AA);
		Flight Flight3 = new Flight(102, date0, date2, 50, 70, 50, 70, DEN, ORD, DL);
		Flight Flight4 = new Flight(103, date8, date10, 50, 70, 50, 70, BHM, DFW, DL);
		Flight Flight5 = new Flight(104, date6, date7, 50, 70, 50, 70, DEN, DFW, WN);
		Flight Flight6 = new Flight(105, date4, date8, 50, 70, 50, 70, ORD, BHM, WN);
		Flight Flight7 = new Flight(106, date1, date3, 50, 70, 50, 70, BHM, ORD, UA);
		Flight Flight8 = new Flight(107, date3, date10, 50, 70, 50, 70, ATL, DEN, UA);
		Flight Flight9 = new Flight(108, date0, date10, 50, 70, 50, 70, DFW, ATL, AC);
		Flight Flight10 = new Flight(109, date1, date4, 50, 70, 50, 70, DEN, ORD, AC);
		Flight Flight11 = new Flight(110, date6, date9, 50, 70, 50, 70, ATL, DFW, AS);
		Flight Flight12 = new Flight(111, date4, date9, 50, 70, 50, 70, ORD, ATL, AS);

		flights.add(Flight1);
		flights.add(Flight2);
		flights.add(Flight3);
		flights.add(Flight4);
		flights.add(Flight5);
		flights.add(Flight6);
		flights.add(Flight7);
		flights.add(Flight8);
		flights.add(Flight9);
		flights.add(Flight10);
		flights.add(Flight11);
		flights.add(Flight12);

		// Price
		Price price1 = new Price(Flight1, 100, 80);
		Price price2 = new Price(Flight2, 150, 60);
		Price price3 = new Price(Flight3, 250, 120);
		Price price4 = new Price(Flight4, 30, 10);
		Price price5 = new Price(Flight5, 160, 150);
		Price price6 = new Price(Flight6, 200, 110);
		Price price7 = new Price(Flight7, 1000, 10);
		Price price8 = new Price(Flight8, 90, 50);
		Price price9 = new Price(Flight9, 190, 130);
		Price price10 = new Price(Flight10, 80, 70);
		Price price11 = new Price(Flight11, 120, 100);
		Price price12 = new Price(Flight12, 140, 80);

		// Addresses
		Address address1 = new Address("123 Sesame Street", "Mahattan", "10001");
		Address address2 = new Address("813 North Timmy Street", "Dimsdale", "60555");
		Address address3 = new Address("5780 Joe Shmoe Street", "Austin", "56709");
		Address address4 = new Address("999 Placeholder Lane", "Phoenix", "71248");
		Address address5 = new Address("742 Evergreen Terrace", "Springfield", "12345");
		Address address6 = new Address("44 Street Street", "Kansas City", "44444");
		Address address7 = new Address("1600 Pennsylvania Avenue", "Washington D.C.", "20500");

		// ExpDates
		Date expDate1 = null;
		Date expDate2 = null;
		Date expDate3 = null;
		Date expDate4 = null;
		try {
			expDate1 = sdf.parse("12/21");
			expDate2 = sdf.parse("01/24");
			expDate3 = sdf.parse("08/29");
			expDate4 = sdf.parse("06/23");
		} catch (Exception e) {

		}

		// Credit Cards
		CreditCard creditCard1 = new CreditCard(address1.getAddressid(), "123456789", expDate1, "Elmo Muppet",
				"MasterCard", 5);
		CreditCard creditCard2 = new CreditCard(address2.getAddressid(), "111111111", expDate2, "Timmy Turner", "Amex",
				6);
		CreditCard creditCard3 = new CreditCard(address3.getAddressid(), "123456789", expDate3, "Steve Steveman", "JCB",
				7);
		CreditCard creditCard4 = new CreditCard(address7.getAddressid(), "343343343", expDate4, "Joe Biden", "Visa", 8);

		// Customers
		ArrayList<Address> c1address = new ArrayList<Address>();
		c1address.add(address1);
		ArrayList<CreditCard> c1 = new ArrayList<CreditCard>();
		c1.add(creditCard1);
		Customer customer1 = new Customer("elmo@pbs.org", "Muppet", "Elmo", LAS, c1address, c1);

		ArrayList<Address> c2address = new ArrayList<Address>();
		c1address.add(address2);
		ArrayList<CreditCard> c2 = new ArrayList<CreditCard>();
		c2.add(creditCard4);
		Customer customer2 = new Customer("joebiden@whitehouse.org", "Biden", "Joe", ORD, c2address, c2);

		customers.add(customer1);
		customers.add(customer2);

	}

	public static ArrayList<Flight> getFlights() {
		return flights;
	}

	public static Customer containsCustomer(String email) {// check if email already associated with a customer
		if (useDatabase == true) {
			return db.containsCustomer(email);
		} else {
			for (Customer customer : customers) {

				if (customer.getEmail().equals(email)) {
					return (customer);
				}
			}
		}

		return (null);

	}

	public static Airport containsIATA(String IATA) {// check if valid IATA, if it is return what airport it is IATA of;
														// otherwise, return null
		for (Airport airport : getAirports()) {
			if (airport.getIATA().equals(IATA)) {
				return (airport);
			}
		}
		return (null);
	}

	public static Airport containsAirportName(String name) {// check if valid IATA, if it is return what airport it is
															// IATA of; otherwise, return null
		for (Airport airport : getAirports()) {
			if (airport.getName().equals(name)) {
				return (airport);
			}
		}
		return (null);
	}

	public static void AddCustomer(Customer c) {
		if (useDatabase) {
			DB.AddCustomer(c);
		} else {
			DataHolder.customers.add(c);
		}

	}

	public static ArrayList<Airport> getAirports() {
		if (useDatabase)
			return DB.GetAirports();
		else
			return airports;
	}

}
