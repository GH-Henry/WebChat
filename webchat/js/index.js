var serverUrl = "ws://" + window.location.hostname + ":8081";
var conn = new WebSocket(serverUrl);

const login_form = document.getElementById("login_form");
login_form.addEventListener("submit", function(event) {
  event.preventDefault();

  const msg = {
    type: "login_request",
    username: document.getElementById("login_form_username").value,
    password: document.getElementById("login_form_password").value,
  }
  conn.send(JSON.stringify(msg));
  console.log("Login form sent"); //RM

  document.getElementById("login_form_username").value = "";
  document.getElementById("login_form_password").value = "";
});

const signup_form = document.getElementById("signup_form");
signup_form.addEventListener("submit", function(event) {
  event.preventDefault();

  const msg = {
    type: "signup_request",
    username: document.getElementById("signup_form_username").value,
    password: document.getElementById("signup_form_password").value,
  }

  conn.send(JSON.stringify(msg));
  console.log("Signup form sent"); //RM

  document.getElementById("signup_form_username").value = "";
  document.getElementById("signup_form_password").value = "";
});

conn.onopen = function(event) {
  //Perhaps implement check for login credentials in browser storage and auto login

}

conn.onmessage = function(event) {
  let msg;
  if(event.isTrusted) {
    msg = JSON.parse(event.data);
  } else return;

  console.log("Received message:"); //RM
  console.log(msg);

  if(msg.type === "typing_status") { //TODO Implement
    showTypingStatus(msg);
  }
  else if (msg.type === "msg") {
    addMsgToChat(msg);
  }
  else if(msg.type === "login_success") {
    login(msg);
  }
  else if(msg.type === "error") {
    if(msg.error_type === "user_not_found") { //Upon login w/ non-existing username
      alert("User doesn't exist");
    }
    else if(msg.error_type === "invalid_password") { // Upon login w/ wrong pwd
      alert("Invalid password");
    }
    else if(msg.error_type === "duplicate_username") { // Upon account creation w/ existing username
      alert("Username already in use");
    }
    else if(msg.error_type === "login_failure") { // General error catching in login
      alert("Login request failed");
    }
    else if(msg.error_type === "signup_failure") { // General error catching in account creation
      alert("Account creation failed");
    }
    else if(msg.error_type === "invalid_request") { // Upon msg request by not logged in user
      alert("Invalid request");
    }
  }
  else {
    console.log("Unsupported message:");
    console.log(msg);
  }
}

conn.onclose = function(event) {
  // Perhaps send msg to chat of this user leaving
  //  Set account status as offine
}

class Client {
  constructor(username, password) {
    this.username = username;
    this.password = password;
  }
}
var client = null;

// var open_chat = document.getElementById("open_chat");

function login(msg) {
  client = new Client(
    msg.content.username,
    msg.content.password
  );
  console.log("Logged in user: " + client.username); //RM

  formToChatPage();
}

function logout() {
  client = null;

}

//Switch between login and signup form displayed
function switchForm() {
  if(login_form.style.display === "none") {
    document.title = "Login";
    login_form.style.display = "block";
    signup_form.style.display = "none";
  }
  else {
    document.title = "Signup";
    login_form.style.display = "none";
    signup_form.style.display = "block";
  }
}

function formToChatPage() {
  document.getElementById("form_container").style.display = "none";
  document.title = "Chat";
  document.getElementById("username").textContent = client.username;

  document.getElementById("chat_container").style.display = "flex";
  document.getElementById("msg_input").style.display = "flex";
}

function chatPageToForm() {
  document.getElementById("form_container").style.display = "flex";
  document.title = "Login";
  document.getElementById("username").textContent = "";

  document.getElementById("chat_container").style.display = "none";
  document.getElementById("msg_input").style.display = "none";
}

//Send message upon send button clicked
const msg_btn = document.getElementById("msg_btn");
msg_btn.addEventListener("click", function(event) {
  event.preventDefault();
  send_msg_request();
});

//Send msg upon enter pressed
//Also handles sending typing status messages
var time = null;
const msg_input = document.getElementById("msg_input");
msg_input.addEventListener("keydown", function(event) {
  if(event.key === "Enter") {
    event.preventDefault();
    send_msg_request();
  }
  else { //Only send typing status notif if last > 5 seconds ago
    if (time) {
      var newtime = new Date();
      if(newtime - time < 5000) return false;
      else time = new Date();
    }
    else time = new Date();
    
    const msg = {
      type: "typing_status"
    }

    conn.send(JSON.stringify(msg));
  }
});

//Send contents of msg input area
function send_msg_request() {
  if(msg_input.value === "") return;
  const msg = {
    type: "msg",
    content: msg_input.value
  };

  conn.send(JSON.stringify(msg));
  msg_input.value = "";
}

function addMsgToChat(msg) {
  var username = msg.from;
  var content = msg.content;

  if(content === "") return;

  var messages = document.getElementById("messages");
  var newMessage = document.createElement("div");
  newMessage.classList.add("message");

  var sender = document.createElement("div");
  sender.textContent = username;
  var msg_content = document.createElement("div");
  msg_content.textContent = content;
  newMessage.append(sender);
  newMessage.append(content);

  messages.append(newMessage);
}

const typing_status = document.getElementById("typing_status");
function showTypingStatus(msg) {
  var from = msg.from;

  typing_status.textContent = from + " is typing...";
  typing_status.style.display = "flex";

  setTimeout(function() {
    typing_status.style.display = "none";
  },5000);
}

document.getElementById("username").addEventListener("click", function(event) {
  chatPageToForm();
})