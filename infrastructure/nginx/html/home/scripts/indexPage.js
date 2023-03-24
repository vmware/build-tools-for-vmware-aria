(function () {
  const PAYLOAD_SERVER_PATH = '/payload'
  const PAYLOAD_PATH = 'payload/'

  $(document).ready(function () {
    getFilesFromServer(PAYLOAD_SERVER_PATH)
  })

  function getFilesFromServer (path) {
    $.get(path, function (data, status) {
      if (status == 'success') {
        parseFilesFromWebPage(data)
      } else {
        console.log()
      }
    })
  }

  function parseFilesFromWebPage (html) {
    const document = $(html)

    // find all links ending with .dar
    document.find("a[href$='.dar']").each(function () {
      const url = $(this).attr('href')
      const downloadUrl = PAYLOAD_PATH + url

      $('#vroFile').attr('href', downloadUrl)
    })
  }
})()
