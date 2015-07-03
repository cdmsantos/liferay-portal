<%--
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
--%>

<%@ include file="/html/taglib/ddm/html/init.jsp" %>

<div class="lfr-ddm-container" id="<%= randomNamespace %>">
	<c:if test="<%= ddmForm != null %>">

		<%
		
		long ddmStructureId = classPK;

		if (classNameId == PortalUtil.getClassNameId(DDMTemplate.class)) {
			DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(classPK);

			ddmStructureId = ddmTemplate.getClassPK();
		}

		DDMStructure ddmStructure = DDMStructureServiceUtil.getStructure(ddmStructureId);
		
		Fields fields = null;

		if(ddmFormValues != null){
			fields = DDMFormValuesToFieldsConverterUtil.convert(ddmStructure, ddmFormValues);
		}
		
		pageContext.setAttribute("checkRequired", checkRequired);
		
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setFields(fields);
		ddmFormFieldRenderingContext.setHttpServletRequest(request);
		ddmFormFieldRenderingContext.setHttpServletResponse(response);
		ddmFormFieldRenderingContext.setLocale(requestedLocale);
		ddmFormFieldRenderingContext.setMode(mode);
		ddmFormFieldRenderingContext.setNamespace(fieldsNamespace);
		ddmFormFieldRenderingContext.setPortletNamespace(portletResponse.getNamespace());
		ddmFormFieldRenderingContext.setReadOnly(readOnly);
		ddmFormFieldRenderingContext.setShowEmptyFieldLabel(showEmptyFieldLabel);
		%>

		<%= DDMFormRendererUtil.render(ddmForm, ddmFormFieldRenderingContext) %>

		<aui:input name="<%= ddmFormValuesInputName %>" type="hidden" />

		<aui:script use="liferay-ddm-form">
			var liferayDDMForm = new Liferay.DDM.Form(
				{
					container: '#<%= randomNamespace %>',
					ddmFormValuesInput: '#<portlet:namespace /><%= ddmFormValuesInputName %>',
					definition: <%= DDMFormJSONSerializerUtil.serialize(ddmForm) %>,
					doAsGroupId: <%= scopeGroupId %>,
					fieldsNamespace: '<%= fieldsNamespace %>',
					mode: '<%= mode %>',
					p_l_id: <%= themeDisplay.getPlid() %>,
					portletNamespace: '<portlet:namespace />',
					repeatable: <%= repeatable %>
					
					<c:if test="<%= ddmFormValues != null %>">
						, values: <%= DDMFormValuesJSONSerializerUtil.serialize(ddmFormValues) %>
					</c:if>
				}
			);

			var onDestroyPortlet = function(event) {
				if (event.portletId === '<%= portletDisplay.getRootPortletId() %>') {
					liferayDDMForm.destroy();

					Liferay.detach('destroyPortlet', onDestroyPortlet);
				}
			};

			Liferay.on('destroyPortlet', onDestroyPortlet);
		</aui:script>
	</c:if>