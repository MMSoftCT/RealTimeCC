/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import realtimecc.StringArray;

/**
 * person repository
 * @author amederake
 */
public class PersonList extends ArrayList<Person> implements Isql
{

    // class variables
    private String dbName;

    /**
     * cunstructor
     */
    public PersonList()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * check if user is listed
     * @param user username
     * @return true/false
     */
    public boolean containUser(String user)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i).getBenutzer().equals(user))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * get person object with matchin usernam and password
     * @param user username
     * @param pw password
     * @return person object
     */
    public Person getUserPw(String user, String pw)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i).getBenutzer().equals(user) && this.get(i).getPasswort().equals(pw))
            {
                return this.get(i);
            }
        }
        return null;
    }

    /**
     * get a list with worker
     * @return list with worker
     */
    public ArrayList<Person> getWorker()
    {
        ArrayList<Person> ret = new ArrayList<>();
        for(int i = 0 ; i< this.size(); i++)
        {
            if(this.get(i).getRolle().equals("Mitarbeiter"))
            {
                ret.add(this.get(i));
            }
        }
        return ret;
    }
    
    /**
     * check if database exists
     * @return true false
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
     * @return true/false
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
                ResultSet res = meta.getTables(getDb(), null, "personen", type);
                return res.isBeforeFirst();
            } catch (SQLException ex)
            {
                Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    /**
     * create new database
     * @return true/false
     */
    public boolean createDb()
    {
        String query = "CREATE DATABASE IF NOT EXISTS " + getDb();

        try
        {
            Connection conn = conn = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "");
            if (conn != null)
            {
                conn.setAutoCommit(false);
                Statement stmt = conn.createStatement();
                stmt.execute(query);
                conn.commit();
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
     * set dbname
     * @param db database name
     */
    public void setDb(String db)
    {
        dbName = db;
    }

    /**
     * get db name
     * @return databasename
     */
    public String getDb()
    {
        return dbName;
    }

    // isql overrides
    
    @Override
    public boolean add(Person obj)
    {
        if (insert(obj))
        {
            return super.add(obj);
        }
        return false;
    }

    @Override
    public Person set(int index, Person obj)
    {
        if (update(obj))
        {
            return super.set(index, obj);
        }
        return null;
    }

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
        String query = "INSERT INTO personen(Vorname, Nachname, Position, Standort, E_Mail, Telefon, Benutzer, Passwort, Rolle)"
                + "VALUES(?,?,?,?,?,?,?,?,?)";

        Connection conn = connect("");
        Person p = (Person) obj;

        if (conn != null)
        {
            try
            {
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, p.getVorname());
                pstmt.setString(2, p.getNachname());
                pstmt.setString(3, p.getPosition());
                pstmt.setString(4, p.getStandort());
                pstmt.setString(5, p.getEmail());
                pstmt.setString(6, p.getTelefon());
                pstmt.setString(7, p.getBenutzer());
                pstmt.setString(8, p.getPasswort());
                pstmt.setString(9, p.getRolle());
                pstmt.executeUpdate();
                ResultSet res = pstmt.getGeneratedKeys();
                if (res.next())
                {
                    p.setId(res.getInt(1));
                }
                pstmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public boolean update(Object obj)
    {
        String query = "UPDATE personen SET "
                + "Vorname = ?,"
                + "Nachname = ?,"
                + "Position = ?,"
                + "Standort = ?,"
                + "E_Mail = ?,"
                + "Telefon = ?,"
                + "Benutzer = ?,"
                + "Passwort = ?,"
                + "Rolle = ?,"
                + "WHERE id = ?;";

        Connection conn = connect("");
        Person p = (Person) obj;

        if (conn != null)
        {
            try
            {
                PreparedStatement pstmt = conn.prepareCall(query);
                pstmt.setString(1, p.getVorname());
                pstmt.setString(2, p.getNachname());
                pstmt.setString(3, p.getPosition());
                pstmt.setString(4, p.getStandort());
                pstmt.setString(5, p.getEmail());
                pstmt.setString(6, p.getTelefon());
                pstmt.setString(7, p.getBenutzer());
                pstmt.setString(8, p.getPasswort());
                pstmt.setString(9, p.getRolle());
                pstmt.setInt(10, p.getId());
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
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
        String query = "CREATE TABLE IF NOT EXISTS personen (\n"
                + "id INT(10) AUTO_INCREMENT NOT NULL PRIMARY KEY,\n"
                + "Vorname VARCHAR(255) NOT NULL,\n"
                + "Nachname VARCHAR(255) NOT NULL,\n"
                + "Position VARCHAR(255) NOT NULL,\n"
                + "Standort VARCHAR(255) NOT NULL,\n"
                + "E_Mail VARCHAR(255) NOT NULL,\n"
                + "Telefon VARCHAR(255) NOT NULL,\n"
                + "Benutzer VARCHAR(255) NOT NULL,\n"
                + "Passwort VARCHAR(255) NOT NULL,\n"
                + "Rolle VARCHAR(255) NOT NULL);";

        Connection conn = connect("");
        if (conn != null)
        {
            try
            {
                Statement stmt = conn.createStatement();
                stmt.execute(query);
                System.out.println("Tabelle personen erstellt!");
                stmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return false;
    }

    @Override
    public boolean load()
    {
        String query = "SELECT * FROM personen";

        Connection conn = connect("");

        if (conn != null)
        {
            try
            {
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(query);
                while (res.next())
                {
                    Person p = new Person();
                    p.setId(res.getInt("id"));
                    p.setVorname(res.getString("Vorname"));
                    p.setNachname(res.getString("Nachname"));
                    p.setPosition(res.getString("Position"));
                    p.setStandort(res.getString("Standort"));
                    p.setEmail(res.getString("E_Mail"));
                    p.setTelefon(res.getString("Telefon"));
                    p.setBenutzer(res.getString("Benutzer"));
                    p.setPasswort(res.getString("Passwort"));
                    p.setRolle(res.getString("Rolle"));
                    super.add(p);
                }
                stmt.close();
                conn.close();
                return true;
            } catch (SQLException ex)
            {
                Logger.getLogger(PersonList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

}
