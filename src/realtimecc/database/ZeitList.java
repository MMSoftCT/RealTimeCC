/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import realtimecc.StringArray;

/**
 * spezialized array list for timesheets
 *
 * @author amederake
 */
public class ZeitList extends ArrayList<Zeit> implements Isql
{

    // class variables
    private String dbName;

    public ZeitList()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(ZeitList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * get the timerecord for given date
     *
     * @param pid peron id
     * @param d date
     * @return time record
     */
    public Zeit getPersDate(int pid, LocalDate d)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i).getPersonId() == pid && this.get(i).getDatum().isEqual(d))
            {
                return this.get(i);
            }
        }
        return null;
    }

    /**
     * get the complete timescheet for given person
     *
     * @param pid person id
     * @return list with timesheet
     */
    public ArrayList<Zeit> getPersTimesheet(int pid)
    {
        ArrayList<Zeit> ret = new ArrayList();

        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i).getPersonId() == pid)
            {
                ret.add(this.get(i));
            }
        }
        return ret;
    }

    /**
     * get timsheet for given person between from and to
     * @param pid PersonId
     * @param from start date
     * @param to end date
     * @return timesheet list
     */
    public ArrayList<Zeit> getTsFromTo(int pid, LocalDate from, LocalDate to)
    {
        ArrayList<Zeit> ret = new ArrayList<>();
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i).getPersonId() == pid
                    && (this.get(i).getDatum().isEqual(from)
                    || (this.get(i).getDatum().isAfter(from)
                    && this.get(i).getDatum().isBefore(to))
                    || this.get(i).getDatum().isEqual(to)))
            {
                ret.add(this.get(i));
            }
        }
        return ret;
    }

    /**
     * check if database exists
     *
     * @return true if exists
     */
    public boolean chkDb()
    {
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "");

            if (conn != null)
            {
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet res = meta.getCatalogs();
                StringArray cat = new StringArray();
                while (res.next())
                {
                    cat.add(res.getString("TABLE_CAT"));
                }
                return cat.contains(getDb());
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * check if table exists
     *
     * @return true if exists
     */
    public boolean chkTable()
    {
        Connection conn = connect("");
        String[] type =
        {
            "TABLE"
        };

        if (conn != null)
        {
            try
            {
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet res = meta.getTables(getDb(), null, "zeiten", type);
                return res.isBeforeFirst();
            } catch (SQLException ex)
            {
                Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    /**
     * create a new database
     *
     * @return true if succsessfull
     */
    public boolean createDb()
    {
        String query = "CREATE DATABASE IF NOT EXISTS Personal";

        try
        {
            Connection conn = conn = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "");
            if (conn != null)
            {
                Statement stmt = conn.createStatement();
                stmt.execute(query);
                stmt.close();
                conn.close();
                return true;
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * set internal database name
     *
     * @param db name of the used databaes
     */
    public void setDb(String db)
    {
        dbName = db;
    }

    /**
     * get the used database name
     *
     * @return
     */
    public String getDb()
    {
        return dbName;
    }

    // overrides for arraylist methodes
    @Override
    public boolean add(Zeit obj)
    {
        if (insert(obj))
        {
            return super.add(obj);
        }
        return false;
    }

    @Override
    public Zeit set(int index, Zeit obj)
    {
        if (update(obj))
        {
            return super.set(index, obj);
        }
        return null;
    }

    // definitions for isql interface
    @Override
    public Connection connect(String url)
    {
        Connection conn = null;
        // set dbName for furture use
        if (getDb().equals("") && !url.equals(""))
        {
            setDb(url);
        }

        if (!getDb().equals(""))
        {
            String conUrl = "jdbc:mysql://localhost/" + getDb();
            try
            {
                conn = DriverManager.getConnection(conUrl, "root", "");
            } catch (SQLException ex)
            {
                Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return conn;
    }

    @Override
    public boolean insert(Object obj)
    {
        String query = "INSERT INTO zeiten(PersonId,Datum,Kommen,Gehen)"
                + "VALUES(?,?,?,?)";

        Connection conn = connect("");
        Zeit z = (Zeit) obj;

        if (conn != null)
        {
            try
            {
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, z.getPersonId());
                pstmt.setDate(2, Date.valueOf(z.getDatum()));
                pstmt.setTime(3, Time.valueOf(z.getKommen()));
                pstmt.setTime(4, Time.valueOf(z.getGehen()));
                pstmt.executeUpdate();
                ResultSet res = pstmt.getGeneratedKeys();
                if (res.next())
                {
                    z.setId(res.getInt(1));
                }

                pstmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(ZeitList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public boolean update(Object obj)
    {
        String query = "UPDATE zeiten SET "
                + "PersonId = ?,"
                + "Datum = ?,"
                + "Kommen = ?,"
                + "Gehen= ?,"
                + "WHERE id = ?;";

        Connection conn = connect("");
        Zeit z = (Zeit) obj;

        if (conn != null)
        {
            try
            {
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, z.getPersonId());
                pstmt.setDate(2, Date.valueOf(z.getDatum()));
                pstmt.setTime(3, Time.valueOf(z.getKommen()));
                pstmt.setTime(4, Time.valueOf(z.getGehen()));
                pstmt.setInt(5, z.getId());
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(ZeitList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public boolean delete(Object obj)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean create()
    {
        String query = "CREATE TABLE IF NOT EXISTS zeiten (\n"
                + "id INT(10) AUTO_INCREMENT NOT NULL PRIMARY KEY,\n"
                + "PersonId INT(10) NOT NULL,\n"
                + "Datum DATE NOT NULL,\n"
                + "Kommen TIME,\n"
                + "Gehen TIME);";

        Connection conn = connect("");

        if (conn != null)
        {
            try
            {
                Statement stmt = conn.createStatement();
                stmt.execute(query);
                System.out.println("Tabelle zeiten erstellt!");
                stmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(ZeitList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public boolean load()
    {
        String query = "SELECT * FROM zeiten";

        Connection conn = connect("");

        if (conn != null)
        {
            try
            {
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(query);
                while (res.next())
                {
                    Zeit z = new Zeit();
                    z.setId(res.getInt("id"));
                    z.setPersonId(res.getInt("PersonId"));
                    z.setDatum(res.getDate("Datum").toLocalDate());
                    z.setKommen(res.getTime("Kommen").toLocalTime());
                    z.setGehen(res.getTime("Gehen").toLocalTime());
                    super.add(z);
                }
                stmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(ZeitList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

}
