/*
 * NewClass.java
 *
 * Created on 13 grudzieñ 2005, 18:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package date;
import javax.faces.component.UIInput;
/**
 * This class extends UIInput. Its used as ValuePlaceHolder for DatePicker component
 * @author baranowb
 */
public class DatePicker extends UIInput{
    
    /**
     *Creates new instance and sets Renderer Type to <b>date.DatePicker</b> 
     */
    public DatePicker() {
        setRendererType("date.DatePicker"); // this component has a renderer
    }
    
}
