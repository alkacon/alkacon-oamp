
                Alkacon OpenCms Add-On Module Package: Survey
                       Version 1.0.3; December 22, 2010


                                    WARNING:

                              USE AT YOUR OWN RISK

The Alkacon OpenCms survey module and these instructions are distributed
in the hope that they will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.

Alkacon Software does not guarantee that there will be no damage to your
existing OpenCms installation when using this module.

Please use our Bugzilla to report bugs in this module:
http://bugzilla.opencms.org

IMPORTANT: Before using this module, make sure you have a full backup
of your OpenCms installation and database.


1. Module functions

With the Survey module, it is possible to create and analyze a survey. Based on the Webform 
Module, it is possible to create highly configurable survey forms without knowledge of HTML. 
Once created, configured and published, a survey can be filled out by website visitors.  The 
analysis of the survey can easily be done by creating and configuration a survey report.



2. Module manufacturer

Alkacon Software GmbH
An der Wachsfabrik 13
D-50996 Cologne, Germany
http://www.alkacon.com


3. Installation

A detailed description of the installation is described in the provided
PDF file "Alkacon_OAMP_Survey.pdf".


4. History of changes

Changes are chronologically ordered from top (most recent)
to bottom (least recent).

Legend:
! New Feature
* Bug fixed
- General comment

2.0.0 April 12th, 2011
- Necessary adaptations to work with OAMP WebForm Module 2.0.0

1.0.3 Nov, 2010
- Extended module documentation.

1.0.2 - February 19th, 2010
* Avoid NullPointerException when advanced form configuration node in survey XML content is missing. 
* Fixed issue with empty report in case of commas in labels. 
- This version requires version 1.3.2 of OAMP webform.

1.0.1 - April 1, 2009
! Am additional text area added to the bottom of the report.
! If a user visits the form for the first time a cookie is set that makes sure a user can not vote twice.

1.0.0 - July 4, 2008
- First public release
