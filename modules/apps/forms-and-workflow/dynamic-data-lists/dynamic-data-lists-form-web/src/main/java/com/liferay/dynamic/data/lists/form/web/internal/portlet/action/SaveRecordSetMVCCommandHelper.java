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

package com.liferay.dynamic.data.lists.form.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.exception.RecordSetSettingsRedirectURLException;
import com.liferay.dynamic.data.lists.form.web.internal.converter.DDLFormRuleDeserializer;
import com.liferay.dynamic.data.lists.form.web.internal.converter.DDLFormRuleToDDMFormRuleConverter;
import com.liferay.dynamic.data.lists.form.web.internal.converter.model.DDLFormRule;
import com.liferay.dynamic.data.lists.form.web.internal.util.DDMFormTemplateContextToDDMForm;
import com.liferay.dynamic.data.lists.form.web.internal.util.DDMFormTemplateContextToDDMFormLayout;
import com.liferay.dynamic.data.lists.form.web.internal.util.DDMFormTemplateContextToDDMFormValues;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSetSettings;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.exception.StructureLayoutException;
import com.liferay.dynamic.data.mapping.form.values.query.DDMFormValuesQuery;
import com.liferay.dynamic.data.mapping.form.values.query.DDMFormValuesQueryFactory;
import com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(immediate = true, service = SaveRecordSetMVCCommandHelper.class)
public class SaveRecordSetMVCCommandHelper {

	public DDLRecordSet saveRecordSet(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		try {
			long recordSetId = ParamUtil.getLong(portletRequest, "recordSetId");

			if (recordSetId == 0) {
				return addRecordSet(portletRequest, portletResponse);
			}
			else {
				return updateRecordSet(portletRequest, portletResponse);
			}
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e);
			}

