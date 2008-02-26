
                Alkacon OpenCms Add-On Module Package: Webform
                       Version 1.1.0; December 21, 2007


                                    WARNING:

                              USE AT YOUR OWN RISK

The Alkacon OpenCms webform module and these instructions are distributed
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

The webform module makes it possible to create input forms for collection of  
data website visitors enter without knowledge of HTML. These so called webforms
are created by mouse clicks and some text input. One can select from different 
input field types. It is possible to define the label, default value and options for 
the input element. Captcha verification is also supported. The resulting data 
of a submission may be sent to a configurable email and / or be stored in the 
OpenCms database. 

2. Module manufacturer

Alkacon Software GmbH
An der Wachsfabrik 13
D-50996 Cologne, Germany
http://www.alkacon.com


3. Installation

A detailed description of the installation is described in the provided
PDF file "Alkacon_OAMP_Webform.pdf".


4. History of changes

Changes are chronologically ordered from top (most recent)
to bottom (least recent).

Legend:
! New Feature
* Bug fixed
- General comment

1.1.0 - February, 2008
* Fixed issue saving site relative form path into database instead of root path.
! New 'dynamic' field type added for customized field value generation.
! 'Database labels' introduced to better maintain the data in the database, use 'label|dblabel' notation in field name.
! New macros with 'database labels' available in the check page, email, confirmation email and confirmation text.
! New improved API to access the form data from the database, see CmsFormDataBaseAccess class.
- Improved CSV Report, including now form path and form submission date.

1.0.0 - December 21, 2007
- First public release
