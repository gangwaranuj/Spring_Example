<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	window.workmarket = window.workmarket || {};
	window.workmarket.locale = "${currentLocale}";
	window.workmarket.format = "${currentFormat}";
	window.workmarket.preferredLocale = "${preferredLocale}";
	window.workmarket.preferredFormat = "${preferredFormat}";
	window.workmarket.supportedLocales = ${supportedLocalesList};
	window.workmarket.isLocaleEnabled = ${isLocaleEnabled};
</script>