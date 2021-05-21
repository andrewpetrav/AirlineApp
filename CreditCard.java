import java.util.Date;

public class CreditCard {
	// What's the ID?
	public int addressid;
	public String cardNumber;
	public Date expDate;
	public String name;
	public String company;
	private int id;
	private Address address;

	public CreditCard(int addressid, String cardNumber, Date expDate, String name, String company, int id) {
		this.addressid = addressid;
		this.cardNumber = cardNumber;
		this.expDate = expDate;
		this.name = name;
		this.company = company;
		this.id = id;
		if (DataHolder.useDatabase) {
			this.address = DB.GetAddress(addressid);
		}

	}

	public int getAddressId() {
		return addressid;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public Date getExpDate() {
		return expDate;
	}

	public String getName() {
		return name;
	}

	public String getCompany() {
		return company;
	}

	// set address?
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	public String toString() {
		if (cardNumber.length() > 4)
			return (company + " ending in " + cardNumber.substring(cardNumber.length() - 4));
		else
			return (company + " ending in " + cardNumber.substring(cardNumber.length()));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
