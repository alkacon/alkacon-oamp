
                Alkacon OpenCms Add-On Module Package: DocCenter
                       Version 2.0.0; June 18th, 2012


                                    WARNING:

                              USE AT YOUR OWN RISK

The Alkacon OpenCms DocCenter module and these instructions are distributed
in the hope that they will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.

Alkacon Software does not guarantee that there will be no damage to your
existing OpenCms installation when using this module.

Please use our GitHub issue tracker to report bugs in this module:
https://github.com/alkacon/alkacon-oamp/issues

IMPORTANT: Before using this module, make sure you have a full backup
of your OpenCms installation and database.


1. Module functions

With the Alkacon OpenCms DocCenter module, it is possible to create “download areas” 
similar to the output of an apache web server that allows directory browsing but 
with the support of the look and feel of the current template and many additional features. 
Once created, configured and filled with content, a DocCenter will offer the listing 
of available files / category subfolders with configurable and sortable information 
columns. Also search in the documents is supported. 


2. Module manufacturer

Alkacon Software GmbH
An der Wachsfabrik 13
D-50996 Cologne, Germany
http://www.alkacon.com


3. Installation

A detailed description of the installation is described in the provided
PDF file "Alkacon_OAMP_DocCenter.pdf".


4. History of changes

Changes are chronologically ordered from top (most recent)
to bottom (least recent).

Legend:
! New Feature
* Bug fixed
- General comment

2.0.0 - June 18th, 2012
- Renamed package to com.alkacon.opencms.v8.documentcenter
- Added module configuration .config
- Compatible with OpenCms 8.0.1 or later

1.0.2 - December 15th, 2010
- Column "size" shows number of contained documents in a folder
* Filenames containing "_" followed by a large number work properly 

1.0.1 - March 19th, 2010
! Croatian language added to the language-bundle 
- Get the locale only from the request context
* Adjusted some keys in the german properties files
* Open document history in the same/another window

1.0.0 - June 15th, 2009
- First public release
