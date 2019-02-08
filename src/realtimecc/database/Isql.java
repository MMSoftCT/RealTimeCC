/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc.database;

import java.sql.Connection;

/**
 * interface for sql access
 * @author amederake
 */
public interface Isql
{

    /**
     * create a connection to given url
     * @param url database url
     * @return connection or null
     */
    public Connection connect(String url);
    
    /**
     * insert a new row
     * @param obj data object
     * @return true if sucsessfull
     */
    public boolean insert(Object obj);
    
    /**
     * update a row on given index
     * @param obj data object
     * @return true if sucsessfull
     */
    public boolean update(Object obj);
    
    /**
     * delete the row with the given index
     * @param obj data object
     * @return true if sucsessfull
     */
    public boolean delete(Object obj);
    
    /**
     * create the table
     * @return true if sucsessfull
     */
    public boolean create();
    
    /**
     * read all table entries
     * @return true if sucsessfull
     */
    public boolean load();
}
