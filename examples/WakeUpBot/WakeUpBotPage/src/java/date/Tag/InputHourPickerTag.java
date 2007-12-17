/*
 * InputHourPickerTag.java
 *
 * Created on 24 luty 2006, 23:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package date.Tag;
import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
/**
 *
 * @author Administrator
 */
public class InputHourPickerTag extends UIComponentTag{
    private String styleClass=null;
    private String value;
    /** Creates a new instance of InputHourPickerTag */
    public InputHourPickerTag() {
    }
    
    public String getComponentType() {
        return "javax.faces.Input";
    }
    
    public String getRendererType() {
        return "date.HourPicker";
    }
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    public void setValue(String value)
    {this.value=value;}
    /**
     * Calls <b>super.setProperties(component)</b> to ensure use of inherited tag parameteres, and after that handles new params.
     *@author baranowb
     *@author kijanowj
     *@version 1.0
     */
    protected void setProperties(UIComponent component) {
        
        super.setProperties(component);
        System.out.println("SET PROPERTIES");
        FacesContext context = getFacesContext( );
        
        if (styleClass != null) {
            if (isValueReference(styleClass)) {
                ValueBinding vb = context.getApplication( ).createValueBinding(styleClass);
                component.setValueBinding("styleClass", vb);
            } else {
                component.getAttributes( ).put("styleClass", styleClass);
            }
        }
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding vb = context.getApplication( ).createValueBinding(value);
                component.setValueBinding("value", vb);
            } else {
                component.getAttributes( ).put("value", value);
            }
            
        }
    }
}
