// This file was automatically generated from multiple_checkbox.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace ddm.
 * @public
 */

if (typeof ddm == 'undefined') { var ddm = {}; }


ddm.multiple_checkbox = function(opt_data, opt_ignored) {
  var output = '<div class="form-group liferay-ddm-form-field-multiple-checkbox" data-fieldname="' + soy.$$escapeHtmlAttribute(opt_data.name) + '">' + ((opt_data.showLabel && opt_data.options.length > 1) ? '<label for="' + soy.$$escapeHtmlAttribute(opt_data.name) + '">' + soy.$$escapeHtml(opt_data.label) + ((opt_data.required) ? '<span class="icon-asterisk text-warning"></span>' : '') + '</label>' + ((opt_data.tip) ? '<p class="liferay-ddm-form-field-tip">' + soy.$$escapeHtml(opt_data.tip) + '</p>' : '') : '') + '<div class="clearfix multiple-checkbox-options">';
  var optionList89 = opt_data.options;
  var optionListLen89 = optionList89.length;
  for (var optionIndex89 = 0; optionIndex89 < optionListLen89; optionIndex89++) {
    var optionData89 = optionList89[optionIndex89];
    output += ((! opt_data.inline) ? '<div>' : '') + ((opt_data.showAsSwitcher) ? '<label class="checkbox-default' + soy.$$escapeHtmlAttribute(opt_data.inline ? ' checkbox-switcher-inline' : '') + ' checkbox-option-' + soy.$$escapeHtmlAttribute(optionData89.value) + '" for="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData89.value) + '"><input class="hide toggle-switch " ' + ((opt_data.readOnly) ? 'disabled' : '') + ' id="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData89.value) + '" name="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" ' + soy.$$filterHtmlAttributes(optionData89.status) + ' type="checkbox" value="' + soy.$$escapeHtmlAttribute(optionData89.value) + '" /><span aria-hidden="true" class="toggle-switch-bar"><span class="toggle-switch-handle"></span></span><span class="toggle-switch-text toggle-switch-text-right">' + soy.$$escapeHtml(optionData89.label) + ((opt_data.required && opt_data.options.length == 1) ? '<span class="icon-asterisk text-warning"></span>' : '') + '</span></label>' : '<label class="checkbox-default' + soy.$$escapeHtmlAttribute(opt_data.inline ? ' checkbox-inline' : '') + ' checkbox-option-' + soy.$$escapeHtmlAttribute(optionData89.value) + '" for="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData89.value) + '"><input class="field" dir="' + soy.$$escapeHtmlAttribute(opt_data.dir) + '" ' + ((opt_data.readOnly) ? 'disabled' : '') + ' id="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData89.value) + '" name="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" ' + soy.$$filterHtmlAttributes(optionData89.status) + ' type="checkbox" value="' + soy.$$escapeHtmlAttribute(optionData89.value) + '" /> ' + soy.$$escapeHtml(optionData89.label) + '</label>' + ((opt_data.required && opt_data.options.length == 1) ? '<span class="icon-asterisk text-warning"></span>' : '')) + ((! opt_data.inline) ? '</div>' : '');
  }
  output += '</div>' + ((opt_data.tip && opt_data.options.length == 1) ? '<p class="liferay-ddm-form-field-tip">' + soy.$$escapeHtml(opt_data.tip) + '</p>' : '') + ((opt_data.childElementsHTML) ? soy.$$filterNoAutoescape(opt_data.childElementsHTML) : '') + '</div>';
  return output;
};
if (goog.DEBUG) {
  ddm.multiple_checkbox.soyTemplateName = 'ddm.multiple_checkbox';
}
