/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import realtimecc.database.*;

/**
 * class for testing
 * @author amederake
 */
public class TestData
{

    /**
     * initialize test data
     */
    public TestData()
    {
        LocalDate start = LocalDate.parse("2018-12-01");
        LocalDate end = LocalDate.now().minusDays(1);
        if (RealTimeCC.getPersonen().getUserPw("TestUser", "TestUser") == null)
        {
            int id = createUser();
            createData(id, start, end);
        }
    }

    /**
     * create a test user
     * @return user id
     */
    private int createUser()
    {
        Person user = new Person();
        user.setVorname("Test");
        user.setNachname("User");
        user.setBenutzer("TestUser");
        user.setPasswort("TestUser");
        user.setRolle("Mitarbeiter");
        RealTimeCC.getPersonen().add(user);
        return RealTimeCC.getPersonen().getUserPw("TestUser", "TestUser").getId();
    }

    /**
     * create timesheets for given user
     * @param id user id
     * @param start start data
     * @param end edn date
     */
    private void createData(int id, LocalDate start, LocalDate end)
    {
        // time range for begin
        String[] von =
        {
            "08:00", "09:00", "10:00"
        };
        
        // time range for end
        String[] bis =
        {
            "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        };

        SecureRandom rnd = new SecureRandom();
        Calendar cal = Calendar.getInstance();
        
        // create a timesheet for one day
        while (start.isBefore(end))
        {
            cal.setTime(Date.valueOf(start));
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            if (dow != Calendar.SUNDAY || dow != Calendar.SATURDAY)
            {
                Zeit z = new Zeit();
                z.setPersonId(id);
                z.setDatum(start);
                // random for begin
                int v = rnd.nextInt(von.length);
                // random for end
                int b = rnd.nextInt(bis.length);
                z.setKommen(LocalTime.parse(von[v]));
                z.setGehen(LocalTime.parse(bis[b]));
                RealTimeCC.getZeiten().add(z);
            }
            start = start.plusDays(1);
        }
    }

}
