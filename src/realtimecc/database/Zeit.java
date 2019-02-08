/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package realtimecc.database;

import java.time.LocalDate;
import java.time.LocalTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 * @author amederake
 */
public class Zeit 
{
    // class propertys
    private IntegerProperty Id = new SimpleIntegerProperty(0);
    private IntegerProperty PersonId = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> Datum = new SimpleObjectProperty<>(null);
    private ObjectProperty<LocalTime> Kommen = new SimpleObjectProperty<>(null);
    private ObjectProperty<LocalTime> Gehen = new SimpleObjectProperty<>(null);

    public Zeit() 
    {

    }

    // property methodes
    
    public IntegerProperty IdProperty()
    {
        return Id;
    }
    
    public IntegerProperty PersonIdProperty()
    {
        return PersonId;
    }
    
    public ObjectProperty<LocalDate> DatumProperty()
    {
        return Datum;
    }
    
    public ObjectProperty<LocalTime> KommenProperty()
    {
        return Kommen;
    }
    
    public ObjectProperty<LocalTime> GehenProperty()
    {
        return Gehen;
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
     * set PersonId
     * @param value PersonId
     */
    public void setPersonId(int value)
    {
        PersonId.set(value);
    }
    
    /**
     * get PersonId
     * @return PersonId
     */
    public int getPersonId()
    {
        return PersonId.get();
    }
    
    /**
     * set Datum
     * @param value Datum
     */
    public void setDatum(LocalDate value)
    {
        Datum.set(value);
    }
    
    /**
     * get Datum
     * @return Datum
     */
    public LocalDate getDatum()
    {
        return Datum.get();
    }
    
    /**
     * set Kommen
     * @param value Kommen
     */
    public void setKommen(LocalTime value)
    {
        Kommen.set(value);
    }
    
    /**
     * get Kommen
     * @return Kommen
     */
    public LocalTime getKommen()
    {
        return Kommen.get();
    }
    
    /**
     * set Gehen
     * @param value Gehen
     */
    public void setGehen(LocalTime value)
    {
        Gehen.set(value);
    }
    
    /**
     * get Gehen
     * @return Gehen
     */
    public LocalTime getGehen()
    {
        return Gehen.get();
    }
    
}
