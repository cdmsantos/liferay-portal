package com.liferay.dynamic.data.lists.form.web.internal.rest.resource;

import com.liferay.dynamic.data.lists.form.web.internal.rest.model.UserModel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
	@Path("/list")
	public List<UserModel> getUsers()
		throws PortalException {
		
		long userIds[] = _userLocalService.getRoleUserIds(34775);
		
		List<UserModel> list = new ArrayList<>();
		
		for(long userId : userIds) {
			list.add(getUser(userId));
		}
		
		return list;
	}

	@GET
	@Produces("application/json")
	@Path("/getUser")
	public UserModel getUser(@QueryParam("userId") long userId)
			throws PortalException {
			
		User user = _userLocalService.getUser(userId);
		
		Format simpleDateFormat =
			FastDateFormatFactoryUtil.getSimpleDateFormat("dd/MM/yyyy");
		
		UserModel userModel = new UserModel();
		userModel.setUserId(userId);
		userModel.setName(user.getFullName());
		userModel.setCpf(user.getScreenName());
		userModel.setBirthday(
			simpleDateFormat.format(user.getBirthday()));
		
		return userModel;
	}

	@Reference
	private UserLocalService _userLocalService;
	
}