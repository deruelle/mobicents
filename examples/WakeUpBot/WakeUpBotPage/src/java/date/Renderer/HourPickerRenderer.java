/*
 * HourPickerRenderer.java
 *
 * Created on 24 luty 2006, 23:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package date.Renderer;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.ValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;
import javax.faces.render.Renderer;
import javax.swing.text.html.HTML;
/**
 *
 * @author Administrator
 */
public class HourPickerRenderer extends Renderer{
    
    /** Creates a new instance of HourPickerRenderer */
    public HourPickerRenderer() {
    }
    /**
     * Decodes Request. Simply checks for specific data sent in request. If its present,
     * this method stores them in HashMap and uses <b><i>setSubmitedValue()</i></b> implemented by
     * component. Otherwise does nothing.
     * <br>In reality:
     * <b>~</b><i>requestParameterMap.get(component.clientId+[_year|_day|_month]</i> values are stored in 
     * separate HashMap and passed to component.
     * 
     *@author baranowb
     *@author kijanowj
     *@version 1.0
     *@param context FacesContext context - current faces context.
     *@param component UIComponent component for which this renderer works
     *hopefuly it implements <b>EditableValueHolder</b>.
     *
     *
     */
    public void decode(FacesContext context, UIComponent component) {
        if (isDisabledOrReadOnly(component)) {
            return;
        }
        String clientId = component.getClientId(context);
        Map params = context.getExternalContext( ).getRequestParameterMap( );
        String hour = (String) params.get(clientId + "_hour");
        if (hour != null) {
            Map dateParts = new HashMap( );
            dateParts.put("hour", hour);
            dateParts.put("minute", params.get(clientId + "_minute"));
            dateParts.put("second", params.get(clientId + "_second"));
            ((EditableValueHolder) component).setSubmittedValue(dateParts);
        }
    }
     private boolean isDisabledOrReadOnly(UIComponent component) {
        boolean disabled = false;
        boolean readOnly = false;
        Object disabledAttr = component.getAttributes( ).get("disabled");
        if (disabledAttr != null) {
            disabled = disabledAttr.equals(Boolean.TRUE);
        }
        Object readOnlyAttr = component.getAttributes( ).get("readonly");
        if (readOnlyAttr != null) {
            readOnly = readOnlyAttr.equals(Boolean.TRUE);
        }
        return disabled || readOnly;
    }
      /**
     * Invoked whenever values passed in request from <b><x:DatePicker ...></b> are going to be passed to model.
     * This method simply converts them into proper object and casts expllicitly to Object. 
     *@author baranowb
     *@author kijanowj
     *@version 1.0
     *@param context FacesContext context - current faces context.
     *@param component UIComponent component for which this renderer works
     *hopefuly it implements <b>EditableValueHolder</b>.
     *@param submitedValue In this case HashMap containing values retrieved in <b> {@link date.renderer.DatePickerRenderer#decode() decode()} </b> method.
     *@return Object, instanceOf java.util.Date
     */
    public Object getConvertedValue(FacesContext context,UIComponent component, Object submittedValue) {
        Map dateParts = (Map) submittedValue;
        int hour = Integer.parseInt((String) dateParts.get("hour"));
        int minute = Integer.parseInt((String) dateParts.get("minute"));
        int second = Integer.parseInt((String) dateParts.get("second"));
        Date tmp= Calendar.getInstance().getTime();
        tmp.setHours(hour);
        tmp.setMinutes(minute);
        tmp.setSeconds(second);
        return tmp;
    }
    /**
     * Starts encoding of this component. In this case it does all work. If component was somehow filled 
     * this method ensures that those values are encoded in repsonse. Otherwise it uses current date to fill it.
     *@author baranowb
     *@author kijanowj
     *@version 1.0
     *@param context FacesContext context - current faces context.
     *@param component UIComponent component for which this renderer works
     *hopefuly it implements <b>EditableValueHolder</b>.
     */
    public void encodeBegin(FacesContext context, UIComponent component)throws IOException {

        if (!component.isRendered( )) {
            return;
        }
        Date date = null;
        //GETTING VALUE MAP FROM COMPONENT
        Map submittedValue = (Map)((EditableValueHolder) component).getSubmittedValue( );
        if (submittedValue != null) {
            date = (Date) getConvertedValue(context, component, submittedValue);
        } else {
            Object value = ((ValueHolder) component).getValue( );
            date = value instanceof Date ? (Date) value : new Date( );
        }
        String styleClass =(String) component.getAttributes( ).get("styleClass");
       
        String clientId = component.getClientId(context);
        ResponseWriter out = context.getResponseWriter( );
        renderMenu(out,getNumbers(24) , date.getHours(),clientId + "_hour", styleClass, component);
        renderMenu(out, getNumbers(60), date.getMinutes(), clientId + "_minute", styleClass, component);
        renderMenu(out,getNumbers(60) , date.getSeconds(), clientId + "_second", styleClass, component);
    }
    private void renderMenu(ResponseWriter out, List items, int selected,String clientId, String styleClass, UIComponent component)  throws IOException {
        out.startElement("select", component);
        out.writeAttribute("name", clientId, "id");
        //out.writeAttribute("size",1,null);
        //out.writeAttribute("multiple","multiple",null);//JUST WANTET TO KNOW HOW IT WORKS ;]
        
        if (styleClass != null) {
            out.writeAttribute("class", styleClass, "styleClass");
        }
        Iterator i = items.iterator( );
        while (i.hasNext( )) {
            
            Integer value = (Integer) i.next( );
            String label = value.toString();
            out.startElement("option", component);
            out.writeAttribute("value", value, null);
            if (value.intValue( ) == selected) {
                out.writeAttribute("selected", "selected", null);
            }
            out.writeText(label, null);
        }
        out.endElement("select");
    }
    
    
    private List getNumbers(int howMany) {
        List numbers = new ArrayList( );
        for (int i = 0; i < howMany; i++) {
            Integer tmp = new Integer(i);
            numbers.add(tmp);
        }
        return numbers;
    }
   
}
