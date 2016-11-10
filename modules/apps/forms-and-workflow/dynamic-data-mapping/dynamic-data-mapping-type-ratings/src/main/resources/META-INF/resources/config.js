;(function() {
	AUI().applyConfig(
		{
			groups: {
				'field-ratings': {
					base: MODULE_PATH + '/',
					combine: Liferay.AUI.getCombine(),
					modules: {
						'liferay-ddm-form-field-ratings': {
							condition: {
								trigger: 'liferay-ddm-form-renderer'
							},
							path: 'ratings_field.js',
							requires: [
								'liferay-ddm-form-renderer-field',
								'aui-rating'
							]
						},
						'liferay-ddm-form-field-ratings-template': {
							condition: {
								trigger: 'liferay-ddm-form-renderer'
							},
							path: 'ratings.soy.js',
							requires: [
								'soyutils'
							]
						}
					},
					root: MODULE_PATH + '/'
				}
			}
		}
	);
})();