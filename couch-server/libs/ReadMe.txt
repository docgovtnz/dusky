
In this directory is "3.25.jar" this is the ArcGIS 3.25 JS non-compact library (because zipped it's smaller than the
compact library). The zip/jar is produced by downloading the ArcGIS library and then zipping the relevant directory
"arcgis_js_v325_api/arcgis_js_api/library/3.25".

Before zipping the library you need to follow the install instructions within the library and modify the downloaded
source code to change the following token "[HOSTNAME_AND_PATH_TO_JSAPI]" to be the location of where this .jar file
will be hosted. For this application a resource path has been created in "StaticResourceConfiguration" for "/esri" so
a relative path of baseUrl:"/esri/3.25/dojo" seems to work ok.

There are TWO files you need to modify:

1. Open C:\Inetpub\wwwroot\arcgis_js_api\library\3.25\3.25\init.js in a text editor and search for the text
"https://[HOSTNAME_AND_PATH_TO_JSAPI]dojo", and replace this text with "https://www.example.com/arcgis_js_api/library/3.25/3.25/dojo"

2. Open C:\Inetpub\wwwroot\arcgis_js_api\library\3.25\3.25\dojo\dojo.js in a text editor and search for the text
"https://[HOSTNAME_AND_PATH_TO_JSAPI]dojo", and replace this text with "https://www.example.com/arcgis_js_api/library/3.25/3.25/dojo"

For Angular development environment there's also some config in the proxy.config.json file.


ALSO: [25-Feb-2019] Some manual changes were made to the esri.css file that also inside of this .jar file. See
"angular-client\src\app\esri-map\esri-map.component.css" for some documentation on what's happening there.

