/*
 * InputDatePickerTag.java
 *
 * Created on 13 grudzieñ 2005, 18:28
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
 * Tag class for DatePicker. Extends UIComponentTag with <i>startYear</i> and <i>years</i> tag parameters.
 *@author baranowb
 *@author kijanowj
 *@version 1.0
 */
public class InputDatePickerTag extends UIComponentTag {
    private String startYear;
    private String years;
    private String value;
    private String styleClass;
    
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }
    public void setYears(String years) {
        this.years = years;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    public String getComponentType( ) {
        return "javax.faces.Input";
    }
    public String getRendererType( ) {
        return "date.DatePicker";
    }
    /**
     * Calls <b>super.setProperties(component)</b> to ensure use of inherited tag parameteres, and after that handles new params.
     *@author baranowb
     *@author kijanowj
     *@version 1.0
     */
    protected void setProperties(UIComponent component) {
        
        super.setProperties(component);
        
        FacesContext context = getFacesContext( );
        if (startYear != null) {
            if (isValueReference(startYear)) {
                ValueBinding vb = context.getApplication( ).createValueBinding(startYear);
                component.setValueBinding("startYear", vb);
            } else {
                component.getAttributes( ).put("startYear",new Integer(startYear));
            }
        }
        if (years != null) {
            if (isValueReference(years)) {
                ValueBinding vb = context.getApplication( ).createValueBinding(years);
                component.setValueBinding("years", vb);
            } else {
                component.getAttributes( ).put("years", new Integer(years));
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
        if (styleClass != null) {
            if (isValueReference(styleClass)) {
                ValueBinding vb = context.getApplication( ).createValueBinding(styleClass);
                component.setValueBinding("styleClass", vb);
            } else {
                component.getAttributes( ).put("styleClass", styleClass);
            }
        }
    }
}