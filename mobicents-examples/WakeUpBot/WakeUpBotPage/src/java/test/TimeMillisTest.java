/*
 * TimeMillisTest.java
 *
 * Created on 25 luty 2006, 10:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import java.util.Calendar;
import java.util.Date;
/**
 *
 * @author Administrator
 */
public class TimeMillisTest {
    
    /** Creates a new instance of TimeMillisTest */
    public TimeMillisTest() {
    }
    public static void main(String[] args)
    {
        Date d=Calendar.getInstance().getTime();
        Date d2=Calendar.getInstance().getTime();
        d2.setHours(12);
        System.out.println("DATY:"+d+"....."+d2);
        System.out.println(d2.getTime()-d.getTime());
    }
}
