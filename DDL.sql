
drop table if exists MilesProgram;
drop table if exists Booking;
drop table if exists creditcard;
drop table if exists Price;
drop table if exists ClassType;
drop table if exists flight;
drop table if exists Airline;
drop table if exists CustomerAddresses;
drop table if exists Customer;
drop table if exists Address;
drop table if exists Airport;






CREATE TABLE IF NOT EXISTS Airport
(
    IATA char(3) NOT NULL,
    Name character varying(80) NOT NULL,
    State character varying(15),
    Country character varying(20) NOT NULL,
    XPosition numeric NOT NULL,
    YPosition numeric NOT NULL,
    PRIMARY KEY (IATA)
);



CREATE SEQUENCE address_id_seq;

CREATE TABLE IF NOT EXISTS Address
(
    AddressID integer NOT NULL DEFAULT nextval('address_id_seq'),
    Street character varying(60),
    City character varying(20),
    State character varying(20),
    ZIP character varying(20),
    Primary Key(AddressID)
	
);

ALTER SEQUENCE address_id_seq
OWNED BY Address.AddressID;

CREATE UNIQUE INDEX if not exists caddressid_ix on Address  (AddressID);


CREATE TABLE IF NOT EXISTS Customer
(
    Email character varying(40),
    FirstName character varying(20),
    LastName character varying(20),
    HomeAirport char(3),
    PRIMARY KEY (Email),
    FOREIGN KEY (HomeAirport) REFERENCES Airport
    
);


CREATE TABLE IF NOT EXISTS CustomerAddresses
(
    CustomerEmail character varying(40),
    AddressID integer,
	PRIMARY KEY (CustomerEmail, AddressID),
    FOREIGN KEY (CustomerEmail) REFERENCES Customer,
	FOREIGN KEY (AddressID) REFERENCES Address
);



CREATE UNIQUE INDEX if not exists cemail_ix on Customer  (Email);


CREATE TABLE IF NOT EXISTS Airline
(
    AirlineCode char(2) NOT NULL,
    Name character varying(20) NOT NULL,
    Country character varying(20) NOT NULL,
    PRIMARY KEY (AirlineCode)
);

CREATE UNIQUE INDEX if not exists airlinecode_ix on Airline  (AirlineCode);

CREATE TABLE IF NOT EXISTS Flight
(
    AirlineCode char(2) NOT NULL,
    FlightNumber integer NOT NULL,
    MaximumSeatsFirstClass integer NOT NULL,
    MaximumSeatsEconomyClass integer NOT NULL,
	FirstClassSeatsLeft integer NOT NULL,
	EconomyClassSeatsLeft integer NOT NULL,
    DepartureAirport char(3) NOT NULL, --IATA
    ArrivalAirport char(3) NOT NULL, --IATA
    DepartureTime timestamp NOT NULL,
    ArrivalTime timestamp NOT NULL,
    PRIMARY KEY (AirlineCode, FlightNumber, DepartureTime),
    FOREIGN KEY (DepartureAirport) REFERENCES Airport(IATA),
	FOREIGN KEY (ArrivalAirport) REFERENCES Airport(IATA),
    FOREIGN KEY (AirlineCode) REFERENCES Airline(AirlineCode)
);

CREATE UNIQUE INDEX if not exists fairlinecode_flightnumber_date_ix on Flight (AirlineCode, FlightNumber, DepartureTime,ArrivalTime);

CREATE UNIQUE INDEX if not exists FlightNumber_ix on Flight (FlightNumber);


CREATE TABLE IF NOT EXISTS ClassType
(
    ClassID integer NOT NULL,
    Name character varying(20) NOT NULL,
    PRIMARY KEY (ClassID)
);

CREATE UNIQUE INDEX if not exists classid_ix on ClassType (ClassID);


CREATE TABLE IF NOT EXISTS Price
(
	AirlineCode char(2) NOT NULL,
	FlightNumber integer NOT NULL,
	DepartureTime timestamp NOT NULL,
    SeatType char NOT NULL, --e or f
    Price numeric NOT NULL,
    PRIMARY KEY (AirlineCode,FlightNumber,DepartureTime,SeatType),
    FOREIGN KEY (AirlineCode,FlightNumber,DepartureTime) REFERENCES Flight(AirlineCode,FlightNumber,DepartureTime),
    FOREIGN KEY (AirlineCode) REFERENCES Airline
);

