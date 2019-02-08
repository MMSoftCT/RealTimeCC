/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc.gui;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import realtimecc.database.*;
import realtimecc.*;

/**
 * FXML Controller class
 *
 * @author amederake
 */
public class AnAbmeldungController implements Initializable
{

    // class variables
    private Person ma;              // person object
    private Zeit zt;                // timesheet
    private int index;              // index of person in list
    @FXML
    private DialogPane dlgPane;     // main panel
    @FXML
    private Label lblName;          // name of person
    @FXML
    private Button btnAktion;       // button for an/abmelden
    @FXML
    private Button btnStatistik;    // button for person statistik
    @FXML
    private Label taMessage;        // message label

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        ma = new Person();
        zt = new Zeit();
        index = -1;

        btnAktion.setOnAction((event) ->
        {
            if (btnAktion.getText().equals("Anmelden"))
            {
                setKommen();
            }
            else
            {
                setGehen();
            }
        });

        btnStatistik.setOnAction((event) ->
        {
            showStatistik();
        });

        dlgPane.getButtonTypes().addAll(ButtonType.OK);
        Button ok = (Button) dlgPane.lookupButton(ButtonType.OK);
        ok.setOnAction((event) ->
        {
            System.exit(0);
        });
        taMessage.setText("Es liegen keine Nachrichten vor!");
    }

    /**
     * set data
     *
     * @param p person object
     */
    public void setData(Person p)
    {
        ma = p;
        index = RealTimeCC.getPersonen().indexOf(p);
        lblName.setText(ma.getVorname() + " " + ma.getNachname());
        LocalDate heute = LocalDate.now();
        zt = RealTimeCC.getZeiten().getPersDate(p.getId(), heute);
        if (zt == null)
        {
            zt = new Zeit();
            zt.setPersonId(p.getId());
            zt.setDatum(heute);
            zt.setKommen(LocalTime.now());
            zt.setGehen(LocalTime.now());
            btnAktion.setText("Anmelden");
        }
        else
        {
            index = RealTimeCC.getZeiten().indexOf(zt);
            zt.setGehen(LocalTime.now());
            btnAktion.setText("Abmelden");
        }
        btnStatistik.setDisable(RealTimeCC.getZeiten().getPersTimesheet(ma.getId()).isEmpty());
        double soll = calcSoll();
        double ist = calcIst();
        if(soll > 0.0 && ist > (soll*1.1))
        {
            taMessage.setText("Sie haben mehr als 10% Überstunden!");
        }
        else if(soll > 0.0 && ist < (soll* 0.9))
        {
            taMessage.setText("Sie haben mehr als 10% Fehlzeiten!");
        }
        else
        {
            taMessage.setText("Es liegen keine Nachrichten vor!");
        }
    }

    /**
     * Anmelden button action
     */
    private void setKommen()
    {
        RealTimeCC.getZeiten().add(zt);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("ComCave Zeiterfassung");
        alert.setHeaderText(null);
        alert.setContentText("Ihr Arbeitsbeginn wurde gespeichert!");

        alert.showAndWait();
        btnAktion.setDisable(true);
    }

    /**
     * Abmelden button action
     */
    private void setGehen()
    {
        RealTimeCC.getZeiten().set(index, zt);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("ComCave Zeiterfassung");
        alert.setHeaderText(null);
        alert.setContentText("Ihr Arbeitsende wurde gespeichert!");

        alert.showAndWait();
        btnAktion.setDisable(true);
    }

    /**
     * schom time statistik of given person
     *
     * @param get
     */
    private void showStatistik()
    {
        ArrayList<Zeit> zd = RealTimeCC.getZeiten().getPersTimesheet(ma.getId());
        if (zd.size() > 0)
        {
            try
            {
                Dialog dlg = new Dialog<>();
                FXMLLoader loader = new FXMLLoader(RealTimeCC.class.getResource("gui/StatDialog.fxml"));
                DialogPane pane = loader.load();
                dlg.setDialogPane(pane);
                StatDialogController ctrl = loader.getController();
                ctrl.setData(zd, LocalDate.now());
                ctrl.setHeader("Zeitübersicht");
                ctrl.setName(ma.getVorname() + " " + ma.getNachname());
                dlg.initOwner(RealTimeCC.getStage());

                dlg.showAndWait();

            } catch (IOException ex)
            {
                Logger.getLogger(PersonalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * calculate working time they should have
     * uses helper class methode
     * 
     * @return woking time till now
     */
    private double calcSoll()
    {
        double ret = 0.0;
        LocalDate now = LocalDate.now();
        if (now.getDayOfMonth() > 1)
        {
            LocalDate from = now.minusDays(now.getDayOfMonth() - 1);
            ret = RealTimeCC.getZeiten().getTsFromTo(ma.getId(), from, now).size() * 8.0;
        }
        return ret;
    }

    /**
     * calculate working time they have
     * uses helper class mthode
     * 
     * @return aktual workingtime for this month
     */
    private double calcIst()
    {
        double ret = 0.0;
        LocalDate date = LocalDate.now();
        int count = date.getDayOfMonth();
        if (RealTimeCC.getZeiten().getPersTimesheet(ma.getId()).size() > 0 && count > 1)
        {
            for (int i = 1; i < count; i++)
            {
                LocalDate tag = LocalDate.parse(String.format("%d-%02d-%02d",date.getYear(),date.getMonthValue(),i));
                Calendar cal = Calendar.getInstance();
                cal.setTime(Date.valueOf(date));
                int dow = cal.get(Calendar.DAY_OF_WEEK);
                if (dow != Calendar.SUNDAY || dow != Calendar.SATURDAY)
                {
                    ret += RealTimeCC.getHelp().getTimeForDay(RealTimeCC.getZeiten().getPersDate(ma.getId(),tag));
                }
                    
            }
        }
        return ret;
    }

}
