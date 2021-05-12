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


public class MonthController {
    
    private Stage dialogStage;
    Connection connection;
    private ZsPractice zsPractice;    
    private StringBuilder content = new StringBuilder("");
    
    @FXML private TextArea monthView;
    
    // closes dialog stage
    @FXML
    public void handleExit() {
        dialogStage.close();
    }

    // set the dialog stage 
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }     

    // function that runs when month.fxml
    public void setStart(ZsPractice zsPractice){
        this.zsPractice = zsPractice;

        content.equals("");
        content.append("Displays schedule sorted by Month (only for Months with scheduled appointment). \n");

        LocalDate monthStart = LocalDate.now();       
        Integer addToEnd = findEnd(monthStart.getYear(),monthStart.getMonthValue(),monthStart.getDayOfMonth());
        String monthName = findMonth(monthStart.getMonthValue());
        LocalDate monthEnd = monthStart.plusDays(addToEnd);
        LocalDate tempMonth = monthEnd;
        LocalDate monthStartOne = monthStart.minusDays((monthStart.getDayOfMonth()-1));
        
        Integer lastDay;
        String tStart;
        String tEnd;
        Integer tCustomerId;
        String tDate;
        String tType;        
 
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
                    if(date.isBefore(monthStart)) {                        
                    }
                    else if(date.isBefore(monthStartOne)){                        
                    }
                    else{
                        boolean stop = false;
                        while(stop == false){
                            if(date.isAfter(monthEnd)){
                                monthStartOne = monthStartOne.plusMonths(1);
                                lastDay = findEnd(monthStartOne.getYear(),monthStartOne.getMonthValue(),monthStartOne.getDayOfMonth());
                                monthEnd = monthStartOne;
                                monthEnd = monthEnd.plusDays(lastDay);                                
                            }
                            else {
                                if( monthStartOne.equals(date)|| monthEnd.equals(date) || date.isAfter(monthStartOne) && date.isBefore(monthEnd) ){      
                                    stop = true;
                                    if(tempMonth != monthStartOne){
                                        tempMonth = monthStartOne;
                                        content.append("Schedule in "+findMonth(monthStartOne.getMonthValue())+" "+monthStartOne.getYear()+"\n");                                         
                                        content.append(tDate+" From:"+tStart.substring(11,16)+" to "+tEnd.substring(11,16)+"; appointment type:"+tType+" Customer index:"+ tCustomerId+"\n");                                        
                                    }
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
        monthView.setText(content.toString());        
    }
    
    // used to find the last day of the month
    public Integer findEnd(int year,int month,int day){
        Integer add=0;
        if(month==2){
            if(year%4==0){
                add = (29-day);
            }
            else{
                add = (28-day);
            }
        }
        else if(month==1 || month==3||month==5||month==7||month==8||month==10||month==12){
            add = (31-day);
        }
        else{
            add = (30-day);
        }        
        return add;
    }    
    
        // used to take month as a number and returns month as a string
        public String findMonth(int month) {
        String name;
        if(month==1){ name="January";}
        else if(month==2){ name="February";}
        else if(month==3){ name="March";}
        else if(month==4){ name="April";}
        else if(month==5){ name="May";}
        else if(month==6){ name="June";}
        else if(month==7){ name="July";}
        else if(month==8){ name="August";}
        else if(month==9){ name="September";}
        else if(month==10){ name="October";}
        else if(month==11){ name="November";}
        else{ name="December";}        
        return name;
    }
        

}
