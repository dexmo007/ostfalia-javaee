const mapGeoBounds = [
  [52.179297884, 10.558656603], // Upper left geo-coordinate of the mapped image
  [52.178659732, 10.560134500]  // Lower right geo-coordinate of the mapped image
]

// Default Map orientation
const mapCenter = [52.178984565, 10.559414327]
const mapZoom = 19

window.onload = function() {
  const $ = function(identifier) { // simple jQuery like syntax
    var id = identifier.charAt(0)
    var name = identifier.substr(1)
    
    if(id === '.')
      return document.getElementsByClassName(name)
    else if(id == '#')
      return document.getElementById(name)
    else
      console.error("Wrong identifier")
  }
  
  var map = L.map('beaconmap').setView(mapCenter, mapZoom)
  var raumplan
  
  L.tileLayer('https://{s}.tile.thunderforest.com/cycle/{z}/{x}/{y}.png', {
    maxZoom: 30,
    maxNativeZoom: 18
  }).addTo(map)
  
  
  function loadmap(filename) {
    return function() {
      if(raumplan)
        map.removeLayer(raumplan)
      raumplan = L.imageOverlay('img/' + filename, mapGeoBounds)
      raumplan.addTo(map)
    }
  }
  
  ($('#map_eg').onclick = loadmap('floor0_eg_1.png'))()
  $('#map_1og').onclick = loadmap('floor1_1og_1.png')
  $('#map_dg').onclick = loadmap('floor2_dg_1.png')
}