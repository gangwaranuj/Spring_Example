webpackJsonp([5],{

/***/ "./src/main/webapp/media/scripts/webpack/home/language_conflict_modal.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_isomorphic_fetch__ = __webpack_require__("./node_modules/isomorphic-fetch/fetch-npm-browserify.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_isomorphic_fetch___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_isomorphic_fetch__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_jquery__ = __webpack_require__("./node_modules/jquery/dist/jquery.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_jquery___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_jquery__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__funcs_wmModal__ = __webpack_require__("./src/main/webapp/media/scripts/webpack/funcs/wmModal.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__templates_language_conflict_modal_hbs__ = __webpack_require__("./src/main/webapp/media/scripts/webpack/home/templates/language_conflict_modal.hbs");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__templates_language_conflict_modal_hbs___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3__templates_language_conflict_modal_hbs__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__core__ = __webpack_require__("./src/main/webapp/media/scripts/webpack/core/index.js");






var ConflictModal = function ConflictModal(locale, preferredLocale) {
	var supportedLocales = window.workmarket.supportedLocales;

	var localeName = supportedLocales.find(function (lang) {
		return lang.code === locale;
	}).language;
	var preferredLocaleName = supportedLocales.find(function (lang) {
		return lang.code === preferredLocale;
	}).language;

	var conflictModal = __webpack_require__.i(__WEBPACK_IMPORTED_MODULE_2__funcs_wmModal__["a" /* default */])({
		autorun: true,
		title: 'WorkMarket Language Change',
		destroyOnClose: true,
		content: __WEBPACK_IMPORTED_MODULE_3__templates_language_conflict_modal_hbs___default()({
			locale: localeName,
			preferred: preferredLocaleName
		}),
		showCloseIcon: false
	});

	__WEBPACK_IMPORTED_MODULE_1_jquery___default()('#language_conflict_modal .cta-confirm-yes').on('click', function () {
		/** todo Update language switching to subdirectory when solidified */
		window.location.href = window.location.origin + window.location.pathname + '?lang=' + preferredLocale;
	});

	__WEBPACK_IMPORTED_MODULE_1_jquery___default()('#language_conflict_modal .cta-confirm-no').on('click', function () {
		__WEBPACK_IMPORTED_MODULE_0_isomorphic_fetch___default()('/service/language/update', {
			method: 'POST',
			credentials: 'same-origin',
			body: JSON.stringify({ locale: locale }),
			headers: new Headers({
				'Content-Type': 'application/json',
				'X-CSRF-Token': __WEBPACK_IMPORTED_MODULE_4__core__["a" /* default */].CSRFToken,
				'Data-Type': 'json'
			})
		}).then(function (res) {
			return res.json();
		}).then(function (res) {
			if (res.successful) {
				conflictModal.hide();
				return res;
			}
			throw new Error('Something went wrong trying to update the users preferred language', res);
		}).catch(function (error) {
			console.error({ error: error }); //eslint-disable-line
		});
	});
};

/* harmony default export */ __webpack_exports__["default"] = (ConflictModal);

/***/ }),

/***/ "./src/main/webapp/media/scripts/webpack/home/templates/language_conflict_modal.hbs":
/***/ (function(module, exports, __webpack_require__) {

var Handlebars = __webpack_require__("./node_modules/handlebars/runtime.js");
function __default(obj) { return obj && (obj.__esModule ? obj["default"] : obj); }
module.exports = (Handlebars["default"] || Handlebars).template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var helper, alias1=depth0 != null ? depth0 : {}, alias2=helpers.helperMissing, alias3="function", alias4=container.escapeExpression;

  return "<div id=\"language_conflict_modal\">\n	<p>You just chose to login in "
    + alias4(((helper = (helper = helpers.locale || (depth0 != null ? depth0.locale : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"locale","hash":{},"data":data}) : helper)))
    + ". The language you originally selected for the WorkMarket platform is "
    + alias4(((helper = (helper = helpers.preferred || (depth0 != null ? depth0.preferred : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"preferred","hash":{},"data":data}) : helper)))
    + ".</p>\n	<p>Going forward, how would you like to continue?</p>\n	<div class=\"wm-action-container\">\n		<button class=\"cta-confirm-no button\">"
    + alias4(((helper = (helper = helpers.locale || (depth0 != null ? depth0.locale : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"locale","hash":{},"data":data}) : helper)))
    + "</button>\n		<button class=\"cta-confirm-yes button\">"
    + alias4(((helper = (helper = helpers.preferred || (depth0 != null ? depth0.preferred : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"preferred","hash":{},"data":data}) : helper)))
    + "</button>\n	</div>\n</div>\n";
},"useData":true});

