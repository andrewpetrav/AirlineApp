import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Timestamp;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class Customer {
	String email;
	String lastName;
	String firstName;
	Airport homeAirport;
	ArrayList<CreditCard> paymentInfo = new ArrayList<CreditCard>();
	ArrayList<Address> addresses = new ArrayList<Address>();
	HashMap<String, List<Booking>> bookings = new HashMap<>();
	ArrayList<MilesProgram> milesPrograms=new ArrayList<MilesProgram>();
	ArrayList<Address> addressesNotInUse = new ArrayList<Address>();
	ArrayList<Airline> airlinesNotPartOf=new ArrayList<Airline>();
	DialogWait wait=new DialogWait();

	Scanner sc = new Scanner(System.in); // scanner will be replaced by something more sophisticated
	JFrame frame = new JFrame();
	SimpleDateFormat sdf = new SimpleDateFormat("MM/YY");
	SimpleDateFormat sdfD = new SimpleDateFormat("MM/DD/YY");// HH:mm"); // this format includes day (for bookings)
	NumberFormat format = NumberFormat.getCurrencyInstance();

	public Customer(String email, String lastName, String firstName, Airport homeAirport,
			ArrayList<Address> homeAddress, ArrayList<CreditCard> creditCards) {
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
		this.homeAirport = homeAirport;
		this.paymentInfo = creditCards;
		airlinesNotPartOf=DataHolder.airlines; //starts as member of no airlines
		if (homeAddress != null) {
			for (int i = 0; i < homeAddress.size(); i++) {
				this.addresses.add(homeAddress.get(i));
			}
		}
		milesPrograms.addAll(DB.getMiles(email));
	}

	public String getEmail() {
		return email;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public Airport getHomeAirport() {
		return homeAirport;
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setHomeAirport(Airport homeAirport) {
		this.homeAirport = homeAirport;
	}
	
	private static HashMap sortByValues(HashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }
	
	public Airport searchForAirport() {
		// used for home and destination

		// JFrame frame = new JFrame();
		frame.setAlwaysOnTop(true);
		Airport[] options = new Airport[DataHolder.getAirports().size()];
		for (int i = 0; i < options.length; i++) {
			options[i] = DataHolder.getAirports().get(i);// .getName();
		}
		JComboBox<Airport> airportText = new JComboBox<Airport>(options);
		airportText.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Airport) {
					Airport airport = (Airport) value;
					StringBuilder sb = new StringBuilder();
					sb.append(airport.getName());
					setText(sb.toString());
				}
				return this;
			}
		});
		Airport airport = (Airport) airportText.getSelectedItem();
		return null;
		
	}

	private void addBookingToMap(Booking booking) {
		if(bookings.get(booking.getName())==null) {
			bookings.put(booking.getName(), new ArrayList<>());
		}
		List<Booking> bookingsPresent = bookings.get(booking.getName());
		bookingsPresent.add(booking);
		bookings.put(booking.getName(), bookingsPresent);
	}

	// book flight
	public void bookFlight(Booking booking) {
		// maybe need to add some error checking
		addBookingToMap(booking);
		//this.miles.addMiles(booking.getFlight().distance, booking.getFlight().airlineCode);
	}




	public void manageCreditCard() {
		// JFrame frame=new JFrame();
		Object[] options = { "Back", "Remove Credit Card", "View Credit Cards", "Add Credit Card" };
		Object selectionObject;
		while (true) {
			int n = JOptionPane.showOptionDialog(frame, "What would you like to do?", null,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if (n == 1) { // remove credit card
				// TODO selectionObject should store credit card object selected so can delete
				// directly
				frame.setAlwaysOnTop(true);
				Object[] removeOptions = new Object[paymentInfo.size()];
				for (int i = 0; i < removeOptions.length; i++) {
					removeOptions[i] = paymentInfo.get(i).toString();
				}
				selectionObject = JOptionPane.showInputDialog(frame, "Please select the card that you'd like to remove",
						"Menu", JOptionPane.PLAIN_MESSAGE, null, removeOptions, null);
				if(selectionObject==null) {
					break; //user hit cancel
				}
				else if (paymentInfo.size() == 1) { // would leave with 0 cards
					JOptionPane.showMessageDialog(null,
							"You must have at least one credit card! This action would leave you with 0 credit cards!",
							"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					String selectionString = selectionObject.toString();
					for (CreditCard card : paymentInfo) {
						if (card.toString().equals(selectionString)) {
							paymentInfo.remove(card);
							if(DataHolder.useDatabase){
								DB.RemoveCreditCard(this,card);
							}
							break;
						}
					}
				}
			} else if (n == 2) { // show credit cards
				JFrame frame = new JFrame();

				JPanel panel = new JPanel(new BorderLayout(5, 5));
				JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
				String lineSep=System.lineSeparator();
				StringBuilder result=new StringBuilder();
				for(CreditCard e: paymentInfo) { //show all addresses
					result.append(e.toString()).append(lineSep);
				}
				JOptionPane.showMessageDialog(null, result);

			} else if (n == 3) { // add credit card
				Address address = null;
				CreditCard creditCard;
				String cardNumber;
				Date expDate;
				String expDateMonth;
				String expDateYear;
				String name;
				String company;
				String addressString;
				String cityString;
				String zipString;
				/*
				 * String cardNumber; Date expDate; String name; String ccCompany;
				 */
				// now enter credit card
				JPanel panel2 = new JPanel(new BorderLayout(5, 5));
				JPanel label2 = new JPanel(new GridLayout(0, 1, 2, 2));
				label2.add(new JLabel("Card Number", SwingConstants.RIGHT));
				label2.add(new JLabel("Expiration Month", SwingConstants.RIGHT));
				label2.add(new JLabel("Expiration Year", SwingConstants.RIGHT));
				label2.add(new JLabel("Name", SwingConstants.RIGHT));
				label2.add(new JLabel("Credit Card Company", SwingConstants.RIGHT));
				panel2.add(label2, BorderLayout.WEST);
				Object[] months = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
				Object[] years = { "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };
				Object[] ccOptions = { "American Express", "Visa", "MasterCard", "Discover", "JCB" };
				JPanel controls2 = new JPanel(new GridLayout(0, 1, 2, 2));
				JTextField cardNumberText = new JTextField();
				JComboBox<Object> monthsText = new JComboBox<Object>(months);
				JComboBox<Object> yearsText = new JComboBox<Object>(years);
				JTextField nameText = new JTextField();
				JComboBox<Object> companyText = new JComboBox<Object>(ccOptions);

				controls2.add(cardNumberText);
				controls2.add(monthsText);
				controls2.add(yearsText);
				controls2.add(nameText);
				controls2.add(companyText);
				panel2.add(controls2, BorderLayout.CENTER);

				JOptionPane.showMessageDialog(frame, panel2, "Credit Card Info", JOptionPane.QUESTION_MESSAGE);
				cardNumber = cardNumberText.getText();
				expDateMonth = (String) monthsText.getSelectedItem();
				expDateYear = (String) yearsText.getSelectedItem();
				name = nameText.getText();
				company = (String) companyText.getSelectedItem();
				String expDateStr = expDateMonth + "/" + expDateYear;
				try {
					expDate = sdf.parse(expDateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					expDate = null;
				}
				if (cardNumber.isEmpty() || name.isEmpty()){
					JOptionPane.showMessageDialog(frame, "Card number or Name cannot be Empty", "Error", JOptionPane.ERROR_MESSAGE);
				break;
				}


				// Address
				JPanel panel = new JPanel(new BorderLayout(5, 5));
				panel.setPreferredSize(new Dimension(300 , 50));
				JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
				JPanel controls = new JPanel(new GridLayout(3, 2, 2, 2));

				label.add(new JLabel("Address", SwingConstants.RIGHT));
				label.add(new JLabel("City", SwingConstants.RIGHT));
				label.add(new JLabel("Zip", SwingConstants.RIGHT));
				JTextField addressText = new JTextField();
				JTextField cityText = new JTextField();
				JTextField zipText = new JTextField();
				controls.add(addressText);
				controls.add(cityText);
				controls.add(zipText);

				panel.add(label, BorderLayout.WEST);
				panel.add(controls, BorderLayout.CENTER);
				JOptionPane.showMessageDialog(frame, panel, "Address Info", JOptionPane.QUESTION_MESSAGE);
				addressString = addressText.getText();
				cityString = cityText.getText();
				zipString = zipText.getText();
				if (addressString.isEmpty() || cityString.isEmpty() || zipString.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Address Information cannot be Empty", "Error", JOptionPane.ERROR_MESSAGE);
					break;
				}
				Address add = new Address(addressString, cityString, zipString);
				creditCard = new CreditCard(add.getAddressid(), cardNumber, expDate, name, company, -1);

				if (DataHolder.useDatabase) {
					DB.AddCreditCard(this, creditCard);
				}

				paymentInfo.add(creditCard);

			} else if (n == 0) {// Back
				break;
			} else if (n == JOptionPane.CLOSED_OPTION) {
				System.exit(0);
			}
		}

	}

	public void manageAddress() {

		Object[] options = { "Back", "Remove Address", "View Addresses", "Add Address" };
		Object selectionObject;
		while (true) {
			int n = JOptionPane.showOptionDialog(frame, "What would you like to do?", null,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			int m = -1; // keep track if want to remove both address and cc
			if (n == 1) { /// remove address
				// TODO selectionObject should store credit card object selected so can delete
				// directly. CLEAN UP
				frame.setAlwaysOnTop(true);
				Object[] removeOptions = new Object[addresses.size()];
				for (int i = 0; i < removeOptions.length; i++) {
					removeOptions[i] = addresses.get(i);
				}
				selectionObject = JOptionPane.showInputDialog(frame,
						"Please select the address that you'd like to remove", "Menu", JOptionPane.PLAIN_MESSAGE, null,
						removeOptions, null);
				if (selectionObject == null) {
					break; // user hit cancel
				}
				boolean associatedWithCreditCard = false; // is this address associated with a card?
				CreditCard cardAssociated = null; // the card that it's associated with
				for (CreditCard card : paymentInfo) {
					if (card.getAddress().getAddressid() == ((Address) selectionObject).getAddressid()) {
						associatedWithCreditCard = true;
						cardAssociated = card;
						break;
					}
				}
				if(addresses.size()==1) {
					JOptionPane.showMessageDialog(null,"This would cause you to delete your last address! All customers must have at least one address.",
					"Error", JOptionPane.ERROR_MESSAGE);
				}
				if (associatedWithCreditCard) {// it IS associated with a credit card
					Object[] addressOptions = { "Cancel", "Remove Both" };
					m = JOptionPane.showOptionDialog(frame, "This address is associated with " + cardAssociated
							+ ". In order to remove this address, you would have to remove the credit card too.", null,
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, addressOptions, null);
					if (m == 1) { // remove both option
						if (paymentInfo.size() == 1) {// make sure not removing last credit card
							JOptionPane.showMessageDialog(null,
									"This would cause you to delete your last credit card! All customers must have at least one credit card.",
									"Error", JOptionPane.ERROR_MESSAGE);
							m = 0;
						} else {// deleting won't remove last cc
							if (DataHolder.useDatabase) {
								DB.RemoveCreditCard(this, cardAssociated);
							}
							paymentInfo.remove(cardAssociated); // remove card (address removed outside if statement
						}
					}
				}
				if (m != 0) { // m will = 0 when cancel option
					if (DataHolder.useDatabase) {
						DB.RemoveAddress(this, (Address) selectionObject);
					}
					addresses.remove(selectionObject); // remove address
					JOptionPane.showMessageDialog(null, "Successfully Removed!", "Task Completed",
							JOptionPane.INFORMATION_MESSAGE);
				}

			} else if (n == 2) {// view addreses
				JFrame frame = new JFrame();

				JPanel panel = new JPanel(new BorderLayout(5, 5));
				JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
				//String lineSep=System.lineSeparator();
				StringBuilder result=new StringBuilder();
				for(Address e: addresses) { //show all addresses
					result.append(e.toString()).append("\n");
				}
				JOptionPane.showMessageDialog(null, result);

			} else if (n == 3) { // add address
				JFrame frame = new JFrame();
				String homeAddress;
				String city;
				String zip;
				JPanel panel = new JPanel(new BorderLayout(5, 5));
				panel.setPreferredSize(new Dimension(400, 50));
				JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
				JPanel button = new JPanel();
				label.add(new JLabel("Home Address", SwingConstants.RIGHT));
				label.add(new JLabel("City", SwingConstants.RIGHT));
				label.add(new JLabel("ZIP", SwingConstants.RIGHT));
				panel.add(label, BorderLayout.WEST);
				JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
				JTextField homeAddressText = new JTextField();
				JTextField cityText = new JTextField();
				JTextField zipText = new JTextField();
				controls.add(homeAddressText);
				controls.add(cityText);
				controls.add(zipText);
				panel.add(controls, BorderLayout.CENTER);
				// WHY IS IT SO SMALL
				// Not anymore
				JOptionPane.showOptionDialog(frame, panel, "Add Address", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				homeAddress = homeAddressText.getText();
				city = cityText.getText();
				zip = zipText.getText();
				Address address = new Address(homeAddress, city, zip);

				if (DataHolder.useDatabase) {
					if(!address.getAddress().isEmpty() && !address.getCity().isEmpty() &&  !address.getZip().isEmpty()){
						DB.AddAddress(this, address);
					}
				}
				if(!address.getAddress().isEmpty() && !address.getCity().isEmpty() &&  !address.getZip().isEmpty()){
					addresses.add(address);
				}
			} else if (n == 0) {// back
				break;
			} else if (n == JOptionPane.CLOSED_OPTION) {
				System.exit(0);
			}
		}

	}

	public void manageFlights() {
		Object[] options = { "Back", "Cancel Booking", "View Bookings", "Book Flight" };
		Object selectionObject;
		this.bookings=new HashMap<>();
		if(DataHolder.useDatabase){
			addAllBookings(DB.GetBookingsFor(this));
		}
		while (true) {
			int n = JOptionPane.showOptionDialog(frame, "What would you like to do?", null,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if (n == 0) {// back
				break;
			} else if (n == 1) {// cancel flight
				// TODO selectionObject should store credit card object selected so can delete
				// directly
				ArrayList<Booking> bookings = new ArrayList<Booking>();
				if (DataHolder.useDatabase) {
					bookings = DB.GetBookingsFor(this);
				}
				frame.setAlwaysOnTop(true);
				Object[] removeOptions = new Object[bookings.size()];
				for (int i = 0; i < removeOptions.length; i++) {
					removeOptions[i] = bookings.get(i).toString();
				}
				selectionObject = JOptionPane.showInputDialog(frame,
						"Please select the booking that you'd like to remove", "Menu", JOptionPane.PLAIN_MESSAGE, null,
						removeOptions, null);

				if (selectionObject == null) {
					break; // user hit cancel
				} else {
					String selectionString = selectionObject.toString();
					for (Booking booking : bookings) {
						if (booking.toString().equals(selectionString)) {
							if (DataHolder.useDatabase) {
								for(MilesProgram milesProgram: this.milesPrograms) {
									if (milesProgram.getAirline().equals(booking.flight.airline)) { //if part of this airline's miles program
//													int pos = this.milesPrograms.indexOf(flights[b].airline);
//													this.milesPrograms.get(pos).alterMiles(flights[b].distance);
										milesProgram.alterMiles(-booking.flight.distance);
									}
								}
								DB.DeleteBooking(booking);
								List<Booking> cachedBookings = this.bookings.get(booking.getName());
								cachedBookings.remove(booking);
								this.bookings.put(booking.getName(), cachedBookings);
								bookings.remove(booking);
								break;
							}
						}
					}
				}
			} else if (n == 2) {// view flights
				this.ViewBookings();
			} else if (n == 3) {// book flight
				//TODO add return trip option
				
//				this.searchForFlight();
//				DB.BookFlight("E", this.paymentInfo.get(0), this.email, 100, "AA");
				JFrame frame=new JFrame();
				JPanel panel = new JPanel(new BorderLayout(5, 5));
				JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
				label.add(new JLabel("From", SwingConstants.RIGHT));
				label.add(new JLabel("Destination", SwingConstants.RIGHT));
				label.add(new JLabel("Month", SwingConstants.RIGHT));
				label.add(new JLabel("Day", SwingConstants.RIGHT));
				label.add(new JLabel("Years", SwingConstants.RIGHT));
				label.add(new JLabel("Seats", SwingConstants.RIGHT));


				//label.add(new JLabel("Return Trip", SwingConstants.RIGHT));

				panel.add(label, BorderLayout.WEST);
				JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));

				Airport fromAirports;
				Airport toAirports;
				Date leaveDate=null;
				Date returnDate=null;
				int nSeats=0;
				int nSeatsRet=0;
				//TODO add date
				//DatePicker datePIcker=new DatePicker();
				frame.setAlwaysOnTop(true);
				
				Airport[] fromAirportsObj = new Airport[DataHolder.getAirports().size()];
				for (int i = 0; i < fromAirportsObj.length; i++) {
					fromAirportsObj[i] = DataHolder.getAirports().get(i);//.getName();
				}
				//TODO DATE --make better
				Object[] months = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
				Object[] years = { "21", "22"};
				Object[] days31= {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
				Object[] days30= {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
				Object[] days28= {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"};
				Object[] numSeats= {"1","2","3","4","5","6","7","8","9","10"};
				JComboBox<Airport> fromText = new JComboBox<Airport>(fromAirportsObj);
				JComboBox<Airport> toText = new JComboBox<Airport>(fromAirportsObj);
				JComboBox<Object> monthsText = new JComboBox<Object>(months);
				JComboBox<Object> daysText = new JComboBox<Object>(days31);
				JComboBox<Object> yearsText = new JComboBox<Object>(years);
				JComboBox<Object> numSeatsText = new JComboBox<Object>(numSeats);

				fromText.setRenderer(new DefaultListCellRenderer() {
					@Override
					public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
						if (value instanceof Airport) {
							Airport airport = (Airport) value;
							StringBuilder sb = new StringBuilder();
							sb.append(airport.getName());
							setText(sb.toString());
						}
						return this;
					}
				});
				toText.setRenderer(new DefaultListCellRenderer() {
					@Override
					public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
						if (value instanceof Airport) {
							Airport airport = (Airport) value;
							StringBuilder sb = new StringBuilder();
							sb.append(airport.getName());
							setText(sb.toString());
						}
						return this;
					}
				});
				controls.add(fromText);
				controls.add(toText);
				controls.add(monthsText);
				controls.add(daysText);
				controls.add(yearsText);
				controls.add(numSeatsText);

				panel.add(controls, BorderLayout.CENTER);
				Airport from;
				Airport to;
				//Date date;
				int choice = JOptionPane.showOptionDialog(frame, panel, "Flight Search", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				if(choice!=2) {//user didn't hit cancel
					from = (Airport) fromText.getSelectedItem();
					to = (Airport) toText.getSelectedItem();
					String month = (String) monthsText.getSelectedItem();
					String day = (String) daysText.getSelectedItem();
					String year = (String) yearsText.getSelectedItem();
					String nSeatsStr= (String) numSeatsText.getSelectedItem();
					String dateStr = month+"/"+day+"/"+year;
					try {
						leaveDate = sdfD.parse(dateStr);
						nSeats=Integer.parseInt(nSeatsStr);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						dateStr = null;
					}
					if(to.equals(from)) { //make sure not wanting to go to same airport leaving from
						JOptionPane.showMessageDialog(null,
								"You cannot set your destination your departure airport!",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else {
						//JFrame frame = new JFrame();
						//Object[] options = { "Back", "Manage Payment Info", "Manage Addresses", "Mileage Program" };
						int ret= JOptionPane.showOptionDialog(frame, "Would you also like to book a return trip?", null,
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
						if(ret!=2){ //user didn't hit cancel
							if(ret==0) { //yes also book return		
								JFrame frameRet=new JFrame();
								JPanel panelRet = new JPanel(new BorderLayout(5, 5));
								JPanel labelRet = new JPanel(new GridLayout(0, 1, 2, 2));
					
								labelRet.add(new JLabel("Month", SwingConstants.RIGHT));
								labelRet.add(new JLabel("Day", SwingConstants.RIGHT));
								labelRet.add(new JLabel("Years", SwingConstants.RIGHT));
								labelRet.add(new JLabel("Seats",SwingConstants.RIGHT));
								//label.add(new JLabel("Return Trip", SwingConstants.RIGHT));

								panelRet.add(labelRet, BorderLayout.WEST);
								JPanel controlsRet = new JPanel(new GridLayout(0, 1, 2, 2));

								//TODO add date
								//DatePicker datePIcker=new DatePicker();
								frameRet.setAlwaysOnTop(true);
								JComboBox<Object> monthsRetText = new JComboBox<Object>(months);
								JComboBox<Object> daysRetText = new JComboBox<Object>(days31);
								JComboBox<Object> yearsRetText = new JComboBox<Object>(years);
								JComboBox<Object> numSeatsRetText = new JComboBox<Object>(numSeats);

								controlsRet.add(monthsRetText);
								controlsRet.add(daysRetText);
								controlsRet.add(yearsRetText);
								controlsRet.add(numSeatsRetText);

								panelRet.add(controlsRet, BorderLayout.CENTER);
								int retC = JOptionPane.showOptionDialog(frameRet, panelRet, "Return Trip", JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE, null, null, null);
								if(retC!=2) { //user didn't hit cancel
									String monthRet = (String) monthsRetText.getSelectedItem();
									String dayRet = (String) daysRetText.getSelectedItem();
									String yearRet = (String) yearsRetText.getSelectedItem();
									String nSeatsRetStr= (String) numSeatsRetText.getSelectedItem();
									String dateRetStr = monthRet+"/"+dayRet+"/"+yearRet;
									try {
										returnDate = sdfD.parse(dateRetStr);
										nSeatsRet=Integer.parseInt(nSeatsRetStr);
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										nSeatsRet=0;
										dateStr = null;
									}
									
								}
								
							}
							else if(ret==1) {//no return trip
								//do nothing
							}
							ArrayList<ArrayList<ArrayList<Booking>>> AAP=new ArrayList<ArrayList<ArrayList<Booking>>>(); //this is the grossest thing that I've ever seen. May God have mercy on whomever views this triple ArrayList
							
							AAP=findBestFlight(from, to, leaveDate,returnDate,nSeats,nSeatsRet);
							ArrayList<ArrayList<Booking>> allAvailablePaths =AAP.get(0);
							ArrayList<ArrayList<Booking>> allAvailablePathsRet =AAP.get(1);
							
							try {
								Iterator<ArrayList<Booking>> iter=allAvailablePaths.iterator();
								while(iter.hasNext()) {
									//int cutoffDay=leaveDate.getDay();
									if(iter.next().get(0).flight.deptDate.after(leaveDate)) {
										allAvailablePaths.remove(iter);
									}
								}
								Iterator<ArrayList<Booking>> iter2=allAvailablePathsRet.iterator();
								while(iter2.hasNext()) {
									//int cutoffDay=leaveDate.getDay();
									if(iter2.next().get(0).flight.deptDate.after(leaveDate)) {
										allAvailablePathsRet.remove(iter2);
									}
								}

							}catch(Exception e) {
								
							}
							
							
							try {
								if(allAvailablePaths.isEmpty()||allAvailablePathsRet.isEmpty()) {//no flights
									JOptionPane.showMessageDialog(null,
											"Sorry there are no available flights within those parameters",
											"Error", JOptionPane.ERROR_MESSAGE);
									allAvailablePaths=null;
									allAvailablePathsRet=null;
								}
							}catch(Exception e) {
								
							}
							
							if(!(allAvailablePaths==null)&&!(allAvailablePathsRet==null)){ //user didn't hit cancel at any point
								Object[] o = { "Back", "Sort By Price", "Sort By Length" };
								Object sO;
								int x = JOptionPane.showOptionDialog(frame, "How would you like to view the paths?", null,
										JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, o, null);
								if(x==0) {//user hit back
									//do nothing
								}
								else {
									Object[] paths=new Object[allAvailablePaths.size()];
									Object[] pathsRet=new Object[allAvailablePathsRet.size()];
									paths=toSort(x, paths,allAvailablePaths);
									pathsRet=toSort(x,pathsRet,allAvailablePathsRet);
									
									Object[] paths2=new Object[allAvailablePaths.size()];
									Object[] pathsRet2=new Object[allAvailablePathsRet.size()];
									JFrame frame2=new JFrame();
									JPanel panel2 = new JPanel(new BorderLayout(5, 5));
									JPanel label2 = new JPanel(new GridLayout(0, 1, 2, 2));
									label2.add(new JLabel("Trips", SwingConstants.RIGHT));
									label2.add(new JLabel("Return Trips", SwingConstants.RIGHT));
									panel2.add(label2, BorderLayout.WEST);
									JPanel controls2 = new JPanel(new GridLayout(0, 1, 2, 2));

									frame2.setAlwaysOnTop(true);
									
									JComboBox<Object> pathsText = new JComboBox<Object>(paths);
									pathsText.setRenderer(new DefaultListCellRenderer() {
										@Override
										public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
												boolean cellHasFocus) {
											super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
											if (value instanceof ArrayList<?>) {
												ArrayList<Booking> b = (ArrayList<Booking>) value;
												StringBuilder sb = new StringBuilder();
												sb.append(Booking.pathString(b));
												setText(sb.toString());
											}
											return this;
										}
									});
									JComboBox<Object> pathsRetText = new JComboBox<Object>(pathsRet);
									pathsRetText.setRenderer(new DefaultListCellRenderer() {
										@Override
										public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
												boolean cellHasFocus) {
											super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
											if (value instanceof ArrayList<?>) {
												ArrayList<Booking> b = (ArrayList<Booking>) value;
												StringBuilder sb = new StringBuilder();
												sb.append(Booking.pathString(b));
												setText(sb.toString());
											}
											return this;
										}
									});
									controls2.add(pathsText);
									controls2.add(pathsRetText);
									panel2.add(controls2, BorderLayout.CENTER);
									int pathChoice = JOptionPane.showOptionDialog(frame2, panel2, "Booking", JOptionPane.OK_CANCEL_OPTION,
											JOptionPane.QUESTION_MESSAGE, null, null, null);
									if(pathChoice!=2) { //user didnt hit cancel
										JFrame frame3=new JFrame();
										JPanel panel3 = new JPanel(new BorderLayout(15, 15));
										JPanel label3 = new JPanel(new GridLayout(10, 1, 2, 2));
										JPanel controls3 = new JPanel(new GridLayout(10, 1, 2, 2));
										ArrayList<Booking> pathSelected = (ArrayList<Booking>) pathsText.getSelectedItem();
										ArrayList<Booking> pathRetSelected = (ArrayList<Booking>) pathsRetText.getSelectedItem();

										
										Object[][] seatTypeText=new Object[pathSelected.size()][nSeats];
										Flight[][] flights=new Flight[pathSelected.size()][nSeats];
										String[][] seats=new String[pathSelected.size()][nSeats];
										Character[][] seatsC=new Character[pathSelected.size()][nSeats];
										Object[][] seatTypeRetText=new Object[pathRetSelected.size()][nSeatsRet];
										Flight[][] flightsRet=new Flight[pathRetSelected.size()][nSeatsRet];
										String[][] seatsRet=new String[pathRetSelected.size()][nSeatsRet];
										Character[][] seatsRetC=new Character[pathRetSelected.size()][nSeatsRet];
										
										//TODO this needs to be cleaned up so formatted properly but I don't have energy right now. Will come back to after final
										int i=0; //looping var
										for(Booking e: pathSelected) { //chose what type of seat for each flight
											Price flightPrices = DB.GetPriceForFlight(e.getFlight());
											
											
											for(int ii=0;ii<nSeats;ii++) {
												flights[i][ii]=e.getFlight();
												Object[] seatChoices= {"Flight #"+(i+1)+" Seat #"+(ii+1)+" Seat Type", "Economy Class $"+flightPrices.getEconomyClassPrice(),"First Class $"+flightPrices.getFirstClassPrice()};
												seatTypeText[i][ii]=new JComboBox<Object>(seatChoices);
												controls3.add((Component) seatTypeText[i][ii]);		
											}
											
											i++;
											
										}
										int k=0;
										for(Booking e: pathRetSelected) { //chose what type of seat for each flight
											Price flightRetPrices = DB.GetPriceForFlight(e.getFlight());
											for(int ii=0;ii<nSeatsRet;ii++) {
												flightsRet[k][ii]=e.getFlight();
												Object[] seatRetChoices= {"Flight #"+(i+1)+" Seat #"+(ii+1)+" Seat Type", "Economy Class $"+flightRetPrices.getEconomyClassPrice(),"First Class $"+flightRetPrices.getFirstClassPrice()};
												seatTypeRetText[k][ii]=new JComboBox<Object>(seatRetChoices);
												controls3.add((Component) seatTypeRetText[k][ii]);		
											}
											
											k++;
										}

										panel3.add(label3, BorderLayout.WEST);
										panel3.add(controls3, BorderLayout.CENTER);
										
										JOptionPane.showMessageDialog(frame3, panel3, "Flight Booking", JOptionPane.QUESTION_MESSAGE);
										for(int j=0;j<seats.length;j++) {
											for(int jj = 0;jj<seats[j].length;jj++) {
												seats[j][jj]=(String)((JComboBox<Airport>) seatTypeText[j][jj]).getSelectedItem();
												if(seats[j][jj].substring(1,2).equals("e")){
													seats[j][jj]="E"; //just to make sure user actually picked a seat type
												}
												else {
													seats[j][jj]=seats[j][jj].substring(0,1);
												}
												
											}
											
										}
										
										for(int j=0;j<seatsRet.length;j++) {
											for(int jj = 0;jj<seatsRet[j].length;jj++) {
												seatsRet[j][jj]=(String)((JComboBox<Airport>) seatTypeRetText[j][jj]).getSelectedItem();
												seatsRet[j][jj]=seatsRet[j][jj].substring(0,1);
											}
											
										}
										Object[] ccs = new Object[paymentInfo.size()];
										for (int a = 0; a < ccs.length; a++) {
											ccs[a] = paymentInfo.get(a).toString();
										}
										//TODO add cancel?
										Object cc = JOptionPane.showInputDialog(frame, "How would you like to pay for these flights?",
												"Menu", JOptionPane.PLAIN_MESSAGE, null, ccs, null);
										String selectionString = cc.toString();
										CreditCard payment=null;
										for (CreditCard card : paymentInfo) {
											if (card.toString().equals(selectionString)) {
												payment=card;
												break;
											}
										}
										String bookingName = Booking.generateRandomBookingName();
										for(int b=0; b<seatsC.length;b++) {
											for(int bb=0;bb<seatsC[b].length;bb++) {
												Booking newBooking = DB.BookFlight(bookingName, seats[b][bb], payment, this.email, flights[b][bb].flightNo, flights[b][bb].airlineCode);
												if(newBooking==null) {
													JOptionPane.showMessageDialog(frame, "ERROR: Unable to add booking");
												} else {
													addBookingToMap(newBooking);
													for(MilesProgram milesProgram: this.milesPrograms) {
														if (milesProgram.getAirline().equals(flights[b][bb].airline)) { //if part of this airline's miles program
//															int pos = this.milesPrograms.indexOf(flights[b].airline);
//															this.milesPrograms.get(pos).alterMiles(flights[b].distance);
															milesProgram.alterMiles(flights[b][bb].distance);
														}
													}
												}
											}
											
										}
										for(int b=0; b<seatsRetC.length;b++) {
											for(int bb=0;bb<seatsRetC[b].length;bb++) {
												Booking newBooking = DB.BookFlight(bookingName, seatsRet[b][bb], payment, this.email, flightsRet[b][bb].flightNo, flightsRet[b][bb].airlineCode);
												if(newBooking==null) {
													JOptionPane.showMessageDialog(frame, "ERROR: Unable to add booking");
												} else {
													addBookingToMap(newBooking);
													for(MilesProgram milesProgram: this.milesPrograms) {
														if (milesProgram.getAirline().equals(flightsRet[b][bb].airline)) { //if part of this airline's miles program
//															int pos = this.milesPrograms.indexOf(flights[b].airline);
//															this.milesPrograms.get(pos).alterMiles(flights[b].distance);
															milesProgram.alterMiles(flightsRet[b][bb].distance);
														}
													}
												}
											}
											
										}
										
				
									}
									
									
								}
								
							}
						}
						

					}

				}
				
			}
		}
	}

	private void addAllBookings(List<Booking> bookingsInDb) {
		for(Booking booking: bookingsInDb) {
			addBookingToMap(booking);
		}
	}
	
	public Object[] toSort(int x, Object[]paths, ArrayList<ArrayList<Booking>> allAvailablePaths) {
		final ArrayList<ArrayList<Booking>> allAvailablePaths1 =allAvailablePaths;
		SwingWorker<Void,Void> mySwingWorker1=new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() throws Exception{
				sort(x,allAvailablePaths1, paths);
				wait.close();
				return null;

			}

		};
		mySwingWorker1.execute();
		wait.makeWait();
		return paths;
	}
	public void sort(int x,ArrayList<ArrayList<Booking>> allAvailablePaths1,Object[] paths ) {
		if(x==1) { //sort by price
			HashMap<ArrayList<Booking>, Double> tripPrices=new HashMap<ArrayList<Booking>,Double>();
			for(ArrayList<Booking> e:allAvailablePaths1) {
				double price=0;
				for(Booking b: e) {
					price+=DB.GetPriceForFlight(b.getFlight()).getEconomyClassPrice();
				}
				tripPrices.put(e, price);
				
			}
			 Map<ArrayList<Booking>, Double> map = sortByValues(tripPrices); 
		      Set set = map.entrySet();
		      Iterator iterator = set.iterator();
		      int i=0;
		      while(iterator.hasNext()) {
		           Map.Entry me = (Map.Entry)iterator.next();
		           paths[i]=me.getKey();
		           i++;								      
		      }
		      
		}
		else if(x==2) { //sort by length
			HashMap<ArrayList<Booking>, Long> tripLengths=new HashMap<ArrayList<Booking>,Long>();
			for(ArrayList<Booking> e:allAvailablePaths1) {
				double len=0;
				java.sql.Timestamp aTime=e.get(e.size()-1).getFlight().arrDate;
				java.sql.Timestamp dTime=e.get(0).getFlight().deptDate;
				long alTime=aTime.getTime();
				long dlTime=dTime.getTime();
				long timeDiff=alTime-dlTime;
				tripLengths.put(e,timeDiff);
			}
			Map<ArrayList<Booking>, Double> map = sortByValues(tripLengths); 
		      Set set = map.entrySet();
		      Iterator iterator = set.iterator();
		      int i=0;
		      while(iterator.hasNext()) {
		           Map.Entry me = (Map.Entry)iterator.next();
		           paths[i]=me.getKey();
		           i++;								      
		      }
		}
	}
	private void ViewBookings() {

		bookings = new HashMap<>();
		addAllBookings(DB.GetBookingsFor(this));
		if(bookings.isEmpty()) {
			//if has no bookings, don't show
			JOptionPane.showMessageDialog(null,
					"You do not have any bookings!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			JFrame frame = new JFrame();

			JPanel panel = new JPanel(new BorderLayout(5, 5));
			JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
			String lineSep=System.lineSeparator();
			StringBuilder result=new StringBuilder();
			if(bookings.isEmpty()) {
				JOptionPane.showMessageDialog(null,
						"You  do not currently have any bookings!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			else {
				for(String e: bookings.keySet()) { //show all bookings
					result.append(e).append(lineSep);
					List<Booking> bookingsToShow = bookings.get(e);
					for(Booking b: bookingsToShow) {
						result.append("\t").append(b.toString()).append(lineSep);
					}
				}
				JOptionPane.showMessageDialog(null, result);
			}
			
		}
		
	}


	public String toString() {
		return "Customer [Email=" + email + ", last name=" + lastName + ", firstName=" + firstName + "]";
	}

	public ArrayList<ArrayList<ArrayList<Booking>>> findBestFlight(Airport from, Airport to, Date date, Date retDate, int seats, int seatsRet) {
		double maxPrice1=0;
		int maxConnections1 = 0;
		while(true) {
			// Max $ willing to pay
			String maxPriceStr = JOptionPane.showInputDialog("Please enter the max amount you are willing to pay");
			if(maxPriceStr==null) { //user hit cancel
				return null;
			}
			try {
				maxPriceStr=maxPriceStr.replaceAll("[$,]", "");
				maxPrice1=Double.parseDouble(maxPriceStr);	
				break;
			}catch(Exception e) {
				JOptionPane.showMessageDialog(null,
						"Invalid Input!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		while(true) {
			String maxConnectionsStr = JOptionPane.showInputDialog("Please enter the max amount of connections that you'd like your trip to have");
			if(maxConnectionsStr==null) { //user hit cancel
				return null;
			}
			try {
				maxConnections1=Integer.parseInt(maxConnectionsStr);
				break;
			}catch(Exception e) {
				JOptionPane.showMessageDialog(null,
						"Invalid Input!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		final int maxConnections=maxConnections1;
		final double maxPrice=maxPrice1;
		ArrayList<ArrayList<Booking>> allAvailablePaths = new ArrayList<ArrayList<Booking>>();
		ArrayList<ArrayList<Booking>> allAvailablePathsRet = new ArrayList<ArrayList<Booking>>();

//		DialogWait wait=new DialogWait();
		SwingWorker<Void,Void> mySwingWorker=new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() throws Exception{
				GetFlight(paymentInfo.get(0), new ArrayList<Booking>(), null, from, to, date, maxConnections, maxPrice, seats, allAvailablePaths);
				GetFlight(paymentInfo.get(0), new ArrayList<Booking>(), null, to, from, retDate, maxConnections, maxPrice,seatsRet, allAvailablePathsRet);
				wait.close();
				return null;
			}
			
		};
		mySwingWorker.execute();
		wait.makeWait();
		ArrayList<ArrayList<ArrayList<Booking>>> killMe=new ArrayList<ArrayList<ArrayList<Booking>>>();
		killMe.add(allAvailablePaths);
		killMe.add(allAvailablePathsRet);
		return(killMe);
	}

	public boolean GetFlight(CreditCard card, ArrayList<Booking> route, Flight currentFlight, Airport from, Airport to,
			Date date, int maxConnections, double Price, int seats,ArrayList<ArrayList<Booking>> allAvailablePaths) {
		if (currentFlight != null) {
			if (currentFlight.destinationAirport.getIATA().equals(to.getIATA())) {
				allAvailablePaths.add(route);
				return true;
			}
		}
		if (Price < 0) {
			return false;
		}
		if(maxConnections<=0) {
			return false; //too many connections
		}
		ArrayList<Flight> flights = null;
		if (currentFlight == null) {
			flights = DB.GetFlightsFrom(from.getIATA());
		} else {
			flights = DB.GetFlightsFrom(currentFlight.destinationAirport.IATA);
		}

		for (Flight f : flights) {
			if(route.isEmpty()||(f.arrDate.getTime()<route.get(route.size()-1).flight.deptDate.getTime())) {//don't book flights you can't make
				
				if (f.destinationAirport.IATA.equals(from.getIATA()))
					continue;
				boolean Skip = false;
				for (Booking b : route) {
					if (b.getFlight().airlineCode.equals(f.airlineCode) && b.getFlight().flightNo == f.flightNo) {
						Skip = true;
					}
				}

				if (Skip)
					continue;

				Price flightPrices = DB.GetPriceForFlight(f);
				if (f.firstClassSeats+f.economyClassSeats >= seats && !route.contains(f)) {
					ArrayList<Booking> booking = new ArrayList<Booking>(route);
					booking.add(new Booking(null, card, f, this.email, -1));
					GetFlight(card, booking, f, from, to, f.arrDate,maxConnections--, Price - seats*flightPrices.getEconomyClassPrice(), seats,
							allAvailablePaths);

				}
			}
			
		}

		return false;

	}
	
	public void mileagePrograms() {
		
		Object[] options = { "Back", "Join Mileage Program", "Leave Mileage Program", "View Miles" };
		List<MilesProgram> customerMiles = DB.getMiles(this.email);
		for(MilesProgram milesProgram: customerMiles) {
			airlinesNotPartOf.remove(milesProgram.getAirline());
		}
		while(true) {
			JFrame frame = new JFrame();
			JPanel panel = new JPanel(new BorderLayout(5, 5));
			JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
			label.add(new JLabel("Airline", SwingConstants.RIGHT));
			panel.add(label, BorderLayout.WEST);
			JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));

			int n = JOptionPane.showOptionDialog(frame, "What would you like to do?", null,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

			if(n==0) { //back
				break;
			}
			else if(n==1) { //join mileage program

				Airline[] airlines = new Airline[airlinesNotPartOf.size()];
				for (int i = 0; i < airlinesNotPartOf.size(); i++) {
					airlines[i] = airlinesNotPartOf.get(i);
				}
				if (airlines.length != 0) {
					JComboBox<Airline> airlinesText = new JComboBox<Airline>(airlines);
					airlinesText.setRenderer(new DefaultListCellRenderer() {
						@Override
						public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
																	  boolean cellHasFocus) {
							super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
							if (value instanceof ArrayList<?>) {
								ArrayList<Airline> b = (ArrayList<Airline>) value;
								StringBuilder sb = new StringBuilder();
								sb.append(b.toString());
								setText(sb.toString());
							}
							return this;
						}
					});
					controls.add(airlinesText);
					panel.add(controls, BorderLayout.CENTER);
					int signUpresult = JOptionPane.showOptionDialog(frame, panel, "Join A Mileage Program", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (signUpresult != 2) { //user didn't hit cancel
						Airline chosen = (Airline) airlinesText.getSelectedItem();
						MilesProgram milesProgram = new MilesProgram(chosen, this.email, 0.0);
						milesPrograms.add(milesProgram);
						milesProgram.addMiles();
						airlinesNotPartOf.remove(chosen);
					}

				} else {
					JOptionPane.showMessageDialog(frame, "You have joined all the available miles program", "Info", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else if(n==2) { //leave mileage program
				if(milesPrograms.size()>=1) { //actually part of a mileage program
					Airline[] airlines=new Airline[milesPrograms.size()];
					for(int i=0; i<airlines.length; i++) {
						airlines[i]=milesPrograms.get(i).getAirline();
					}
					JComboBox<Airline> airlinesText = new JComboBox<Airline>(airlines);
					airlinesText.setRenderer(new DefaultListCellRenderer() {
						@Override
						public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
								boolean cellHasFocus) {
							super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
							if (value instanceof ArrayList<?>) {
								ArrayList<Airline> b = (ArrayList<Airline>) value;
								StringBuilder sb = new StringBuilder();
								sb.append(b.toString());
								setText(sb.toString());
							}
							return this;
						}
					});
					controls.add(airlinesText);
					panel.add(controls, BorderLayout.CENTER);
					int signUpresult = JOptionPane.showOptionDialog(frame, panel, "Leave A Mileage Program", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, null, null);
					if(signUpresult!=2){
						Airline chosen = (Airline) airlinesText.getSelectedItem();
						for (MilesProgram e : milesPrograms) {
							if (e.getAirline().equals(chosen)) {
								milesPrograms.remove(e);
								if (DataHolder.useDatabase) {
									DB.removeMilesProgram(this.email, e.getAirline().getCode());
								}
								break;
							}
						}
						airlinesNotPartOf.add(chosen);
					}
				}
				else {
					JOptionPane.showMessageDialog(null,
							"You are not currently part of any mileage programs!",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
			else if(n==3) { //view miles
				if(milesPrograms.isEmpty()) {
					milesPrograms.addAll(DB.getMiles(email));
				}

				String lineSep=System.lineSeparator();
				StringBuilder result=new StringBuilder();
				if(milesPrograms.size()==0) {
					JOptionPane.showMessageDialog(null,
							"You are not a part of any mileage programs!",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				else {
					for(MilesProgram e: milesPrograms) {
						if(e.getAirline()!=null) {
							result.append(e.toString()).append(lineSep);
						}
					}
					JOptionPane.showMessageDialog(null, result);
				}
			}
		}
		
	}

}
