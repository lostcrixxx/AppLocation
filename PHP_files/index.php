<!DOCTYPE html >
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>App Location Mobile</title>
    <style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map {
        height: 100%;
      }
      /* Optional: Makes the sample page fill the window. */
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
    </style>
  </head>

  <body>
    <div id="map"></div>

    <script>
    var point2;
    
    // Tipos de Points
      var customLabel = {
        restaurant: {
          label: 'R'
        },
        bar: {
          label: 'B'
        }
      };
      
     
        // var latlngbounds = new google.maps.LatLngBounds(); new google.maps.LatLng(-23.5489, -46.638823),          zoom: 11
        function initMap() {

        var map = new google.maps.Map(document.getElementById('map'), {
        // Tipo de Mapa
        mapTypeId: google.maps.MapTypeId.ROADMAP
          //center: new google.maps.LatLng(-23.5489, -46.638823),
          //zoom: 11
        });
        
        // Retorna os limites lat / lng para dar o zoom 
        var bounds = new google.maps.LatLngBounds();
        
        var infoWindow = new google.maps.InfoWindow;
        
          // Retornar todos os valores do arquivo XML, gerado pala página PHP
          downloadUrl('resultado.php', function(data) {
            var xml = data.responseXML;
            var markers = xml.documentElement.getElementsByTagName('marker');
            Array.prototype.forEach.call(markers, function(markerElem) {
            
            // Titulo do local
              var name = markerElem.getAttribute('name');
              // Endereço conforme sua localização
              var address = markerElem.getAttribute('address');
              // Tipo de Point
              var type = markerElem.getAttribute('type');
              // Point
              var point = new google.maps.LatLng(
                  parseFloat(markerElem.getAttribute('lat')),
                  parseFloat(markerElem.getAttribute('lng')));
                  
              var infowincontent = document.createElement('div');
              var strong = document.createElement('strong');
              strong.textContent = name
              infowincontent.appendChild(strong);
              infowincontent.appendChild(document.createElement('br'));

              var text = document.createElement('text');
              text.textContent = address
              infowincontent.appendChild(text);
              var icon = customLabel[type] || {};
              

         
            // Adicionando Point
              var marker = new google.maps.Marker({
                map: map,
                position: point,
                label: icon.label
              });
              
              bounds.extend(marker.position);
              
              // Exibe informações ao clicar no Point
              // marker.addListener('click', function() {
              //  infoWindow.setContent(infowincontent);
              //  infoWindow.open(map, marker);
              // });
              
              
              map.fitBounds(bounds);
              
             
    
            });
            
           /*
           
           // Adicionar PolyLine
           var flightPlanCoordinates = [
                   {lat: -23.40888125, lng: -46.75347317},
                   {lat: -23.40644984, lng: -46.75463795}
            ];


          var flightPath = new google.maps.Polyline({
           path: flightPlanCoordinates,
           geodesic: true,
           strokeColor: '#FF0000',
           strokeOpacity: 1.0,
           strokeWeight: 2
           
         });
         flightPath.setMap(map);       
         
         */
    
         }); // FIM consulta XML
         


        
        } // FIM initMap


      function downloadUrl(url, callback) {
        var request = window.ActiveXObject ?
            new ActiveXObject('Microsoft.XMLHTTP') :
            new XMLHttpRequest;

        request.onreadystatechange = function() {
          if (request.readyState == 4) {
            request.onreadystatechange = doNothing;
            callback(request, request.status);
          }
        };

        request.open('GET', url, true);
        request.send(null);
      }

      function doNothing() {}
    
    /*
    
      function initMap() {

        /// Traçar rota
        //var service = new google.maps.DirectionsService;
        
        var map = new google.maps.Map(document.getElementById('map'), {
        // Tipo de Mapa
        mapTypeId: google.maps.MapTypeId.ROADMAP
          //center: new google.maps.LatLng(-23.5489, -46.638823),
          //zoom: 11
        });
        
        // Retorna os limites lat / lng para dar o zoom 
        var bounds = new google.maps.LatLngBounds();
        
        var infoWindow = new google.maps.InfoWindow;
     
          // Retornar todos os valores do arquivo XML, gerado pala página PHP
             downloadUrl('resultado.php', function(data) {
            var xml = data.responseXML;
            var markers = xml.documentElement.getElementsByTagName('marker');

            Array.prototype.forEach.call(markers, function(markerElem) {
            
            // Titulo do local
              var name = markerElem.getAttribute('name');
              // Endereço conforme sua localização
              var address = markerElem.getAttribute('address');
              // Tipo de Point
              var type = markerElem.getAttribute('type');
              // Point
              var response = new google.maps.LatLng(
                  parseFloat(markerElem.getAttribute('lat')),
                  parseFloat(markerElem.getAttribute('lng')));
                  
  var directionsService = new google.maps.DirectionsService;
  var directionsDisplay = new google.maps.DirectionsRenderer({
    suppressInfoWindows: true,
    suppressMarkers: true
  });


  var response = {
    "ret-abcd": {
      "points": [
        [-23.40888125, -46.75347317, "2016-07-18 07:29:38 - 28.529400 - 77.250200"],
        [-23.40644984, -46.75463795, "2016-07-18 07:43:59 - 28.554100 - 77.263700"],
        [-23.40616076, -46.75497414, "2016-07-18 07:46:08 - 28.563500 - 77.264800"],
        [-23.40685769, -46.7621076, "2016-07-18 07:48:07 - 28.572100 - 77.257900"],
        [-23.42524459, -46.78758072, "2016-07-18 07:49:14 - 28.574600 - 77.260800"],
        [-23.4414467, -46.78598004, "2016-07-18 07:51:16 - 28.578700 - 77.281300"],
        [-23.48058876, -46.7705811, "2016-07-18 07:52:39 - 28.573100 - 77.308600"],
        [-23.50806518, -46.73550222, "2016-07-18 07:58:00 - 28.590200 - 77.336000"],
        [-23.51960983, -46.70196814, "2016-07-18 08:49:47 - 28.625200 - 77.373500"]
      ]
    }
  };
  

  var timeout = 100;
  var m = 0;
  var cnt = 0;
  var markers = [];
  var combinedResults;
  var directionsResultsReturned = 0;
  var linecolors = ['red', 'blue', 'green', 'yellow'];
  var colorIdx = 0;
  var dd = [];

  for (key in response) {
    if (response[key].points.length > 0) {
      var blocks = [];
      var k = 0;
      for (var i = 0; i < response[key].points.length; i++) {
        if (i != 0 && i % 10 == 0) {
          k++;
        }
        //console.log(k);
        if (typeof blocks[k] == 'undefined') {
          blocks[k] = [];
        }

        blocks[k].push(response[key].points[i]);
      }

      ds = new google.maps.DirectionsService;

      for (i = 0; i < blocks.length; i++) {

        waypts = [];
        markers.push([blocks[i][0][0], blocks[i][0][1], blocks[i][0][2]]);
        for (var j = 1; j < blocks[i].length - 1; j++) {
          waypts.push({
            location: blocks[i][j][0] + ',' + blocks[i][j][1],
            stopover: true
          });
          //var myLatlng = new google.maps.LatLng(blocks[i][j][0],blocks[i][j][1]);
          markers.push([blocks[i][j][0], blocks[i][j][1], blocks[i][j][2]]);

        }
        markers.push([blocks[i][blocks[i].length - 1][0], blocks[i][blocks[i].length - 1][1], blocks[i][blocks[i].length - 1][2]]);
        //data.start[0]+','+data.start[1],
        //ds[m].route({   

        ds.route({
            'origin': blocks[i][0][0] + ',' + blocks[i][0][1],
            'destination': blocks[i][blocks[i].length - 1][0] + ',' + blocks[i][blocks[i].length - 1][1],
            'waypoints': waypts,
            'travelMode': 'DRIVING'
          },
          function(directions, status) {
            dd.push(new google.maps.DirectionsRenderer({
              suppressInfoWindows: true,
              suppressMarkers: true,
              //polylineOptions: {
              //  strokeColor: linecolors[colorIdx++ % 3]
              //},
              map: map
            }));

            if (status == google.maps.DirectionsStatus.OK) {
              dd[dd.length - 1].setDirections(directions);
            }
          }
        );

      }
    }

    for (h = 0; h < markers.length; h++) {
      createMapMarker(map, new google.maps.LatLng(markers[h][0], markers[h][1]), markers[h][2], "", "");
    }
    cnt++;

  }



function createMapMarker(map, latlng, label, html, sign) {
  var marker = new google.maps.Marker({
    position: latlng,
    map: map,
    icon: "http://www.google.com/mapfiles/marker" + sign + ".png",
    title: label,
  });

  marker.myname = label;


  return marker;
}
      /*

function initMap() {
  map = new google.maps.Map(document.getElementById('map'), {
    zoom: 14,
    center: {
      lat: 28.6247,
      lng: 77.3731
    },
    disableDefaultUI: true,
  });

  var directionsService = new google.maps.DirectionsService;
  var directionsDisplay = new google.maps.DirectionsRenderer({
    suppressInfoWindows: true,
    suppressMarkers: true
  });


  var response = {
    "ret-abcd": {
      "points": [
        [-23.40888125, -46.75347317, "2016-07-18 07:29:38 - 28.529400 - 77.250200"],
        [-23.40644984, -46.75463795, "2016-07-18 07:43:59 - 28.554100 - 77.263700"],
        [-23.40616076, -46.75497414, "2016-07-18 07:46:08 - 28.563500 - 77.264800"],
        [-23.40685769, -46.7621076, "2016-07-18 07:48:07 - 28.572100 - 77.257900"],
        [-23.42524459, -46.78758072, "2016-07-18 07:49:14 - 28.574600 - 77.260800"],
        [-23.4414467, -46.78598004, "2016-07-18 07:51:16 - 28.578700 - 77.281300"],
        [-23.48058876, -46.7705811, "2016-07-18 07:52:39 - 28.573100 - 77.308600"],
        [-23.50806518, -46.73550222, "2016-07-18 07:58:00 - 28.590200 - 77.336000"],
        [-23.51960983, -46.70196814, "2016-07-18 08:49:47 - 28.625200 - 77.373500"]
      ]
    }
  };
  

  var timeout = 100;
  var m = 0;
  var cnt = 0;
  var markers = [];
  var combinedResults;
  var directionsResultsReturned = 0;
  var linecolors = ['red', 'blue', 'green', 'yellow'];
  var colorIdx = 0;
  var dd = [];

  for (key in response) {
    if (response[key].points.length > 0) {
      var blocks = [];
      var k = 0;
      for (var i = 0; i < response[key].points.length; i++) {
        if (i != 0 && i % 10 == 0) {
          k++;
        }
        //console.log(k);
        if (typeof blocks[k] == 'undefined') {
          blocks[k] = [];
        }

        blocks[k].push(response[key].points[i]);
      }

      ds = new google.maps.DirectionsService;

      for (i = 0; i < blocks.length; i++) {

        waypts = [];
        markers.push([blocks[i][0][0], blocks[i][0][1], blocks[i][0][2]]);
        for (var j = 1; j < blocks[i].length - 1; j++) {
          waypts.push({
            location: blocks[i][j][0] + ',' + blocks[i][j][1],
            stopover: true
          });
          //var myLatlng = new google.maps.LatLng(blocks[i][j][0],blocks[i][j][1]);
          markers.push([blocks[i][j][0], blocks[i][j][1], blocks[i][j][2]]);

        }
        markers.push([blocks[i][blocks[i].length - 1][0], blocks[i][blocks[i].length - 1][1], blocks[i][blocks[i].length - 1][2]]);
        //data.start[0]+','+data.start[1],
        //ds[m].route({   

        ds.route({
            'origin': blocks[i][0][0] + ',' + blocks[i][0][1],
            'destination': blocks[i][blocks[i].length - 1][0] + ',' + blocks[i][blocks[i].length - 1][1],
            'waypoints': waypts,
            'travelMode': 'DRIVING'
          },
          function(directions, status) {
            dd.push(new google.maps.DirectionsRenderer({
              suppressInfoWindows: true,
              suppressMarkers: true,
              //polylineOptions: {
              //  strokeColor: linecolors[colorIdx++ % 3]
              //},
              map: map
            }));

            if (status == google.maps.DirectionsStatus.OK) {
              dd[dd.length - 1].setDirections(directions);
            }
          }
        );

      }
    }

    for (h = 0; h < markers.length; h++) {
      createMapMarker(map, new google.maps.LatLng(markers[h][0], markers[h][1]), markers[h][2], "", "");
    }
    cnt++;

  }
}


function createMapMarker(map, latlng, label, html, sign) {
  var marker = new google.maps.Marker({
    position: latlng,
    map: map,
    icon: "http://www.google.com/mapfiles/marker" + sign + ".png",
    title: label,
  });

  marker.myname = label;


  return marker;
}

*/

//google.maps.event.addDomListener(window, "load", initMap);

      //function doNothing() {}
    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDH0elODdv85HtYSH6Xai2Npc9qHCWIEuQ&callback=initMap">
    // GoogleMaps AIzaSyA0T4F8wuvZCxoFf6ztyOoyAaKCnauDr8E
    // API Directions AIzaSyDH0elODdv85HtYSH6Xai2Npc9qHCWIEuQ
    </script>
  </body>
</html>