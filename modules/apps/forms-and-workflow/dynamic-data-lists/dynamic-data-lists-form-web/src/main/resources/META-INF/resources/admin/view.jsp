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

<%@ include file="/admin/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "forms");
%>

<liferay-util:include page="/admin/search_bar.jsp" servletContext="<%= application %>" />

<liferay-util:include page="/admin/toolbar.jsp" servletContext="<%= application %>" />

<c:choose>
	<c:when test='<%= tabs1.equals("forms") %>'>
		<liferay-util:include page="/admin/view_forms.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= tabs1.equals("field-library") %>'>
		<liferay-util:include page="/admin/view_field_library.jsp" servletContext="<%= application %>" />
	</c:when>
</c:choose>