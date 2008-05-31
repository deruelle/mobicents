/*
 * Mobicents: The Open Source VoIP Middleware Platform
 *
 * Copyright 2003-2006, Mobicents, 
 * and individual contributors as indicated
 * by the @authors tag. See the copyright.txt 
 * in the distribution for a full listing of   
 * individual contributors.
 *
 * This is free software; you can redistribute it
 * and/or modify it under the terms of the 
 * GNU General Public License (GPL) as
 * published by the Free Software Foundation; 
 * either version 2 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the 
 * GNU General Public
 * License along with this software; 
 * if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, 
 * Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.mobicents.slee.container.management.console.client.log;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author baranowb
 *
 */
public class HandlerInfo implements IsSerializable {

	private int index=-1;
	private String name=null;
	
	private String filterClassName=null;
	
	private String formatterClassName=null;
	
	private String handelerClassName=null;
	
	private String level=null;
	/**
	 * @gwt.typeArgs <java.lang.String,java.lang.String>
	 */
	private HashMap otherOptions=null;

	public HandlerInfo()
	{
		super();
	}
	
	
	public HandlerInfo(int index, String name, String filterClassName,
			String formatterClassName, String handelerClassName, String level,
			HashMap otherOptions) {
		super();
		this.index = index;
		this.name = name;
		this.filterClassName = filterClassName;
		this.formatterClassName = formatterClassName;
		this.handelerClassName = handelerClassName;
		this.level = level;
		this.otherOptions = otherOptions;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getFilterClassName() {
		return filterClassName;
	}

	public String getFormatterClassName() {
		return formatterClassName;
	}

	public String getHandelerClassName() {
		return handelerClassName;
	}

	public String getLevel() {
		return level;
	}

	/**
	 * 
	 * @return
	 * @gwt.typeArgs <java.lang.String,java.lang.String>
	 */
	public HashMap getOtherOptions() {
		return otherOptions;
	}
	
	
	
	
	
	
}
