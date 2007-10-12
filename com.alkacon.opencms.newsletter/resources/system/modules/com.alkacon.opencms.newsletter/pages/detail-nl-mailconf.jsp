<%@ page session="false" %><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<cms:contentload collector="singleFile" param="%(opencms.uri)" editable="true">

<cms:contentshow element="MailHead" />
<cms:contentcheck ifexists="MailText">
	<cms:contentshow element="MailText" />
</cms:contentcheck>
<cms:contentcheck ifexistsnone="MailText">
<h1>Duis autem vel eum iriure</h1>
<p>
Hic patrio de more Iovi cum sacra <a href="#" title="">parassent</a>,
ut vetus incensis incanduit ignibus ara,
serpere caeruleum Danai videre draconem
in platanum, coeptis quae stabat proxima sacris.
Nidus erat volucrum bis quattuor arbore summa:
quas simul et matrem circum sua damna volantem
corripuit serpens avidoque recondidit ore.

</p>
<ul>
	<li>Argumentum baculinum!</li>
	<li>Beati pauperes spiritu.</li>
	<li>Ira furor brevis est.</li>
</ul>
<p>
Obstipuere omnes, at veri providus augur
Thestorides "vincemus", ait "gaudete, Pelasgi!
Troia cadet, sed erit nostri mora longa laboris",
atque novem volucres in belli digerit annos.
ille, ut erat, virides amplexus in arbore ramos,
fit lapis, et superat serpentis imagine saxum.
</p>
<table>
<tr>
	<td>Infestis pilis!</td>
	<td>Qualis artifex pereo!</td>
</tr>
<tr>
	<td>Singularis Porcus</td>
	<td>Vae victis!</td>
</tr>
</table>
</cms:contentcheck>

<cms:contentshow element="MailFoot" />
</cms:contentload>