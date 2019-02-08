/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import realtimecc.database.*;
import realtimecc.gui.*;

/**
 *
 * @author amederake
 */
public class RealTimeCC extends Application
{

    // class variables
    private static PersonList personen; // list of stored person
    private static ZeitList zeiten;     // list of all stored timesheets
    private static Helper help;         // handle to helper class
    private final BooleanProperty ready = new SimpleBooleanProperty(false);
    private static Stage stage;         // main stage
    private static PersonalController admin;        // handle for admin gui controller
    private static AnAbmeldungController worker;    // handle for worker gui controller

    /**
     * get the person list
     *
     * @return person list
     */
    public static PersonList getPersonen()
    {
        return RealTimeCC.personen;
    }

    /**
     * get the zeiten list
     *
     * @return zeiten list
     */
    public static ZeitList getZeiten()
    {
        return RealTimeCC.zeiten;
    }

    /**
     * get helper instance
     *
     * @return helper instance
     */
    public static Helper getHelp()
    {
        return RealTimeCC.help;
    }

    /**
     * get the stage
     *
     * @return stage
     */
    public static Stage getStage()
    {
        return RealTimeCC.stage;
    }

    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            // initalize class variables
            RealTimeCC.stage = primaryStage;
            RealTimeCC.personen = new PersonList();
            RealTimeCC.zeiten = new ZeitList();
            RealTimeCC.help = new Helper();

            // load admin workbench
            FXMLLoader pLoader = new FXMLLoader(RealTimeCC.class.getResource("gui/Personal.fxml"));
            Parent pRoot = pLoader.load();
            RealTimeCC.admin = pLoader.getController();

            // load worker dialog
            FXMLLoader wLoader = new FXMLLoader(RealTimeCC.class.getResource("gui/AnAbmeldung.fxml"));
            Parent wRoot = wLoader.load();
            RealTimeCC.worker = wLoader.getController();

            load();
            
            // After the app is ready, show the stage
            ready.addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) ->
            {
                if (Boolean.TRUE.equals(t1))
                {
                    Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // create testdata 
                            TestData d = new TestData();
                            // show login dialog
                            Person akt = login();
                            // check person role
                            if (akt != null)
                            {
                                if (akt.getRolle().equals("Mitarbeiter"))
                                {
                                    getStage().setScene(new Scene(wRoot));
                                    getStage().setTitle("RealTimeCC Zeiterfassung");
                                    RealTimeCC.worker.setData(akt);
                                    getStage().show();
                                }
                                else
                                {
                                    getStage().setScene(new Scene(pRoot));
                                    getStage().setTitle("RealTimeCC Mitarbeiterverwaltung");
                                    RealTimeCC.admin.Start();
                                }
                            }
                            else
                            {
                                Alert dlg = new Alert(Alert.AlertType.ERROR, "Benutzername oder Passwort ist falsch!", ButtonType.OK);
                                dlg.showAndWait();
                                System.exit(0);
                            }
                        }
                    });
                }
            });
        } catch (IOException ex)
        {
            Logger.getLogger(RealTimeCC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * login dialog
     *
     * @return Person object
     */
    private Person login()
    {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("ComCave Zeiterfassung");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) ->
        {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton ->
        {
            if (dialogButton == loginButtonType)
            {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Person ret = null;

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent())
        {
            ret = getPersonen().getUserPw(result.get().getKey(), result.get().getValue());
        };
        return ret;
    }

    /**
     * load task
     */
    private void load()
    {
        Task task;

        // preloader task
        task = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                int max = 2;
                int i = 0;
                notifyPreloader(new AppNotification("Lade Datenbank", (double) i / max));

                // set database name
                getPersonen().setDb("realtimecc");
                if (!getPersonen().chkDb())
                {
                    // create database
                    getPersonen().createDb();
                }
                if (getPersonen().chkDb())
                {
                    if (!getPersonen().chkTable())
                    {
                        // create table personen
                        getPersonen().create();

                        // add superuser
                        Person p = new Person();
                        p.setNachname("Administrator");
                        p.setBenutzer("Admin");
                        p.setPasswort("sysadmin");
                        p.setRolle("Bearbeiter");
                        getPersonen().add(p);
                    }
                    else
                    {
                        // load personen data
                        getPersonen().load();
                    }
                }
                Thread.sleep(500);
                i++;
                notifyPreloader(new Preloader.ProgressNotification((double) i / max));
                // set database name
                getZeiten().setDb("realtimecc");
                if (!getZeiten().chkTable())
                {
                    // create table zeiten
                    getZeiten().create();
                }
                else
                {
                    // load zeiten data
                    getZeiten().load();
                }
                Thread.sleep(500);
                i++;
                notifyPreloader(new Preloader.ProgressNotification((double) i / max));

                ready.setValue(Boolean.TRUE);

                notifyPreloader(new Preloader.StateChangeNotification(
                        Preloader.StateChangeNotification.Type.BEFORE_START));

                return null;
            }
        };
        // start init task
        new Thread(task).start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }

}
