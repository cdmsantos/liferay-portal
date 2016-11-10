// This file was automatically generated from ratings.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace ddm.
 * @hassoydeltemplate {ddm.field}
 * @public
 */

if (typeof ddm == 'undefined') { var ddm = {}; }


ddm.__deltemplate_s2_767414e2 = function(opt_data, opt_ignored) {
  return '' + ddm.ratings(opt_data);
};
if (goog.DEBUG) {
  ddm.__deltemplate_s2_767414e2.soyTemplateName = 'ddm.__deltemplate_s2_767414e2';
}
soy.$$registerDelegateFn(soy.$$getDelTemplateId('ddm.field'), 'ratings', 0, ddm.__deltemplate_s2_767414e2);


ddm.ratings = function(opt_data, opt_ignored) {
  var output = '';
  var displayValue__soy5 = opt_data.value ? opt_data.value : opt_data.predefinedValue;
  output += '<div class="form-group' + soy.$$escapeHtmlAttribute(opt_data.visible ? '' : ' hide') + '" data-fieldname="' + soy.$$escapeHtmlAttribute(opt_data.name) + '">' + ((opt_data.showLabel) ? '<label class="control-label">' + soy.$$escapeHtml(opt_data.label) + ((opt_data.required) ? '<span class="icon-asterisk text-warning"></span>' : '') + '</label>' + ((opt_data.tip) ? '<p class="liferay-ddm-form-field-tip">' + soy.$$escapeHtml(opt_data.tip) + '</p>' : '') : '') + '<div class="clearfix ratings"><div class="ratings-options" ' + ((opt_data.readOnly) ? 'readonly' : '') + '>';
  var curValueLimit29 = opt_data.range;
  for (var curValue29 = 0; curValue29 < curValueLimit29; curValue29++) {
    var checked__soy30 = curValue29 == displayValue__soy5 ? 'checked' : '';
    output += '<input ' + soy.$$filterHtmlAttributes(checked__soy30) + ' id="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(curValue29) + '" name="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" type="radio" value="' + soy.$$escapeHtmlAttribute(curValue29) + '" />';
  }
  output += '</div></div>' + ((opt_data.childElementsHTML) ? soy.$$filterNoAutoescape(opt_data.childElementsHTML) : '') + '</div>';
  return output;
};
if (goog.DEBUG) {
  ddm.ratings.soyTemplateName = 'ddm.ratings';
}
