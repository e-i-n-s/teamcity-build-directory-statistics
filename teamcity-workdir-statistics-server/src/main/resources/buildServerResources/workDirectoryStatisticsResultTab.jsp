<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript"
        src="${teamcityPluginResourcesPath}node_modules/datatables/media/js/jquery.dataTables.js"></script>
<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}node_modules/datatables/media/css/jquery.dataTables.css"/>

<table id="files" class="display" style="width:100%">
    <thead>
    <tr>
        <th>Path</th>
        <th>Bytes</th>
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
            ]
        });
    });
</script>
