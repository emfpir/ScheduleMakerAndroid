/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author emf82
 */
public class ReportController  {

    private Stage dialogStage;
    Connection connection;
    private ZsPractice zsPractice;    
    private StringBuilder content = new StringBuilder("");
    boolean openSplash;
    
    @FXML private TextArea reportView;
    @FXML private RadioButton typeButton;
    @FXML private RadioButton customerButton;
    @FXML private RadioButton countButton; 
    
    // closes dialog stage to clear text area
    @FXML
    private void handleClear() {
        dialogStage.close();
    }
    
    // creates reports based on radio choice in text area
    @FXML
    private void handleReport() {
        content.equals("");
        
        // creates type report (counts type versus total)
        if(typeButton.isSelected() == true){
            System.out.println("type");
            String year, month;  
            String tempDate="1999-01-01 01:00:00";
            String tempYear="1998";
            Integer countPurchaser=0,countVendor=0;
            try {
                PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT type, start, appointmentId FROM U04bZK.appointment order by start;");
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    
                    year=rs.getString("start").substring(0,4);
                    month=rs.getString("start").substring(5,7);
                    if(year!=tempDate.substring(0,4) && month!=tempDate.substring(5,7)){
                        if(tempDate.substring(0,4).equals("1999")){
                            content.append("In "+month+" "+year+" there are ");
                            tempDate=rs.getString("start");
                            if(rs.getString("type").equals("purchaser")){
                                if(countPurchaser==0){
                                    countPurchaser++;
                                }
                                    else{countPurchaser=1;}
                            }
                            else{
                                if(countVendor==0){
                                    countVendor++;
                                }
                                else{countVendor=1;}
                            }
                        }
                        else{
                            content.append(countPurchaser+" appointments with a type of purchaser and "+countVendor+" appointments with a type of vendor \n");
                            content.append("In "+month+" "+year+" there are ");
                            tempDate=rs.getString("start");
                            if(rs.getString("type").equals("purchaser")){
                                if(countPurchaser==0){
                                    countPurchaser++;
                                    countVendor=0;
                                }
                                else{
                                    countPurchaser=1;
                                    countVendor=0;
                                }
                            }
                            else{
                                if(countVendor==0){
                                    countVendor++;
                                    countPurchaser=0;
                                }
                                else{
                                    countVendor=1;
                                    countPurchaser=0;
                                }
                            }                            
                        }
                    }
                    else if(year==tempDate.substring(0,4) && month==tempDate.substring(5,7)){
                       if(rs.getString("type").equals("purchaser")){
                           countPurchaser++;
                       }
                       else{countVendor++;}
                    }
                }
                content.append(countPurchaser+" appointments with a type of purchaser and "+countVendor+" appointments with a type of vendor \n");
                reportView.setText(content.toString());
            }
            catch (SQLException sqe) {
                System.out.println("Check your SQL");            
            }             
            
            /*
            try {
                PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT  count(type) as count  FROM U04bZK.appointment WHERE type = 'purchaser';");
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    count = rs.getString("count");
                    content.append("There has been a total of "+count+" appointments with a type of purchaser. \n");
                }
            }
            catch (SQLException sqe) {
                System.out.println("Check your SQL");            
            } 
            try {
                PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT  count(type) as count  FROM U04bZK.appointment WHERE type = 'vendor';");
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    count = rs.getString("count");
                    content.append("There has been a total of "+count+" appointments with a type of vendor. \n");
                }
            }
            catch (SQLException sqe) {
                System.out.println("Check your SQL");            
            }             
            */
            
            reportView.setText(content.toString());            
        }
        
        // create customer report (group schedules by customer)
        if(customerButton.isSelected() == true){
            System.out.println("customer");  
            String tDate;
            String tStart;
            String tEnd;
            Integer tScheduleCustomerId;
            String tType;               
            try {
                PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT customerId, start, end FROM U04bZK.appointment ORDER BY customerId, start;");
                ResultSet rs = statement.executeQuery();
                while(rs.next()){

                    tStart = rs.getString("start");
                    tEnd = rs.getString("end");
                    tDate = tStart.substring(0,10);
                    tScheduleCustomerId = Integer.parseInt(rs.getString("customerId"));                
                    content.append("Customer number:"+tScheduleCustomerId+" is scheduled on:"+tDate+" from:"+tStart.substring(11,16)+" to "+tEnd.substring(11,16)+"\n");
                }
            }
            catch (SQLException sqe) {
                System.out.println("Check your SQL");            
            }  
            reportView.setText(content.toString());
        }
        // count report for counting all appointments
        if(countButton.isSelected() == true){
            System.out.println("count");       
            String count;
            try {
                PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT  count(start) as count  FROM U04bZK.appointment;");
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    count = rs.getString("count");
                    content.append("There has been a total of "+count+" appointments saved. \n");
                }
            }
            catch (SQLException sqe) {
                System.out.println("Check your SQL");            
            }             
            reportView.setText(content.toString());      
        }   
             
    }
    
    // exiting the report 
    @FXML
    public void handleExit() {
        openSplash = true;
        dialogStage.close();
    }

    // set dialog stage
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }     

    // function run when report.fxml is called
    public void setStart(ZsPractice zsPractice){
        this.zsPractice = zsPractice;
        
    }
    
    // function to set return value for when report.fxml is exitted
    public boolean exitClicked(){
        return openSplash;
    }
      
}
