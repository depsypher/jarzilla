## Java jar file viewer for Mac
### Jarzilla allows you to view the contents of any jar, ear, sar, war or zip file.
<br/>

**NOTE** Running Jarzilla on Mountain Lion currently requires the following steps:

  1. Open Security & Privacy Preferences
  2. Change setting "Allow applications downloaded from" to setting "Anywhere"
  3. Run Jarzilla
  4. Optionally, change security setting for gatekeeper back to 'Mac App Store & identified developers' to restore gatekeeper (This allows jarzilla to run, but keeps gatekeeper turned on)

Available for Mac OS X

![jarzilla-main-window](https://cloud.githubusercontent.com/assets/113138/6840855/8c56547c-d33d-11e4-9a69-21b5de1b36ac.png)

![jarzilla-details-window](https://cloud.githubusercontent.com/assets/113138/6840863/9f8622ca-d33d-11e4-8fb5-f4de250e2c5e.png)

![jarzilla-html-window](https://cloud.githubusercontent.com/assets/113138/6840865/a6eba4b8-d33d-11e4-8f52-bbcc6b34db36.png)

![jarzilla-image-window](https://cloud.githubusercontent.com/assets/113138/6840867/af98f9b2-d33d-11e4-8adf-c6da26d3a1da.png)

### Overview
Jarzilla allows you to view most kinds of zip files, with a focus on inspecting the contents of java jar files.

* Simply drag a jar file onto Jarzilla or use the file open menu to view its contents.
* You can also associate jar files to Jarzilla with right click > Get Info > Open With > Change All.
* If the jar file is executable, the Run button will launch it.
* Double click on any file within the archive to get a custom resource view.
    * Java class files get a decompiled source view and outline view.
    * Html files get a rendered html view and html source view.
    * Images get a rendered image view.
    * Everything else gets rendered as text.
