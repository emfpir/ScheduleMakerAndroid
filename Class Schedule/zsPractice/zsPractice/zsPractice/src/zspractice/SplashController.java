package zspractice;

import Variables.DatabaseConnection;
import static Variables.FileLog.fileUpdate;
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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;


public class SplashController  {

    private Stage dialogStage;
    Connection connection;
    private ZsPractice zsPractice;
    String currentLocation;
    String location;
    String zoneLoc;
    LocalDateTime rightNow;
    ZoneId newYorkZoneId = ZoneId.of("America/New_York");
    ZoneId londonZoneId = ZoneId.of("Europe/London");    
    ZoneId losAngelesZoneId = ZoneId.of("America/Los_Angeles");
    ZoneId parisZoneId = ZoneId.of("Europe/Paris");      
    Locale locale;
    ResourceBundle messages;    

    
        
    // open main.fxml    
    @FXML
    private void handleViewAll(){
        boolean openSplash = false;
        while(openSplash == false){
            openSplash = zsPractice.showMain();
        }
    }
    
    // open month.fxml    
    @FXML
    private void handleMonth(){
        zsPractice.showMonth();
    }
    
    // open week.fxml    
    @FXML
    private void handleWeek() {
        zsPractice.showWeek();
    }
    
    // open report.fxml        
    @FXML
    private void handleFormCreator() {
        boolean openSplash = false;
        while(openSplash == false){
            openSplash =zsPractice.showReport();
        }
    }

    // set dialog stage    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }        

    // called on openning splashController    
    public void setStart(ZsPractice zsPractice) {   
        this.zsPractice = zsPractice;

        // opens dialog for choosing location        
        location = setLocation();
        messages = ResourceBundle.getBundle("properties/Bundle",locale);           
        Boolean verified = false;
        while (verified == false) {        
            // opens dialog for user login             
            verified = setUser(location);
            if (verified == false) {                             
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(messages.getString("lat"));
                alert.setHeaderText(messages.getString("lah"));
                alert.setContentText(messages.getString("lac"));
                alert.showAndWait();                                     
             }
        }
        fileUpdate();
        // check if next appointment is with 15 minutes and opens dialog if true        
        nextAppointment();
        
        
    }    
    
