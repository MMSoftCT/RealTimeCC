/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package realtimecc.database;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author amederake
 */
public class Person 
{
    // class propertys
    private IntegerProperty Id = new SimpleIntegerProperty();
    private StringProperty Vorname = new SimpleStringProperty();
    private StringProperty Nachname = new SimpleStringProperty();
    private StringProperty Position = new SimpleStringProperty();
    private StringProperty Standort = new SimpleStringProperty();
    private StringProperty Email = new SimpleStringProperty();
    private StringProperty Telefon = new SimpleStringProperty();
    private StringProperty Benutzer = new SimpleStringProperty();
    private StringProperty Passwort = new SimpleStringProperty();
    private StringProperty Rolle = new SimpleStringProperty();
    
    public Person() 
    {
        Id.set(0);
        Vorname.set("");
        Nachname.set("");
        Position.set("");
        Standort.set("");
        Email.set("");
        Telefon.set("");
        Benutzer.set("");
        Passwort.set("");
        Rolle.set("");
    }
    
    // property methodes
    
    public IntegerProperty IdProperty()
    {
        return Id;
    }
    
    public StringProperty VornameProperty()
    {
        return Vorname;
    }
    
    public StringProperty NachnameProperty()
    {
        return Nachname;
    }
    
    public StringProperty PositionProperty()
    {
        return Position;
    }
    
    public StringProperty StandortProperty()
    {
        return Standort;
    }
    
    public StringProperty EmailProperty()
    {
        return Email;
    }
    
    public StringProperty TelefonProperty()
    {
        return Telefon;
    }
    
    public StringProperty BenutzerProperty()
    {
        return Benutzer;
    }
    
    public StringProperty PasswortProperty()
    {
        return Passwort;
    }
    
    public StringProperty RolleProperty()
    {
        return Rolle;
    }

    // setter/getter
    
    /**
     * set Id
     * @param value Id
     */
    public void setId(int value)
    {
        Id.set(value);
    }
    
    /**
     * get Id
     * @return Id
     */
    public int getId()
    {
        return Id.get();
    }
    
    /**
     * set Vorname
     * @param value Vorname
     */
    public void setVorname(String value)
    {
        Vorname.set(value);
    }
    
    /**
     * get Vorname
     * @return Vorname
     */
    public String getVorname()
    {
        return Vorname.get();
    }
    
    /**
     * set Nachname
     * @param value Nachname
     */
    public void setNachname(String value)
    {
        Nachname.set(value);
    }
    
    /**
     * get Nachname
     * @return Nachname
     */
    public String getNachname()
    {
        return Nachname.get();
    }
    
    /**
     * set Position
     * @param value Position
     */
    public void setPosition(String value)
    {
        Position.set(value);
    }
    
    /**
     * get Position
     * @return Position
     */
    public String getPosition()
    {
        return Position.get();
    }
    
    /**
     * set Standort
     * @param value Standort
     */
    public void setStandort(String value)
    {
        Standort.set(value);
    }
    
    /**
     * get Standort
     * @return Standort
     */
    public String getStandort()
    {
        return Standort.get();
    }
    
    /**
     * set Email
     * @param value Email
     */
    public void setEmail(String value)
    {
        Email.set(value);
    }
    
    /**
     * get Email
     * @return Email
     */
    public String getEmail()
    {
        return Email.get();
    }
    
    /**
     * set Telefon
     * @param value Telefon
     */
    public void setTelefon(String value)
    {
        Telefon.set(value);
    }
    
    /**
     * get Telefon
     * @return Telefon
     */
    public String getTelefon()
    {
        return Telefon.get();
    }
    
    /**
     * set Benutzer
     * @param value Benutzer
     */
    public void setBenutzer(String value)
    {
        Benutzer.set(value);
    }
    
    /**
     * get Benutzer
     * @return Benutzer
     */
    public String getBenutzer()
    {
        return Benutzer.get();
    }
    
    /**
     * set Passwort
     * @param value Passwort
     */
    public void setPasswort(String value)
    {
        Passwort.set(value);
    }
    
    /**
     * get Passwort
     * @return Passwort
     */
    public String getPasswort()
    {
        return Passwort.get();
    }
    
    /**
     * set Rolle
     * @param value Rolle
     */
    public void setRolle(String value)
    {
        Rolle.set(value);
    }
    
    /**
     * get Rolle
     * @return Rolle
     */
    public String getRolle()
    {
        return Rolle.get();
    }
    
    
}