/***/ })

});
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vLi9zcmMvbWFpbi93ZWJhcHAvbWVkaWEvc2NyaXB0cy93ZWJwYWNrL2hvbWUvbGFuZ3VhZ2VfY29uZmxpY3RfbW9kYWwuanMiLCJ3ZWJwYWNrOi8vLy4vc3JjL21haW4vd2ViYXBwL21lZGlhL3NjcmlwdHMvd2VicGFjay9ob21lL3RlbXBsYXRlcy9sYW5ndWFnZV9jb25mbGljdF9tb2RhbC5oYnMiXSwibmFtZXMiOlsiQ29uZmxpY3RNb2RhbCIsImxvY2FsZSIsInByZWZlcnJlZExvY2FsZSIsInN1cHBvcnRlZExvY2FsZXMiLCJ3aW5kb3ciLCJ3b3JrbWFya2V0IiwibG9jYWxlTmFtZSIsImZpbmQiLCJsYW5nIiwiY29kZSIsImxhbmd1YWdlIiwicHJlZmVycmVkTG9jYWxlTmFtZSIsImNvbmZsaWN0TW9kYWwiLCJ3bU1vZGFsIiwiYXV0b3J1biIsInRpdGxlIiwiZGVzdHJveU9uQ2xvc2UiLCJjb250ZW50IiwibGFuZ3VhZ2VDb25mbGljdE1vZGFsVGVtcGxhdGUiLCJwcmVmZXJyZWQiLCJzaG93Q2xvc2VJY29uIiwiJCIsIm9uIiwibG9jYXRpb24iLCJocmVmIiwib3JpZ2luIiwicGF0aG5hbWUiLCJmZXRjaCIsIm1ldGhvZCIsImNyZWRlbnRpYWxzIiwiYm9keSIsIkpTT04iLCJzdHJpbmdpZnkiLCJoZWFkZXJzIiwiSGVhZGVycyIsIkFwcGxpY2F0aW9uIiwiQ1NSRlRva2VuIiwidGhlbiIsInJlcyIsImpzb24iLCJzdWNjZXNzZnVsIiwiaGlkZSIsIkVycm9yIiwiY2F0Y2giLCJlcnJvciIsImNvbnNvbGUiXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7OztBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUEsSUFBTUEsZ0JBQWdCLFNBQWhCQSxhQUFnQixDQUFDQyxNQUFELEVBQVNDLGVBQVQsRUFBNkI7QUFBQSxLQUMxQ0MsZ0JBRDBDLEdBQ3JCQyxPQUFPQyxVQURjLENBQzFDRixnQkFEMEM7O0FBRWxELEtBQU1HLGFBQWFILGlCQUFpQkksSUFBakIsQ0FBc0I7QUFBQSxTQUFRQyxLQUFLQyxJQUFMLEtBQWNSLE1BQXRCO0FBQUEsRUFBdEIsRUFBb0RTLFFBQXZFO0FBQ0EsS0FBTUMsc0JBQXNCUixpQkFBaUJJLElBQWpCLENBQXNCO0FBQUEsU0FBUUMsS0FBS0MsSUFBTCxLQUFjUCxlQUF0QjtBQUFBLEVBQXRCLEVBQTZEUSxRQUF6Rjs7QUFFQSxLQUFNRSxnQkFBZ0Isc0ZBQUFDLENBQVE7QUFDN0JDLFdBQVMsSUFEb0I7QUFFN0JDLFNBQU8sNEJBRnNCO0FBRzdCQyxrQkFBZ0IsSUFIYTtBQUk3QkMsV0FBUyw4RUFBQUMsQ0FBOEI7QUFDdENqQixXQUFRSyxVQUQ4QjtBQUV0Q2EsY0FBV1I7QUFGMkIsR0FBOUIsQ0FKb0I7QUFRN0JTLGlCQUFlO0FBUmMsRUFBUixDQUF0Qjs7QUFXQUMsQ0FBQSw4Q0FBQUEsQ0FBRSwyQ0FBRixFQUErQ0MsRUFBL0MsQ0FBa0QsT0FBbEQsRUFBMkQsWUFBTTtBQUNoRTtBQUNBbEIsU0FBT21CLFFBQVAsQ0FBZ0JDLElBQWhCLEdBQTBCcEIsT0FBT21CLFFBQVAsQ0FBZ0JFLE1BQWhCLEdBQXlCckIsT0FBT21CLFFBQVAsQ0FBZ0JHLFFBQW5FLGNBQW9GeEIsZUFBcEY7QUFDQSxFQUhEOztBQUtBbUIsQ0FBQSw4Q0FBQUEsQ0FBRSwwQ0FBRixFQUE4Q0MsRUFBOUMsQ0FBaUQsT0FBakQsRUFBMEQsWUFBTTtBQUMvREssRUFBQSx3REFBQUEsQ0FBTSwwQkFBTixFQUFrQztBQUNqQ0MsV0FBUSxNQUR5QjtBQUVqQ0MsZ0JBQWEsYUFGb0I7QUFHakNDLFNBQU1DLEtBQUtDLFNBQUwsQ0FBZSxFQUFFL0IsY0FBRixFQUFmLENBSDJCO0FBSWpDZ0MsWUFBUyxJQUFJQyxPQUFKLENBQVk7QUFDcEIsb0JBQWdCLGtCQURJO0FBRXBCLG9CQUFnQixzREFBQUMsQ0FBWUMsU0FGUjtBQUdwQixpQkFBYTtBQUhPLElBQVo7QUFKd0IsR0FBbEMsRUFVRUMsSUFWRixDQVVPO0FBQUEsVUFBT0MsSUFBSUMsSUFBSixFQUFQO0FBQUEsR0FWUCxFQVdFRixJQVhGLENBV08sVUFBQ0MsR0FBRCxFQUFTO0FBQ2QsT0FBSUEsSUFBSUUsVUFBUixFQUFvQjtBQUNuQjVCLGtCQUFjNkIsSUFBZDtBQUNBLFdBQU9ILEdBQVA7QUFDQTtBQUNELFNBQU0sSUFBSUksS0FBSixDQUFVLG9FQUFWLEVBQWdGSixHQUFoRixDQUFOO0FBQ0EsR0FqQkYsRUFrQkVLLEtBbEJGLENBa0JRLFVBQUNDLEtBQUQsRUFBVztBQUNqQkMsV0FBUUQsS0FBUixDQUFjLEVBQUVBLFlBQUYsRUFBZCxFQURpQixDQUNTO0FBQzFCLEdBcEJGO0FBcUJBLEVBdEJEO0FBdUJBLENBNUNEOztBQThDQSwrREFBZTVDLGFBQWYsRTs7Ozs7OztBQ3BEQTtBQUNBLHlCQUF5Qix1REFBdUQ7QUFDaEYsaUVBQWlFO0FBQ2pFLG1EQUFtRDs7QUFFbkQ7QUFDQSwwS0FBMEsseUJBQXlCLGFBQWE7QUFDaE47QUFDQSxnTEFBZ0wsNEJBQTRCLGFBQWE7QUFDek47QUFDQSwwS0FBMEsseUJBQXlCLGFBQWE7QUFDaE47QUFDQSxnTEFBZ0wsNEJBQTRCLGFBQWE7QUFDek47QUFDQSxDQUFDLGdCQUFnQixFIiwiZmlsZSI6Imxhbmd1YWdlQ29uZmxpY3RNb2RhbC5qcyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCBmZXRjaCBmcm9tICdpc29tb3JwaGljLWZldGNoJztcbmltcG9ydCAkIGZyb20gJ2pxdWVyeSc7XG5pbXBvcnQgd21Nb2RhbCBmcm9tICcuLi9mdW5jcy93bU1vZGFsJztcbmltcG9ydCBsYW5ndWFnZUNvbmZsaWN0TW9kYWxUZW1wbGF0ZSBmcm9tICcuL3RlbXBsYXRlcy9sYW5ndWFnZV9jb25mbGljdF9tb2RhbC5oYnMnO1xuaW1wb3J0IEFwcGxpY2F0aW9uIGZyb20gJy4uL2NvcmUnO1xuXG5jb25zdCBDb25mbGljdE1vZGFsID0gKGxvY2FsZSwgcHJlZmVycmVkTG9jYWxlKSA9PiB7XG5cdGNvbnN0IHsgc3VwcG9ydGVkTG9jYWxlcyB9ID0gd2luZG93LndvcmttYXJrZXQ7XG5cdGNvbnN0IGxvY2FsZU5hbWUgPSBzdXBwb3J0ZWRMb2NhbGVzLmZpbmQobGFuZyA9PiBsYW5nLmNvZGUgPT09IGxvY2FsZSkubGFuZ3VhZ2U7XG5cdGNvbnN0IHByZWZlcnJlZExvY2FsZU5hbWUgPSBzdXBwb3J0ZWRMb2NhbGVzLmZpbmQobGFuZyA9PiBsYW5nLmNvZGUgPT09IHByZWZlcnJlZExvY2FsZSkubGFuZ3VhZ2U7XG5cblx0Y29uc3QgY29uZmxpY3RNb2RhbCA9IHdtTW9kYWwoe1xuXHRcdGF1dG9ydW46IHRydWUsXG5cdFx0dGl0bGU6ICdXb3JrTWFya2V0IExhbmd1YWdlIENoYW5nZScsXG5cdFx0ZGVzdHJveU9uQ2xvc2U6IHRydWUsXG5cdFx0Y29udGVudDogbGFuZ3VhZ2VDb25mbGljdE1vZGFsVGVtcGxhdGUoe1xuXHRcdFx0bG9jYWxlOiBsb2NhbGVOYW1lLFxuXHRcdFx0cHJlZmVycmVkOiBwcmVmZXJyZWRMb2NhbGVOYW1lXG5cdFx0fSksXG5cdFx0c2hvd0Nsb3NlSWNvbjogZmFsc2Vcblx0fSk7XG5cblx0JCgnI2xhbmd1YWdlX2NvbmZsaWN0X21vZGFsIC5jdGEtY29uZmlybS15ZXMnKS5vbignY2xpY2snLCAoKSA9PiB7XG5cdFx0LyoqIHRvZG8gVXBkYXRlIGxhbmd1YWdlIHN3aXRjaGluZyB0byBzdWJkaXJlY3Rvcnkgd2hlbiBzb2xpZGlmaWVkICovXG5cdFx0d2luZG93LmxvY2F0aW9uLmhyZWYgPSBgJHt3aW5kb3cubG9jYXRpb24ub3JpZ2luICsgd2luZG93LmxvY2F0aW9uLnBhdGhuYW1lfT9sYW5nPSR7cHJlZmVycmVkTG9jYWxlfWA7XG5cdH0pO1xuXG5cdCQoJyNsYW5ndWFnZV9jb25mbGljdF9tb2RhbCAuY3RhLWNvbmZpcm0tbm8nKS5vbignY2xpY2snLCAoKSA9PiB7XG5cdFx0ZmV0Y2goJy9zZXJ2aWNlL2xhbmd1YWdlL3VwZGF0ZScsIHtcblx0XHRcdG1ldGhvZDogJ1BPU1QnLFxuXHRcdFx0Y3JlZGVudGlhbHM6ICdzYW1lLW9yaWdpbicsXG5cdFx0XHRib2R5OiBKU09OLnN0cmluZ2lmeSh7IGxvY2FsZSB9KSxcblx0XHRcdGhlYWRlcnM6IG5ldyBIZWFkZXJzKHtcblx0XHRcdFx0J0NvbnRlbnQtVHlwZSc6ICdhcHBsaWNhdGlvbi9qc29uJyxcblx0XHRcdFx0J1gtQ1NSRi1Ub2tlbic6IEFwcGxpY2F0aW9uLkNTUkZUb2tlbixcblx0XHRcdFx0J0RhdGEtVHlwZSc6ICdqc29uJ1xuXHRcdFx0fSlcblx0XHR9KVxuXHRcdFx0LnRoZW4ocmVzID0+IHJlcy5qc29uKCkpXG5cdFx0XHQudGhlbigocmVzKSA9PiB7XG5cdFx0XHRcdGlmIChyZXMuc3VjY2Vzc2Z1bCkge1xuXHRcdFx0XHRcdGNvbmZsaWN0TW9kYWwuaGlkZSgpO1xuXHRcdFx0XHRcdHJldHVybiByZXM7XG5cdFx0XHRcdH1cblx0XHRcdFx0dGhyb3cgbmV3IEVycm9yKCdTb21ldGhpbmcgd2VudCB3cm9uZyB0cnlpbmcgdG8gdXBkYXRlIHRoZSB1c2VycyBwcmVmZXJyZWQgbGFuZ3VhZ2UnLCByZXMpO1xuXHRcdFx0fSlcblx0XHRcdC5jYXRjaCgoZXJyb3IpID0+IHtcblx0XHRcdFx0Y29uc29sZS5lcnJvcih7IGVycm9yIH0pOyAvL2VzbGludC1kaXNhYmxlLWxpbmVcblx0XHRcdH0pO1xuXHR9KTtcbn07XG5cbmV4cG9ydCBkZWZhdWx0IENvbmZsaWN0TW9kYWw7XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9zcmMvbWFpbi93ZWJhcHAvbWVkaWEvc2NyaXB0cy93ZWJwYWNrL2hvbWUvbGFuZ3VhZ2VfY29uZmxpY3RfbW9kYWwuanMiLCJ2YXIgSGFuZGxlYmFycyA9IHJlcXVpcmUoXCIvaG9tZS90aGlua3N5c3VzZXIvcmVwby93b3JrbWFya2V0L2FwcGxpY2F0aW9uL25vZGVfbW9kdWxlcy9oYW5kbGViYXJzL3J1bnRpbWUuanNcIik7XG5mdW5jdGlvbiBfX2RlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgKG9iai5fX2VzTW9kdWxlID8gb2JqW1wiZGVmYXVsdFwiXSA6IG9iaik7IH1cbm1vZHVsZS5leHBvcnRzID0gKEhhbmRsZWJhcnNbXCJkZWZhdWx0XCJdIHx8IEhhbmRsZWJhcnMpLnRlbXBsYXRlKHtcImNvbXBpbGVyXCI6WzcsXCI+PSA0LjAuMFwiXSxcIm1haW5cIjpmdW5jdGlvbihjb250YWluZXIsZGVwdGgwLGhlbHBlcnMscGFydGlhbHMsZGF0YSkge1xuICAgIHZhciBoZWxwZXIsIGFsaWFzMT1kZXB0aDAgIT0gbnVsbCA/IGRlcHRoMCA6IHt9LCBhbGlhczI9aGVscGVycy5oZWxwZXJNaXNzaW5nLCBhbGlhczM9XCJmdW5jdGlvblwiLCBhbGlhczQ9Y29udGFpbmVyLmVzY2FwZUV4cHJlc3Npb247XG5cbiAgcmV0dXJuIFwiPGRpdiBpZD1cXFwibGFuZ3VhZ2VfY29uZmxpY3RfbW9kYWxcXFwiPlxcblx0PHA+WW91IGp1c3QgY2hvc2UgdG8gbG9naW4gaW4gXCJcbiAgICArIGFsaWFzNCgoKGhlbHBlciA9IChoZWxwZXIgPSBoZWxwZXJzLmxvY2FsZSB8fCAoZGVwdGgwICE9IG51bGwgPyBkZXB0aDAubG9jYWxlIDogZGVwdGgwKSkgIT0gbnVsbCA/IGhlbHBlciA6IGFsaWFzMiksKHR5cGVvZiBoZWxwZXIgPT09IGFsaWFzMyA/IGhlbHBlci5jYWxsKGFsaWFzMSx7XCJuYW1lXCI6XCJsb2NhbGVcIixcImhhc2hcIjp7fSxcImRhdGFcIjpkYXRhfSkgOiBoZWxwZXIpKSlcbiAgICArIFwiLiBUaGUgbGFuZ3VhZ2UgeW91IG9yaWdpbmFsbHkgc2VsZWN0ZWQgZm9yIHRoZSBXb3JrTWFya2V0IHBsYXRmb3JtIGlzIFwiXG4gICAgKyBhbGlhczQoKChoZWxwZXIgPSAoaGVscGVyID0gaGVscGVycy5wcmVmZXJyZWQgfHwgKGRlcHRoMCAhPSBudWxsID8gZGVwdGgwLnByZWZlcnJlZCA6IGRlcHRoMCkpICE9IG51bGwgPyBoZWxwZXIgOiBhbGlhczIpLCh0eXBlb2YgaGVscGVyID09PSBhbGlhczMgPyBoZWxwZXIuY2FsbChhbGlhczEse1wibmFtZVwiOlwicHJlZmVycmVkXCIsXCJoYXNoXCI6e30sXCJkYXRhXCI6ZGF0YX0pIDogaGVscGVyKSkpXG4gICAgKyBcIi48L3A+XFxuXHQ8cD5Hb2luZyBmb3J3YXJkLCBob3cgd291bGQgeW91IGxpa2UgdG8gY29udGludWU/PC9wPlxcblx0PGRpdiBjbGFzcz1cXFwid20tYWN0aW9uLWNvbnRhaW5lclxcXCI+XFxuXHRcdDxidXR0b24gY2xhc3M9XFxcImN0YS1jb25maXJtLW5vIGJ1dHRvblxcXCI+XCJcbiAgICArIGFsaWFzNCgoKGhlbHBlciA9IChoZWxwZXIgPSBoZWxwZXJzLmxvY2FsZSB8fCAoZGVwdGgwICE9IG51bGwgPyBkZXB0aDAubG9jYWxlIDogZGVwdGgwKSkgIT0gbnVsbCA/IGhlbHBlciA6IGFsaWFzMiksKHR5cGVvZiBoZWxwZXIgPT09IGFsaWFzMyA/IGhlbHBlci5jYWxsKGFsaWFzMSx7XCJuYW1lXCI6XCJsb2NhbGVcIixcImhhc2hcIjp7fSxcImRhdGFcIjpkYXRhfSkgOiBoZWxwZXIpKSlcbiAgICArIFwiPC9idXR0b24+XFxuXHRcdDxidXR0b24gY2xhc3M9XFxcImN0YS1jb25maXJtLXllcyBidXR0b25cXFwiPlwiXG4gICAgKyBhbGlhczQoKChoZWxwZXIgPSAoaGVscGVyID0gaGVscGVycy5wcmVmZXJyZWQgfHwgKGRlcHRoMCAhPSBudWxsID8gZGVwdGgwLnByZWZlcnJlZCA6IGRlcHRoMCkpICE9IG51bGwgPyBoZWxwZXIgOiBhbGlhczIpLCh0eXBlb2YgaGVscGVyID09PSBhbGlhczMgPyBoZWxwZXIuY2FsbChhbGlhczEse1wibmFtZVwiOlwicHJlZmVycmVkXCIsXCJoYXNoXCI6e30sXCJkYXRhXCI6ZGF0YX0pIDogaGVscGVyKSkpXG4gICAgKyBcIjwvYnV0dG9uPlxcblx0PC9kaXY+XFxuPC9kaXY+XFxuXCI7XG59LFwidXNlRGF0YVwiOnRydWV9KTtcblxuXG4vLy8vLy8vLy8vLy8vLy8vLy9cbi8vIFdFQlBBQ0sgRk9PVEVSXG4vLyAuL3NyYy9tYWluL3dlYmFwcC9tZWRpYS9zY3JpcHRzL3dlYnBhY2svaG9tZS90ZW1wbGF0ZXMvbGFuZ3VhZ2VfY29uZmxpY3RfbW9kYWwuaGJzXG4vLyBtb2R1bGUgaWQgPSAuL3NyYy9tYWluL3dlYmFwcC9tZWRpYS9zY3JpcHRzL3dlYnBhY2svaG9tZS90ZW1wbGF0ZXMvbGFuZ3VhZ2VfY29uZmxpY3RfbW9kYWwuaGJzXG4vLyBtb2R1bGUgY2h1bmtzID0gNSJdLCJzb3VyY2VSb290IjoiIn0=