    // checking if the user has logged in within 15minutes of an appointment
    private void nextAppointment() {
        LocalDateTime fifteenMinutes = rightNow.plusMinutes(15);
        if(location.equals("Los Angeles, CA")){
            zoneLoc = "America/Los_Angeles";
        }
        if(location.equals("New York, NY")){
            zoneLoc = "America/New_York";
        }
        if(location.equals("London, England")){
            zoneLoc = "Europe/London";
        }
        if(location.equals("Paris, France")){
            zoneLoc = "Europe/Paris";
        }

        ZoneId rightNowZoneId = ZoneId.of(zoneLoc);        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");        
        String tStart;
        try {
            PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT start FROM U04bZK.appointment ORDER BY start");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                tStart = rs.getString("start");;
                
                if(zoneLoc.toString().equals("America/New_York")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(tStart,formatter);

                    if(nyStartLDT.isBefore(rightNow)){                    
                    }
                    else{
                        if(nyStartLDT.isAfter(fifteenMinutes)){                        
                        }
                        else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Appointment reminder");
                            alert.setHeaderText("There is an appointment scheduled");
                            alert.setContentText(nyStartLDT.toString().substring(0,10)+" "+nyStartLDT.toString().substring(11,16));
                            alert.showAndWait();                        
                        }
                    }                    
                }
                else if(zoneLoc.toString().equals("America/Los_Angeles")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(tStart,formatter);
                    ZonedDateTime nyStartZDT = nyStartLDT.atZone(newYorkZoneId);                     
                    ZonedDateTime laStartZDT = nyStartZDT.withZoneSameInstant(losAngelesZoneId);
                    LocalDateTime date = laStartZDT.toLocalDateTime();
                    if(date.isBefore(rightNow)){                    
                    }
                    else{
                        if(date.isAfter(fifteenMinutes)){                        
                        }
                        else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Appointment reminder");
                            alert.setHeaderText("There is an appointment scheduled");
                            alert.setContentText(date.toString().substring(0,10)+" "+date.toString().substring(11,16));
                            alert.showAndWait();                        
                        }
                    }  
                }
                else if(zoneLoc.toString().equals("Europe/London")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(tStart,formatter);
                    ZonedDateTime nyStartZDT = nyStartLDT.atZone(newYorkZoneId);                      
                    ZonedDateTime londonStartZDT = nyStartZDT.withZoneSameInstant(londonZoneId);
                    LocalDateTime date = londonStartZDT.toLocalDateTime();
                    if(date.isBefore(rightNow)){                    
                    }
                    else{
                        if(date.isAfter(fifteenMinutes)){                        
                        }
                        else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Appointment reminder");
                            alert.setHeaderText("There is an appointment scheduled");
                            alert.setContentText(date.toString().substring(0,10)+" "+date.toString().substring(11,16));
                            alert.showAndWait();                        
                        }
                    }                      
                }
                else if(zoneLoc.toString().equals("Europe/Paris")){
                    LocalDateTime nyStartLDT = LocalDateTime.parse(tStart,formatter);
                    ZonedDateTime nyStartZDT = nyStartLDT.atZone(newYorkZoneId);                      
                    ZonedDateTime parisStartZDT = nyStartZDT.withZoneSameInstant(parisZoneId);
                    LocalDateTime date = parisStartZDT.toLocalDateTime();
                    if(date.isBefore(rightNow)){                    
                    }
                    else{
                        if(date.isAfter(fifteenMinutes)){                        
                        }
                        else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Appointment reminder");
                            alert.setHeaderText("There is an appointment scheduled");
                            alert.setContentText(date.toString().substring(0,10)+" "+date.toString().substring(11,16));
                            alert.showAndWait();                        
                        }
                    }  

                }
                else {
                }                   
            }
        }
        catch (SQLException s) {
            System.out.println("check your SQL");
        }
    }
    
    // dialog for choosing location and returning string    
    public String setLocation () {
        List<String> choices = new ArrayList<>();
        choices.add("Los Angeles, CA");
        choices.add("New York, NY");
        choices.add("London, England");
        choices.add("Paris, France");
                
        ChoiceDialog<String> dialog = new ChoiceDialog<>("select location", choices);
        dialog.setTitle("Office location");
        dialog.setHeaderText("enter the location where you working");
       
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            currentLocation = result.get();
            if(currentLocation.equals("select location")){
                System.exit(0);
            }
            else if(currentLocation.equals("Los Angeles, CA")){
                locale = new Locale("en","US");
            }
            else if(currentLocation.equals("New York, NY")){
                locale = new Locale("en","US");
            }
            else if(currentLocation.equals("London, England")){
                locale = new Locale("en","GB");
            }
            else if(currentLocation.equals("Paris, France")){
                locale = new Locale("fr","FR");
            } 
            else{System.exit(0);}            
        }
        else {
            System.exit(0);
        }

        // The way to get the response value with lambda expression
        result.ifPresent(letter -> System.out.println("Your choice: " + letter));
        return currentLocation;
    }
    
    // dialog for user/pass returns boolean to check login      
    private boolean setUser(String location) {
        Dialog<Pair<String, String>> loginDialog = new Dialog<>();
        loginDialog.setTitle(messages.getString("lpt"));
        loginDialog.setHeaderText(messages.getString("lph"));
      
        // Set the button types
        loginDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the username and password labels and fields
        GridPane loginGrid = new GridPane();
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(20, 150, 10, 10));

        //set textField for user/pass
        TextField username = new TextField();
        username.setPromptText(messages.getString("lpn"));
        PasswordField password = new PasswordField();
        password.setPromptText(messages.getString("lpp"));

        //layout for user/pass textField
        loginGrid.add(new Label(messages.getString("lpn")), 0, 0);
        loginGrid.add(username, 1, 0);
        loginGrid.add(new Label(messages.getString("lpp")), 0, 1);
        loginGrid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered
        Node loginButton = loginDialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setDisable(true);

        // Do some validation using lambda syntax
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        
        loginDialog.getDialogPane().setContent(loginGrid);

        // focus on the username field by default
        Platform.runLater(() -> username.requestFocus());

        
        // Convert the result to a username-password-pair when the login button is clicked
        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
        
        String user, pass;
        Optional<Pair<String, String>> result = loginDialog.showAndWait();
        rightNow = LocalDateTime.now();
        String tUser, tPass;
        Integer tUserid;

        // check username & password collected if it matches the database data 
        try {
            PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT userName, password, userid FROM U04bZK.user;");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                tUser = rs.getString("userName");
                if(tUser.equals(username.getText())) {
                    tPass = rs.getString("password");
                    if(tPass.equals(password.getText())) {
                        tUserid = rs.getInt("userid");
                        try {
                            PreparedStatement loc = DatabaseConnection.getConnect().prepareStatement("UPDATE U04bZK.user SET active = ?, login=? WHERE userid = ?;");
                            loc.setString(1,location);
                            loc.setString(2,rightNow.toString());
                            loc.setInt(3,tUserid);
                            loc.executeUpdate();
                        }
                        catch (SQLException sss) {
                            System.out.println("check your SQL");
                        }   
                        return true;
                    }
                }
            }
        }
        catch (SQLException sqe) {
            System.out.println("check your SQL");
        }
        return false;
    }
    
}
