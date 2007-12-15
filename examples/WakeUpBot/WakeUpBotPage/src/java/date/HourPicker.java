/*
 * HourPicker.java
 *
 * Created on 24 luty 2006, 23:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package date;
import javax.faces.component.UIInput;
/**
 *
 * @author baranowb
 */
public class HourPicker extends UIInput{
    
    /** Creates a new instance of HourPicker */
    public HourPicker() {
        setRendererType("date.HourPicker"); // this component has a renderer
    }
    
}
