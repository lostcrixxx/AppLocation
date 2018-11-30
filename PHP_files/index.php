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
        
        /* ERRO no PolyLine
          downloadUrl('resultado.php', function(data) {
                  var xml = data.responseXML; 
                  var markers = xml.documentElement.getElementsByTagName("marker"); 
                  var points = []; for (var i = 0; i < markers.length; i++) { 
                  points.push(new google.maps.LatLng(parseFloat(markers[i].getAttribute("lat")), 
                  parseFloat(markers[i].getAttribute("lng")))); } 
                  var polyline = new GPolyline(points, '#ff0000', 5, 0.7); 
                  map.addOverlay(polyline); 
         });

*/

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
            
                
           var flightPlanCoordinates = [
                   {lat: , lng: },
                   {lat: , lng: },
                   {lat: , lng: },
                   {lat: , lng: }
            ];


          var flightPath = new google.maps.Polyline({
           path: flightPlanCoordinates,
           geodesic: true,
           strokeColor: '#FF0000',
           strokeOpacity: 1.0,
           strokeWeight: 2
           
         });
         flightPath.setMap(map);
        
         
         
    
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
    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=&callback=initMap">
    </script>
  </body>
</html>