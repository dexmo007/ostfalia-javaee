const mapGeoBounds = [
  [52.179297884, 10.558656603], // Upper left geo-coordinate of the mapped image
  [52.178659732, 10.560134500]  // Lower right geo-coordinate of the mapped image
]

// Default Map orientation
const mapCenter = [52.178984565, 10.559414327]
const mapZoom = 19

window.onload = function() {
  function Beacon(elem) {
		if(elem) {
	    [
	      this.id,
	      this.longitude,
	      this.latitude,
	      this.height,
	      this.name,
	      this.room
	    ] = Array.from(elem.children).map(td => td.textContent)
		}
		
		Object.defineProperty(this, 'pos', {
			get: () => [this.longitude, this.latitude],
		 	set: pos => [this.longitude, this.latitude] = [pos.lat, pos.lng]
		})
		
		this.show = () => {
			// Set beacon values to the sidebar
			var values = [this.id, this.longitude, this.latitude, this.height, this.name, this.room]
			for(var i = 0; i < values.length; i++)
				$('.sidebar .item input:eq(' + i + ')').val(values[i])
			
			sidebar.sidebar('toggle')
		}
  }
	
	var sidebar = $('.sidebar').sidebar('setting', 'transition', 'overlay');
  var map = L.map('beaconmap').setView(mapCenter, mapZoom)
  var raumplan
  
  L.tileLayer('https://{s}.tile.thunderforest.com/cycle/{z}/{x}/{y}.png', {
    maxZoom: 30,
    maxNativeZoom: 18
  }).addTo(map)
  
  
  function loadmap(filename) {
    if(raumplan)
      map.removeLayer(raumplan)
    raumplan = L.imageOverlay('img/' + filename, mapGeoBounds)
    raumplan.addTo(map)
  }
	
	$('#roombuttons').dropdown({
		onChange: value => loadmap(value + '.png')
	})
  loadmap('floor0_eg_1.png')
  
  // Place all Beacons
	var beaconlist = new Array()
  $('#beaconlist').children().each(function() {
		console.log(this)
		var beacon = new Beacon(this)
		L.marker(beacon.pos).addTo(map)
		beaconlist.push(beacon)
	})
	
	$('#addbeacon').on('click', () => {
		// Draggable marker
		var dragme = L.marker([0, 0]).addTo(map)
		var moveMarker = e => dragme.setLatLng(e.latlng)
		$('#beaconmap .leaflet-map-pane').addClass('hidecursor')
		map.on('mousemove', moveMarker)
		map.once('click', e => {
			map.off('mousemove', moveMarker)
			$('#beaconmap .leaflet-map-pane').removeClass('hidecursor')
			
			var beacon = new Beacon()
			beacon.pos = dragme.getLatLng()
			beacon.show()
		})
	}).popup()
}