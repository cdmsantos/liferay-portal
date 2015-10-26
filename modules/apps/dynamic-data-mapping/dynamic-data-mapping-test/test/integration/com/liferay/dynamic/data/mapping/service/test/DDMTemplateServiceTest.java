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

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.service.DDMTemplateServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.AdvancedPermissionChecker;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class DDMTemplateServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_childGroup = GroupTestUtil.addGroup(group.getGroupId());

		_otherGroup = GroupTestUtil.addGroup();

		_recordSetClassNameId = PortalUtil.getClassNameId(
			DDL_RECORD_SET_CLASS_NAME);

		_structureClassNameId = PortalUtil.getClassNameId(DDMStructure.class);

		_siteAdminUser = UserTestUtil.addGroupAdminUser(group);

		setUpPermissionThreadLocal();

		setUpPrincipalThreadLocal();
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testGetTemplates() throws Exception {
		addFormTemplate(
			0, StringUtil.randomString(), WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			0, StringUtil.randomString(), WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			0, StringUtil.randomString(), WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, _recordSetClassNameId,
			WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(3, ddmTemplates.size());
	}

	@Test
	public void testGetTemplatesByClassPK() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);

		addTemplate(
			0, ddmStructure.getStructureId(), StringUtil.randomString(), null,
			null, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			0, ddmStructure.getStructureId(), StringUtil.randomString(), null,
			null, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			0, ddmStructure.getStructureId(), StringUtil.randomString(), null,
			null, language, script, WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates =
			DDMTemplateServiceUtil.getTemplatesByClassPK(
				TestPropsValues.getCompanyId(), group.getGroupId(),
				ddmStructure.getStructureId(), _recordSetClassNameId,
				WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(3, ddmTemplates.size());
	}

	@Test
	public void testGetTemplatesByIncludeAncestorTemplates() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		addDisplayTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addDisplayTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), _childGroup.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, true, WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(2, ddmTemplates.size());

		ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), _childGroup.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, false, WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(0, ddmTemplates.size());
	}

	@Test
	public void testGetTemplatesByStructureAnyStatus() throws Exception {
		DDMStructure ddmStructure1 = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		DDMStructure ddmStructure2 = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		addDisplayTemplate(
			ddmStructure1.getPrimaryKey(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		addDisplayTemplate(
			ddmStructure1.getPrimaryKey(), StringUtil.randomString(),
			WorkflowConstants.STATUS_DRAFT);

		addDisplayTemplate(
			ddmStructure2.getPrimaryKey(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure1.getStructureId(),
			_recordSetClassNameId, WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(2, ddmTemplates.size());

		ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure2.getStructureId(),
			_recordSetClassNameId, WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(1, ddmTemplates.size());
	}

	@Test
	public void testGetTemplatesByStructureClassNameId() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		List<DDMTemplate> createdTemplates = new ArrayList<>(3);

		DDMTemplate ddmTemplate = addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		createdTemplates.add(ddmTemplate);

		ddmTemplate = addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		createdTemplates.add(ddmTemplate);

		ddmTemplate = addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		createdTemplates.add(ddmTemplate);

		List<DDMTemplate> foundDDMTemplates =
			DDMTemplateServiceUtil.getTemplatesByStructureClassNameId(
				group.getGroupId(), _recordSetClassNameId,
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(3, foundDDMTemplates.size());

		for (DDMTemplate curTemplate : createdTemplates) {
			Assert.assertTrue(foundDDMTemplates.contains(curTemplate));
		}
	}

	@Test
	public void testGetTemplatesByStructureClassNameIdCount() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		int count =
			DDMTemplateServiceUtil.getTemplatesByStructureClassNameIdCount(
				group.getGroupId(), _recordSetClassNameId,
				WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(3, count);
	}

	@Test
	public void testGetTemplatesByType() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		addDisplayTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addDisplayTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(2, ddmTemplates.size());

		ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, DDMTemplateConstants.TEMPLATE_TYPE_FORM,
			WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(1, ddmTemplates.size());
	}

	@Test
	public void testGetTemplatesByWorkflowStatus() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		addDisplayTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		addDisplayTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		addDisplayTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_DRAFT);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(2, ddmTemplates.size());

		ddmTemplates = DDMTemplateServiceUtil.getTemplates(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(1, ddmTemplates.size());
	}

	@Test
	public void testSearch() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.search(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, StringPool.BLANK,
			DDMTemplateConstants.TEMPLATE_TYPE_FORM, null,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(3, ddmTemplates.size());
	}

	@Test
	public void testSearch2() throws Exception {
		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		addFormTemplate(
			ddmStructure.getStructureId(), StringUtil.randomString(),
			WorkflowConstants.STATUS_ANY);

		long[] groupIds =
			new long[] {group.getGroupId(), _otherGroup.getGroupId()};

		long[] classNameIds = new long[] {_structureClassNameId};

		long[] classPKs = new long[] {ddmStructure.getStructureId()};

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.search(
			TestPropsValues.getCompanyId(), groupIds, classNameIds, classPKs,
			_recordSetClassNameId, StringPool.BLANK,
			DDMTemplateConstants.TEMPLATE_TYPE_FORM,
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(3, ddmTemplates.size());
	}

	@Test
	public void testSearchByNameAndDescription1() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;

		DDMTemplate ddmTemplate = addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.search(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, name, description, type, mode, language,
			WorkflowConstants.STATUS_ANY, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(1, ddmTemplates.size());

		DDMTemplate foundDDMTemplate = ddmTemplates.get(0);

		Assert.assertEquals(ddmTemplate, foundDDMTemplate);
	}

	@Test
	public void testSearchByNameAndDescription2() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;
		long[] groupIds =
			new long[] {group.getGroupId(), _otherGroup.getGroupId()};

		long[] classNameIds = new long[] {_structureClassNameId};

		long[] classPKs = new long[] {ddmStructure.getStructureId()};

		DDMTemplate ddmTemplate = addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.search(
			TestPropsValues.getCompanyId(), groupIds, classNameIds, classPKs,
			_recordSetClassNameId, name, description, type, mode, language,
			WorkflowConstants.STATUS_ANY, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(1, ddmTemplates.size());

		DDMTemplate foundDDMTemplate = ddmTemplates.get(0);

		Assert.assertEquals(ddmTemplate, foundDDMTemplate);
	}

	@Test
	public void testSearchByNameORDescription1() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.search(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, name, description, type, mode, language,
			WorkflowConstants.STATUS_ANY, false, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(3, ddmTemplates.size());
	}

	@Test
	public void testSearchByNameORDescription2() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;
		long[] groupIds =
			new long[] {group.getGroupId(), _otherGroup.getGroupId()};

		long[] classNameIds = new long[] {_structureClassNameId};

		long[] classPKs = new long[] {ddmStructure.getStructureId()};

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		List<DDMTemplate> ddmTemplates = DDMTemplateServiceUtil.search(
			TestPropsValues.getCompanyId(), groupIds, classNameIds, classPKs,
			_recordSetClassNameId, name, description, type, mode, language,
			WorkflowConstants.STATUS_ANY, false, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(3, ddmTemplates.size());
	}

	@Test
	public void testSearchCountByNameAndDescription1() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		int count = DDMTemplateServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, name, description, type, mode, language,
			WorkflowConstants.STATUS_ANY, true);

		Assert.assertEquals(1, count);
	}

	@Test
	public void testSearchCountByNameAndDescription2() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;
		long[] groupIds =
			new long[] {group.getGroupId(), _otherGroup.getGroupId()};

		long[] classNameIds = new long[] {_structureClassNameId};

		long[] classPKs = new long[] {ddmStructure.getStructureId()};

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		int count = DDMTemplateServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), groupIds, classNameIds, classPKs,
			_recordSetClassNameId, name, description, type, mode, language,
			WorkflowConstants.STATUS_ANY, true);

		Assert.assertEquals(1, count);
	}

	@Test
	public void testSearchCountByNameORDescription1() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		int count = DDMTemplateServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(),
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, name, description, type, mode, language,
			WorkflowConstants.STATUS_ANY, false);

		Assert.assertEquals(3, count);
	}

	@Test
	public void testSearchCountByNameORDescription2() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		DDMStructure ddmStructure = addStructure(
			_recordSetClassNameId, StringUtil.randomString());

		String language = TemplateConstants.LANG_TYPE_FTL;
		String script = getTestTemplateScript(language);
		String type = null;
		String mode = null;
		long[] groupIds =
			new long[] {group.getGroupId(), _otherGroup.getGroupId()};

		long[] classNameIds = new long[] {_structureClassNameId};

		long[] classPKs = new long[] {ddmStructure.getStructureId()};

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, description, type, mode,
			language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, name, StringUtil.randomString(), type,
			mode, language, script, WorkflowConstants.STATUS_ANY);

		addTemplate(
			_structureClassNameId, ddmStructure.getStructureId(),
			_recordSetClassNameId, null, StringUtil.randomString(), description,
			type, mode, language, script, WorkflowConstants.STATUS_ANY);

		int count = DDMTemplateServiceUtil.searchCount(
					TestPropsValues.getCompanyId(), groupIds, classNameIds,
					classPKs, _recordSetClassNameId, name, description, type,
					mode, language, WorkflowConstants.STATUS_ANY, false);

		Assert.assertEquals(3, count);
	}

	protected void setUpPermissionThreadLocal() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			new AdvancedPermissionChecker() {
				{
					init(_siteAdminUser);
				}
			});
	}

	protected void setUpPrincipalThreadLocal() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_siteAdminUser.getUserId());
	}

	private static long _recordSetClassNameId;
	private static long _structureClassNameId;

	@DeleteAfterTestRun
	private Group _childGroup;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@DeleteAfterTestRun
	private Group _otherGroup;

	@DeleteAfterTestRun
	private User _siteAdminUser;

}