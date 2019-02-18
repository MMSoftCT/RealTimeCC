/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc.gui;

import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import realtimecc.database.Person;
import realtimecc.*;

/**
 * FXML Controller class
 *
 * @author amederake
 */
public class PersonDialogController implements Initializable
{
    // class variables
    private Person data;            // person data
    private ButtonType cancel;      // cancel button
    private ButtonType save;        // save button
    private Button btnSave;         // save button object
    @FXML
    private DialogPane dlgPane;     // main panel
    @FXML
    private Label lblTitle;         // dialog title
    @FXML
    private TextField txtVorname;   // Vorname input
    @FXML
    private TextField txtNachname;  // Nachname input
    @FXML
    private TextField txtPosition;  // Position input
    @FXML
    private TextField txtStandort;  // Standort input
    @FXML
    private TextField txtEmail;     // Email input
    @FXML
    private TextField txtTelefon;   // Telefon input
    @FXML
    private TextField txtBenutzer;  // Benutzer input
    @FXML
    private Button btnCreateUser;   // create Benutzer button
    @FXML
    private PasswordField txtPasswort; // Passwort input
    @FXML
    private Button btnCreatePw;     // create PAsswort button
    @FXML
    private ComboBox<String> cbRolle;   // Rolle selector

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        data = new Person();
        // set focus listener
        txtVorname.focusedProperty().addListener(new FocusListener(txtVorname));
        txtNachname.focusedProperty().addListener(new FocusListener(txtNachname));
        txtPosition.focusedProperty().addListener(new FocusListener(txtPosition));
        txtStandort.focusedProperty().addListener(new FocusListener(txtStandort));
        txtEmail.focusedProperty().addListener(new EmailFocusListener());
        txtTelefon.focusedProperty().addListener(new FocusListener(txtTelefon));
	txtTelefon.addEventFilter(KeyEvent.KEY_TYPED, new PhoneInput());
        
        // set combobox content
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("Mitarbeiter");
        tmp.add("Bearbeiter");
        
        cbRolle.setItems(FXCollections.observableArrayList(tmp));
        
	// create and add buttons
	save = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
	cancel = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
	dlgPane.getButtonTypes().addAll(save, cancel);

	btnSave = (Button) dlgPane.lookupButton(save);
        
        // set button actions
        btnCreateUser.setOnAction((event) ->
        {
            createUser();
        });
        
        btnCreatePw.setOnAction((event) ->
        {
            createPw();
        });
    }    
    
    /**
     * set title text
     * @param txt 
     */
    public void setHeader(String txt)
    {
        lblTitle.setText(txt);
    }
    
    /**
     * set data and input fields
     * @param p person object
     */
    public void setData(Person p)
    {
        data = p;
        txtVorname.setText(data.getVorname());
        txtNachname.setText(data.getNachname());
        txtPosition.setText(data.getPosition());
        txtStandort.setText(data.getStandort());
        txtEmail.setText(data.getEmail());
        txtTelefon.setText(data.getTelefon());
        txtBenutzer.setText(data.getBenutzer());
        txtPasswort.setText(data.getPasswort());
        cbRolle.setValue(data.getRolle());
        bindToFields();
        UpdateUi();
        txtVorname.requestFocus();
    }

    /**
     * get changed data
     * @return Person object
     */
    public Person getData()
    {
        return data;
    }
    
    /**
     * bind data fields to input fields
     */
    private void bindToFields()
    {
        data.VornameProperty().bind(txtVorname.textProperty());
        data.NachnameProperty().bind(txtNachname.textProperty());
        data.PositionProperty().bind(txtPosition.textProperty());
        data.StandortProperty().bind(txtStandort.textProperty());
        data.EmailProperty().bind(txtEmail.textProperty());
        data.TelefonProperty().bind(txtTelefon.textProperty());
        data.BenutzerProperty().bind(txtBenutzer.textProperty());
        data.PasswortProperty().bind(txtPasswort.textProperty());
        data.RolleProperty().bind(cbRolle.valueProperty());
    }

    /**
     * enable/disable buttons
     */
    private void UpdateUi()
    {
        btnSave.setDisable(txtVorname.getText().isEmpty()
                || txtNachname.getText().isEmpty()
                || txtPosition.getText().isEmpty()
                || txtStandort.getText().isEmpty()
                || txtEmail.getText().isEmpty()
                || txtTelefon.getText().isEmpty()
                || txtBenutzer.getText().isEmpty()
                || txtPasswort.getText().isEmpty()
        );
        
        btnCreateUser.setDisable(!txtBenutzer.getText().isEmpty());
        btnCreatePw.setDisable(!txtPasswort.getText().isEmpty());
    }
    
    /**
     * create user button action
     */
    public void createUser()
    {
        String vor = txtVorname.getText();
        String nach = txtNachname.getText();
        String benutzer = "";
        
        int l = 0;
        do
        {
            l++;
            benutzer = vor.substring(0, l);
        }while(RealTimeCC.getPersonen().containUser(benutzer + nach));
        
        txtBenutzer.setText(benutzer+nach);
        UpdateUi();
    }
    
    /**
     * create passwort button action 
     */
    public void createPw()
    {
        txtPasswort.setText(createPassword(8));
        UpdateUi();
    }

    // helper methodes
    /**
     * create a random passwort with given length
     * @param length length
     * @return password
     */
    private String createPassword(int length)
    {
        final String allowedChars = "0123456789abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOP!§$%&?*+#";
        SecureRandom random = new SecureRandom();
        StringBuilder pass = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            pass.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
        return pass.toString();
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

    // Event classes

    /**
     * Event handler class for numeric input
     */
    private class PhoneInput implements EventHandler<KeyEvent>
    {

	@Override
	public void handle(KeyEvent event)
	{
	    if (!"() 0123456789".contains(event.getCharacter()))
	    {
		event.consume();
	    }
	}
    }

    /**
     * event handler class for focus change
     */
    private class FocusListener implements ChangeListener<Boolean>
    {

	private final TextField field;

	public FocusListener(TextField field)
	{
	    this.field = field;
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
	{
	    if (!newValue)
	    {
		UpdateUi();
	    }
	    else
	    {
		field.selectAll();
	    }
	}
    }

    /**
     * event handler class for email focus change and email validation
     */
    private class EmailFocusListener implements ChangeListener<Boolean>
    {

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
	{
	    if (!newValue)
	    {
		// check email format
		final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
		if (!txtEmail.getText().isEmpty() && !EMAIL_REGEX.matcher(txtEmail.getText()).matches())
		{
		    Message("Das EmailFormat ist nicht gültig !");
		    txtEmail.requestFocus();
		} else
		{
		    UpdateUi();
		}
	    }
	    else
	    {
		txtEmail.selectAll();
	    }
	}
    }

}
