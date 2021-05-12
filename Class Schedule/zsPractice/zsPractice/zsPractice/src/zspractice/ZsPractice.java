package zspractice;

import Variables.Customer;
import Variables.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ZsPractice extends Application {
    
    private static Connection connection;
    private Stage stage;
    private BorderPane root;
    
//launch the root page and splash page            
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.stage.setTitle("customer scheduler");
        
        initrootLayout();
        showSplash();    
    }

// create get for stage    
    public Stage getStage() {
        return stage;
    }
    
// loading the root.fxml     
    public void initrootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ZsPractice.class.getResource("root.fxml"));
            root = (BorderPane) loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
// loading the splash.fxml    
    public void showSplash() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ZsPractice.class.getResource("splash.fxml"));
            AnchorPane main = (AnchorPane) loader.load();
            root.setCenter(main);                       
            SplashController controller = loader.getController();
            controller.setStart(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
// loading the main.fxml    
    public boolean showMain() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ZsPractice.class.getResource("main.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Manager Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);            
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            MainController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStart(this);
            dialogStage.showAndWait();
            
            return controller.exitClicked();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }        

    }    
    
// loading the week.fxml     
    public void showWeek() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ZsPractice.class.getResource("week.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Manager Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);            
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            WeekController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStart(this);
            dialogStage.showAndWait();            
        }
        catch (IOException e) {
            e.printStackTrace();
        }        

    }    
    
// loading the month.fxml    
    public void showMonth() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ZsPractice.class.getResource("month.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Manager Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);            
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            MonthController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStart(this);
            dialogStage.showAndWait();            
        }
        catch (IOException e) {
            e.printStackTrace();
        }        

    }      
    
// loading the report.fxml    
    public boolean showReport() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ZsPractice.class.getResource("report.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Manager Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);            
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            ReportController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStart(this);
            dialogStage.showAndWait();    
            
            return controller.exitClicked();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }        

    }     
    
// code start connecting to DB     
    public static void main(String[] args) {
        DatabaseConnection.database();
        connection = DatabaseConnection.getConnect();
        launch(args);
        DatabaseConnection.closeConnect();
    }
    
}
