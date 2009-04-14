package org.mobicents.jain.protocol.ip.mgcp.pkg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Value implements Map<Parameter, Value> {

	Map<Parameter, Value> parameterValueMap = new HashMap<Parameter, Value>();

	public Value() {

	}

	public void clear() {
		parameterValueMap.clear();
	}

	public Set<Entry<Parameter, Value>> entrySet() {
		return parameterValueMap.entrySet();
	}

	public boolean isEmpty() {
		return parameterValueMap.isEmpty();
	}

	public Set<Parameter> keySet() {
		return parameterValueMap.keySet();
	}

	public Value put(Parameter key, Value value) {
		return parameterValueMap.put(key, value);
	}

	public void putAll(Map<? extends Parameter, ? extends Value> t) {
		parameterValueMap.putAll(t);
	}

	public int size() {
		return parameterValueMap.size();
	}

	public Collection<Value> values() {
		return parameterValueMap.values();
	}

	public boolean containsKey(Object key) {
		return parameterValueMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return parameterValueMap.containsKey(value);
	}

	public Value get(Object key) {
		return parameterValueMap.get(key);
	}

	public Value remove(Object key) {
		return parameterValueMap.remove(key);
	}

}
