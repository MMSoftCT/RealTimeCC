/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package realtimecc;

import java.util.ArrayList;

/**
 * specialized ArrayList for String
 * @author micim
 */
public class StringArray extends ArrayList<String>
{
    /**
     * search for matching String
     * @param name  String
     * @return      Boolean
     */
    public boolean contains(String name)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i).equals(name))
            {
                return true;
            }
        }
        return false;
    }
    
     /**
     * return the index of the matching String
     * @param name  String
     * @return      int
     */
     public int indexOf(String name)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i).equals(name))
            {
                return i;
            }
        }
        return -1;
    }

}