CREATE UNIQUE INDEX if not exists pclassid_flightnumber_airlinecode_ix on Price (SeatType, FlightNumber, AirlineCode);

CREATE SEQUENCE credit_card_id_generator;


CREATE TABLE IF NOT EXISTS CreditCard
(
    ID integer NOT NULL DEFAULT nextval('credit_card_id_generator'),
    AddressID integer,
    CustomerEmail character varying(40) NOT NULL,
    CreditCard character varying(40) NOT NULL,
    ExpDate character varying(10) NOT NULL,
    name character varying(20) NOT NULL,
    company character varying(20) NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (CustomerEmail) REFERENCES Customer,
	FOREIGN KEY (AddressID) REFERENCES Address

);

ALTER SEQUENCE credit_card_id_generator
OWNED BY CreditCard.ID;

CREATE UNIQUE INDEX if not exists id_ix on CreditCard (ID);

CREATE SEQUENCE booking_id_generator;
CREATE TABLE IF NOT EXISTS Booking
(
    ID integer NOT NULL DEFAULT nextval('booking_id_generator'), --booking id
    bookingName character varying(5) not null,
	SeatType char NOT NULL, --e or f
    CreditCardID integer,
	CustomerEmail character varying(40) NOT NULL, --keep direct track of customer bookings
    FlightNumber integer NOT NULL,
    AirlineCode character(2),
    PRIMARY KEY (ID),
	FOREIGN KEY (FlightNumber) REFERENCES Flight(FlightNumber),
    FOREIGN KEY (AirlineCode) REFERENCES Airline,
    FOREIGN KEY (CreditCardID) REFERENCES CreditCard,
	FOREIGN KEY (CustomerEmail) REFERENCES Customer
);

ALTER SEQUENCE booking_id_generator
OWNED BY Booking.ID;

CREATE TABLE IF NOT EXISTS MilesProgram
(
    Miles numeric NOT NULL,
    CustomerEmail character varying(40) NOT NULL,
    AirlineCode char(2) NOT NULL,
    PRIMARY KEY (CustomerEmail, AirlineCode),
    FOREIGN KEY (CustomerEmail) REFERENCES Customer,
    FOREIGN KEY (AirlineCode) REFERENCES Airline
);

CREATE UNIQUE INDEX if not exists customeremail_airclinecode_ix on MilesProgram (CustomerEmail, AirlineCode);

/*
 Airlines
*/
insert into airline(airlinecode,name,country)
values('AA', 'American Airlines', 'USA');
insert into airline(airlinecode,name,country)
values('DL', 'Delta Airlines', 'USA');
insert into airline(airlinecode,name,country)
values('WN', 'Southwest Airlines', 'USA');
insert into airline(airlinecode,name,country)
values('UA', 'United Airlines', 'USA');
insert into airline(airlinecode,name,country)
values('AC', 'Air Canada', 'CAN');
insert into airline(airlinecode,name,country)
values('AS', 'Alaska Airlines', 'USA');

/*
 Airport
*/

insert into airport(iata, name, state, country, xposition, yposition)
 values ('ATL', 'Hartsfield-Jackson International Airport', 'USA', 'GA', 90, 10);

insert into airport(iata, name, state, country, xposition, yposition)
values ('JFK', 'John F. Kennedy Airport', 'New York', 'USA', 5,3);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('BHM', 'Birmingham Shuttlesworth International Airport', 'USA', 'AL', 100, 5);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('DFW', 'Dallas/Fort Worth International Airport', 'USA', 'TX', 60, 0);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('DEN', 'Denver International Airport', 'USA', 'CO', 10, 80);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('ORD', 'O''Hare International Airport', 'USA', 'IL', 100, 100);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('LAX', 'Los Angeles International Airport', 'USA', 'CA', 0, 50);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('CLT', 'Charlotte Douglas International Airport', 'USA', 'NC', 95, 55);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('IAH', 'George Bush Intercontinental Airport', 'USA', 'TX', 30, 10);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('LAS', 'McCarran International Airport', 'USA', 'NV', 20, 40);

