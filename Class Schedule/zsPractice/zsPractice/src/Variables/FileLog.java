package Variables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class FileLog {
    private static final String FILENAME = "C:\\Users\\Public\\loginRecord.txt";
    public static String loginInformation;
        
    public static void fileUpdate() {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            PreparedStatement statement = DatabaseConnection.getConnect().prepareStatement("SELECT MAX(login) as current, userName FROM U04bZK.user ;");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                loginInformation = rs.getString("userName")+" "+rs.getString("current");
                }                
            }
        catch (SQLException e){}
        
        try {
            File file = new File(FILENAME);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(loginInformation+";");
            } 
            catch (IOException e) {
                e.printStackTrace();                
            } 
            finally {
                try {
                    if (bw != null)
                        bw.close();
                    if (fw != null)
                        fw.close();
                    } 
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
	}
    
}