			throw e;
		}
	}

	protected DDMStructure addDDMStructure(
			PortletRequest portletRequest, DDMFormValues settingsDDMFormValues)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMStructure.class.getName(), portletRequest);

		long groupId = ParamUtil.getLong(portletRequest, "groupId");
		String structureKey = ParamUtil.getString(
			portletRequest, "structureKey");
		String storageType = getStorageType(settingsDDMFormValues);
		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");
		DDMForm ddmForm = getDDMForm(portletRequest, serviceContext);
		DDMFormLayout ddmFormLayout = getDDMFormLayout(portletRequest);

		return ddmStructureService.addStructure(
			groupId, DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			_portal.getClassNameId(DDLRecordSet.class), structureKey,
			getLocalizedMap(
				name, ddmForm.getDefaultLocale(),
				ddmForm.getAvailableLocales()),
			getLocalizedMap(
				description, ddmForm.getDefaultLocale(),
				ddmForm.getAvailableLocales()),
			ddmForm, ddmFormLayout, storageType,
			DDMStructureConstants.TYPE_AUTO, serviceContext);
	}

	protected DDLRecordSet addRecordSet(
			PortletRequest portletRequest, long ddmStructureId,
			Locale defaultLocale, Set<Locale> availableLocales)
		throws Exception {

		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");

		return addRecordSet(
			portletRequest, ddmStructureId,
			getLocalizedMap(name, defaultLocale, availableLocales),
			getLocalizedMap(description, defaultLocale, availableLocales));
	}

	protected DDLRecordSet addRecordSet(
			PortletRequest portletRequest, long ddmStructureId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap)
		throws Exception {

		long groupId = ParamUtil.getLong(portletRequest, "groupId");
		String recordSetKey = ParamUtil.getString(
			portletRequest, "recordSetKey");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDLRecordSet.class.getName(), portletRequest);

		if (ParamUtil.getBoolean(portletRequest, "autoSave")) {
			serviceContext.setAttribute(
				"status", WorkflowConstants.STATUS_DRAFT);
		}

		return ddlRecordSetService.addRecordSet(
			groupId, ddmStructureId, recordSetKey, nameMap, descriptionMap,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT,
			DDLRecordSetConstants.SCOPE_FORMS, serviceContext);
	}

	protected DDLRecordSet addRecordSet(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		DDMFormValues settingsDDMFormValues = getSettingsDDMFormValues(
			portletRequest);

		DDMStructure ddmStructure = addDDMStructure(
			portletRequest, settingsDDMFormValues);

		DDMForm ddmForm = ddmStructure.getDDMForm();

		DDLRecordSet recordSet = addRecordSet(
			portletRequest, ddmStructure.getStructureId(),
			ddmForm.getDefaultLocale(), ddmForm.getAvailableLocales());

		updateRecordSetSettings(
			portletRequest, recordSet, settingsDDMFormValues);

		return recordSet;
	}

	protected DDMForm getDDMForm(
			PortletRequest portletRequest, ServiceContext serviceContext)
		throws PortalException {

		try {
			String serializedContext = ParamUtil.getString(
				portletRequest, "serializedContext");

			return ddmFormTemplateContextToDDMForm.deserialize(
				serializedContext);
		}
		catch (PortalException pe) {
			throw new StructureDefinitionException(pe);
		}
	}

	protected DDMFormLayout getDDMFormLayout(PortletRequest portletRequest)
		throws PortalException {

		try {
			String serializedContext = ParamUtil.getString(
				portletRequest, "serializedContext");

			return ddmFormTemplateContextToDDMFormLayout.deserialize(
				serializedContext);
		}
		catch (PortalException pe) {
			throw new StructureLayoutException(pe);
		}
	}

	protected List<DDMFormRule> getDDMFormRules(PortletRequest portletRequest)
		throws PortalException {

		String rules = ParamUtil.getString(portletRequest, "rules");

		if (Validator.isNull(rules) || Objects.equals("[]", rules)) {
			return Collections.emptyList();
		}

		List<DDLFormRule> ddlFormRules = ddlFormRuleDeserializer.deserialize(
			rules);

		return ddlFormRulesToDDMFormRulesConverter.convert(ddlFormRules);
	}

	protected Map<Locale, String> getLocalizedMap(
			String value, Locale defaultLocale, Set<Locale> availableLocales)
		throws PortalException {

		Map<Locale, String> localizedMap = new HashMap<>();
		JSONObject jsonObject = jsonFactory.createJSONObject(value);

		for (Locale availableLocale : availableLocales) {
			String valueString = jsonObject.getString(
				LocaleUtil.toLanguageId(availableLocale), null);

			if (valueString == null) {
				valueString = jsonObject.getString(
					LocaleUtil.toLanguageId(defaultLocale));
			}

			localizedMap.put(availableLocale, valueString);
		}

		return localizedMap;
	}

	protected DDMFormValues getSettingsDDMFormValues(
			PortletRequest portletRequest)
		throws PortalException {

		String settingsContext = ParamUtil.getString(
			portletRequest, "serializedSettingsContext");

		DDMForm ddmForm = DDMFormFactory.create(DDLRecordSetSettings.class);

		DDMFormValues settingsDDMFormValues =
			ddmFormTemplateContextToDDMFormValues.deserialize(
				ddmForm, settingsContext);

		return settingsDDMFormValues;
	}

	protected String getSingleValue(String value) {
		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(value);

			if (jsonArray.length() > 0) {
				return jsonArray.getString(0);
			}

			return StringPool.BLANK;
		}
		catch (Exception e) {
			return value;
		}
	}

	protected String getStorageType(DDMFormValues ddmFormValues)
		throws PortalException {

		DDMFormValuesQuery ddmFormValuesQuery =
			ddmFormValuesQueryFactory.create(ddmFormValues, "/storageType");

		DDMFormFieldValue ddmFormFieldValue =
			ddmFormValuesQuery.selectSingleDDMFormFieldValue();

		Value value = ddmFormFieldValue.getValue();

		String storageType = getSingleValue(
			value.getString(ddmFormValues.getDefaultLocale()));

		if (Validator.isNull(storageType)) {
			storageType = StorageType.JSON.toString();
		}

		return storageType;
	}

	protected String getWorkflowDefinition(DDMFormValues ddmFormValues)
		throws PortalException {

		DDMFormValuesQuery ddmFormValuesQuery =
			ddmFormValuesQueryFactory.create(
				ddmFormValues, "/workflowDefinition");

		DDMFormFieldValue ddmFormFieldValue =
			ddmFormValuesQuery.selectSingleDDMFormFieldValue();

		Value value = ddmFormFieldValue.getValue();

		return getSingleValue(
			value.getString(ddmFormValues.getDefaultLocale()));
	}

	protected DDMStructure updateDDMStructure(PortletRequest portletRequest)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMStructure.class.getName(), portletRequest);

		long ddmStructureId = ParamUtil.getLong(
			portletRequest, "ddmStructureId");
		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");
		DDMForm ddmForm = getDDMForm(portletRequest, serviceContext);
		DDMFormLayout ddmFormLayout = getDDMFormLayout(portletRequest);

		return ddmStructureService.updateStructure(
			ddmStructureId, DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			getLocalizedMap(
				name, ddmForm.getDefaultLocale(),
				ddmForm.getAvailableLocales()),
			getLocalizedMap(
				description, ddmForm.getDefaultLocale(),
				ddmForm.getAvailableLocales()),
			ddmForm, ddmFormLayout, serviceContext);
	}

	protected DDLRecordSet updateRecordSet(
			PortletRequest portletRequest, long ddmStructureId,
			Locale defaultLocale, Set<Locale> availableLocales)
		throws Exception {

		long recordSetId = ParamUtil.getLong(portletRequest, "recordSetId");

		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDLRecordSet.class.getName(), portletRequest);

		if (ParamUtil.getBoolean(portletRequest, "autoSave")) {
			serviceContext.setAttribute(
				"status", WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		return ddlRecordSetService.updateRecordSet(
			recordSetId, ddmStructureId,
			getLocalizedMap(name, defaultLocale, availableLocales),
			getLocalizedMap(description, defaultLocale, availableLocales),
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT, serviceContext);
	}

	protected DDLRecordSet updateRecordSet(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		DDMStructure ddmStructure = updateDDMStructure(portletRequest);

		DDMForm ddmForm = ddmStructure.getDDMForm();

		DDLRecordSet recordSet = updateRecordSet(
			portletRequest, ddmStructure.getStructureId(),
			ddmForm.getDefaultLocale(), ddmForm.getAvailableLocales());

		DDMFormValues settingsDDMFormValues = getSettingsDDMFormValues(
			portletRequest);

		updateRecordSetSettings(
			portletRequest, recordSet, settingsDDMFormValues);

		return recordSet;
	}

	protected void updateRecordSetSettings(
			PortletRequest portletRequest, DDLRecordSet recordSet,
			DDMFormValues settingsDDMFormValues)
		throws PortalException {

		validateRedirectURL(settingsDDMFormValues);

		ddlRecordSetService.updateRecordSet(
			recordSet.getRecordSetId(), settingsDDMFormValues);

		updateWorkflowDefinitionLink(
			portletRequest, recordSet, settingsDDMFormValues);
	}

	protected void updateWorkflowDefinitionLink(
			PortletRequest portletRequest, DDLRecordSet recordSet,
			DDMFormValues ddmFormValues)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(portletRequest, "groupId");

		String workflowDefinition = getWorkflowDefinition(ddmFormValues);

		if (workflowDefinition.equals("no-workflow")) {
			workflowDefinition = "";
		}

		workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			themeDisplay.getUserId(), themeDisplay.getCompanyId(), groupId,
			DDLRecordSet.class.getName(), recordSet.getRecordSetId(), 0,
			workflowDefinition);
	}

	protected void validateRedirectURL(DDMFormValues settingsDDMFormValues)
		throws PortalException {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			settingsDDMFormValues.getDDMFormFieldValuesMap();

		if (!ddmFormFieldValuesMap.containsKey("redirectURL")) {
			return;
		}

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			"redirectURL");

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		for (Locale availableLocale : value.getAvailableLocales()) {
			String valueString = value.getString(availableLocale);

			if (Validator.isNotNull(valueString)) {
				String escapedRedirect = _portal.escapeRedirect(valueString);

				if (Validator.isNull(escapedRedirect)) {
					throw new RecordSetSettingsRedirectURLException();
				}
			}
		}
	}

	@Reference
	protected DDLFormRuleDeserializer ddlFormRuleDeserializer;

	@Reference
	protected DDLFormRuleToDDMFormRuleConverter
		ddlFormRulesToDDMFormRulesConverter;

	@Reference
	protected DDLRecordSetService ddlRecordSetService;

	@Reference
	protected DDMFormJSONDeserializer ddmFormJSONDeserializer;

	@Reference
	protected DDMFormLayoutJSONDeserializer ddmFormLayoutJSONDeserializer;

	@Reference
	protected DDMFormTemplateContextToDDMForm ddmFormTemplateContextToDDMForm;

	@Reference
	protected DDMFormTemplateContextToDDMFormLayout
		ddmFormTemplateContextToDDMFormLayout;

	@Reference
	protected DDMFormTemplateContextToDDMFormValues
		ddmFormTemplateContextToDDMFormValues;

	@Reference
	protected DDMFormValuesJSONDeserializer ddmFormValuesJSONDeserializer;

	@Reference
	protected DDMFormValuesQueryFactory ddmFormValuesQueryFactory;

	@Reference
	protected DDMStructureService ddmStructureService;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected volatile WorkflowDefinitionLinkLocalService
		workflowDefinitionLinkLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		SaveRecordSetMVCCommandHelper.class);

	@Reference
	private Portal _portal;

}