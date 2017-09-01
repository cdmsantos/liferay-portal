/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.lists.form.web.internal.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rafael Praxedes
 */
@XmlRootElement
public class UserModel {
	
	public UserModel() {}
	
	@XmlElement
	public long getUserId() {
		return _userId;
	}

	@XmlElement
	public String getName() {
		return _name;
	}
	
	public void setUserId(long userId) {
		this._userId = userId;
	}

	public void setName(String userId) {
		this._name = userId;
	}

	private long _userId;
	private String _name;
}
