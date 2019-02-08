/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc.gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import realtimecc.*;
import realtimecc.database.*;

/**
 * FXML Controller class
 *
 * @author amederake
 */
public class PersonalController implements Initializable
{

    // class variables
    private Stage stage;                                // handle for mainstage
    private PersonList personen;                        // array of person
    private ZeitList zeiten;                            // array of timesheet
    @FXML
    private MenuItem miClose;                           // menue entry colse
    @FXML
    private MenuItem miAnlegen;                         // menue entry add new person 
    @FXML
    private MenuItem miChange;                          // menue entry change selected person data
    @FXML
    private TableView<Person> tblPerson;                // table view
    @FXML
    private TableColumn<Person, Integer> colId;         // id column
    @FXML
    private TableColumn<Person, String> colVorname;     // firstname column
    @FXML
    private TableColumn<Person, String> colNachname;    // lastname column
    @FXML
    private TableColumn<Person, String> colPosition;    // position column
    @FXML
    private TableColumn<Person, String> colStandort;    // standort column
    @FXML
    private MenuItem miState;                           // menue entry person statistik
    @FXML
    private MenuItem miAbout;                           // menue entry about
    @FXML
    private MenuItem cmAdd;                             // context menue entry add new person
    @FXML
    private MenuItem cmChange;                          // context menue entry change selectedt person data
    @FXML
    private MenuItem cmStat;                            // context menue entry person statistik

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        personen = new PersonList();
        zeiten = new ZeitList();
        initTable();
        initMenu();
    }

    /**
     * start methode
     */
    public void Start()
    {
        personen = RealTimeCC.getPersonen();
        tblPerson.setItems(FXCollections.observableArrayList(personen.getWorker()));
        zeiten = RealTimeCC.getZeiten();
        stage = RealTimeCC.getStage();
        stage.show();
        UpdateUi();
    }

    /**
     * initialize person table
     */
    private void initTable()
    {
        tblPerson.setItems(FXCollections.observableArrayList(personen));
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colVorname.setCellValueFactory(new PropertyValueFactory<>("Vorname"));
        colNachname.setCellValueFactory(new PropertyValueFactory<>("Nachname"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("Position"));
        colStandort.setCellValueFactory(new PropertyValueFactory<>("Standort"));
        tblPerson.getSelectionModel().selectedItemProperty().
                addListener(new ChangeListener<Person>()
                {
                    @Override
                    public void changed(ObservableValue<? extends Person> observable, Person oldValue, Person newValue)
                    {
                        UpdateUi();
                    }
                });
    }

    /**
     * initialize menu item actions
     */
    private void initMenu()
    {
        miClose.setOnAction((event) ->
        {
            System.exit(0);
        });
        
        miAnlegen.setOnAction((event) ->
        {
            addPerson();
        });
        
        cmAdd.setOnAction((event) ->
        {
            addPerson();
        });
        
        miChange.setOnAction((event) ->
        {
            editPerson(tblPerson.getSelectionModel().selectedItemProperty().get());
        });
        
        cmChange.setOnAction((event) ->
        {
            editPerson(tblPerson.getSelectionModel().selectedItemProperty().get());
        });
        
        miState.setOnAction((event) ->
        {
            showStatistik(tblPerson.getSelectionModel().selectedItemProperty().get());
        });
        
        cmStat.setOnAction((event) ->
        {
            showStatistik(tblPerson.getSelectionModel().selectedItemProperty().get());
        });
        
        miAbout.setOnAction((event) ->
        {
            showAbout();
        });
    }

    /**
     * add a new person to list
     */
    private void addPerson()
    {
        try
        {
            Person pers = new Person();
            pers.setRolle("Mitarbeiter");
            Dialog<ButtonType> dlg = new Dialog<>();
            FXMLLoader loader = new FXMLLoader(RealTimeCC.class.getResource("gui/PersonDialog.fxml"));
            DialogPane pane = loader.load();
            dlg.setDialogPane(pane);
            PersonDialogController controler = loader.getController();
            controler.setData(pers);
            controler.setHeader("Neuen Mitarbeiter anlegen");
            dlg.initOwner(stage);

            Optional<ButtonType> result = dlg.showAndWait();

            if (result.isPresent())
            {
                if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE)
                {
                    if (personen.add(controler.getData()))
                    {
                        tblPerson.setItems(FXCollections.observableArrayList(personen.getWorker()));
                        UpdateUi();
                    }
                }

            }
        } catch (IOException ex)
        {
            Logger.getLogger(PersonalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * edit given person data
     *
     * @param get
     */
    private void editPerson(Person get)
    {
        try
        {
            int index = personen.indexOf(get);
            Dialog<ButtonType> dlg = new Dialog<>();
            FXMLLoader loader = new FXMLLoader(RealTimeCC.class.getResource("gui/PersonDialog.fxml"));
            DialogPane pane = loader.load();
            dlg.setDialogPane(pane);
            PersonDialogController controler = loader.getController();
            controler.setData(get);
            controler.setHeader("Neuen Mitarbeiter anlegen");
            dlg.initOwner(stage);

            Optional<ButtonType> result = dlg.showAndWait();

            if (result.isPresent())
            {
                if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE)
                {
                    personen.set(index, controler.getData());
                    tblPerson.setItems(FXCollections.observableArrayList(personen.getWorker()));
                    UpdateUi();
                }

            }
        } catch (IOException ex)
        {
            Logger.getLogger(PersonalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * schom time statistik of given person
     *
     * @param get
     */
    private void showStatistik(Person get)
    {
        ArrayList<Zeit> zd = zeiten.getPersTimesheet(get.getId());
        if(zd.size() > 0)
        {
            try {
                Dialog dlg = new Dialog<>();
                FXMLLoader loader = new FXMLLoader(RealTimeCC.class.getResource("gui/StatDialog.fxml"));
                DialogPane pane = loader.load();
                dlg.setDialogPane(pane);
                StatDialogController ctrl = loader.getController();
                ctrl.setData(zd, LocalDate.now());
                ctrl.setHeader("Zeitübersicht");
                ctrl.setName(get.getVorname() + " " + get.getNachname());
                dlg.initOwner(stage);
                
                dlg.showAndWait();
                
            } catch (IOException ex) {
                Logger.getLogger(PersonalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            Message("Es liegen keine Aufzeichnungen vor!");
        }
    }

    /**
     * display about dialog
     */
    private void showAbout()
    {
        try
        {
            Dialog dlg = new Dialog<>();
            FXMLLoader loader = new FXMLLoader(RealTimeCC.class.getResource("gui/Splash.fxml"));
            DialogPane pane = loader.load();
            dlg.setDialogPane(pane);
            dlg.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
            dlg.initOwner(stage);
            
            dlg.showAndWait();
        } catch (IOException ex)
        {
            Logger.getLogger(PersonalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * update meue and table
     */
    private void UpdateUi()
    {
        miChange.setDisable(tblPerson.getSelectionModel().selectedIndexProperty().get() < 0);
        cmChange.setDisable(tblPerson.getSelectionModel().selectedIndexProperty().get() < 0);
        miState.setDisable(tblPerson.getSelectionModel().selectedIndexProperty().get() < 0);
        cmStat.setDisable(tblPerson.getSelectionModel().selectedIndexProperty().get() < 0);
    }

     /**
     * show alert dialog with given message
     *
     * @param msg alert message
     */
    private void Message(String msg)
    {
	Alert dlg = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
	dlg.showAndWait();
    }


}
