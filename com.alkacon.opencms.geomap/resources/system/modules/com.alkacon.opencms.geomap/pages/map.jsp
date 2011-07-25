<%@page import="com.alkacon.opencms.geomap.*, org.opencms.file.*, org.opencms.main.*, org.opencms.util.*"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<c:set var="locale" value="${cms:vfs(pageContext).context.locale}" />

<fmt:setLocale value="${locale}" />
<fmt:bundle basename="com.alkacon.opencms.geomap.frontend">
<cms:formatter var="map">

<div>
	

	
	<%-- calculate map size: width and height --%>
	<c:set var="mapw">600</c:set>
	<c:set var="maph">400</c:set>
	<c:set var="mapsize">${map.value.MapSize}</c:set>
	<c:set var="sizesep">${fn:indexOf(mapsize, "x")}</c:set>
	<c:if test="${sizesep != -1}">
		<c:set var="mapw">${fn:trim(fn:substringBefore(mapsize, "x"))}</c:set>
		<c:set var="maph">${fn:trim(fn:substringAfter(mapsize, "x"))}</c:set>
	</c:if>
	<c:if test="${not fn:contains(mapw, '%')}">
		<c:set var="mapw">${mapw}px</c:set>
	</c:if>
	<c:if test="${not fn:contains(maph, '%')}">
		<c:set var="maph">${maph}px</c:set>
	</c:if>
	<c:if test="${mapw != '100%'}">
		<c:set var="mapw">width: ${mapw};</c:set>
	</c:if>
	<c:if test="${mapw == '100%'}">
		<c:set var="mapw">width: ${mapw};</c:set>
	</c:if>
	<c:set var="maph">height: ${maph};</c:set>
	
	<%-- get map key from property map.key or module parameter map.key --%>
	<c:set var="mapkey"><cms:property name="map.key" file="search" default="" /></c:set>
	<%
		String mapKey = (String)pageContext.getAttribute("mapkey");
		if (CmsStringUtil.isEmptyOrWhitespaceOnly(mapKey)) {
			mapKey = OpenCms.getModuleManager().getModule("com.alkacon.opencms.geomap").getParameter("map.key", "");
		}
		pageContext.setAttribute("mapkey", mapKey);
	%>

	<%-- include Google Maps JS --%>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false&language=${locale}&key=${mapkey}"></script>
	<script type="text/javascript">
		// map object
		var map;
		// geocoder used to get address data of coordinates
		var geocoder = new google.maps.Geocoder();
		// set default map center
		var mapCenterLatLng = new google.maps.LatLng(50.870474,6.997884);
		// global arrays of the markers, marker coords and info windows
		var marker = [];
		var mapMarkerLatLng = [];
		var infoWindow = [];
		// stores the query error count
		var queryErrors = 0;

	<%-- get first manually entered coordinate, it is used as map center --%>
	<c:forEach var="mapcoord" items="${map.valueList.MapCoord}" end="0">
		<c:set var="loccent">${mapcoord.value.Coord}</c:set>
		
		<%
			CmsGoogleMapWidgetValue val = new CmsGoogleMapWidgetValue((String)pageContext.getAttribute("loccent"));
			pageContext.setAttribute("loccent", val);
		%>
		mapCenterLatLng = new google.maps.LatLng(${loccent.lat}, ${loccent.lng});
	</c:forEach>

		function showGoogleMap() {
			// set the map options
			var mapOptions = {
				zoom: ${map.value.MapZoom},
				center: mapCenterLatLng,
				mapTypeControlOptions: {style: google.maps.MapTypeControlStyle.DEFAULT, mapTypeIds: new Array(google.maps.MapTypeId.ROADMAP, google.maps.MapTypeId.SATELLITE, google.maps.MapTypeId.HYBRID)},
				mapTypeId: google.maps.MapTypeId.${map.value.MapType}
			};
			// create the map
			map = new google.maps.Map(document.getElementById("AlkaconGeoMapgooglemap"), mapOptions);

	<c:choose>
		<%-- use KML/KMZ file to render map --%>
		<c:when test="${not map.value.Kml.isEmptyOrWhitespaceOnly}">
			<c:set var="cms" value="${cms:getCmsObject(pageContext)}" />
			<c:set var="kml">${map.value.Kml}</c:set>
			<%
				CmsObject cms = (CmsObject)pageContext.getAttribute("cms");
				// generate link including server URL, necessary to make KML/KMZ files work
				String kml = OpenCms.getLinkManager().getServerLink(cms, (String)pageContext.getAttribute("kml"));
				pageContext.setAttribute("kml", kml);
			%>
			var kmlLayer = new google.maps.KmlLayer('${kml}',
				{
					map: map
				});

		</c:when>
		<%-- show manually entered coordinates --%>
		<c:otherwise>			
			var contentString;
			<c:forEach var="mapcoord" items="${map.valueList.MapCoord}" varStatus="status">
				<c:set var="loc">${mapcoord.value.Coord}</c:set>
				<%
					CmsGoogleMapWidgetValue val = new CmsGoogleMapWidgetValue((String)pageContext.getAttribute("loc"));
					pageContext.setAttribute("loc", val);
				%>
					// set coordinates
					mapMarkerLatLng[${status.index}] = new google.maps.LatLng(${loc.lat}, ${loc.lng});
					<c:set var="title" value="" />
					<c:if test="${not mapcoord.value.Caption.isEmptyOrWhitespaceOnly}">
						<c:set var="title">,title: "${mapcoord.value.Caption}"</c:set>
					</c:if>
					// create new marker with coordinates
					marker[${status.index}] = new google.maps.Marker({
						position: mapMarkerLatLng[${status.index}],
						map: map
						${title}
					});

					// create content for info window
					contentString = "";
					<c:if test="${not mapcoord.value.Caption.isEmptyOrWhitespaceOnly}">
						contentString += "<b>${mapcoord.value.Caption}</b><br/>";
					</c:if>
					
					<c:choose>
						<c:when test="${mapcoord.value.Address.exists}">
							<c:set var="gAdr">${mapcoord.value.Address}</c:set>
							<%
								String gAdr = (String)pageContext.getAttribute("gAdr");
								pageContext.setAttribute("gAdr", CmsStringUtil.escapeJavaScript(CmsStringUtil.escapeHtml(gAdr)));
							%>
							contentString += "${gAdr}";
							<c:set var="callgeocode">false</c:set>
						</c:when>
						<c:otherwise>
							contentString += "AlkaconGeoMapAddr";
							<c:set var="callgeocode">true</c:set>
						</c:otherwise>
					</c:choose>
					
					<c:if test="${map.value.Route == 'true'}">
						// add calculate route form input
						contentString += "<br/><br/><fmt:message key='geomap.route.howToGetHere' /><br/><fmt:message key='geomap.route.enterStartAddress' />"
							+ "<form action=\"http://maps.google.com/maps\" method=\"get\" target=\"_blank\">"
							+ "<input type=\"text\" size=\"15\" maxlength=\"60\" name=\"saddr\" value=\"\" />"
							+ "&nbsp;&nbsp;<input value=\"<fmt:message key='geomap.route.getDirections' />\" type=\"submit\"><input type=\"hidden\" name=\"daddr\" value=\""
							+ "${loc.lat},${loc.lng}\"/>";
					</c:if>

					infoWindow[${status.index}] = new google.maps.InfoWindow({
						content: contentString
					});

					google.maps.event.addListener(marker[${status.index}], 'click', function() {
						mapOpenInfo(${status.index});
					});

					<c:if test="${callgeocode == true}">
					geoCodeCoords(${status.index});
					</c:if>
			</c:forEach>
		</c:otherwise>
	</c:choose>

		}
		
		// tries to geocode the given map coordinate
		function geoCodeCoords(mIndex) {
			geocoder.geocode({'latLng': mapMarkerLatLng[mIndex] }, function(results, status) {
				setMapInfoWindowContent(results, status, mIndex);
			});
		}
		
		// sets the content of the specified info window
		function setMapInfoWindowContent(results, status, winIndex) {
			var infoContent = infoWindow[winIndex].getContent();
			if (status == google.maps.GeocoderStatus.OK) {
				if (results[0]) {
					infoContent = infoContent.replace(/AlkaconGeoMapAddr/, getMapInfoAddress(results[0]));
				}
			} else {
				if (status == google.maps.GeocoderStatus.OVER_QUERY_LIMIT && queryErrors <= 20) {
					setTimeout("geoCodeCoords(" + winIndex + ");", 500 + (queryErrors * 50));
					queryErrors++;
				} else {
					infoContent = infoContent.replace(/AlkaconGeoMapAddr/, "");
				}
			}
			infoWindow[winIndex].setContent(infoContent);
		}

		// returns the address from a geocode result in nicely formatted way
		function getMapInfoAddress(result) {
			var street = "";
			var strNum = "";
			var zip = "";
			var city = "";
			var foundAdr = false;
			for (var i = 0; i < result.address_components.length; i++) {
				var t = String(result.address_components[i].types);
				if (street == "" && t.indexOf("route") != -1) {
					street = result.address_components[i].long_name;
					foundAdr = true;
				}
				if (t.indexOf("street_number") != -1) {
					strNum = result.address_components[i].long_name;
					foundAdr = true;
				}
				if (t.indexOf("postal_code") != -1) {
					zip = result.address_components[i].long_name;
					foundAdr = true;
				}
				if (city == "" && t.indexOf("locality") != -1) {
					city = result.address_components[i].long_name;
					foundAdr = true;
				}
			}
			if (foundAdr == true) {
				return street + " " + strNum + "<br/>" + zip + " " + city;
			} else {
				return result.formatted_address;
			}
		}

		// open the info window for the clicked marker and close other open info windows
		function mapOpenInfo(mIndex) {
			for (var i = 0; i < marker.length; i++) {
				if (i != mIndex) {
					infoWindow[i].close();
				}
			}
			infoWindow[mIndex].open(map, marker[mIndex]);
		}
	</script>

	<h1>${map.value.Headline}</h1>

	<%-- align the text, set styles --%>
	<c:set var="mapstyle"></c:set>
	<c:choose>
		<c:when test="${map.value.TextAlign == 'top'}">
			<c:set var="mapstyle">margin-top: 8px;</c:set>
		</c:when>
		<c:when test="${map.value.TextAlign == 'bottom'}">
			<c:set var="mapstyle">margin-bottom: 8px;</c:set>
		</c:when>
		<c:when test="${map.value.TextAlign == 'left'}">
			<c:set var="mapstyle">float: right; margin-left: 5px;</c:set>
		</c:when>
		<c:when test="${map.value.TextAlign == 'right'}">
			<c:set var="mapstyle">float: left; margin-right: 5px;</c:set>
		</c:when>
	</c:choose>
	
	<c:set var="maptext">${map.value.Text}</c:set>

	<c:if test="${map.value.TextAlign == 'top'}">
		${maptext}
	</c:if>
	<div id="AlkaconGeoMapgooglemap" style="${mapw}${maph}${mapstyle}"></div>

	<c:if test="${map.value.TextAlign != 'top'}">
		${maptext}
	</c:if>
	<c:if test="${map.value.TextAlign == 'left' || map.value.TextAlign == 'right'}">
		<div style="clear: both;"></div>
	</c:if>

<script type="text/javascript">
	// show map after loading
	showGoogleMap();
</script>
</div>
</cms:formatter>


</fmt:bundle>
