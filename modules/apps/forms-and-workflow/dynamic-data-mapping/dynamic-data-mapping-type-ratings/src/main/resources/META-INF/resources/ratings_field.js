AUI.add(
	'liferay-ddm-form-field-ratings',
	function(A) {
		var RatingsField = A.Component.create(
			{
				ATTRS: {
					range: {
						state: true,
						value : 5
					},

					type: {
						value: 'ratings'
					}
				},

				EXTENDS: Liferay.DDM.Renderer.Field,

				NAME: 'liferay-ddm-form-field-ratings',

				prototype: {

					getTemplateContext: function() {
						var instance = this;

						return A.merge(
							RatingsField.superclass.getTemplateContext.apply(instance, arguments),
							{
								range: instance.get('range')
							}
						);
					},

					render: function() {
						var instance = this;

						var container = instance.get('container');

						var context = instance.get("context");

						RatingsField.superclass.render.apply(instance, arguments);

						instance._starRatings = new A.StarRating(
							{
								boundingBox: container.one('.ratings-options'),
								render: true,
								canReset: false
							}
						);

						if (context) {
							instance.setValue(context.value);
						}

						instance._starRatings.render();
					},

					getValue: function() {
						var instance = this;

						if (instance._starRatings) {
							return instance._starRatings.get("selectedIndex");
						}
						else {
							return 0;
						}
					},

					setValue: function(value) {
						var instance = this;

						value ? value : 0;

						if (instance._starRatings) {
							instance._starRatings.select(parseInt(value));
						}
					}
				}
			}
		);

		Liferay.namespace('DDM.Field').Ratings = RatingsField;
	},
	'',
	{
		requires: ['aui-ratings','liferay-ddm-form-renderer-field']
	}
);