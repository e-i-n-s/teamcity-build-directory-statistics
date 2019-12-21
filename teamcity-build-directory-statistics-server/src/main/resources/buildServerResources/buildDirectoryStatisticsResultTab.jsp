<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript"
        src="${teamcityPluginResourcesPath}node_modules/datatables.net/js/jquery.dataTables.js"></script>
<script type="text/javascript"
        src="${teamcityPluginResourcesPath}node_modules/datatables.net-dt/js/dataTables.dataTables.js"></script>
<script type="text/javascript" src="${teamcityPluginResourcesPath}js/file-size.js"></script>
<script type="text/javascript" src="${teamcityPluginResourcesPath}js/d3.v5.min.js"></script>
<script type="text/javascript" src="${teamcityPluginResourcesPath}js/sunburst-chart.min.js"></script>
<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}node_modules/datatables.net-dt/css/jquery.dataTables.css"/>

<div id="chart" style="width: 100%; margin: 0"></div>

<table id="files" class="display" style="width:100%">
    <thead>
    <tr>
        <th>Path</th>
        <th>Size</th>
    </tr>
    </thead>
</table>

<script type="text/javascript">

    renderFileSize = function (size) {
        const kb = size / 1024;
        if (kb < 1) {
            return size + ' B';
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
    };

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
                    "render": renderFileSize,
                    "targets": 1,
                    "type": 'file-size'
                }
            ]
        });

        const buildDirectory = {
            name: "root",
        };

        data.forEach(function (file) {
            const directories = file.path.split("/");
            let currentPath = buildDirectory;
            for (let i = 0; i < directories.length; i++) {

                if (!currentPath.children) {
                    currentPath.children = [];
                }

                if (!currentPath.children.find(item => item.name == directories[i])) {
                    if (i == directories.length - 1) {
                        currentPath.children.push({name: directories[i], size: file.bytes})
                    } else {
                        currentPath.children.push({name: directories[i]})
                    }
                }

                currentPath = currentPath.children.find(item => item.name == directories[i]);
            }
        });

        const color = d3.scaleOrdinal(d3.schemePaired);
        Sunburst().data(buildDirectory)
            .showLabels(true)
            .size('size')
            .color((d, parent) => color(parent ? parent.data.name : null))
            .tooltipContent((d, node) => "Size: " + renderFileSize(node.value))
            (document.getElementById('chart'));
    });
</script>
