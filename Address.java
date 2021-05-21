
public class Address {
	private int addressid;
	private String address; //123 Sesame Street
	private String city;
	private String zip;
	
	private static int id = 1;
	
	public Address(int addressid, String address, String city, String zip) {
		this.addressid = addressid;
		this.address=address;
		this.city=city;
		this.zip=zip;
	}
	
	public Address(String address, String city, String zip) {
		this.address=address;
		this.city=city;
		this.zip=zip;
		if(DataHolder.useDatabase) {
			DB.AddAddress(this);
		} else {
			this.addressid = this.id++;
		}
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address=address;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city=city;
	}
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip=zip;
	}
	
	public String toString() {
		return(address+", "+city + ", " + zip);
	}

	public int getAddressid() {
		return addressid;
	}

	public void setAddressid(int addressid) {
		this.addressid = addressid;
	}
}
