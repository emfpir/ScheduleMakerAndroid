package zspractice;

import Variables.Customer;
import Variables.DatabaseConnection;
import Variables.Schedule;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class MainController {
    
    // variables     
    private boolean vendorRadio;    
    private boolean openSplash;
    String location;
    String zoneLoc;
    public static String tName ="change name";
    public static String tAddress="update address";
    public static String tPhone="add phone number";
    public static String tType="purchaser";
    public static String tStart="2018-01-01 05:00";
    public static String tEnd="2018-01-01 08:00";    
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private ObservableList<Customer> getCustomerList() {return customerList;}
    private ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();
    private ObservableList<Schedule> getScheduleList() {return scheduleList;}    
    ZoneId newYorkZoneId = ZoneId.of("America/New_York");
    ZoneId londonZoneId = ZoneId.of("Europe/London");    
    ZoneId losAngelesZoneId = ZoneId.of("America/Los_Angeles");
    ZoneId parisZoneId = ZoneId.of("Europe/Paris");  
    private Stage dialogStage;
    Connection connection;
    private ZsPractice zsPractice;    
    
    //set fxml & table content for the customer    
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> name;
    @FXML private TableColumn<Customer, String> address;
    @FXML private TableColumn<Customer, String> phone;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
        
    //set fxml & table content for the schedule    
    @FXML private TableView<Schedule> scheduleTable;
    @FXML private TableColumn<Schedule, String> startColumn;
    @FXML private TableColumn<Schedule, String> endColumn;
    @FXML private TableColumn<Schedule, Integer> idColumn;
    @FXML private TableColumn<Schedule, String> typeColumn;
    @FXML private TextField dateYearField;
    @FXML private TextField dateMonthField;
    @FXML private TextField dateDayField;    
    @FXML private TextField startHourField;
    @FXML private TextField startMinuteField;    
    @FXML private TextField endHourField;
    @FXML private TextField endMinuteField;    
    @FXML private TextField customerIdField; 
    @FXML private TextField scheduleIdField;    
    @FXML private RadioButton vendorRadioButton;
    @FXML private RadioButton purchaserRadioButton;

    // adds new customer to DB and loads the customer in customer table
    @FXML
    private void handleNewCustomer() {        
        addCustomer();     
        newCustomerAlert();
        dialogStage.close();
    }
  
    // removes customer from DB and the customer table 
    @FXML
    private void handleDeleteCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        removeCustomer(selectedCustomer);        
        dialogStage.close();        
    }
  
    // get customer to update with values in fields
    @FXML 
    private void handleSaveCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if(selectedCustomer.getName().equals(nameField.getText()) && selectedCustomer.getAddress().equals(addressField.getText()) && selectedCustomer.getPhone().equals(phoneField.getText())){
            clearTextField();
        }
        else{    
            if(checkName(nameField.getText())==true && checkAddress(addressField.getText())==true && checkPhone(phoneField.getText())==true){
                selectedCustomer.setName(nameField.getText());
                selectedCustomer.setAddress(addressField.getText());
                selectedCustomer.setPhone(phoneField.getText());            
                updateCustomer(selectedCustomer);

                dialogStage.close();
            }
            else{
                invalidEntryCustomer();
            }
                
        }
    }    
    
    //add new schedule using selected customer to DB and schedule table
    @FXML
    private void handleNewSchedule() {
        if(Integer.parseInt(customerIdField.getText())>=1){
        addSchedule(Integer.parseInt(customerIdField.getText()));
        newScheduleAlert();        
        dialogStage.close();        
        }
        
        else{
            noNewScheduleAlert();
        }
    }    
    
    // remove schedule from database and schedule table
    @FXML
    private void handleDeleteSchedule() {
        Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
        removeSchedule(selectedSchedule);        
        dialogStage.close();             
    }    
  
    // update selected schedule with data in text field 
    @FXML
    private void handleSaveSchedule() {
        String start = dateYearField.getText()+"-"+dateMonthField.getText()+"-"+dateDayField.getText()+" "+startHourField.getText()+":"+startMinuteField.getText()+":00";
        String end = dateYearField.getText()+"-"+dateMonthField.getText()+"-"+dateDayField.getText()+" "+endHourField.getText()+":"+endMinuteField.getText()+":00";        
        if(checkDate(start)==true && checkTime(start)==true && checkTime(end)==true && checkAppointmentTime(start,end)==true && checkOpen(start,end)==true){
            updateDB();
            dialogStage.close();
        }
        else{
            invalidEntrySchedule();
        }
    }    
    
    // control the vendor radio button 
    @FXML
    private void handleVendorRadio() {
        vendorRadio = true;
    }
    
    // control the purchaser radio button
    @FXML
    private void handlePurchaserRadio() {
        vendorRadio = false;
    }
    
    // closes dialog stage
    @FXML
    private void handleExit() {
        openSplash = true;
        dialogStage.close();
    }    
    
    // create new customer by adding default vaules to database
    public static void addCustomer() {     

        try {
            PreparedStatement addRow = DatabaseConnection.getConnect().prepareStatement("INSERT INTO U04bZK.customer(customerName,address,phone) VALUES(?,?,?);");
            addRow.setString(1,tName);
            addRow.setString(2,tAddress);
            addRow.setString(3,tPhone);
            addRow.execute();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    //deletes customer from DB by customerId    
    private void removeCustomer(Customer customer) {
        try { 
            PreparedStatement ps = DatabaseConnection.getConnect().prepareStatement("DELETE FROM U04bZK.customer WHERE customerId = ?");
            ps.setInt(1, customer.getId());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }        
    }       

    // update customer in database by Customer value passed in
    public static void updateCustomer(Customer customer) {
        try {
            PreparedStatement modifyRow = DatabaseConnection.getConnect().prepareStatement("UPDATE U04bZK.customer SET customerName = ?, address = ?, phone = ? WHERE customerId = ?");
            modifyRow.setString(1,customer.getName());
            modifyRow.setString(2,customer.getAddress());
            modifyRow.setString(3,customer.getPhone());
            modifyRow.setInt(4,customer.getId());
            modifyRow.executeUpdate();
        }
        catch (SQLException ee) {
            ee.printStackTrace();
        }

    }    
    
    // add schedule using default values and associating customerId passed in 
    public static void addSchedule(Integer customerId) {
        try {
            PreparedStatement addRow = DatabaseConnection.getConnect().prepareStatement("INSERT INTO U04bZK.appointment(customerId, type,start,end) VALUES(?,?,?,?);");
            addRow.setInt(1, customerId);
            addRow.setString(2,tType);
            addRow.setString(3,tStart);
            addRow.setString(4,tEnd);
            addRow.execute();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }       
        
    }

    // remove schedule from database using schedule id value passed in  
    private void removeSchedule(Schedule schedule) {
        try { 
            PreparedStatement ps = DatabaseConnection.getConnect().prepareStatement("DELETE FROM U04bZK.appointment WHERE appointmentId = ?");
            ps.setInt(1, schedule.getScheduleId());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }   
        
    }           

    // returns string value based on the radio button position
    private String findRadio() {
        if(vendorRadio==true){
            return  "vendor";
        }
        else{
            return "purchaser";
        }
    }
    

    // collect data from field to update schedule in database
    private void updateDB() {
        String xStart = dateYearField.getText()+"-"+dateMonthField.getText()+"-"+dateDayField.getText()+" "+startHourField.getText()+":"+startMinuteField.getText()+":00";
        String xEnd = dateYearField.getText()+"-"+dateMonthField.getText()+"-"+dateDayField.getText()+" "+endHourField.getText()+":"+endMinuteField.getText()+":00";
        Integer xScheduleId = Integer.parseInt(scheduleIdField.getText());
        Integer xCustomerId = Integer.parseInt(customerIdField.getText());
        String xType = findRadio();
        System.out.println(location+"   "+zoneLoc+"   "+xStart);
        LocalDateTime rightNow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");        
        ZoneId rightNowZoneId = ZoneId.of(zoneLoc);
        //ZonedDateTime loginZoneSet =rightNow.atZone(rightNowZoneId);
        LocalDateTime startLDT = LocalDateTime.parse(xStart,formatter);
        ZonedDateTime startZDT = startLDT.atZone(rightNowZoneId);
        ZonedDateTime startNY = startZDT.withZoneSameInstant(newYorkZoneId);
        String startSave = startNY.toString().substring(0,10)+" "+startNY.toString().substring(11,16)+":00";
        
        LocalDateTime endLDT = LocalDateTime.parse(xEnd,formatter);
        ZonedDateTime endZDT = endLDT.atZone(rightNowZoneId);
        ZonedDateTime endNY = endZDT.withZoneSameInstant(newYorkZoneId);
        String endSave = endNY.toString().substring(0,10)+" "+endNY.toString().substring(11,16)+":00";        
        System.out.println(startSave+" >< "+endSave);
        try {
            PreparedStatement ps = DatabaseConnection.getConnect().prepareStatement("UPDATE U04bZK.appointment SET customerId=?, type=?, start=?, end=? WHERE appointmentId=?;");
            ps.setInt(1, xCustomerId); 
            ps.setString(2,xType);
            ps.setString(3,startSave);
            ps.setString(4,endSave);
            ps.setInt(5, xScheduleId);
            ps.executeUpdate();
    }
        catch (SQLException sqe) {
            System.out.println("Check your SQL collecting appoint");            
        }
        catch (Exception e) {
            System.out.println("Something besides the SQL went wrong.");            
        }             
    }
 
    // return boolean of openSplash (the function is called using the dialog is closed)
    public boolean exitClicked(){
        return openSplash;
    }
 
    // set dialog stage 
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }        
    
    // runs when main.fxml is opened
    public void setStart(ZsPractice zsPractice) {   
        this.zsPractice = zsPractice;

        // set columns for the customer table 
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
                
        // set values for each row of the customer table
        customerTable.getItems().setAll(parseCustomers()); 
        
        // lambda onclick sets data from the customer table to the fields associated 
        customerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->showCustomerDetails(newValue));

        // get value of the user's login
        location =setLocation();
        
        //set schedule table for correct time zone 
        setScheduleTable();
        
        // lambda onclick sets data from the schedule table to the fields associated
        scheduleTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->showScheduleDetails(newValue));     
        customerIdField.setText("0");
    }    

    //set the schedule table and loads data with time zone correction
    private void setScheduleTable() {
        
        //set columns for the schedule table 
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerScheduleId"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));   
           
        LocalDateTime rightNow = LocalDateTime.now();
        
        // set zoneLoc based on location the user is located
        if(location.equals("Los Angeles, CA")){
            zoneLoc = "America/Los_Angeles";
        }
        else if(location.equals("New York, NY")){
            zoneLoc = "America/New_York";
        }
        else if(location.equals("London, England")){
            zoneLoc = "Europe/London";
        }
        else if(location.equals("Paris, France")){
            zoneLoc = "Europe/Paris";
        }
        else {
            System.exit(0);
        }

        // set format for LocalDateTime 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");        
        ZoneId rightNowZoneId = ZoneId.of(zoneLoc);
        ZonedDateTime loginZoneSet =rightNow.atZone(rightNowZoneId);
        
        // variables for set data from schedule in database
        Integer xScheduleId;
        String xStart;
        String xEnd;
        Integer xCustomerId;
        String xType;
       
        // collecting data from database for all appointments
        try {
        PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT *  FROM U04bZK.appointment;");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {

                // setting SQL data to variables
                xScheduleId = Integer.parseInt(rs.getString("appointmentId"));

                // setting the start datetime to LA TZ; then returning the data to datetime
                xStart = rs.getString("start");
                xEnd = rs.getString("end");  
                xCustomerId = Integer.parseInt(rs.getString("customerId"));
                xType = rs.getString("type");
                
                // checking which location the user is at and setting the zone to match
                if(zoneLoc.toString().equals("America/New_York")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(xStart,formatter);
                    ZonedDateTime nyStartZDT = nyStartLDT.atZone(newYorkZoneId);            
                    String nyStart = nyStartZDT.toString().substring(0,10)+" "+nyStartZDT.toString().substring(11,16);

                    LocalDateTime nyEndLDT = LocalDateTime.parse(xEnd,formatter);
                    ZonedDateTime nyEndZDT = nyEndLDT.atZone(newYorkZoneId);                       
                    String nyEnd = nyEndZDT.toString().substring(0,10)+" "+nyEndZDT.toString().substring(11,16);
            
                    scheduleList.add(new Schedule(xScheduleId, nyStart, nyEnd, xCustomerId, xType));                       
                }
                else if(zoneLoc.toString().equals("America/Los_Angeles")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(xStart,formatter);
                    ZonedDateTime nyStartZDT = nyStartLDT.atZone(newYorkZoneId);                     
                    ZonedDateTime laStartZDT = nyStartZDT.withZoneSameInstant(losAngelesZoneId);
                    
                    LocalDateTime nyEndLDT = LocalDateTime.parse(xEnd,formatter);
                    ZonedDateTime nyEndZDT = nyEndLDT.atZone(newYorkZoneId);                         
                    ZonedDateTime laEndZDT = nyEndZDT.withZoneSameInstant(losAngelesZoneId);
                    String laStart = laStartZDT.toString().substring(0,10)+" "+laStartZDT.toString().substring(11,16);
                    String laEnd = laEndZDT.toString().substring(0,10)+" "+laEndZDT.toString().substring(11,16);
                    scheduleList.add(new Schedule(xScheduleId, laStart, laEnd, xCustomerId, xType));
                }
                else if(zoneLoc.toString().equals("Europe/London")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(xStart,formatter);
                    ZonedDateTime nyStartZDT = nyStartLDT.atZone(newYorkZoneId);                      
                    ZonedDateTime londonStartZDT = nyStartZDT.withZoneSameInstant(londonZoneId);
                    
                    LocalDateTime nyEndLDT = LocalDateTime.parse(xEnd,formatter);
                    ZonedDateTime nyEndZDT = nyEndLDT.atZone(newYorkZoneId);                         
                    ZonedDateTime londonEndZDT = nyEndZDT.withZoneSameInstant(londonZoneId);
                    String londonStart = londonStartZDT.toString().substring(0,10)+" "+londonStartZDT.toString().substring(11,16);
                    String londonEnd = londonEndZDT.toString().substring(0,10)+" "+londonEndZDT.toString().substring(11,16);
                    scheduleList.add(new Schedule(xScheduleId, londonStart, londonEnd, xCustomerId, xType));
                }
                else if (zoneLoc.toString().equals("Europe/Paris")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(xStart,formatter);
                    ZonedDateTime nyStartZDT = nyStartLDT.atZone(newYorkZoneId);                      
                    ZonedDateTime parisStartZDT = nyStartZDT.withZoneSameInstant(parisZoneId);
                    
                    LocalDateTime nyEndLDT = LocalDateTime.parse(xEnd,formatter);
                    ZonedDateTime nyEndZDT = nyEndLDT.atZone(newYorkZoneId);                         
                    ZonedDateTime parisEndZDT = nyEndZDT.withZoneSameInstant(parisZoneId);
                    String parisStart = parisStartZDT.toString().substring(0,10)+" "+parisStartZDT.toString().substring(11,16);
                    String parisEnd = parisEndZDT.toString().substring(0,10)+" "+parisEndZDT.toString().substring(11,16);
                    scheduleList.add(new Schedule(xScheduleId, parisStart, parisEnd, xCustomerId, xType));
                }
                else {
                    System.exit(0);
                }                       
            }
            scheduleTable.getItems().setAll(scheduleList);   
        }
        catch (SQLException sqe) {
            System.out.println("Check your SQL collecting appoint");            
        }
        catch (Exception e) {
            System.out.println("Something besides the SQL went wrong.");            
        }     
    }
   
    // return string for the user login location
    private String setLocation() {
       try {
        PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT MAX(login) as login, active  FROM U04bZK.user;");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                return rs.getString("active");
            }
        }
        catch (SQLException sqe) {
            System.out.println("Check your SQL collecting appoint");            
        }
        catch (Exception e) {
            System.out.println("Something besides the SQL went wrong.");            
        }
       return null;
    }

