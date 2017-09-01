package com.liferay.dynamic.data.lists.form.web.internal.rest.application;


import com.liferay.dynamic.data.lists.form.web.internal.rest.context.provider.UserContextProvider;
import com.liferay.dynamic.data.lists.form.web.internal.rest.resource.UserClientResource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@ApplicationPath("/finantial-data")
@Component(immediate = true, service = Application.class)
public class FormsJaxRsApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();

		singletons.add(_userContextProvider);
		singletons.add(_userClientResource);

		return singletons;
	}


	@Reference
	private UserContextProvider _userContextProvider;

	@Reference
	private UserClientResource _userClientResource;

}