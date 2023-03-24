(function () {
  const PLATFORM_DOC = 'setup-platform.html'
  const WORKSTATION_DOC = 'setup-workstation.html'
  const WORKSTATION_CLOUD_CLIENT_DOC = 'setup-workstation-cloud-client.html'
  const WORKSTATION_MAVEN_DOC = 'setup-workstation-maven.html'
  const WORKSTATION_VSCODE_DOC = 'setup-workstation-vs-code-for-vro.html'

  $(document).ready(function () {
    $('#readme').hide()
    $('#readme .modal-footer .btn').click(function () {
      $('#readme').hide()
    })
    $('#platform').click(function () {
      showWaitDialog()
      loadReadme('platform')
    })

    $('#workstation').click(function () {
      showWaitDialog()
      loadReadme('workstation')
    })

    $('#workstation-cloud-client').click(function () {
      showWaitDialog()
      loadReadme('workstation-cloud-client')
    })

    $('#workstation-maven').click(function () {
      showWaitDialog()
      loadReadme('workstation-maven')
    })

    $('#workstation-vs-code').click(function () {
      showWaitDialog()
      loadReadme('workstation-vs-code')
    })
  })

  function loadReadme (type) {
    let filePath = null
    if (type == 'platform') {
      filePath = PLATFORM_DOC
    } else if (type == 'workstation') {
      filePath = WORKSTATION_DOC
    } else if (type == 'workstation-cloud-client') {
      filePath = WORKSTATION_CLOUD_CLIENT_DOC
    } else if (type == 'workstation-maven') {
      filePath = WORKSTATION_MAVEN_DOC
    } else if (type == 'workstation-vs-code') {
      filePath = WORKSTATION_VSCODE_DOC
    }

    $.get(filePath, function (html, status) {
      if (status == 'success') {
        $('#readme .modal-body').html(html)
        $('#readme').show()
      } else {
        alert('Error loading info. Status: ' + status)
      }
    })
  }

  function showWaitDialog () {
    // TODO: progress
  }
})()
