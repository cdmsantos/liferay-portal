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

package com.liferay.dynamic.data.lists.form.web.internal.util;

import com.liferay.dynamic.data.lists.form.web.internal.converter.DDLFormRuleDeserializer;
import com.liferay.dynamic.data.lists.form.web.internal.converter.DDLFormRuleToDDMFormRuleConverter;
import com.liferay.dynamic.data.lists.form.web.internal.converter.model.DDLFormRule;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(immediate = true, service = DDMFormTemplateContextToDDMForm.class)
public class DDMFormTemplateContextToDDMForm {

	public DDMForm deserialize(String serializedDDMFormTemplateContext)
		throws PortalException {

		DDMForm ddmForm = new DDMForm();

		JSONObject jsonObject = jsonFactory.createJSONObject(
			serializedDDMFormTemplateContext);

		JSONArray jsonArray = jsonObject.getJSONArray("availableLanguageIds");

		Set<Locale> availableLocales = new HashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			availableLocales.add(
				LocaleUtil.fromLanguageId(jsonArray.getString(i)));
		}

		ddmForm.setAvailableLocales(availableLocales);

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			jsonObject.getString("defaultLanguageId"));

		ddmForm.setDefaultLocale(defaultLocale);

		JSONArray rulesJSONArray = jsonObject.getJSONArray("rules");

		ddmForm.setDDMFormRules(getDDMFormRules(rulesJSONArray.toString()));

		JSONObject successPageJSONObject = jsonObject.getJSONObject(
			"successPageSettings");

		DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
			new DDMFormSuccessPageSettings();

		JSONObject bodyJSONObject = successPageJSONObject.getJSONObject("body");

		ddmFormSuccessPageSettings.setBody(
			bodyJSONObject.getString(LocaleUtil.toLanguageId(defaultLocale)));

		JSONObject titleJSONObject = successPageJSONObject.getJSONObject(
			"title");

		ddmFormSuccessPageSettings.setTitle(
			titleJSONObject.getString(LocaleUtil.toLanguageId(defaultLocale)));

		ddmFormSuccessPageSettings.setEnabled(
			successPageJSONObject.getBoolean("enabled"));

		ddmForm.setDDMFormSuccessPageSettings(ddmFormSuccessPageSettings);

		DDMFormTemplateJSONContextVisitor ddmFormTemplateContextVisitor =
			new DDMFormTemplateJSONContextVisitor(
				jsonObject.getJSONArray("pages"));

		ddmFormTemplateContextVisitor.onVisitField(
			new Consumer<JSONObject>() {

				@Override
				public void accept(JSONObject fieldJSONObject) {
					JSONObject jsonObject = fieldJSONObject.getJSONObject(
						"settingsContext");

					DDMFormTemplateJSONContextVisitor
						settingsTemplateContextVisitor =
							new DDMFormTemplateJSONContextVisitor(
								jsonObject.getJSONArray("pages"));

					DDMFormField ddmFormField = new DDMFormField();

					settingsTemplateContextVisitor.onVisitField(
						new Consumer<JSONObject>() {

							@Override
							public void accept(JSONObject fieldJSONObject) {
								try {
									boolean localizable =
										fieldJSONObject.getBoolean(
											"localizable");

									String valueProperty = "value";

									// || fieldJSONObject.getString(
									// "type").equals("options")

									if (localizable) {
										valueProperty = "localizedValue";
									}

									Object deserializedDDMFormFieldProperty =
										deserializeDDMFormFieldProperty(
											fieldJSONObject.getString(
												valueProperty),
											localizable,
											fieldJSONObject.getString(
												"dataType"),
											fieldJSONObject.getString("type"));

									ddmFormField.setProperty(
										fieldJSONObject.getString("fieldName"),
										deserializedDDMFormFieldProperty);
								}
								catch (PortalException pe) {
									pe.printStackTrace();
								}
							}

							protected DDMFormFieldOptions
									deserializeDDMFormFieldOptions(
										String serializedDDMFormFieldProperty)
								throws PortalException {

								if (Validator.isNull(
										serializedDDMFormFieldProperty)) {

									return new DDMFormFieldOptions();
								}

								JSONObject jsonObject =
									jsonFactory.createJSONObject(
										serializedDDMFormFieldProperty);

								return getDDMFormFieldOptions(jsonObject);
							}

							protected Object deserializeDDMFormFieldProperty(
									String serializedDDMFormFieldProperty,
									boolean localizable, String dataType,
									String type)
								throws PortalException {

								if (Objects.equals(dataType, "ddm-options")) {
									return deserializeDDMFormFieldOptions(
										serializedDDMFormFieldProperty);
								}
								else if (Objects.equals(dataType, "boolean")) {
									return Boolean.valueOf(
										serializedDDMFormFieldProperty);
								}
								else if (Objects.equals(type, "validation")) {
									return deserializeDDMFormFieldValidation(
										serializedDDMFormFieldProperty);
								}
								else if (Objects.equals(type, "select")) {
									return deserializeDDMFormFieldSelect(
										serializedDDMFormFieldProperty);
								}
								else if (localizable) {
									return deserializeLocalizedValue(
										serializedDDMFormFieldProperty);
								}
								else {
									return serializedDDMFormFieldProperty;
								}
							}

							protected String deserializeDDMFormFieldSelect(
									String serializedDDMFormFieldProperty)
								throws PortalException {

								JSONArray jsonArray =
									jsonFactory.createJSONArray(
										serializedDDMFormFieldProperty);

								if (jsonArray.length() == 0) {
									return "";
								}

								return jsonArray.getString(0);
							}

							protected DDMFormFieldValidation
									deserializeDDMFormFieldValidation(
										String serializedDDMFormFieldProperty)
								throws PortalException {

								DDMFormFieldValidation ddmFormFieldValidation =
									new DDMFormFieldValidation();

								if (Validator.isNull(
										serializedDDMFormFieldProperty)) {

									return ddmFormFieldValidation;
								}

								JSONObject jsonObject =
									jsonFactory.createJSONObject(
										serializedDDMFormFieldProperty);

								ddmFormFieldValidation.setExpression(
									jsonObject.getString("expression"));
								ddmFormFieldValidation.setErrorMessage(
									jsonObject.getString("errorMessage"));

								return ddmFormFieldValidation;
							}

							protected LocalizedValue deserializeLocalizedValue(
									String serializedDDMFormFieldProperty)
								throws PortalException {

								LocalizedValue localizedValue =
									new LocalizedValue(defaultLocale);

								JSONObject jsonObject =
									jsonFactory.createJSONObject(
										serializedDDMFormFieldProperty);

								for (Locale availableLocale :
										availableLocales) {

									String valueString = jsonObject.getString(
										LocaleUtil.toLanguageId(
											availableLocale),
										null);

									if (valueString == null) {
										valueString = jsonObject.getString(
											LocaleUtil.toLanguageId(
												defaultLocale));
									}

									localizedValue.addString(
										availableLocale, valueString);
								}

								return localizedValue;
							}

							protected DDMFormFieldOptions
								getDDMFormFieldOptions(JSONObject jsonObject) {

								DDMFormFieldOptions ddmFormFieldOptions =
									new DDMFormFieldOptions();

								for (Locale availableLocale :
										availableLocales) {

									JSONArray jsonArray =
										jsonObject.getJSONArray(
											LocaleUtil.toLanguageId(
												availableLocale));

									if (jsonArray == null) {
										jsonArray = jsonObject.getJSONArray(
											LocaleUtil.toLanguageId(
												defaultLocale));
									}

									int length = jsonArray.length();

									for (int i = 0; i < length; i++) {
										JSONObject jsonObject2 =
											jsonArray.getJSONObject(i);

										ddmFormFieldOptions.addOptionLabel(
											jsonObject2.getString("value"),
											availableLocale,
											jsonObject2.getString("label"));
									}
								}

								ddmFormFieldOptions.setDefaultLocale(
									defaultLocale);

								return ddmFormFieldOptions;
							}

						});

					settingsTemplateContextVisitor.visit();

					ddmForm.addDDMFormField(ddmFormField);
				}

			});

		ddmFormTemplateContextVisitor.visit();

		return ddmForm;
	}

	protected List<DDMFormRule> getDDMFormRules(String rules)
		throws PortalException {

		if (Validator.isNull(rules) || Objects.equals("[]", rules)) {
			return Collections.emptyList();
		}

		List<DDLFormRule> ddlFormRules = ddlFormRuleDeserializer.deserialize(
			rules);

		return ddlFormRulesToDDMFormRulesConverter.convert(ddlFormRules);
	}

	@Reference
	protected DDLFormRuleDeserializer ddlFormRuleDeserializer;

	@Reference
	protected DDLFormRuleToDDMFormRuleConverter
		ddlFormRulesToDDMFormRulesConverter;

	@Reference
	protected JSONFactory jsonFactory;

}