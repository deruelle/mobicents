package org.mobicents.javax.media.mscontrol.resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Symbol;

/**
 * 
 * @author amit bhayani
 *
 */
public class ParametersImpl implements Parameters {
	
	Map<Symbol, Object> parameters;
	
	public ParametersImpl(){
		parameters = new HashMap<Symbol, Object>();
	}

	public void clear() {
		parameters.clear();

	}

	public boolean containsKey(Object key) {
		return parameters.containsKey(key);		
	}

	public boolean containsValue(Object value) {
		return parameters.containsValue(value);	
		
	}

	public Set<java.util.Map.Entry<Symbol, Object>> entrySet() {		
		return parameters.entrySet();
	}

	public Object get(Object key) {
		return parameters.get(key);
	}

	public boolean isEmpty() {		
		return parameters.isEmpty();
	}

	public Set<Symbol> keySet() {		
		return parameters.keySet();
	}

	public Object put(Symbol key, Object value) {
		return parameters.put(key, value);
	}

	public void putAll(Map<? extends Symbol, ? extends Object> t) {
		parameters.putAll(t);
	}

	public Object remove(Object key) {		
		return parameters.remove(key);
	}

	public int size() {		
		return parameters.size();
	}

	public Collection<Object> values() {		
		return parameters.values();
	}

}
