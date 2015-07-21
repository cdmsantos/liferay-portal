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

package com.liferay.dynamic.data.mapping.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;

import java.io.Serializable;

import javax.portlet.PortletRequest;

/**
 * @author Eduardo Lundgren
 * @author Marcellus Tavares
 * @author Leonardo Barros
 */
public interface DDM {
	
	public static final String FIELDS_DISPLAY_NAME = "_fieldsDisplay";

	public static final String INSTANCE_SEPARATOR = "_INSTANCE_";

	public static final String TYPE_CHECKBOX = "checkbox";

	public static final String TYPE_DDM_DATE = "ddm-date";

	public static final String TYPE_DDM_DOCUMENTLIBRARY = "ddm-documentlibrary";

	public static final String TYPE_DDM_IMAGE = "ddm-image";

	public static final String TYPE_DDM_LINK_TO_PAGE = "ddm-link-to-page";

	public static final String TYPE_DDM_TEXT_HTML = "ddm-text-html";

	public static final String TYPE_RADIO = "radio";

	public static final String TYPE_SELECT = "select";

	public DDMDisplay getDDMDisplay(long classNameId) throws PortalException;

	public DDMForm getDDMForm(long classNameId, long classPK)
		throws PortalException;

	public DDMForm getDDMForm(PortletRequest portletRequest)
		throws PortalException;

	public JSONArray getDDMFormFieldsJSONArray(
		DDMStructure ddmStructure, String script);

	public JSONArray getDDMFormFieldsJSONArray(
		DDMStructureVersion ddmStructureVersion, String script);

	public DDMFormValues getDDMFormValues(
			long ddmStructureId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException;

	public DDMPermissionHandler getDDMPermissionHandler(long classNameId);

	public DDMFormLayout getDefaultDDMFormLayout(DDMForm ddmForm);

	public Serializable getDisplayFieldValue(
			ThemeDisplay themeDisplay, Serializable fieldValue, String type)
		throws Exception;

	public Fields getFields(
			long ddmStructureId, long ddmTemplateId,
			ServiceContext serviceContext)
		throws PortalException;

	public Fields getFields(
			long ddmStructureId, long ddmTemplateId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException;

	public Fields getFields(long ddmStructureId, ServiceContext serviceContext)
		throws PortalException;

	public Fields getFields(
			long ddmStructureId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException;

	public String[] getFieldsDisplayValues(Field fieldsDisplayField)
		throws Exception;

	public Serializable getIndexedFieldValue(
			Serializable fieldValue, String type)
		throws Exception;

	public OrderByComparator<DDMStructure> getStructureOrderByComparator(
		String orderByCol, String orderByType);

	public OrderByComparator<DDMTemplate> getTemplateOrderByComparator(
		String orderByCol, String orderByType);

	public Fields mergeFields(Fields newFields, Fields existingFields);

}