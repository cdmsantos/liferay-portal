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

package com.liferay.dynamic.data.mapping.web.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.DDMStructureManager;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.util.DDM;

import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + PortletKeys.DYNAMIC_DATA_MAPPING,
		"mvc.command.name=addStructure"
	},
	service = MVCActionCommand.class
)
public class AddStructureMVCActionCommand extends DDMBaseMVCActionCommand {

	protected DDMStructure addStructure(ActionRequest actionRequest)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long scopeClassNameId = ParamUtil.getLong(
			actionRequest, "scopeClassNameId");
		String structureKey = ParamUtil.getString(
			actionRequest, "structureKey");
		long parentStructureId = ParamUtil.getLong(
			actionRequest, "parentStructureId",
			DDMStructureManager.STRUCTURE_DEFAULT_PARENT_STRUCTURE_ID);
		Map<Locale, String> nameMap = LocalizationUtil.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap =
			LocalizationUtil.getLocalizationMap(actionRequest, "description");
		DDMForm ddmForm = _ddm.getDDMForm(actionRequest);
		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);
		String storageType = ParamUtil.getString(actionRequest, "storageType");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMStructure.class.getName(), actionRequest);

		return _ddmStructureService.addStructure(
			groupId, parentStructureId, scopeClassNameId, structureKey, nameMap,
			descriptionMap, ddmForm, ddmFormLayout, storageType,
			DDMStructureManager.STRUCTURE_TYPE_DEFAULT, serviceContext);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		DDMStructure structure = addStructure(actionRequest);

		setRedirectAttribute(actionRequest, structure);
	}

	@Reference
	protected void setDDM(DDM ddm) {
		_ddm = ddm;
	}

	@Reference
	protected void setDDMStructureService(
		DDMStructureService ddmStructureService) {

		_ddmStructureService = ddmStructureService;
	}

	private DDM _ddm;
	private DDMStructureService _ddmStructureService;

}