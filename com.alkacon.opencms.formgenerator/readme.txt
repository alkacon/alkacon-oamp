
                Alkacon OpenCms Add-On Module Package: Webform
                       Version 1.4.1; Nov 08, 2010


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

1.4.1 - Nov, 2010
- Moved Captcha presets into module folder
- Some TableField layout enhancements
! Add module parameter 'font-prefix' to configure fonts to be used for capture generation
! Added optional confirmation mail sender address

1.4.0 - May, 2010
! Added sub fields that are shown for select boxes or radio button fields depending on the chosen value
! Configurable front end report output of form entries stored in the database
! Uploaded files can now be stored in the OpenCms VFS instead of the servers real file system
! Static text can be shown between single input fields
! Option to keep the user session when viewing forms by sending a server request in a configurable time interval
! Option to generate valid XHTML output
! Option to define unique fields if storing submitted forms in the database
! Added form availability to show a configurable error text after the form is expired
! The CSS used for the emails can now be changed individually
! The detail view of forms stored in the database allows to view the uploaded files
! Only users with the role "Database Manager" or with write permissions on the corresponding form file
  can edit the stored database entries
* Unconfigured new form does not throw an error anymore when trying to preview it
- Improved formatting of generated text emails
- Improved editor layout for forms by adding tabs

1.3.2 - April 23th, 2010
* Fixed issue with Microsoft Excel reformatting numerical columns (contribution by Mathias Lin, SYSVISION).
* Fixed issue with file upload fields not working in version 1.3.1 / OpenCms 7.5.0. 
- Slight API changes (wider access modifiers) for allowing bugfix version 1.0.2 of the OAMP survey module.
- Added some new field input types (password field, display field, hidden display field)
- Added possibilities for a special property file per web form and an action class after the form was saved/sent

1.3.1 - June 15th, 2009
- Compatibility update: this is the only version that works with OpenCms 7.5.0 or later!

1.3.0 - April, 2009
* Fixed issues with CmsEmptyField stored to database. 
* Fixed issue with CVS - export button not working in the workplace list for Internet Explorer.
* Fixed issue with UUID in Resource Path column of CSV exported from workplace tool. 
! Added macro %(currentsite) for folder selection of widget com.alkacon.opencms.formgenerator.CmsSelectWidgetXMLContentType. 
! Added new middle text that is shown after the form data and before the submit button. 
! Added module parameter "export.encoding" to transform exported csv files (Microsoft - Excel Support: choose windows-1252). 
! Alternative tool location in the workplace, visibility of tool configurable by module parameter "usergroup". 
! Time format of exported csv data may be controlled by module parameter "export.timeformat". 
! CSS class attributes are generated form-specific via the new  "Style" configuration option.  
! Added module parameter "export.lineseparator" for configuration of the line break in exported CSV: Values are "windows" or "unix".
  For Excel compatibility use "unix" or "excel". 
! New math captcha (maptcha) for verification of human beings filling out the form. 

1.2.0 - May, 2008
! Added a new Administration Tool under Database Management.
! Added a new field type for tables.
! Added word based captchas in english and german.
! Added Oracle support.
* Fixed issue with captcha image caching.
* Fixed issue with hidden fields and cross-site-scripting.
* Fixed a bug in the captcha mechanism resulting in several false negatives.
* Fixed image size increment algorithm to keep image ratio.
- Improved database persistence and data manipulation facilities.
- Removed com.sun.* dependencies.

1.1.0 - February, 2008
* Fixed issue saving site relative form path into database instead of root path.
* Fixed issue with form attributes written in the wrong position in the html code.
! New 'dynamic' field type added for customized field value generation.
! 'Database labels' introduced to better maintain the data in the database, use 'label|dblabel' notation in field name.
! New macros with 'database labels' available in the check page, email, confirmation email and confirmation text.
! New improved API to access the form data from the database, see CmsFormDataBaseAccess class.
- Improved CSV Report, including now form path and form submission date.

1.0.0 - December 21, 2007
- First public release
