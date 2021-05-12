package Variables;

import java.time.ZonedDateTime;

public class Schedule {
    

    String start;
    String end;
    Integer customerScheduleId;
    String type;
    Integer scheduleId;
    
    public Schedule() {        
    }
    
    public Schedule(Integer scheduleId, String start, String end, Integer customerScheduleId, String type){
        this.scheduleId = scheduleId;

        this.start = start;
        this.end = end;
        this.customerScheduleId = customerScheduleId;
        this.type = type;        
    }    
    
    public String getStart() {
        return start;
    }
    
    public String getEnd() {
        return end;
    }
    
    public Integer getCustomerScheduleId() {
        return customerScheduleId;
    }
    
    public String getType() {
        return type;
    }
    
    public Integer getScheduleId() {
        return scheduleId;
    }
    
    public void setStart(String start) {
        this.start = start;
    }
    
    public void setEnd(String end) {
        this.end = end;
    }
    
    public void setCustomerScheduleId(Integer customerScheduleId) {
        this.customerScheduleId = customerScheduleId;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

}
