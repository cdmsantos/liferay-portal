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

package com.liferay.document.library.google.docs.util;

import com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializer;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.ClassNameLocalService;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portlet.documentlibrary.model.DLFileEntryMetadata;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalService;
import com.liferay.portlet.dynamicdatamapping.DDMStructureManager;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class GoogleDocsDLFileEntryTypeHelper {

	public GoogleDocsDLFileEntryTypeHelper(
		Company company, ClassNameLocalService classNameLocalService, DDM ddm,
		DDMFormXSDDeserializer ddmFormXSDDeserializer,
		DDMStructureLocalService ddmStructureLocalService,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
		UserLocalService userLocalService) {

		_company = company;
		_classNameLocalService = classNameLocalService;
		_ddm = ddm;
		_ddmFormXSDDeserializer = ddmFormXSDDeserializer;
		_ddmStructureLocalService = ddmStructureLocalService;
		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
		_userLocalService = userLocalService;

		_dlFileEntryMetadataClassNameId = _classNameLocalService.getClassNameId(
			DLFileEntryMetadata.class);
	}

	public com.liferay.dynamic.data.mapping.model.DDMStructure
		addGoogleDocsDDMStructure() throws PortalException {

		long defaultUserId = _userLocalService.getDefaultUserId(
			_company.getCompanyId());

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.getDefault(), "Google Docs Metadata");

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(LocaleUtil.getDefault(), "Google Docs Metadata");

		String definition = ResourceUtil.get(
			this, "dependencies/ddm_structure_google_docs.xml");

		DDMForm ddmForm = _ddmFormXSDDeserializer.deserialize(definition);

		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(_company.getGroupId());
		serviceContext.setUserId(defaultUserId);

		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureLocalService.addStructure(
				defaultUserId, _company.getGroupId(),
				DDMStructureManager.STRUCTURE_DEFAULT_PARENT_STRUCTURE_ID,
				_dlFileEntryMetadataClassNameId,
				GoogleDocsConstants.DDM_STRUCTURE_KEY_GOOGLE_DOCS, nameMap,
				descriptionMap, ddmForm, ddmFormLayout,
				StorageType.JSON.toString(),
				DDMStructureManager.STRUCTURE_TYPE_DEFAULT, serviceContext);

		return ddmStructure;
	}

	public DLFileEntryType addGoogleDocsDLFileEntryType()
		throws PortalException {

		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureLocalService.fetchStructure(
				_company.getGroupId(), _dlFileEntryMetadataClassNameId,
				GoogleDocsConstants.DDM_STRUCTURE_KEY_GOOGLE_DOCS);

		if (ddmStructure == null) {
			ddmStructure = addGoogleDocsDDMStructure();
		}

		List<DLFileEntryType> dlFileEntryTypes =
			_dlFileEntryTypeLocalService.getFileEntryTypes(
				ddmStructure.getStructureId());

		if (!dlFileEntryTypes.isEmpty()) {
			return dlFileEntryTypes.get(0);
		}

		return addGoogleDocsDLFileEntryType(ddmStructure.getStructureId());
	}

	protected DLFileEntryType addGoogleDocsDLFileEntryType(long ddmStructureId)
		throws PortalException {

		long defaultUserId = _userLocalService.getDefaultUserId(
			_company.getCompanyId());

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.getDefault(), "Google Docs");

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(LocaleUtil.getDefault(), "Google Docs");

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(_company.getGroupId());
		serviceContext.setUserId(defaultUserId);

		return _dlFileEntryTypeLocalService.addFileEntryType(
			defaultUserId, _company.getGroupId(),
			GoogleDocsConstants.DL_FILE_ENTRY_TYPE_KEY, nameMap, descriptionMap,
			new long[] {ddmStructureId}, serviceContext);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final Company _company;
	private final DDM _ddm;
	private final DDMFormXSDDeserializer _ddmFormXSDDeserializer;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final long _dlFileEntryMetadataClassNameId;
	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;
	private final UserLocalService _userLocalService;

}