insert into airport(iata, name, state, country, xposition, yposition)
 values ('PHX', 'Phoenix Sky Harbor International Airport', 'USA', 'AZ', 30, 30);


/*
Flights
*/

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 100,  5, 10, 'ORD', 'ATL', '2021-06-5 8:00', '2021-06-5 10:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime, FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 101,  10, 15, 'ATL', 'ORD', '2021-06-5 9:00', '2021-06-5 10:30', 10, 15);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 102,  5, 10, 'ORD', 'DEN', '2021-06-5 5:00', '2021-06-5 11:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 103,  15, 20, 'DEN', 'ORD', '2021-06-5 1:00', '2021-06-5 3:00', 15, 20);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 104,  2, 2, 'ATL', 'DEN', '2021-06-5 2:00', '2021-06-5 5:00', 2, 2);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 105,  10, 500, 'DEN', 'ATL', '2021-06-5 19:00', '2021-06-5 20:00', 10, 500);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 106,  50, 30, 'JFK', 'ATL', '2021-06-5 17:00', '2021-06-5 22:00', 50, 30);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 107,  5, 10, 'ATL', 'JFK', '2021-06-5 18:00', '2021-06-5 14:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 108,  5, 10, 'JFK', 'LAX', '2021-06-5 20:00', '2021-06-5 23:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 109,  5, 10, 'LAX', 'JFK', '2021-06-5 8:00', '2021-06-5 10:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 110,  10, 15, 'DFW', 'LAX', '2021-06-5 9:00', '2021-06-5 10:30', 10, 15);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 111,  5, 10, 'LAX', 'DFW', '2021-06-5 5:00', '2021-06-5 11:00', 5 ,10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 112,  5, 10, 'ORD', 'ATL', '2021-06-9 8:00', '2021-06-5 10:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime, FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 113,  10, 15, 'ATL', 'ORD', '2021-06-13 9:00', '2021-06-5 10:30', 10, 15);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 114,  5, 10, 'ORD', 'DEN', '2021-06-2 5:00', '2021-06-5 11:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 115,  15, 20, 'DEN', 'ORD', '2021-06-25 1:00', '2021-06-5 3:00', 15, 20);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 116,  2, 2, 'ATL', 'DEN', '2021-06-4 2:00', '2021-06-5 5:00', 2, 2);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 117,  10, 500, 'DEN', 'ATL', '2021-06-4 19:00', '2021-06-5 20:00', 10, 500);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 118,  50, 30, 'JFK', 'ATL', '2021-06-1 17:00', '2021-06-5 22:00', 50, 30);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 119,  5, 10, 'ATL', 'JFK', '2021-06-15 18:00', '2021-06-5 14:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 120,  5, 10, 'JFK', 'LAX', '2021-06-8 20:00', '2021-06-5 23:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 121,  5, 10, 'LAX', 'JFK', '2021-06-3 8:00', '2021-06-5 10:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 122,  10, 15, 'DFW', 'LAX', '2021-06-9 9:00', '2021-06-5 10:30', 10, 15);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 123,  5, 10, 'LAX', 'DFW', '2021-06-7 5:00', '2021-06-5 11:00', 5 ,10);



/*
Classes
*/
insert into classtype(classid, name)
values('0','Economy Class');

insert into classtype(classid, name)
values('1','First Class');



/*
Customers
*/


/*
New Address
*/
with address1 as (insert into Address(street, city, state, zip)
values ('1a','1b','1c','1d')
returning addressid)



/*
New Customer
*/



,customer1 as (insert into customer(email,firstname,lastname,homeairport)
values ('1','2','3','BHM')
returning customer email)

insert into customeraddresses(customeremail, addressid)
values('1', (select * from address1));

/*
New Address
*/
with address2 as (insert into Address(street, city, state, zip)
values ('2a','2b','2c','2d')
returning addressid)



