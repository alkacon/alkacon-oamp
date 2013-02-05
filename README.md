# OpenCms Add-On Modules Package(OAMP)#

###Provided by Alkacon Software###

![Alkacon Software - The OpenCms Experts](http://www.alkacon.com/export/system/modules/org.opencms.website.template/resources/img/logo/logo_alkacon.gif "Alkacon Software - The OpenCms Experts")
![OpenCms - the Open Source Content Management System](http://www.opencms.org/export/system/modules/org.opencms.website.template/resources/img/logo/logo_opencms.gif "OpenCms - the Open Source Content Management System")

##About OAMP##

---
The [Alkacon OpenCms Add-On Module Package](http://www.alkacon.com/oamp/) (also called OAMP) is a set of free, open source extension modules for [OpenCms](http://www.opencms.org). Alkacon OAMP adds front-end related features to OpenCms that may be useful in case special functionalities are required.


The Alkacon OAMP modules are installed on a standard OpenCms system. 

##Building OAMP##

---

The build scripts read the alkacon.properties configuration file. These scripts supports now the separation of the opencms and oamp project trees.

###1. Check out this repository###
Create the initial directory structure and check out this repo:
```
$ mkdir -p ~/src/opencms
$ cd ~/src/opencms
$ git clone https://github.com/alkacon/alkacon-oamp.git
```
   This will copy this repository sources under `~/src/opencms/alkacon-oamp`

###2. Check out the dependencies###
If you are building modules that depend upon [opencms-core](https://github.com/alkacon/opencms-core), [modules-v7](https://github.com/alkacon/modules-v7) or [modules-v8](https://github.com/alkacon/modules-v8), check out them too:
```
$ git clone https://github.com/alkacon/opencms-core.git
$ git clone https://github.com/alkacon/modules-v7.git
```
   Note that to build all modules, you need `opencms-core` and `modules-v7`.

###3. Configure the build###
Edit `alkacon.properties` to fit your directory structure. If you have followed this instructions, you should be fine. The file has some comments that should already help, but just in case, the most important variables to set are:
```
# Where are opencms-core modules and build scripts
opencms.input
opencms.input.warfiles
opencms.output
# Where are the alkacon-oamp modules and where to save the artifacts
oamp.input
oamp.output
```

###4. Build###
Build the artifacts. By default, you will be asked what modules to build:
```
$ ant 
```

Notice that if you are building all modules, there is one module (`com.alkacon.opencms.oampdemo`) that also depends upon `modules-v7`. To build it, you have to pass to `ant` the directory where the `modules-v7` sources are. If you followed this instructions:
```
$ ant -Dexternaldirectories=/home/myuser/src/opencms/modules-v7
```
Note.- `externaldirectories` must be an absolute path (tilde is not expanded!).

To automate the build process, you can skip the selection dialog and have `ant` build everything:
```
$ ant -Dexternaldirectories=/home/myuser/src/opencms/modules-v7 -Dmodules.selection.mode=all
```

or you can configure the modules to build setting the build variable `modules.selection` (for example, edit `alkacon.properties`) and have `ant` build only the selected modules:
```
$ ant -Dexternaldirectories=/home/myuser/src/opencms/modules-v7 -Dmodules.selection.mode=selection
```




##Contributing to the OAMP modules development##

---
In case you consider contributing to the OAMP modules development by sending us [pull requests](http://help.github.com/send-pull-requests/), please download and read the OpenCms Contributor License Agreement (CLA).

The intention of the CLA is to clarify the legal status of your contribution(s). Basically, it says that you grant Alkacon Software GmbH full rights to use your contribution in the OpenCms distribution, without giving any warranties that it actually works.

[Click here to download the OpenCms CLA (PDF, 70 kb)](http://www.opencms.org/export/sites/opencms/en/development/contribute/opencms_cla.pdf).


In order to include your contribution in the OpenCms core distribution, we need a signed copy of the CLA from you. Please sign the CLA and fax it to:

    Alkacon Software GmbH
    Fax: +49 2236 3826-20

We regret that such legal stuff is required, however we believe it is better for all OpenCms users if legal issues are sorted out right from the beginning.

Should you have any questions regarding the CLA, please contact us.




##Contact##

----------

Alkacon Software GmbH<br />
An der Wachsfabrik 13<br />
DE-50996 Cologne, Germany<br />

[http://www.alkacon.com](http://www.alkacon.com)<br />
[http://www.opencms.org](http://www.opencms.org)

Please use our [contact form](http://www.alkacon.com/en/company/contact/form.html) to contact us directly.