//sets the selected schedule in schedule table to schedule fields    
    private void showScheduleDetails(Schedule selectedSchedule) {

        // get selected information to set in fields        
        String placeHoldStart = selectedSchedule.getStart().toString();
        String placeHoldEnd = selectedSchedule.getEnd().toString();
    
        // collecting the date        
        String placeHoldYear = Character.toString(placeHoldStart.charAt(0))+Character.toString(placeHoldStart.charAt(1))+Character.toString(placeHoldStart.charAt(2))+Character.toString(placeHoldStart.charAt(3));
        String placeHoldMonth = Character.toString(placeHoldStart.charAt(5))+Character.toString(placeHoldStart.charAt(6));
        String placeHoldDay = Character.toString(placeHoldStart.charAt(8))+Character.toString(placeHoldStart.charAt(9));

        // collecting the start time
        String placeHoldStartHour = Character.toString(placeHoldStart.charAt(11))+Character.toString(placeHoldStart.charAt(12));
        String placeHoldStartMin = Character.toString(placeHoldStart.charAt(14))+Character.toString(placeHoldStart.charAt(15));
        
        // collecting the end time
        String placeHoldEndHour = Character.toString(placeHoldEnd.charAt(11))+Character.toString(placeHoldEnd.charAt(12));
        String placeHoldEndMin = Character.toString(placeHoldEnd.charAt(14))+Character.toString(placeHoldEnd.charAt(15));

        // checking the state of the radio button
        if(selectedSchedule.getType().equals("vendor")) {          
            vendorRadioButton.setSelected(true);            
        }
        else if(selectedSchedule.getType().equals("purchaser")) {        
            purchaserRadioButton.setSelected(true);        
        }

        // setting the field data
        dateYearField.setText(placeHoldYear);
        dateMonthField.setText(placeHoldMonth);
        dateDayField.setText(placeHoldDay);
        startHourField.setText(placeHoldStartHour);
        startMinuteField.setText(placeHoldStartMin);
        endHourField.setText(placeHoldEndHour);
        endMinuteField.setText(placeHoldEndMin);
        customerIdField.setText(Integer.toString(selectedSchedule.getCustomerScheduleId()));  
        scheduleIdField.setText(Integer.toString(selectedSchedule.getScheduleId()));
    }
    
    // sets the customer fields based on the passed in selectedCustomer
    private void showCustomerDetails(Customer selectedCustomer) {
        clearTextField();
        nameField.setText(selectedCustomer.getName());
        addressField.setText(selectedCustomer.getAddress());
        phoneField.setText(selectedCustomer.getPhone());
        idField.setText(Integer.toString(selectedCustomer.getId()));
        customerIdField.setText(Integer.toString(selectedCustomer.getId()));        
    }
   
    // removes the values in fields 
    private void clearTextField() {
        dateYearField.clear();
        dateMonthField.clear();
        dateDayField.clear();        
        startHourField.clear();        
        startMinuteField.clear();        
        endHourField.clear();        
        endMinuteField.clear();        
        customerIdField.clear();        
        scheduleIdField.clear();        
        idField.clear();       
        nameField.clear();        
        addressField.clear();        
        phoneField.clear();   
    }
    
    // creates list of schedule from values in Database 
    private List<Schedule> parseSchedule() {
        Integer tAppointmentId;
        Integer tCustomerId;
        String tType;
        String tStart;
        String tEnd;
        System.out.println("parse customer has started");
        try (
            PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT appointmentId, customerId, type, start, end From U04bZK.appointment;");
            ResultSet rs = statement.executeQuery();) {
            while (rs.next()) {
                tAppointmentId = Integer.parseInt(rs.getString("appointment.appointmentId"));
                tStart = rs.getString("appointment.start");
                tEnd = rs.getString("appointment.end");
                tCustomerId = Integer.parseInt(rs.getString("appointment.customerId"));                
                tType = rs.getString("appointment.type");

                scheduleList.add(new Schedule(tAppointmentId,  tStart, tEnd, tCustomerId, tType));
                System.out.println(tAppointmentId);
            }
            return scheduleList;
        }
        catch (SQLException sqe) {
            System.out.println("Check SQL");
        }
        catch (Exception e) {
            System.out.println("something else is wrong.");
        }
        return scheduleList;
    }     
    
    //create list of customer based on values in database
    private List<Customer> parseCustomers() {
        Integer tId;
        String tName;
        String tAddress;
        String tPhone;
        try (
            PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT customerId, customerName, address, phone From U04bZK.customer;");
            ResultSet rs = statement.executeQuery();) {
            while (rs.next()) {
                tId = Integer.parseInt(rs.getString("customer.customerId"));
                tName = rs.getString("customer.customerName");
                tAddress = rs.getString("customer.address");
                tPhone = rs.getString("customer.phone");

                customerList.add(new Customer(tId, tName, tAddress, tPhone));
            }
            return customerList;
        }
        catch (SQLException sqe) {
            System.out.println("Check SQL");
        }
        catch (Exception e) {
            System.out.println("something else is wrong.");
        }
        return customerList;        
    }
    
    // check if name is correct (only a-z & ' ')
    private boolean checkName(String name) {
        name = name.toLowerCase();
        for(int i=0; i<name.length();i++){
            char temp = name.charAt(i);
            if(temp=='a'){}   
            else if(temp=='b'){}               
            else if(temp=='c'){}       
            else if(temp=='d'){}  
            else if(temp=='e'){}  
            else if(temp=='f'){}                                  
            else if(temp=='g'){}  
            else if(temp=='h'){}  
            else if(temp=='i'){}  
            else if(temp=='j'){}  
            else if(temp=='k'){}  
            else if(temp=='l'){}  
            else if(temp=='m'){}  
            else if(temp=='n'){}  
            else if(temp=='o'){}  
            else if(temp=='p'){}  
            else if(temp=='q'){}  
            else if(temp=='r'){}  
            else if(temp=='s'){}  
            else if(temp=='t'){}                  
            else if(temp=='u'){}  
            else if(temp=='v'){}  
            else if(temp=='w'){}  
            else if(temp=='x'){}  
            else if(temp=='y'){}  
            else if(temp=='z'){}  
            else if(temp==' '){}     
            else{return false;}
        }
        return true;
    }
        
    //check address is correct (start with '0-9'; then ' '; then 'a-z')
    private boolean checkAddress(String address){
        Integer space=0;
        for(int i=0; i<address.length(); i++){
            char temp = address.charAt(i);
            if(temp == '0'){}
            else if(temp == '1'){}
            else if(temp=='2'){}
            else if(temp=='3'){}
            else if(temp=='4'){}
            else if(temp=='5'){}
            else if(temp=='6'){}
            else if(temp=='7'){}
            else if(temp=='8'){}
            else if(temp=='9'){}   
            else if(temp==' '){
                if(i==0){
                    return false;
                }
                else{
                    space=i;
                    i=address.length();
                }
            }
            else{return false;}
        }
        address = address.toLowerCase();
        for(int i=space;i<address.length();i++){
            char temp=address.charAt(i);
            if(temp=='a'){}   
            else if(temp=='b'){}               
            else if(temp=='c'){}       
            else if(temp=='d'){}  
            else if(temp=='e'){}  
            else if(temp=='f'){}                                  
            else if(temp=='g'){}  
            else if(temp=='h'){}  
            else if(temp=='i'){}  
            else if(temp=='j'){}  
            else if(temp=='k'){}  
            else if(temp=='l'){}  
            else if(temp=='m'){}  
            else if(temp=='n'){}  
            else if(temp=='o'){}  
            else if(temp=='p'){}  
            else if(temp=='q'){}  
            else if(temp=='r'){}  
            else if(temp=='s'){}  
            else if(temp=='t'){}                  
            else if(temp=='u'){}  
            else if(temp=='v'){}  
            else if(temp=='w'){}  
            else if(temp=='x'){}  
            else if(temp=='y'){}  
            else if(temp=='z'){}  
            else if(temp==' '){}     
            else{return false;}
        }
        return true;
    }
        
    // chcek phone is correct (must have '0-9' || ' ' || '-' || '(' || ')'  )
    private boolean checkPhone(String phone){
        for(int i=0;i<phone.length();i++){
            char temp=phone.charAt(i);
            if(temp=='0'){}
            else if(temp=='1'){}
            else if(temp=='2'){}                
            else if(temp=='3'){}
            else if(temp=='4'){}
            else if(temp=='5'){}
            else if(temp=='6'){}
            else if(temp=='7'){}
            else if(temp=='8'){}
            else if(temp=='0'){}
            else if(temp==' '){}
            else if(temp=='-'){}
            else if(temp=='('){}
            else if(temp==')'){}     
            else {return false;}
        }
        return true;
    }
        
    // check date is correct (year must be within 2000-2099, month must be 01-12, day must be correct with month and leap year
    private boolean checkDate(String date){
        System.out.println(date.substring(0,1));
        if(date.substring(0,1).equals("2")){
            System.out.println(date.substring(1,2));
            if(date.substring(1,2).equals("0")){
                System.out.println(date.substring(2,3));
                if(checkTenPlace(date.substring(2,3))==true){       
                    System.out.println(date.substring(3,4));
                    if(checkTenPlace(date.substring(3,4))==true){                        
                        System.out.println(date.substring(5,7));
                        if(checkMonth(date.substring(5, 7))==true){
                            return checkDay(date);
                        }        
                        else {return false;}
                    }
                    else{return false;}
                }
                else{return false;}
            }
            else{return false;}
        }
        else{return false;}
    }
  
        
    // check the last two digits in the year (must be '0-9' for both)
    private boolean checkTenPlace(String ten){
        if(ten.equals("0")){}
        else if(ten.equals("1")){}
        else if(ten.equals("2")){}
        else if(ten.equals("3")){}
        else if(ten.equals("4")){}
        else if(ten.equals("5")){}
        else if(ten.equals("6")){}
        else if(ten.equals("7")){}
        else if(ten.equals("8")){}
        else if(ten.equals("9")){}
        else {return false;}
        return true;
    }
        
    // check the month (must be '01-12')
    private boolean checkMonth(String month){
        if(month.equals("01")){}
        else if (month.equals("02")){}
        else if (month.equals("03")){}
        else if (month.equals("04")){}
        else if (month.equals("05")){}
        else if (month.equals("06")){}
        else if (month.equals("07")){}
        else if (month.equals("08")){}
        else if (month.equals("09")){}
        else if (month.equals("10")){}
        else if (month.equals("11")){}
        else if (month.equals("12")){}
        else {return false;}
        return true;
    }
        
    // find the maximum number of days for month/year combo
    private Integer numberOfDays(String date){
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        if(Integer.parseInt(year)%4==0){
            if(Integer.parseInt(month)==02){
                return 29;
            }
        }
        if(month.equals("01")){return 31;}
        else if(month.equals("02")){return 28;}
        else if(month.equals("03")){return 31;}
        else if(month.equals("04")){return 30;}
        else if(month.equals("05")){return 31;}
        else if(month.equals("06")){return 30;}
        else if(month.equals("07")){return 31;}
        else if(month.equals("08")){return 31;}
        else if(month.equals("09")){return 30;}
        else if(month.equals("10")){return 31;}
        else if(month.equals("11")){return 30;}
        else if(month.equals("12")){return 31;}
        else {return -1;}

    }
    
    //check the day is correct (must be within '01-mostDays')
    private boolean checkDay(String date){
        Integer mostDays = numberOfDays(date);
        if(mostDays == -1){
            System.out.print("mostDays is set at -1");
            return false;
        }
        Integer currentDay = Integer.parseInt(date.substring(8,10));
        if(currentDay<=mostDays && currentDay>0){return true;}
        System.out.println("checkDay caused issue");
        return false;
    }
        
    // check start/end hour & min (must be '00-23' for hour & '00-59' for minute)
    private boolean checkTime(String date){
        Integer hour = Integer.parseInt(date.substring(11,13));
        Integer minute = Integer.parseInt(date.substring(14,16)); 
        if(hour>=0 && hour<24 && minute>=0 && minute<60){
            return true;
        }
        return false;
    }
        
    private boolean checkAppointmentTime(String start, String end){
        Integer startTimeHour = Integer.parseInt(start.substring(11,13));
        Integer startTimeMinute = Integer.parseInt(start.substring(14,16));
        Integer endTimeHour = Integer.parseInt(end.substring(11,13));
        Integer endTimeMinute = Integer.parseInt(end.substring(14,16));
        if(startTimeHour==endTimeHour && startTimeMinute<endTimeMinute || startTimeHour<endTimeHour){
            return true;
        }
        return false;
    }    
    
    private boolean checkOpen(String start, String end) {
        Integer startTimeHour = Integer.parseInt(start.substring(11,13));
        Integer endTimeHour = Integer.parseInt(end.substring(11,13));
        Integer endTimeMinute = Integer.parseInt(end.substring(14,16));        
        if(startTimeHour<8||endTimeHour>17|| endTimeHour==17 &&endTimeMinute>0){return false;}
        return true;
    }
    
     private void invalidEntryCustomer(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Data Entry Error");
        alert.setHeaderText("Some of the data enter was not formatted incorrectly!");
        alert.setContentText("The customer name can only contain alphabetic character. The address must start with numeric character and end with alpahbetic characters. The phone number must only have numeric characters, hyphens, and parentheses.");
        alert.showAndWait();   
    }
 
    private void invalidEntrySchedule(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Data Entry Error");
        alert.setHeaderText("Some of the data enter was not formatted incorrectly!");
        alert.setContentText("The Schedule fields must only contain numeric characters. The start of an appointment can't be before the end. No appointment can be scheduled outside of 8am to 5pm.");
        alert.showAndWait();   
    }
    
    private void newCustomerAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("New Customer added");
        alert.setHeaderText("A new customer was added with default values");
        alert.setContentText("Please, change the new entry with the correct information!");
        alert.showAndWait();                   
    }
         
    private void newScheduleAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("New Schedule added");
        alert.setHeaderText("A new schedule was added for the selected customer with default values");
        alert.setContentText("Please, change the new entry with the correct information!");
        alert.showAndWait();                   
    }    

    private void noNewScheduleAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Schedule Added");
        alert.setHeaderText("A new schedule needs to have a customer associated.");
        alert.setContentText("Please, choose a customer. Then, a new schedule will be created with default values.");
        alert.showAndWait();                   
    }        
    
}
