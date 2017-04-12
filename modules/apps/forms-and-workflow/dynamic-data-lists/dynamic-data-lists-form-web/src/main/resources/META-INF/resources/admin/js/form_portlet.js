AUI.add(
	'liferay-ddl-portlet',
	function(A) {
		var LayoutSerializer = Liferay.DDL.LayoutSerializer;
		
		var Settings = Liferay.DDL.Settings;
		
		var FormBuilderUtil = Liferay.DDL.FormBuilderUtil;

		var EMPTY_FN = A.Lang.emptyFn;

		var MINUTE = 60000;

		var TPL_BUTTON_SPINNER = '<span aria-hidden="true"><span class="icon-spinner icon-spin"></span></span>';

		var STR_TRANSLATION_MANAGER = 'translationManager';

		var DDLPortlet = A.Component.create(
			{
				ATTRS: {
					availableLanguageIds: {
						value: [
							themeDisplay.getDefaultLanguageId()
						]
					},

					defaultLanguageId: {
						value: themeDisplay.getDefaultLanguageId()
					},

					description: {
						value: {}
					},
					
					editForm: {
					},
					
					editingLocale:{
						value: themeDisplay.getDefaultLanguageId()
					},

					formBuilder: {
						valueFn: '_valueFormBuilder'
					},

					functionsMetadata: {
						value: []
					},

					getFunctionsURL: {
						value: ''
					},

					getRolesURL: {
						value: ''
					},

					context: {
					},

					name: {
						value: {}
					},
					
					recordSetId: {
						value: 0
					},

					ruleBuilder: {
						valueFn: '_valueRuleBuilder'
					translationManager: {
					},

					}
				},

				AUGMENTS: [Liferay.PortletBase],

				EXTENDS: A.Base,

				NAME: 'liferay-ddl-portlet',

				prototype: {
					initializer: function() {
						var instance = this;
						
						var defaultLanguageId = instance.get('defaultLanguageId');

						instance.layoutSerializer = new LayoutSerializer(
							{
								builder: instance.get('formBuilder'),
								defaultLanguageId: defaultLanguageId
							}
						);
						
						instance.renderUI();

						instance.bindUI();

						instance.savedState = instance.initialState = instance.getState();
						
						var name = {};
						
						name[defaultLanguageId] = Liferay.Language.get('untitled-form');
						
						instance.set('name', name);
						
						var description = {};
						
						description[defaultLanguageId] = '';
						
						instance.set('description', description);
					},

					renderUI: function() {
						var instance = this;

						instance.one('#loader').remove();

						instance.one('.portlet-forms').removeClass('hide');

						instance.get('formBuilder').render(instance.one('#formBuilder'));

						instance.get('ruleBuilder').render(instance.one('#ruleBuilder'));

						instance.createEditor(instance.ns('descriptionEditor'));
						instance.createEditor(instance.ns('nameEditor'));
					},

					bindUI: function() {
						var instance = this;

						var formBuilder = instance.get('formBuilder');
						
						var translationManager = instance.get(STR_TRANSLATION_MANAGER);

						instance._eventHandlers = [
							instance.after('autosave', instance._afterAutosave),
							formBuilder._layoutBuilder.after('layout-builder:moveEnd', A.bind(instance._afterFormBuilderLayoutBuilderMoveEnd, instance)),
							formBuilder._layoutBuilder.after('layout-builder:moveStart', A.bind(instance._afterFormBuilderLayoutBuilderMoveStart, instance)),
							instance.one('.back-url-link').on('click', A.bind('_onBack', instance)),
							instance.one('#preview').on('click', A.bind('_onPreviewButtonClick', instance)),
							instance.one('#publish').on('click', A.bind('_onPublishButtonClick', instance)),
							instance.one('#save').on('click', A.bind('_onSaveButtonClick', instance)),
							instance.one('#showRules').on('click', A.bind('_onRulesButtonClick', instance)),
							instance.one('#showForm').on('click', A.bind('_onFormButtonClick', instance)),
							instance.one('#requireAuthenticationCheckbox').on('change', A.bind('_onRequireAuthenticationCheckboxChanged', instance)),
							Liferay.on('destroyPortlet', A.bind('_onDestroyPortlet', instance)),
							translationManager.on('editingLocaleChange', instance._afterEditingLocaleChange.bind(instance))
						];

						var autosaveInterval = Settings.autosaveInterval;

						if (autosaveInterval > 0) {
							instance._intervalId = setInterval(A.bind('_autosave', instance), autosaveInterval * MINUTE);
						}
					},

					destructor: function() {
						var instance = this;

						clearInterval(instance._intervalId);

						instance.get('formBuilder').destroy();
						instance.get('ruleBuilder').destroy();

						(new A.EventHandle(instance._eventHandlers)).detach();
					},

					createEditor: function(editorName) {
						var instance = this;

						var editor = window[editorName];

						if (editor) {
							editor.create();
						}
						else {
							Liferay.once(
								'editorAPIReady',
								function(event) {
									if (event.editorName === editorName) {
										event.editor.create();
									}
								}
							);
						}
					},

					disableDescriptionEditor: function() {
						var instance = this;

						var descriptionEditor = CKEDITOR.instances[instance.ns('descriptionEditor')];

						descriptionEditor.setReadOnly(true);
					},

					disableNameEditor: function() {
						var instance = this;

						var nameEditor = CKEDITOR.instances[instance.ns('nameEditor')];

						nameEditor.setReadOnly(true);
					},

					enableDescriptionEditor: function() {
						var instance = this;

						var descriptionEditor = CKEDITOR.instances[instance.ns('descriptionEditor')];

						descriptionEditor.setReadOnly(false);
					},

					enableNameEditor: function() {
						var instance = this;

						var nameEditor = CKEDITOR.instances[instance.ns('nameEditor')];

						nameEditor.setReadOnly(false);
					},

					getState: function() {
						var instance = this;

						var formBuilder = instance.get('formBuilder');

						var ruleBuilder = instance.get('ruleBuilder');

						var pages = formBuilder.get('layouts');

						var rules = JSON.stringify(ruleBuilder.get('rules'));

						instance.layoutSerializer.set('pages', pages);

						var layout = JSON.parse(instance.layoutSerializer.serialize());
						
						var translationManager = instance.get('translationManager');
						
						return {
							availableLanguageIds: translationManager.get('availableLocales'),
							defaultLanguageId: translationManager.get('defaultLocale'),
							description: instance.get('description'),
							pages: layout.pages,
							name: instance.get('name'),
							paginationMode: formBuilder.get('pageManager').get('mode'),
							rules: rules
						};
					},

					isEmpty: function() {
						var instance = this;
						
						var formBuilder = instance.get('formBuilder');
						
						var count = 0;

						formBuilder.eachFields(function(field) {
							count++;
						});

						return count === 0;
					},

					openConfirmationModal: function(confirm, cancel) {
						var instance = this;

						var dialog = Liferay.Util.Window.getWindow(
							{
								dialog: {
									bodyContent: Liferay.Language.get('any-unsaved-changes-will-be-lost-are-you-sure-you-want-to-leave'),
									destroyOnHide: true,
									height: 200,
									resizable: false,
									toolbars: {
										footer: [
											{
												cssClass: 'btn-lg btn-primary',
												label: Liferay.Language.get('leave'),
												on: {
													click: function() {
														confirm.call(instance, dialog);
													}
												}
											},
											{
												cssClass: 'btn-lg btn-link',
												label: Liferay.Language.get('stay'),
												on: {
													click: function() {
														cancel.call(instance, dialog);
													}
												}
											}
										]
									},
									width: 600
								},
								title: Liferay.Language.get('leave-form')
							}
						);

						return dialog;
					},

					openPublishModal: function() {
						var instance = this;

						var publishCheckbox = instance.one('#publishCheckbox');

						publishCheckbox.setData('previousValue', publishCheckbox.attr('checked'));

						var requireAuthenticationCheckbox = instance.one('#requireAuthenticationCheckbox');

						requireAuthenticationCheckbox.setData('previousValue', requireAuthenticationCheckbox.attr('checked'));

						Liferay.Util.openWindow(
							{
								dialog: {
									cssClass: 'publish-modal-container',
									height: 430,
									resizable: false,
									'toolbars.footer': [
										{
											cssClass: 'btn-lg btn-primary',
											label: Liferay.Language.get('confirm'),
											on: {
												click: A.bind('_onConfirmPublishModal', instance)
											}
										},
										{
											cssClass: 'btn-lg btn-link',
											label: Liferay.Language.get('cancel'),
											on: {
												click: A.bind('_onCancelPublishModal', instance)
											}
										}
									],
									width: 720
								},
								id: instance.ns('publishModalContainer'),
								title: Liferay.Language.get('publish-form')
							},
							function(dialogWindow) {
								var publishNode = instance.byId(instance.ns('publishModal'));

								if (publishNode) {
									publishNode.show();

									dialogWindow.bodyNode.append(publishNode);
								}
							}
						);
					},

					publishForm: function() {

					},

					serializeFormBuilder: function() {
						var instance = this;

						var state = instance.getState();

						instance.one('#description').val(JSON.stringify(state.description));
						instance.one('#serializedContext').val(JSON.stringify(state));


						instance.one('#name').val(JSON.stringify(state.name));

						var publishCheckbox = instance.one('#publishCheckbox');

						var settingsDDMForm = Liferay.component('settingsDDMForm');

						var publishedField = settingsDDMForm.getField('published');

						publishedField.setValue(publishCheckbox.attr('checked'));

						var requireAuthenticationCheckbox = instance.one('#requireAuthenticationCheckbox');

						var requireAuthenticationField = settingsDDMForm.getField('requireAuthentication');

						requireAuthenticationField.setValue(requireAuthenticationCheckbox.attr('checked'));

						var settings = settingsDDMForm.toJSON();

						var settingsInput = instance.one('#serializedSettingsDDMFormValues');

						settingsInput.val(JSON.stringify(settings));
					},

					submitForm: function() {
						var instance = this;
						
						instance.serializeFormBuilder();

						var editForm = instance.get('editForm');

						submitForm(editForm.form);
					},

					_afterEditingLocaleChange: function(event) {
						var instance = this;
						
						var oldLocale = instance.get('editingLocale');
						
						var name = instance.get('name');
						var description = instance.get('description');
						
						name[oldLocale] = instance._getName(); 
						description[oldLocale] = instance._getDescription();

						var editingLocale = event.newVal;
						var defaultLanguageId = instance.get('defaultLanguageId');

						var formBuilder = instance.get('formBuilder');

						formBuilder.eachFields(function(field) {
							field.set('locale', editingLocale);
							
							field.saveSettings();
						});
						
						var pageManager = formBuilder.get('pageManager');
						
						pageManager.fire('localeChange', {
							editingLocale: editingLocale
						});
						
						instance.set('editingLocale', editingLocale);
						
						instance._setName(name[editingLocale] || name[defaultLanguageId] || '');
						instance._setDescription(description[editingLocale] || description[defaultLanguageId] || '');
					},

					_afterAutosave: function(event) {
						var instance = this;

						var autosaveMessage = A.Lang.sub(
							Liferay.Language.get('draft-saved-on-x'),
							[
								event.modifiedDate
							]
						);

						instance.one('#autosaveMessage').set('innerHTML', autosaveMessage);
					},

					_afterFormBuilderLayoutBuilderMoveEnd: function() {
						var instance = this;

						instance.enableDescriptionEditor();
						instance.enableNameEditor();
					},

					_afterFormBuilderLayoutBuilderMoveStart: function() {
						var instance = this;

						instance.disableDescriptionEditor();
						instance.disableNameEditor();
					},

					_autosave: function(callback) {
						var instance = this;

						callback = callback || EMPTY_FN;

						instance.serializeFormBuilder();

						var state = instance.getState();

						if (!instance.isEmpty()) {
							if (!instance._isSameState(instance.savedState, state)) {
								var editForm = instance.get('editForm');

								var formData = instance._getFormData(A.IO.stringify(editForm.form));

								A.io.request(
									Settings.autosaveURL,
									{
										after: {
											success: function() {
												var responseData = this.get('responseData');

												instance._defineIds(responseData);

												instance.savedState = state;

												instance.fire(
													'autosave',
													{
														modifiedDate: responseData.modifiedDate
													}
												);

												callback.call();
											}
										},
										data: formData,
										dataType: 'JSON',
										method: 'POST'
									}
								);
							}
							else {
								callback.call();
							}
						}
					},

					_createFormURL: function() {
						var instance = this;

						var formURL;

						var requireAuthenticationCheckbox = instance.one('#requireAuthenticationCheckbox');

						if (requireAuthenticationCheckbox.attr('checked')) {
							formURL = Settings.restrictedFormURL;
						}
						else {
							formURL = Settings.sharedFormURL;
						}

						var recordSetId = instance.byId('recordSetId').val();

						return formURL + recordSetId;
					},

					_createPreviewURL: function() {
						var instance = this;

						var formURL = instance._createFormURL();

						return formURL + '/preview';
					},

					_defineIds: function(response) {
						var instance = this;

						var recordSetIdNode = instance.byId('recordSetId');

						var ddmStructureIdNode = instance.byId('ddmStructureId');

						if (recordSetIdNode.val() === '0') {
							recordSetIdNode.val(response.recordSetId);
						}

						if (ddmStructureIdNode.val() === '0') {
							ddmStructureIdNode.val(response.ddmStructureId);
						}
					},
					
					_getDescription: function() {
						var instance = this;

						var editor = instance._getDescriptionEditor();
						
						return editor.getHTML();
					},

					_getDescriptionEditor: function() {
						var instance = this;

						return window[instance.ns('descriptionEditor')];
					},

					_getFormData: function(formString) {
						var instance = this;

						var formObject = A.QueryString.parse(formString);

						formObject[instance.ns('name')] = instance.get('name');

						formString = A.QueryString.stringify(formObject);

						return formString;
					},
					
					_getName: function() {
						var instance = this;

						var editor = instance._getNameEditor();
						
						return editor.getHTML();
					},

					_getNameEditor: function() {
						var instance = this;

						return window[instance.ns('nameEditor')];
					},

					_isSameState: function(state1, state2) {
						var instance = this;

						return AUI._.isEqual(
							state1,
							state2,
							function(value1, value2, key) {
								return (key === 'instanceId') || undefined;
							}
						);
					},

					_onBack: function(event) {
						var instance = this;

						if (!instance._isSameState(instance.getState(), instance.initialState)) {
							event.preventDefault();
							event.stopPropagation();

							instance.openConfirmationModal(
								function(dialog) {
									window.location.href = event.currentTarget.get('href');

									dialog.hide();
								},
								function(dialog) {
									dialog.hide();
								}
							);
						}
					},

					_onCancelPublishModal: function() {
						var instance = this;

						var publishCheckbox = instance.one('#publishCheckbox');

						publishCheckbox.attr('checked', publishCheckbox.getData('previousValue'));

						var requireAuthenticationCheckbox = instance.one('#requireAuthenticationCheckbox');

						requireAuthenticationCheckbox.attr('checked', requireAuthenticationCheckbox.getData('previousValue'));

						Liferay.Util.getWindow(instance.ns('publishModalContainer')).hide();
					},

					_onConfirmPublishModal: function() {
						var instance = this;

						instance._setFormAsPublished();

						Liferay.Util.getWindow(instance.ns('publishModalContainer')).hide();
					},

					_onDestroyPortlet: function(event) {
						var instance = this;

						instance.destroy();
					},

					_onFormButtonClick: function() {
						var instance = this;

						instance.one('#formBuilder').show();

						instance.get('ruleBuilder').hide();

						A.one('.ddl-form-builder-buttons').removeClass('hide');
						A.one('.portlet-forms').removeClass('liferay-ddl-form-rule-builder');

						instance.one('#showRules').removeClass('active');
						instance.one('#showForm').addClass('active');
					},

					_onPreviewButtonClick: function() {
						var instance = this;

						instance._autosave(
							function() {
								var previewURL = instance._createPreviewURL();

								window.open(previewURL, '_blank');
							}
						);
					},

					_onPublishButtonClick: function(event) {
						var instance = this;

						event.preventDefault();

						var publishButton = instance.one('#publish');

						publishButton.html(Liferay.Language.get('saving'));

						publishButton.append(TPL_BUTTON_SPINNER);

						var saveAndPublish = instance.one('input[name*="saveAndPublish"]');

						saveAndPublish.set('value', 'true');

						instance.submitForm();
					},

					_onRequireAuthenticationCheckboxChanged: function() {
						var instance = this;

						var clipboardInput = instance.one('#clipboard');

						clipboardInput.set('value', instance._createFormURL());
					},

					_onRulesButtonClick: function() {
						var instance = this;

						instance.one('#formBuilder').hide();

						instance.get('ruleBuilder').show();

						A.one('.ddl-form-builder-buttons').addClass('hide');
						A.one('.portlet-forms').addClass('liferay-ddl-form-rule-builder');

						instance.one('#showRules').addClass('active');
						instance.one('#showForm').removeClass('active');
					},

					_onSaveButtonClick: function(event) {
						var instance = this;

						event.preventDefault();

						var saveButton = instance.one('#save');

						saveButton.html(Liferay.Language.get('saving'));

						saveButton.append(TPL_BUTTON_SPINNER);

						var saveAndPublish = instance.one('input[name*="saveAndPublish"]');

						saveAndPublish.set('value', 'false');

						instance.submitForm();
					},

					_setFormAsPublished: function() {
						var instance = this;

						var publishCheckbox = instance.one('#publishCheckbox');

						var payload = instance.ns(
							{
								published: publishCheckbox.attr('checked'),
								recordSetId: instance.get('recordSetId')
							}
						);

						A.io.request(
							Settings.publishRecordSetURL,
							{
								data: payload,
								dataType: 'JSON',
								method: 'POST'
							}
						);
					},
					
					_setDescription: function(value) {
						var instance = this;

						var editor = instance._getDescriptionEditor();
						
						editor.setHTML(value);
					},

					_setName: function(value) {
						var instance = this;
						
						var editor = instance._getNameEditor();

						editor.setHTML(value);
					},

					_valueFormBuilder: function() {
						var instance = this;

						return new Liferay.DDL.FormBuilder(
							{
								context: instance.get('context')
							}
						);
					},

					_valueRuleBuilder: function() {
						var instance = this;

						return new Liferay.DDL.FormBuilderRuleBuilder(
							{
								formBuilder: instance.get('formBuilder'),
								functionsMetadata: Settings.functionsMetadata,
								getDataProviderInstancesURL: Settings.getDataProviderInstancesURL,
								getDataProviderParametersSettingsURL: Settings.getDataProviderParametersSettingsURL,
								rules: Settings.rules,
								visible: false
							}
						);
					}
				}
			}
		);

		Liferay.namespace('DDL').Portlet = DDLPortlet;
	},
	'',
	{
		requires: ['io-base', 'liferay-ddl-form-builder', 'liferay-ddl-form-builder-util', 'liferay-ddl-form-builder-layout-serializer', 'liferay-ddl-form-builder-rule-builder', 'liferay-portlet-base', 'liferay-util-window', 'querystring-parse']
	}
);
