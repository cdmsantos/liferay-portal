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

package com.liferay.dynamic.data.lists.form.web.internal.listener;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageEngine;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.push.notifications.service.PushNotificationsDeviceLocalService;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(immediate = true, service = ModelListener.class)
public class DDLFormEntryModelListener extends BaseModelListener<DDLRecord>  {

	@Override
	public void onBeforeUpdate(DDLRecord ddlRecord) throws ModelListenerException {
		super.onBeforeCreate(ddlRecord);
		
		try {
			DDLRecordVersion recordVersion =
				_ddlRecordVersionLocalService.fetchLatestRecordVersion(
					ddlRecord.getUserId(), ddlRecord.getRecordSetId(), 
					ddlRecord.getRecordSet().getVersion(),
					WorkflowConstants.STATUS_APPROVED);
			
			if(recordVersion == null) {
				return;
			}
			
			DDMFormValues ddmFormValues = getDDMFormValues(
					ddlRecord.getDDMStorageId());
			
			Map<String,List<DDMFormFieldValue>> map =
					ddmFormValues.getDDMFormFieldValuesMap();

			DDMFormFieldValue ddmFormFieldValue = map.get("Users").get(0);
			
			String json = ddmFormFieldValue.getValue().getString(
					ddmFormValues.getDefaultLocale());

			JSONArray jsonArray = _jsonFactory.createJSONArray(json);
			
			long clientUserId = Long.parseLong(jsonArray.getString(0));

			User clientUser = _userLocalService.getUser(clientUserId);

			if (isCallCenterUser(ddlRecord.getUserId())) {
				
				ddlRecord.setUserId(clientUserId);
				
				
				ddlRecord.setUserName(clientUser.getFullName());
				
				recordVersion.setUserId(clientUserId);
				recordVersion.setStatusByUserId(clientUserId);
				recordVersion.setStatusByUserName(clientUser.getFullName());
				recordVersion.setStatus(WorkflowConstants.STATUS_DRAFT);
				
				_ddlRecordVersionLocalService.updateDDLRecordVersion(recordVersion);
				
				JSONObject payloadJSONObject = _jsonFactory.createJSONObject();
				
				payloadJSONObject.put("message", "test push notification");
				payloadJSONObject.put(
					"formUrl",
					"http://192.168.109.48:8080/group/forms/shared/-/form/34874");
				
				_pushNotificationsDeviceLocalService.sendPushNotification(
					new long[]{clientUserId}, payloadJSONObject);
				
				try {
					
					InternetAddress fromAddress =
						new InternetAddress("no-reply@rayssoftware.com", "");
					
					InternetAddress toAddress =
						new InternetAddress(clientUser.getEmailAddress(), clientUser.getFullName());
					
					MailMessage mailMessage = new MailMessage(
						fromAddress, toAddress, "Solicitacao de Credito",
						"visit: http://192.168.109.48:8080/group/forms/shared/-/form/34874",
						true);

					_mailService.sendEmail(mailMessage);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			}
			else {
				
				DDMFormFieldValue rendMensalFormFieldValue =
					map.get("RendaMensal").get(0);
				
				Double rendaMensal = GetterUtil.getDouble(
					rendMensalFormFieldValue.getValue().getString(
						ddmFormValues.getDefaultLocale()));
				
				
				if (rendaMensal >= 5000) {
					
					JSONObject payloadJSONObject = _jsonFactory.createJSONObject();
					
					payloadJSONObject.put("creditLimit", 7500);
					payloadJSONObject.put("cardType", "Platinum");
					payloadJSONObject.put("message", "Parabens");
					
					_pushNotificationsDeviceLocalService.sendPushNotification(
						new long[]{clientUserId}, payloadJSONObject);
					
					try {
						
						InternetAddress fromAddress =
							new InternetAddress("no-reply@rayssoftware.com", "");
						
						InternetAddress toAddress =
							new InternetAddress(clientUser.getEmailAddress(), clientUser.getFullName());
						
						MailMessage mailMessage = new MailMessage(
							fromAddress, toAddress, "Credito Aprovado",
							"visit: http://192.168.109.48:8080/group/forms/shared/-/form/34874",
							true);

						_mailService.sendEmail(mailMessage);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					
					JSONObject payloadJSONObject = _jsonFactory.createJSONObject();
					
					payloadJSONObject.put("message", "test push notification");
					payloadJSONObject.put(
						"formUrl",
						"http://192.168.109.48:8080/group/forms/shared/-/form/34874");
					
					_pushNotificationsDeviceLocalService.sendPushNotification(
						new long[]{clientUserId}, payloadJSONObject);
					
					try {
						
						InternetAddress fromAddress =
							new InternetAddress("no-reply@rayssoftware.com", "");
						
						InternetAddress toAddress =
							new InternetAddress(clientUser.getEmailAddress(), clientUser.getFullName());
						
						MailMessage mailMessage = new MailMessage(
							fromAddress, toAddress, "Credito Nao Aprovado",
							"Nao foi dessa vez",
							true);

						_mailService.sendEmail(mailMessage);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			
		} catch (PortalException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean isCallCenterUser(long userId) {
		try {
			long callCenterRoleId = 34725;
			
			Role callCenterRole = _roleLocalService.getRole(callCenterRoleId);
			
			User user = _userLocalService.getUser(userId);
			
			return user.getRoles().contains(callCenterRole);
			
		} catch (PortalException e) {
			
		}
		
		return false;
	}
	
	public DDMFormValues getDDMFormValues(long ddmStorageId)
		throws StorageException {

		return _storageEngine.getDDMFormValues(ddmStorageId);
	}
	
	@Reference
	private MailService _mailService;

	@Reference
	private DDLRecordVersionLocalService _ddlRecordVersionLocalService;
	
	@Reference
	private StorageEngine _storageEngine;
	
	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;
	
	@Reference
	private PushNotificationsDeviceLocalService _pushNotificationsDeviceLocalService;
	
	@Reference
	private JSONFactory _jsonFactory;
}
