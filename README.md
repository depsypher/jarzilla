## Java jar file viewer for Mac
### Jarzilla allows you to view the contents of any jar, ear, sar, war or zip file.
<br/>

**NOTE** Running Jarzilla on Mountain Lion currently requires the following steps:

  1. Open Security & Privacy Preferences
  2. Change setting "Allow applications downloaded from" to setting "Anywhere"
  3. Run Jarzilla
  4. Optionally, change security setting for gatekeeper back to 'Mac App Store & identified developers' to restore gatekeeper (This allows jarzilla to run, but keeps gatekeeper turned on)

Available for Mac OS X

![Main Window](http://jarzilla.googlecode.com/svn/wiki/images/jarzilla-main-window.png "Main Window")

![Details Window](http://jarzilla.googlecode.com/svn/wiki/images/jarzilla-details-window.png "Details Window")

![HTML Window](http://jarzilla.googlecode.com/svn/wiki/images/jarzilla-html-window.png "HTML Window")

![Image Window](http://jarzilla.googlecode.com/svn/wiki/images/jarzilla-image-window.png "Image Window")

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
