<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript"
        src="${teamcityPluginResourcesPath}node_modules/datatables.net/js/jquery.dataTables.js"></script>
<script type="text/javascript"
        src="${teamcityPluginResourcesPath}node_modules/datatables.net-dt/js/dataTables.dataTables.js"></script>
<script type="text/javascript"
        src="${teamcityPluginResourcesPath}node_modules/datatables.net-plugins/sorting/file-size.js"></script>
<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}node_modules/datatables.net-dt/css/jquery.dataTables.css"/>

<table id="files" class="display" style="width:100%">
    <thead>
    <tr>
        <th>Path</th>
        <th>Size</th>
    </tr>
    </thead>
</table>

<script type="text/javascript">
    jQuery(document).ready(function () {
        const data = JSON.parse("${data}");
        jQuery('#files').DataTable({
            "data": data,
            "pageLength": 100000,
            "lengthChange": false,
            "columns": [
                {"data": "path"},
                {"data": "bytes"},
            ],
            "columnDefs": [
                {
                    "render": function (data) {
                        const kb = data / 1024;
                        if (kb < 1) {
                            return data + ' B';
                        }
                        const mb = kb / 1024;
                        if (mb < 1) {
                            return kb.toFixed(2) + ' KB';
                        }
                        const gb = mb / 1024;
                        if (gb < 1) {
                            return mb.toFixed(2) + ' MB';
                        }
                        return gb.toFixed(2) + ' GB';
                    },
                    "targets": 1,
                    "type": 'file-size'
                }
            ]
        });
    });
</script>
