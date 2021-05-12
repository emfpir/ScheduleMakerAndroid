package zspractice;

import Variables.DatabaseConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class WeekController  {

    private Stage dialogStage;
    Connection connection;
    private ZsPractice zsPractice;    
    private StringBuilder content = new StringBuilder("");
    
    // set the text area
    @FXML private TextArea weekView;    
    
    // exit the week.fxml stage 
    @FXML
    public void handleExit() {
        dialogStage.close();
    }
    
    // set dialog stage
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }     

    // runs function when week.fxml is opened
    public void setStart(ZsPractice zsPractice){
        this.zsPractice = zsPractice;

        // set the stringbuilder database that will be added to text area
        content.equals("");
        content.append("Displays schedule sorted by week (only for weeks with scheduled appointment). \n");
        
        // variables to create week schedule
        LocalDate weekStart = LocalDate.now();
        LocalDate weekEnd = weekStart.plusDays(6);
        String tStart;
        String tEnd;
        Integer tCustomerId;
        String tDate;
        String tType;        
        LocalDate tempWeek= weekEnd;   
        
            // collects appointment data from database
            try {
                PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT  start, end, type, customerId  FROM U04bZK.appointment Order By start;");
                ResultSet rs = statement.executeQuery();
                while(rs.next()) {
                    tStart = rs.getString("start");
                    tDate = tStart.substring(0,10);
                    LocalDate date = LocalDate.parse(tStart.substring(0, 10));
                    tType = rs.getString("type");
                    tEnd = rs.getString("end");
                    tCustomerId = Integer.parseInt(rs.getString("customerId"));   
                    
                    // check if start is before current day (skip if earlier)
                    if(date.isBefore(weekStart)) {                        
                    }
                    else{
                        boolean stop = false;                                              
                        while(stop == false){
                            
                            // setting the week which the appointment is within
                            if(date.isAfter(weekEnd)){
                                weekStart = weekStart.plusDays(7);
                                weekEnd = weekEnd.plusDays(7);                                  
                            }
                            else {
                                
                                // checking the appointment is within the week that is being created 
                                if( weekStart.equals(date)|| weekEnd.equals(date) || date.isAfter(weekStart) && date.isBefore(weekEnd) ){      
                                    stop = true;
                                    
                                    //start new week and adds first appointment for this week
                                    if(tempWeek != weekStart){
                                        tempWeek = weekStart;
                                        content.append("Schedule from "+weekStart+" to "+weekEnd+"\n");                                         
                                        content.append(tDate+" From:"+tStart.substring(11,16)+" to "+tEnd.substring(11,16)+"; appointment type:"+tType+" Customer index:"+ tCustomerId+"\n");                                        
                                    }
                                    
                                    // adds appointment for the current week
                                    else {
                                        content.append(tDate+" From:"+tStart.substring(11,16)+" to "+tEnd.substring(11,16)+"; appointment type:"+tType+" Customer index:"+ tCustomerId+"\n");                                        
                                    }
                                }
                            }
                        }
                    }

                }
            }
            catch (SQLException sqe) {
                System.out.println("Check your SQL");            
            }         
            
        // set string builder data to display in the text area
        weekView.setText(content.toString());        
    }
    
}
