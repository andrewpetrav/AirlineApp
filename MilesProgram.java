import java.util.List;

public class MilesProgram {

    private double miles;
    private String customerEmail;
    private Airline airline;
    
    public MilesProgram(Airline airline, String customerEmail, double miles) {
        this.airline=airline;
        this.customerEmail=customerEmail;
        this.miles = miles;
    }

    public double getMiles() {
        return miles;
    }
    public Airline getAirline() {
        return airline;
    }
    public void alterMiles(double m) {
        //used to add or subtract miles, if wanna subtract miles (say when spending miles), pass a neg number
        miles+=m;
        if(miles >= 0.0) {
            DB.updateMiles(customerEmail, airline.getCode(), m);
        } else {
            miles = 0.0;
        }
    }

    public void addMiles() {
        DB.addMiles(customerEmail, airline.getCode(), miles);
    }

    public static List<MilesProgram> getMilesFromDBForCustomer(Customer customer) {
        return DB.getMiles(customer.email);
    }
    public String toString() {
        return(airline+": "+miles);
    }

}
