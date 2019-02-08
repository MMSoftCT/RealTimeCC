/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package realtimecc;

import javafx.application.Preloader.PreloaderNotification;

/**
 * notification class
 * @author micim
 */
public class AppNotification extends Object implements PreloaderNotification 
{
    private String msg;
    private Double progr;
    
    public  AppNotification(String message, Double progress) 
    {
	msg = message;
	progr = progress;
    }
    
    public String getMessage()
    {
	return msg;
    }
    
    public Double getProgress()
    {
	return progr;
    }
}
