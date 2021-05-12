/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Variables;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author FIRE CP
 */
public class Customer {
/*    
    private StringProperty name;
    private StringProperty address;
    private StringProperty phone;
    
    public Customer() {
        name = new SimpleStringProperty();
        address = new SimpleStringProperty();
        phone = new SimpleStringProperty();
    }
    public Customer(StringProperty name, StringProperty address, StringProperty phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }   
    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }
    public void setAddress(String address) {
        this.name = new SimpleStringProperty(address);
    }
    public void setPhone(String phone) {
        this.name = new SimpleStringProperty(phone);
    }
    public String getName() {
        return name.get();
    }
    public String getAddress() {
        return address.get();
    }
    public String getPhone() {
        return phone.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty addressProperty() {
        return address;
    }
    public StringProperty phoneProperty() {
        return phone;
    }
*/
    private Integer id;
    private String name;
    private String address;
    private String phone;
    
    public Customer() {        
    }

    public Customer( Integer id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
    
    public void setId(Integer id) {
        this.id =id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

}
