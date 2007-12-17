/*
 * DataColector.java
 *
 * Created on 25 luty 2006, 09:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.examples.wakeupbot.jsf.datacollector;

import java.util.Calendar;
import java.util.Date;
import org.mobicents.examples.wakeupbot.jsf.sleeconnector.SleeConnector;

/**
 *
 * @author baranowb
 */
public class DataCollector {
    private Date yearMonthDay=null;
    private Date hourMinuteSecond=null;
    
    //EXAMLES VALUE IS SET
    private String UID="user@gtalk.com";
    /** Creates a new instance of DataColector */
    public DataCollector() {
    }
    
    public Date getYearMonthDay() {
        return yearMonthDay;
    }
    
    public void setYearMonthDay(Date yearMonthDay) {
        this.yearMonthDay = yearMonthDay;
    }
    
    public Date getHourMinuteSecond() {
        return hourMinuteSecond;
    }
    
    public void setHourMinuteSecond(Date hourMinuteSecond) {
        this.hourMinuteSecond = hourMinuteSecond;
    }
    public String getUID() {
        return UID;
    }
    
    public void setUID(String UID) {
        this.UID = UID;
    }
    public  String sendRequest() {
        String value=null;
        long timeMilisDifference;
       
        //we have to check if its the same day, if it is than we need to check if someone hadnt enter date in the past
        Date current=Calendar.getInstance().getTime();
        if( (current.getYear()==yearMonthDay.getYear())&&(current.getMonth()==yearMonthDay.getMonth())&&(current.getDay()==yearMonthDay.getDay()))
        {
            //This is day we currently have so we need to check hour date...
            if((current.getHours()<hourMinuteSecond.getHours())&&(current.getMinutes()<hourMinuteSecond.getMinutes())&&(current.getSeconds()<hourMinuteSecond.getSeconds()))
            {
                //TODO: send message...
                return value;
            }
        }
        //Lets create timeMillisDiff
        //yearMonthDay contains information about day, year and current hour, minute etc.
        //its good but we need information about exact time. We lack information about hour, minute etc
        //at which we should send request. This info is retrieved from hourMinuteSecond.getTime()-current.getTime();
        //It gives difference between current times hours, minutes etc and one set by user in miliseconds.
        System.out.println("DATY" +
                "\nCURRENT:" +current+
                "\nyearsMonth:" +yearMonthDay+
                "\nminutes:"+hourMinuteSecond);
        timeMilisDifference=yearMonthDay.getTime()-current.getTime()+hourMinuteSecond.getTime()-current.getTime();
        System.out.print("SENDING TO SLEE:"+timeMilisDifference+".UID:"+UID);
        new SleeConnector().sendData(timeMilisDifference,UID);
        
        return value;
    }
    
    
}
