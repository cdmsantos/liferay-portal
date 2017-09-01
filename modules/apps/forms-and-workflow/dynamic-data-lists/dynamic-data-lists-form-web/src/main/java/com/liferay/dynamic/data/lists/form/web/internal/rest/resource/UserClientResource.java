package com.liferay.dynamic.data.lists.form.web.internal.rest.resource;

import com.liferay.dynamic.data.lists.form.web.internal.rest.model.UserModel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(immediate = true, service = UserClientResource.class)
@Path("/clients")
public class UserClientResource {

	@GET
	@Produces("application/json")
	public List<UserModel> getUsers()
		throws PortalException {
		
		long userIds[] = _userLocalService.getRoleUserIds(34775);
		
		List<UserModel> list = new ArrayList<>();
		
		for(long userId : userIds) {
			User user = _userLocalService.getUserById(userId);
			
			UserModel userModel = new UserModel();
			userModel.setUserId(userId);
			userModel.setName(user.getFullName());
			
			list.add(userModel);
		}
		
		return list;
	}

	@Reference
	private UserLocalService _userLocalService;
	
}