
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Driver implements ActionListener {
	static Scanner sc = new Scanner(System.in);
	static SimpleDateFormat sdf = new SimpleDateFormat("MM/YY");
	static SimpleDateFormat sdfD = new SimpleDateFormat("MM/DD/YY"); // this format includes day (for bookings)
	static Customer customer = new Customer(null, null, null, null, null, null); // who the user is
	// import list of all airport
	// import list of all customers

	// main function -- where all the magic happens
	public static void main(String[] args) {
		// JFrame frame=new JFrame();

		DataHolder.main(args);// initialize all data
		while (true) {
			logInScreen();

			// options on what to do
			while (true) {
				JFrame frame = new JFrame();

				Object[] options = { "Sign Out", "Manage Info", "Manage Flights" };
				int input = JOptionPane.showOptionDialog(frame,
						"Welcome " + customer.getFirstName() + " " + customer.getLastName() + "!", null,
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom
																								// Icon
						options, // the titles of buttons
						null);// default button title
				switch (input) {
				case 0:
					// signout
					break;
				case 1:
					// manage info
					manageInfoScreen();
					break;
				case 2:
					// book/cancel flight
					customer.manageFlights();
					break;
				}
				if (input == 0) { // signout
					break;
				} else if (input == JOptionPane.CLOSED_OPTION) {
					System.exit(0);
				}
			}
		}
	}

	public static void manageInfoScreen() {
		JFrame frame = new JFrame();
		Object[] options = { "Back", "Manage Payment Info", "Manage Addresses", "Mileage Program" };
		while (true) {
			int n = JOptionPane.showOptionDialog(frame, "What would you like to manage?", null,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if (n == 1) {// manage payment info
				customer.manageCreditCard();
			} else if (n == 2) {// manage addresses
				customer.manageAddress();
			} else if (n == 3) { // mileage program
				customer.mileagePrograms();
			} else if (n == 0) {// go back to main menu
				break;
			} else if (n == JOptionPane.CLOSED_OPTION) {
				System.exit(0);
			}
		}
	}

	public static void logInScreen() {
		while (true) {
			JFrame frame = new JFrame();
			Object[] options = { "Quit Application", "Log In", "Sign Up" };
			int n = JOptionPane.showOptionDialog(frame, "Thank you for choosing CS425 airline manager", null,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if (n == 1) {// log in
				try {
					customer = logIn();
					if (customer == null) {
						// JOptionPane.showDialog("Please enter in your email");
						JOptionPane.showMessageDialog(null, "That email is not associated with an account!", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						break;
					}
				} catch (Exception NullPointerException) {

				}

			} else if (n == 2) { // sign up
				try {
					customer = signUp();
					if (customer == null) {
						JOptionPane.showMessageDialog(null, "That email is already associated with an account!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						break;
					}
				} catch (Exception NullPointerException) {

				}

			} else if (n == 0) {
				System.exit(0);
			}
			if (n == JOptionPane.CLOSED_OPTION) {
				System.exit(0);
			}
		}
	}

	public static Customer signUp() {
		// TODO add something to catch if user closes window
		String email;
		String lastName;
		String firstName;
		Airport homeAirport;
		Address homeAddress;
		String homeAddressString;
		String homeAddressCityString;
		String homeAddressZipString;

		JFrame frame = new JFrame();

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel button = new JPanel();
		label.add(new JLabel("E-Mail", SwingConstants.RIGHT));
		label.add(new JLabel("Last Name", SwingConstants.RIGHT));
		label.add(new JLabel("First Name", SwingConstants.RIGHT));
		label.add(new JLabel("Home Airport", SwingConstants.RIGHT));
		label.add(new JLabel("Home Address", SwingConstants.RIGHT));
		label.add(new JLabel("City", SwingConstants.RIGHT));
		label.add(new JLabel("ZIP", SwingConstants.RIGHT));
		// label.add(new JLabel("Credit Card", SwingConstants.RIGHT));
		panel.add(label, BorderLayout.WEST);
		Airport[] options = new Airport[DataHolder.getAirports().size()];
		for (int i = 0; i < options.length; i++) {
			options[i] = DataHolder.getAirports().get(i);// .getName();
		}

		JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
		JTextField emailText = new JTextField();
		JTextField lastNameText = new JTextField();
		JTextField firstNameText = new JTextField();
		JComboBox<Airport> homeAirportText = new JComboBox<Airport>(options);
		JTextField homeAddressText = new JTextField();
		JTextField homeAddressCityText = new JTextField();
		JTextField homeAddressZipText = new JTextField();

		homeAirportText.setRenderer(new DefaultListCellRenderer() {
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

		controls.add(emailText);
		controls.add(lastNameText);
		controls.add(firstNameText);
		controls.add(homeAirportText);
		controls.add(homeAddressText);
		controls.add(homeAddressCityText);
		controls.add(homeAddressZipText);

		panel.add(controls, BorderLayout.CENTER);

		int signUpresult = JOptionPane.showOptionDialog(frame, panel, "Sign-Up", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (signUpresult != 0) {
			logInScreen();
		}
		email = emailText.getText();
		lastName = lastNameText.getText();
		firstName = firstNameText.getText();
		homeAirport = (Airport) homeAirportText.getSelectedItem();
		homeAddressString = homeAddressText.getText();
		homeAddressCityString = homeAddressCityText.getText();
		homeAddressZipString = homeAddressZipText.getText();
		homeAddress = new Address(homeAddressString, homeAddressCityString, homeAddressZipString);

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

		CreditCard creditCard;
		String cardNumber;
		Date expDate;
		String expDateMonth;
		String expDateYear;
		String name;
		String company;
		int creditCardResult = JOptionPane.showOptionDialog(frame, panel2, "Sign-Up", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null);
		// JOptionPane.showMessageDialog(frame, panel2, "Sign-Up",
		// JOptionPane.QUESTION_MESSAGE);
		if (creditCardResult != 0) {
			logInScreen();
		}
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
		creditCard = new CreditCard(homeAddress.getAddressid(), cardNumber, expDate, name, company, -1);
		ArrayList<Address> customerAddress = new ArrayList<Address>();
		customerAddress.add(homeAddress);
		ArrayList<CreditCard> c1 = new ArrayList<CreditCard>();
		c1.add(creditCard);
		Customer cust = new Customer(email, firstName, lastName, homeAirport, customerAddress, c1);
		DataHolder.AddCustomer(cust);
		return cust;

	}
	// log in
	public static Customer logIn() {
		String email;
		email = JOptionPane.showInputDialog("Please enter in your email");
		if (email == null) {
			throw new NullPointerException(); // user hit cancel
		}

		customer = DataHolder.containsCustomer(email);
		return customer;
	}

	// get home airport
	public static Airport addHomeAirport() { // for signup only
		JFrame frame = new JFrame();
		Object selectionObject;
		frame.setAlwaysOnTop(true);
		Object[] options = new Object[DataHolder.getAirports().size()];
		for (int i = 0; i < options.length; i++) {
			options[i] = DataHolder.getAirports().get(i).getName();
		}
		selectionObject = JOptionPane.showInputDialog(frame, "Please select your home airport", "Menu",
				JOptionPane.PLAIN_MESSAGE, null, options, null);
		String selectionString = selectionObject.toString();
		return (DataHolder.containsAirportName(selectionString));
	}

	// add address
	public static Address addAddress() { // for signup only
		String address = JOptionPane.showInputDialog("Please enter in your address");
		String city = JOptionPane.showInputDialog("Please enter in your city");
		String zip = JOptionPane.showInputDialog("Please enter in your ZIP code");
		return (new Address(address, city, zip));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}

}
