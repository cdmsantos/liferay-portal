AUI.add(
	'liferay-ddl-form-builder-action-jump-to-page',
	function(A) {
		var FormBuilderActionJumpToPage = A.Component.create(
			{
				ATTRS: {
					action: {
						value: ''
					},

					index: {
						value: ''
					},

					options: {
						value: []
					},

					type: {
						value: 'jump-to-page'
					}
				},

				AUGMENTS: [],

				EXTENDS: Liferay.DDL.FormBuilderAction,

				NAME: 'liferay-ddl-form-builder-action-jump-to-page',

				prototype: {
					conditionChange: function(pages) {
						var instance = this;

						var startIndex = pages[pages.length - 1] + 1;

						var options = instance.get('options').slice(startIndex);

						instance._setSourcePage(String(Math.max(pages)));
						instance._setTargetOptions(options);
					},

					getValue: function() {
						var instance = this;

						return {
							source: instance._sourceField.getValue(),
							target: instance._targetField.getValue()
						};
					},

					render: function() {
						var instance = this;

						var index = instance.get('index');

						var fieldsListContainer = instance.get('boundingBox').one('.target-' + index);

						instance._createSourceField().render(fieldsListContainer);
						instance._createTargetField().render(fieldsListContainer);
					},

					_createSourceField: function() {
						var instance = this;

						var value;

						var action = instance.get('action');

						if (action && action.source) {
							if (action.source.value) {
								value = action.source.value;
							}
							else {
								var options = instance.get('options');

								value = options[action.source].value;
							}
						}

						var context = {
							fieldName: instance.get('index') + '-action',
							label: Liferay.Language.get('the'),
							options: instance.get('options'),
							showLabel: false,
							visible: false
						};

						instance._sourceField = new Liferay.DDM.Field.Select(
							{
								bubbleTargets: [instance],
								context: context,
								value: value
							}
						);

						instance._sourceField.get('container').addClass('lfr-ddm-form-field-container-inline');

						return instance._sourceField;
					},

					_createTargetField: function() {
						var instance = this;

						var value;

						var action = instance.get('action');

						if (action && action.target) {
							if (action.target.value) {
								value = action.target.value;
							}
							else {
								var options = instance.get('options');

								value = options[action.target].value;
							}
						}

						var context = {
							fieldName: instance.get('index') + '-action',
							label: Liferay.Language.get('the'),
							options: instance.get('options'),
							showLabel: false,
							visible: true
						};

						instance._targetField = new Liferay.DDM.Field.Select(
							{
								bubbleTargets: [instance],
								context: context,
								value: value
							}
						);

						instance._targetField.get('container').addClass('lfr-ddm-form-field-container-inline');

						return instance._targetField;
					},

					_setSourcePage: function(pageIndex) {
						var instance = this;

						instance._sourceField.setValue(String(pageIndex));
					},

					_setTargetOptions: function(pages) {
						var instance = this;

						instance._targetField.set('options', pages);
					}
				}
			}
		);

		Liferay.namespace('DDL').FormBuilderActionJumpToPage = FormBuilderActionJumpToPage;
	},
	'',
	{
		requires: ['liferay-ddl-form-builder-action']
	}
);