/* 
New Credit Card
*/
insert into creditcard(customeremail, creditcard, expdate, name, company, addressid)
values('1', '1234567', '05/25', 'name', 'company', (select * from address2));

/*
New Address
*/
with address3 as (insert into Address(street, city, state, zip)
values ('3a','3b','3c','3d')
returning addressid)



/*
New Customer
*/
, customer2 as (insert into customer(email,firstname,lastname,homeairport)
values ('2','2','3','BHM'))

insert into customeraddresses(customeremail, addressid)
values ('2', (select * from address3));


/*
New Address
*/
with address4 as (insert into Address(street, city, state, zip)
values ('4a','4b','4c','4d')
returning addressid)

/* 
New Credit Card
*/
insert into creditcard(customeremail, creditcard, expdate, name, company, addressid)
values('2', '1234667686', '03/24', 'test', 'test2', (select * from address4));




/*
Booking
*/

insert into booking(bookingName, seattype,creditcardid,flightnumber,airlinecode, customeremail)
values('BO123', 'r','1',104,'AA', '1');
insert into booking(bookingName, seattype,creditcardid,flightnumber,airlinecode, customeremail)
values('BO355', 'r','2',104,'AA','2');


/* Prices
*/

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 100, '2021-06-5 8:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 101, '2021-06-5 9:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 102, '2021-06-5 5:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 103, '2021-06-5 1:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 104, '2021-06-5 2:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 105, '2021-06-5 19:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 106, '2021-06-5 17:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 107, '2021-06-5 18:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 108, '2021-06-5 20:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 109, '2021-06-5 8:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 110, '2021-06-5 9:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 111, '2021-06-5 5:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 112, '2021-06-9 8:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 113, '2021-06-13 9:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 114, '2021-06-2 5:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 115, '2021-06-25 1:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 116, '2021-06-4 2:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 117, '2021-06-4 19:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 118, '2021-06-1 17:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 119, '2021-06-15 18:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 120, '2021-06-8 20:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 121, '2021-06-3 8:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 122, '2021-06-9 9:00', 'E', 100);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 123, '2021-06-7 5:00', 'E', 100);


insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 100, '2021-06-5 8:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 101, '2021-06-5 9:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 102, '2021-06-5 5:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 103, '2021-06-5 1:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 104, '2021-06-5 2:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 105, '2021-06-5 19:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 106, '2021-06-5 17:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 107, '2021-06-5 18:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 108, '2021-06-5 20:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 109, '2021-06-5 8:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 110, '2021-06-5 9:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 111, '2021-06-5 5:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 112, '2021-06-9 8:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 113, '2021-06-13 9:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 114, '2021-06-2 5:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 115, '2021-06-25 1:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 116, '2021-06-4 2:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 117, '2021-06-4 19:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 118, '2021-06-1 17:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 119, '2021-06-15 18:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 120, '2021-06-8 20:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 121, '2021-06-3 8:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 122, '2021-06-9 9:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 123, '2021-06-7 5:00', 'F', 400);



insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 92,  5, 10, 'LAX', 'ORD', '2021-10-5 8:00', '2021-10-5 10:00', 5, 10);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime, FirstClassSeatsLeft,economyclassseatsleft)
values('DL', 41,  10, 15, 'LAX', 'ATL', '2021-10-5 9:00', '2021-10-5 10:30', 10, 15);

insert into flight(airlinecode,flightnumber,maximumseatsfirstclass, maximumseatseconomyclass,departureairport,arrivalairport,departuretime, arrivaltime,FirstClassSeatsLeft,economyclassseatsleft)
values('AA', 458,  5, 10, 'ATL', 'ORD', '2021-10-5 5:00', '2021-10-5 11:00', 5, 10);



insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 92, '2021-10-5 8:00', 'F', 400);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 92, '2021-10-5 8:00', 'E', 350);

values('DL', 41, '2021-10-5 9:00', 'F', 230);

insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('DL', 41, '2021-10-5 9:00', 'E', 120);


insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 458, '2021-10-5 5:00', 'F', 1000);


insert into price(airlinecode, flightnumber, departuretime, seattype, price)
values('AA', 458, '2021-10-5 5:00', 'E', 400);
	   

