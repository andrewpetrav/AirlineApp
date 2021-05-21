import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DB {
	private static final double minPriceRange = 101.0;
	private static final double maxPriceRange = 1001.0;
	public void registerCustomer(String email, String firstname, String lastname) {

		String mySQL = "insert into customer(email,firstname ,lastname) " +

				"values (?,?,?)\r\n";

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, email.trim());
			pStmt.setString(2, firstname.trim());
			pStmt.setString(3, lastname.trim());

			ResultSet rs = pStmt.executeQuery();

		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	public ArrayList<Address> getCustomerAddresses(String email) {

		String mySQL = "select * from address " + "where customeremail = ?";

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, email.trim());

			ResultSet rs = pStmt.executeQuery();

			ArrayList<Address> addresses = new ArrayList<Address>();

			while (rs.next()) {

//            	Address newAddress = new Address(rs.getString("customeremail"),rs.getString("street"), rs.getString("city"),rs.getString("state"),rs.getString("zip"));

//            	addresses.add(newAddress);

			}
			return addresses;
		} catch (SQLException e) {
			DebugSQLException(e);
		}

		return null;

	}

	public static Airport GetAirportByName(String iata) {
		String mySQL = "select * from airport where iata = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, iata);

			ResultSet rs = pStmt.executeQuery();
			rs.next();
			int[] p = { rs.getInt("xposition"), rs.getInt("yposition") };
			return new Airport(rs.getString("iata"), rs.getString("name"), rs.getString("country"), p);

		} catch (SQLException e) {
			DebugSQLException(e);
		}
		return null;
	}

	public ArrayList<CreditCard> GetCreditCard(String email) {
		String mySQL = "select * from creditcard where customeremail = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, email);

			ResultSet rs = pStmt.executeQuery();

			ArrayList<CreditCard> result = new ArrayList<CreditCard>();
			while (rs.next()) {

				SimpleDateFormat format = new SimpleDateFormat("MM/yy");

				CreditCard cc = new CreditCard(rs.getInt("addressid"), rs.getString("creditcard"),
						format.parse(rs.getString("expDate")), rs.getString("name"), rs.getString("company"),
						rs.getInt("id"));

				result.add(cc);
			}

			return result;
		} catch (SQLException e) {
			DebugSQLException(e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Address> GetAddresses(String email) {
		String mySQL = "select * from customeraddresses natural join address where customeremail = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, email);

			ResultSet rs = pStmt.executeQuery();

			ArrayList<Address> addr = new ArrayList<Address>();

			while (rs.next()) {

				addr.add(new Address(rs.getInt("addressid"), rs.getString("street"), rs.getString("city"),
						rs.getString("zip")));

			}
			return addr;

		} catch (SQLException e) {
			DebugSQLException(e);
		}
		return null;
	}

	public Customer containsCustomer(String email) {
		String mySQL = "select * from customer where email = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, email.trim());

			ResultSet rs = pStmt.executeQuery();
			rs.next();
			return new Customer(rs.getString("email"), rs.getString("lastname"), rs.getString("firstname"),
					GetAirportByName(rs.getString("homeairport")), GetAddresses(email), GetCreditCard(email));

		} catch (SQLException e) {
			DebugSQLException(e);
		}

		return null;
	}

	public static void AddCustomer(Customer c) {

		String mySQL = "insert into customer (email, firstname, lastname, homeairport) values (?,?,?,?)";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, c.email.trim());
			pStmt.setString(2, c.firstName.trim());
			pStmt.setString(3, c.lastName.trim());
			pStmt.setString(4, c.homeAirport.IATA);

			pStmt.execute();

			AddAddress(c, c.addresses.get(0));
			AddCreditCard(c, c.paymentInfo.get(0));
		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	public static void AddCreditCard(Customer c, CreditCard p) {
		String mySQL = "insert into creditcard (addressid, customeremail, creditcard, expdate, name,company) values (?,?,?,?,?,?) returning ID; ";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			DateFormat df = new SimpleDateFormat("mm/yyyy");

			pStmt.setInt(1, p.addressid);
			pStmt.setString(2, c.email.trim());
			pStmt.setString(3, p.cardNumber.trim());
			pStmt.setString(4, df.format(p.getExpDate()));
			pStmt.setString(5, p.name.toString());
			pStmt.setString(6, p.company.toString());

			ResultSet rs = pStmt.executeQuery();
			rs.next();

			p.setId(rs.getInt("id"));

		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	public static ArrayList<Airport> GetAirports() {

		String mySQL = "select * from airport";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			ResultSet rs = pStmt.executeQuery();

			ArrayList<Airport> airports = new ArrayList<Airport>();

			while (rs.next()) {

				int[] cords = { rs.getInt("xposition"), rs.getInt("yposition") };
				Airport newAirport = new Airport(rs.getString("iata"), rs.getString("name"), rs.getString("country"),
						cords);

				airports.add(newAirport);
			}
			return airports;

		} catch (SQLException e) {
			DebugSQLException(e);
		}

		return null;
	}

	public static void RemoveCreditCard(Customer customer, CreditCard cardAssociated) {

		String mySQL = "delete from creditcard where id = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setInt(1, cardAssociated.getId());

			ResultSet rs = pStmt.executeQuery();

		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	public static void RemoveAddress(Customer customer, Address selectionObject) {

		String mySQL = "delete from customeraddresses where Addressid = ?; delete from address where Addressid = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setInt(1, selectionObject.getAddressid());
			pStmt.setInt(2, selectionObject.getAddressid());
			pStmt.execute();

		} catch (SQLException e) {
			DebugSQLException(e);
		}
	}

	public static void AddAddress(Customer customer, Address address) {
		String mySQL = "insert into customeraddresses (customeremail, addressid) values (?,?)";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, customer.getEmail().trim());
			pStmt.setInt(2, address.getAddressid());

			pStmt.execute();

		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	public static void AddAddress(Address address) {
		String mySQL = "insert into address (street, city, zip) values (?,?,?) returning addressid";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, address.getAddress().trim());
			pStmt.setString(2, address.getCity().trim());
			pStmt.setString(3, address.getZip().trim());

			ResultSet rs = pStmt.executeQuery();
			ResultSet rsKeys = pStmt.getGeneratedKeys();
			rs.next();
			address.setAddressid(rs.getInt("addressid"));

		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	public static Address GetAddress(int addressid) {
		String mySQL = "select * from address where addressid = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setInt(1, addressid);

			ResultSet rs = pStmt.executeQuery();

			rs.next();
			return new Address(addressid, rs.getString("street"), rs.getString("city"), rs.getString("zip"));

		} catch (SQLException e) {
			DebugSQLException(e);
		}
		return null;
	}

	private static void DebugSQLException(SQLException e) {
		System.out.println(e.getMessage());

	}

	public static ArrayList<Booking> GetBookingsFor(Customer customer) {
		ArrayList<Booking> bookings = new ArrayList<Booking>();

		for (CreditCard creditCard : customer.paymentInfo) {

			String mySQL = "select * from booking where creditcardid = ?";

			try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
					PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

				pStmt.setInt(1, creditCard.getId());

				ResultSet rs = pStmt.executeQuery();

				while (rs.next()) {
					int id = rs.getInt("id");
					String bookingName = rs.getString("bookingName");
					String seattype = rs.getString("seattype");
					int creditcardid = rs.getInt("creditcardid");
					String customerEmail = rs.getString("customeremail");
					int flightNumber = rs.getInt("flightnumber");
					String airlineCode = rs.getString("airlinecode");

					Flight f = getFlight(flightNumber, airlineCode);

					Booking b = new Booking(bookingName, seattype, creditCard, f, customerEmail, id);
					bookings.add(b);
				}

				return bookings;

			} catch (SQLException e) {
				DebugSQLException(e);
			}

		}

		return null;

	}

	private static Flight getFlight(int flightNumber, String airlineCode) {
		String mySQL = "select * from flight where flightnumber = ? and airlinecode = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setInt(1, flightNumber);
			pStmt.setString(2, airlineCode);

			ResultSet rs = pStmt.executeQuery();

			rs.next();

			Airport departureAirport = GetAirportByName(rs.getString("departureairport"));
			Airport distinationAirport = GetAirportByName(rs.getString("arrivalairport"));

			Airline airline = GetAirline(rs.getString("airlinecode"));

			return new Flight(rs.getInt("flightnumber"), rs.getTimestamp("departuretime"), rs.getTimestamp("arrivaltime"),
					rs.getInt("maximumseatsfirstclass"), rs.getInt("maximumseatseconomyclass"),
					rs.getInt("firstclassseatsleft"), rs.getInt("economyclassseatsleft"), departureAirport,
					distinationAirport, airline);

		} catch (SQLException e) {
			DebugSQLException(e);
		}
		return null;
	}

	private static Airline GetAirline(String airlineCode) {
		String mySQL = "select * from airline where airlinecode = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, airlineCode);

			ResultSet rs = pStmt.executeQuery();

			rs.next();
			return new Airline(rs.getString("airlinecode"), rs.getString("name"), rs.getString("country"));

		} catch (SQLException e) {
			DebugSQLException(e);
		}
		return null;
	}

	public static void DeleteBooking(Booking booking) {

		ChangeSeatsLeft(booking, +1);
		String mySQL = "delete from booking where id = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setInt(1, booking.getId());
			pStmt.execute();

		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	private static void ChangeSeatsLeft(Booking booking, int i) {
		String mySQL = "update flight " + "set firstclassseatsleft = firstclassseatsleft+?,"
				+ "economyclassseatsleft= economyclassseatsleft+?" + "where airlinecode = ? and flightnumber = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			int firstClassChange = 0;
			int economyChange = 0;
			if (booking.getClassID().equals("E")) {
				economyChange = i;
			} else {
				firstClassChange = i;
			}
			pStmt.setInt(1, firstClassChange);
			pStmt.setInt(2, economyChange);
			pStmt.setString(3, booking.getFlight().airlineCode);
			pStmt.setInt(4, booking.getFlight().flightNo);

			pStmt.execute();

		} catch (SQLException e) {
			DebugSQLException(e);
		}

	}

	public static Booking BookFlight(String bookingName, String type, CreditCard creditCard, String customerEmail, int flightNumber,
			String airlinecode) {

		String mySQL = "insert into booking (bookingName, seattype, creditcardid, customeremail, flightnumber, airlinecode) values (?, ?,?,?,?,?) returning id";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, bookingName);
			pStmt.setString(2, type);
			pStmt.setInt(3, creditCard.getId());
			pStmt.setString(4, customerEmail);
			pStmt.setInt(5, flightNumber);
			pStmt.setString(6, airlinecode);

			ResultSet rs = pStmt.executeQuery();
			ResultSet rsKeys = pStmt.getGeneratedKeys();
			rs.next();

			int id = rs.getInt("id");
			Flight flight = getFlight(flightNumber, airlinecode);
			Booking b = new Booking(bookingName, type, creditCard, flight, customerEmail, id);
			ChangeSeatsLeft(b, -1);
			return b;
		} catch (SQLException e) {
			DebugSQLException(e);
		}

		return null;

	}

	public static ArrayList<Flight> GetFlightsFrom(String fromAirport) {
		String mySQL = "select * from flight where departureairport = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, fromAirport);

			ResultSet rs = pStmt.executeQuery();
			ArrayList<Flight> flights = new ArrayList<Flight>();
			while (rs.next()) {
				Airport departureAirport = GetAirportByName(rs.getString("departureairport"));
				Airport distinationAirport = GetAirportByName(rs.getString("arrivalairport"));

				Airline airline = GetAirline(rs.getString("airlinecode"));

				flights.add(new Flight(rs.getInt("flightnumber"), rs.getTimestamp("departuretime"),rs.getTimestamp("arrivaltime"),
						rs.getInt("maximumseatsfirstclass"), rs.getInt("maximumseatseconomyclass"),
						rs.getInt("firstclassseatsleft"), rs.getInt("economyclassseatsleft"),
						departureAirport, distinationAirport, airline));
			}
			return flights;
		} catch (SQLException e) {
			DebugSQLException(e);
		}
		return null;
	}

	public static Price GetPriceForFlight(Flight f) {
		String mySQL = "select * from Price where airlinecode = ? and flightnumber = ?";

		String insertPrice = "insert into Price (AirlineCode, FlightNumber, SeatType, Price, DepartureTime) values (?,?, ?, ?, ?)";

		Price p = null;
		boolean inDb = false;
		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, f.airlineCode);
			pStmt.setInt(2, f.flightNo);


			Random r = new Random();
			double randomPrice = minPriceRange + (maxPriceRange - minPriceRange) * r.nextDouble();

			ResultSet rs = pStmt.executeQuery();

			double priceEconomy = 2.0 * randomPrice;
			double priceFirst = 10.0 * randomPrice;
			while (rs.next()) {
				inDb = true;
				if (rs.getString("seattype").equals("E")) {
					priceEconomy = rs.getDouble("price");
				} else {
					priceFirst = rs.getDouble("price");
				}
			}
			p = new Price(f, priceFirst, priceEconomy);
		} catch (SQLException e) {
			DebugSQLException(e);
		}

		if(!inDb) {
			try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
				 PreparedStatement pStmt = conn.prepareStatement(insertPrice)) {

				pStmt.setString(1, f.airlineCode);
				pStmt.setInt(2, f.flightNo);
				pStmt.setString(3, "E");
				pStmt.setDouble(4, p.getEconomyClassPrice());
				pStmt.setTimestamp(5, f.deptDate);

				pStmt.execute();

				pStmt.setString(1, f.airlineCode);
				pStmt.setInt(2, f.flightNo);
				pStmt.setString(3, "F");
				pStmt.setDouble(4, p.getFirstClassPrice());
				pStmt.setTimestamp(5, f.deptDate);

				pStmt.execute();

			} catch (SQLException e) {
				DebugSQLException(e);
			}
		}
		return p;
	}

	public static void updateMiles(String customeremail, String airlineCode, double miles) {
		String mySQL = "select  Miles from MilesProgram where CustomerEmail = ? and AirlineCode = ?";
		String updateMilesSql = "update MilesProgram set Miles = ? " +
				"where CustomerEmail = ? and AirlineCode = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
			 PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, customeremail);
			pStmt.setString(2, airlineCode);

			ResultSet rs = pStmt.executeQuery();
			ArrayList<Flight> flights = new ArrayList<Flight>();
			while (rs.next()) {
				double milesFromDb = rs.getDouble("Miles");

				miles += milesFromDb;
			}
		} catch (SQLException e) {
			DebugSQLException(e);
		}

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
			 PreparedStatement pStmt = conn.prepareStatement(updateMilesSql)) {

			pStmt.setDouble(1, miles);
			pStmt.setString(2, customeremail);
			pStmt.setString(3, airlineCode);

			int rowsUpdates = pStmt.executeUpdate();
		} catch (SQLException e) {
			DebugSQLException(e);
		}

		return;
	}

	public static List<MilesProgram> getMiles(String customeremail) {
		List<MilesProgram> milesPrograms = new ArrayList<>();

		String mySQL = "select  * from MilesProgram where CustomerEmail = ?";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
			 PreparedStatement pStmt = conn.prepareStatement(mySQL)) {

			pStmt.setString(1, customeremail);

			ResultSet rs = pStmt.executeQuery();
			while (rs.next()) {
				double miles = rs.getDouble("Miles");
				String customer = rs.getString("CustomerEmail");
				String airlineCode = rs.getString("AirlineCode");
				MilesProgram milesProgram = new MilesProgram(DB.GetAirline(airlineCode), customeremail, miles);
				milesPrograms.add(milesProgram);
			}
		} catch (SQLException e) {
			DebugSQLException(e);
		}

		return milesPrograms;
	}

	public static void addMiles(String customeremail, String airlineCode, double miles) {
		String addMilesSql = "insert into MilesProgram (Miles, CustomerEmail, AirlineCode) values (?,?,?)";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
			 PreparedStatement pStmt = conn.prepareStatement(addMilesSql)) {

			pStmt.setDouble(1, miles);
			pStmt.setString(2, customeremail);
			pStmt.setString(3, airlineCode);

			pStmt.execute();

		} catch (SQLException e) {
			DebugSQLException(e);
		}
	}

	public static void removeMilesProgram(String customeremail, String airlineCode) {
		String removeMileProgram = "delete from MilesProgram where AirlineCode=? and CustomerEmail =? ";

		try (Connection conn = DriverManager.getConnection(Utility.url, Utility.user, Utility.password);
			 PreparedStatement pStmt = conn.prepareStatement(removeMileProgram)) {

			pStmt.setString(1, airlineCode);
			pStmt.setString(2, customeremail);

			pStmt.execute();

		} catch (SQLException e) {
			DebugSQLException(e);
		}
	}
}
