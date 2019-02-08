/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc.gui;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import realtimecc.database.*;
import realtimecc.*;

/**
 * FXML Controller class
 *
 * @author amederake
 */
public class StatDialogController implements Initializable
{

    // class variables
    private ArrayList<Zeit> data;           // list of timesheets
    @FXML
    private DialogPane dlgPane;             // main panel
    @FXML
    private Label lblTitle;                 // title lable
    @FXML
    private VBox vbView;                    // panel for timesheets
    @FXML
    private ComboBox<String> cbMontYear;    // month selection
    @FXML
    private Label lblSoll;                  // shows working time they should have
    @FXML
    private Label lblIst;                   // shows working time they have
    @FXML
    private Label lblName;                  // show name of the viewed person

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        data = new ArrayList<>();
        cbMontYear.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            setStartDate(LocalDate.parse(getDate(newValue)));
        });

        dlgPane.getButtonTypes().addAll(ButtonType.OK);
    }

    /**
     * set title text
     *
     * @param txt new text
     */
    public void setHeader(String txt)
    {
        lblTitle.setText(txt);
    }

    /**
     * set name text
     *
     * @param txt name
     */
    public void setName(String txt)
    {
        lblName.setText(txt);
    }

    /**
     * set data and date
     *
     * @param d list of timecheats
     * @param s start date
     */
    public void setData(ArrayList<Zeit> d, LocalDate s)
    {
        data = d;
        ArrayList<String> daten = new ArrayList<>();
        for (int i = 0; i < data.size(); i++)
        {
            String ent = data.get(i).getDatum().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            if (!daten.contains(ent))
            {
                daten.add(ent);
            }
        }
        cbMontYear.setItems(FXCollections.observableArrayList(daten));
        cbMontYear.getSelectionModel().select(s.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        //setStartDate(s);
    }

    /**
     * create statistic entrys
     *
     * @param date date for month and year to create the bars
     */
    private void setStartDate(LocalDate date)
    {
        vbView.getChildren().clear();
        LocalDate from = LocalDate.parse(String.format("%d-%02d-%02d", date.getYear(), date.getMonthValue(), 1));
        LocalDate to = LocalDate.now().minusDays(1);
        if(date.getMonthValue() != to.getMonthValue())
        {
            to = date.plusDays(date.lengthOfMonth());
        }
        double toHave = RealTimeCC.getZeiten().getTsFromTo(data.get(0).getPersonId(), from, to).size() * 8.0;
        double have = 0.0;

        // loop through days of month
        while (from.isBefore(to))
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Date.valueOf(from));
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            if (dow != Calendar.SUNDAY || dow != Calendar.SATURDAY)
            {
                DayStat ds = new DayStat();
                ds.setLabel(from.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                double t = getTimeData(from);
                ds.setTime(t);
                have += t;
                vbView.getChildren().add(ds);
            }
            from = from.plusDays(1);
        }
        // set text 
        lblSoll.setText("Soll Zeit: " + String.valueOf(toHave));
        lblIst.setText("Ist Zeit: " + String.valueOf(have));
    }

    // helper methodes
    /**
     * get the houres btween come and go time
     *
     * @param d data to check
     * @return houres in dezimal
     */
    public double getTimeData(LocalDate d)
    {
        for (int i = 0; i < data.size(); i++)
        {
            if (data.get(i).getDatum().equals(d))
            {
                return RealTimeCC.getHelp().getTimeForDay(data.get(i));
            }
        }
        return 0.0;
    }

    /**
     * reformate given text to parseble format
     *
     * @param txt string with month name and year
     * @return
     */
    private String getDate(String txt)
    {
        String[] date = txt.split(" ");
        String month = "";
        switch (date[0])
        {
            case "Januar":
                month = "01";
                break;
            case "Februar":
                month = "02";
                break;
            case "März":
                month = "03";
                break;
            case "April":
                month = "04";
                break;
            case "Mai":
                month = "05";
                break;
            case "Juni":
                month = "06";
                break;
            case "Juli":
                month = "07";
                break;
            case "August":
                month = "08";
                break;
            case "September":
                month = "09";
                break;
            case "Oktober":
                month = "10";
                break;
            case "November":
                month = "11";
                break;
            case "Dezember":
                month = "12";
                break;
        }
        return date[1] + "-" + month + "-" + "01";
    }

    /**
     * class for day statistik
     */
    private class DayStat extends HBox
    {

        private Label lbl;          // label for date
        private Label houres;       // label for working houres
        private ProgressBar time;   // graphical output

        /**
         * constructor
         */
        public DayStat()
        {
            lbl = new Label();
            lbl.setPrefSize(80, 25);
            lbl.setPadding(new Insets(0, 10, 0, 0));
            time = new ProgressBar();
            time.setPrefWidth(200);
            houres = new Label();
            houres.setPrefSize(80, 25);
            houres.setPadding(new Insets(0, 0, 0, 10));
            this.getChildren().addAll(lbl, time, houres);
        }

        /**
         * set date label text
         * @param txt 
         */
        public void setLabel(String txt)
        {
            lbl.setText(txt);
        }

       /**
        * set houre label text and value of graphical output
        * @param t 
        */
        public void setTime(double t)
        {
            time.setProgress(t / 10);
            houres.setText(String.format("%.2f", t));
        }

    }
}
