var connection = null;

var serverUrl = "ws://" + window.location.hostname + ":8081";
      
// Create the connection with the server
connection = new WebSocket(serverUrl);

connection.onopen = function (evt) {
  console.log("open");
}
connection.onclose = function (evt) {
  console.log("close");
  document.getElementById("topMessage").innerHTML = "Server Offline";
  document.getElementById("secondMessage").innerHTML = "";
}

connection.onmessage = function (evt) {
  var msg;
  msg = evt.data;

  console.log("Message received: " + msg);
  document.getElementById("topMessage").innerHTML = msg;
  var obj = JSON.parse(msg);

  if( "client_id" in obj) {
    document.getElementById("secondMessage").innerHTML = obj.client_id;
  }       
}