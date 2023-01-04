(function () {
	var PAYLOAD_SERVER_PATH = "/payload";
	var PAYLOAD_PATH = "payload/";

	$(document).ready(function () {
		getFilesFromServer(PAYLOAD_SERVER_PATH);
	});

	function getFilesFromServer(path) {
		$.get(path, function (data, status) {
			if (status == "success") {
				parseFilesFromWebPage(data);
			} else {
				console.log();
			}
		});
	}

	function parseFilesFromWebPage(html) {
		var document = $(html);

		// find all links ending with .dar
		document.find("a[href$='.dar']").each(function () {
			var url = $(this).attr('href');
			var downloadUrl = PAYLOAD_PATH + url;

			$("#vroFile").attr("href", downloadUrl);
		});
	}
})();