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

AUI.add(
	'liferay-ddm-form-field-multiple-checkbox',
	function(A) {
		var MultipleCheckboxField = A.Component.create(
			{
				ATTRS: {

					showAsSwitcher: {
						value: false
					},

					type: {
						value: 'multiple-checkbox'
					},

					inline: {
						value: true
					},

					options: {
						validator: Array.isArray,
						value: []
					}

				},

				EXTENDS: Liferay.DDM.Renderer.Field,

				NAME: 'liferay-ddm-form-field-multiple-checkbox',

				prototype: {
					getTemplateContext: function() {
						var instance = this;

						return A.merge(
							MultipleCheckboxField.superclass.getTemplateContext.apply(instance, arguments),
							{
								showAsSwitcher: instance.get('showAsSwitcher'),
								inline: instance.get('inline'),
								options: instance.getOptions()
							}
						);
					},

					getOptions: function() {
						var instance = this;

						var value = instance.getContextValue();

						return A.map(
							instance.get('options'),
							function(item) {
								return {
									label: item.label[instance.get('locale')],
									status: value.indexOf(item.value) > -1 ? 'checked' : '',
									value: item.value
								};
							}
						);
					},

					getValue: function() {
						var instance = this;

						var container = instance.get('container');

						var values = [];

						container.all(instance.getInputSelector()).each(
							function(optionNode) {
								var isChecked = !!optionNode.attr('checked');

								if (isChecked) {
									values.push(optionNode.val());
								}
							}
						);

						return values.join();
					},

					setValue: function(value) {
						var instance = this;

						var container = instance.get('container');

						var checkboxNodeList = container.all('input[type="checkbox"]');

						for (var i = 0; i < checkboxNodeList.length; i++) {
							if (value.includes(checkboxNodeList[i].val())) {
								var node = checkboxNodeList[i];
								node.attr('checked', true);

								instance.fire(
									'valueChanged',
									{
										field: instance,
										value: value
									}
								);
							}
						}
					},

					_renderErrorMessage: function() {
						var instance = this;

						var container = instance.get('container');

						MultipleCheckboxField.superclass._renderErrorMessage.apply(instance, arguments);

						container.all('.help-block').appendTo(container);
					},

					_showFeedback: function() {
						var instance = this;

						var container = instance.get('container');

						MultipleCheckboxField.superclass._showFeedback.apply(instance, arguments);

						container.all('.form-control-feedback').appendTo(container);
					}
				}
			}
		);

		Liferay.namespace('DDM.Field').MultipleCheckbox = MultipleCheckboxField;
	},
	'',
	{
		requires: ['liferay-ddm-form-renderer-field']
	}
);