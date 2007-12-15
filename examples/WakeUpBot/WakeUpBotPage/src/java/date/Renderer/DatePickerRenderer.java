    /*
 * DatePickerRenderer.java
 *
 * Created on 13 grudzieñ 2005, 18:17
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
 * HTML renderer for DatePicker comopnent. It Ovverides nessesary methods to generate designed comopnent:
 *<ul>
 *<li> dropdown list with years
 *<li> dropdown list with months (localized)
 *<li> dropdown list with day
 *</ul>
 * @author baranowb
 * @version 1.0
 */
public class DatePickerRenderer extends Renderer {
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
        String year = (String) params.get(clientId + "_year");
        if (year != null) {
            Map dateParts = new HashMap( );
            dateParts.put("year", year);
            dateParts.put("month", params.get(clientId + "_month"));
            dateParts.put("day", params.get(clientId + "_day"));
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
        int year = Integer.parseInt((String) dateParts.get("year"));
        int month = Integer.parseInt((String) dateParts.get("month"));
        int day = Integer.parseInt((String) dateParts.get("day"));
        Date tmp= Calendar.getInstance().getTime();
        tmp.setYear(year - 1900);
        tmp.setMonth(month - 1);
        tmp.setDate(day);
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
            date = value instanceof Date ? (Date) value : Calendar.getInstance().getTime();;
        }
        String styleClass =(String) component.getAttributes( ).get("styleClass");
        int startYear = date.getYear( ) + 1900;
        Object startYearAttr = component.getAttributes( ).get("startYear");
        if (startYearAttr != null) {
            
            startYear = ((Number) startYearAttr).intValue( );
        }
        int years = 5;
        Object yearsAttr = component.getAttributes( ).get("years");
        if (yearsAttr != null) {
            years = ((Number) yearsAttr).intValue( );
        }
        String clientId = component.getClientId(context);
        ResponseWriter out = context.getResponseWriter( );
        renderMenu(out, getYears(startYear, years), date.getYear( ) + 1900,clientId + "_year", styleClass, component);
        renderMenu(out, getMonths( ), date.getMonth( ) + 1, clientId + "_month", styleClass, component);
        renderMenu(out, getDays( ), date.getDate( ), clientId + "_day", styleClass, component);
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
            SelectItem si = (SelectItem) i.next( );
            Integer value = (Integer) si.getValue( );
            String label = si.getLabel( );
            out.startElement("option", component);
            out.writeAttribute("value", value, null);
            if (value.intValue( ) == selected) {
                out.writeAttribute("selected", "selected", null);
            }
            out.writeText(label, null);
        }
        out.endElement("select");
    }
    private List getYears(int startYear, int noOfyears) {
        List years = new ArrayList( );
        for (int i = startYear; i < startYear + noOfyears; i++) {
            Integer year = new Integer(i);
            years.add(new SelectItem(year, year.toString( )));
        }
        return years;
    }
    private List getMonths( ) {
        Locale locale=getLocale();
        DateFormatSymbols dfs;
        if(locale==null)
            dfs = new DateFormatSymbols();
        else
            dfs = new DateFormatSymbols(locale);
        String[] names = dfs.getMonths( );
        List months = new ArrayList( );
        for (int i = 0; i < 12; i++) {
            Integer key = new Integer(i + 1);
            String label = names[i];
            months.add(new SelectItem(key, label));
        }
        return months;
    }
    private List getDays( ) {
        List days = new ArrayList( );
        for (int i = 1; i < 32; i++) {
            Integer day = new Integer(i);
            days.add(new SelectItem(day, day.toString( )));
        }
        return days;
    }
    private Locale getLocale() {
        Locale locale = null;
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        if (viewRoot != null)
            locale = viewRoot.getLocale();
        if (locale == null)
            locale = Locale.getDefault();
        return locale;
    }
}