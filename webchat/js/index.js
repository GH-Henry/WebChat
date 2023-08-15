const serverUrl = "ws://" + window.location.hostname + ":" + Number(Number(window.location.port) + 1);
const conn = new WebSocket(serverUrl);

const login_form = document.getElementById("login_form");
login_form.addEventListener("submit", function (event) {
  event.preventDefault();

  const msg = {
    type: "login_request",
    username: document.getElementById("login_form_username").value,
    password: document.getElementById("login_form_password").value
  };
  conn.send(JSON.stringify(msg));
  console.log("Login form sent"); //RM

  document.getElementById("login_form_username").value = "";
  document.getElementById("login_form_password").value = "";
});

const signup_form = document.getElementById("signup_form");
signup_form.addEventListener("submit", function (event) {
  event.preventDefault();

  const msg = {
    type: "signup_request",
    username: document.getElementById("signup_form_username").value,
    password: document.getElementById("signup_form_password").value
  };

  conn.send(JSON.stringify(msg));
  console.log("Signup form sent"); //RM

  document.getElementById("signup_form_username").value = "";
  document.getElementById("signup_form_password").value = "";
});

conn.onmessage = function (event) {
  let msg;
  if (event.isTrusted) {
    msg = JSON.parse(event.data);
  } else return;

  console.log("Received message:"); //RM
  console.log(msg);

  switch (msg.type) {
    case "typing_status":
      showTypingStatus(msg);
      break;
    case "msg":
      message_notif(msg);
      addMsgToChat(msg);
      break;
    case "login_success":
      login(msg);
      break;
    case "error":
      switch (msg.error_type) {
        case "user_not_found":
          alert("User doesn't exist");
          break;
        case "invalid_password":
          alert("Invalid password");
          break;
        case "duplicate_username":
          alert("Username already in use");
          break;
        case "login_failure":
          alert("Login request failed");
          break;
        case "signup_failure":
          alert("Account creation failed");
          break;
        case "invalid_request":
          alert("Invalid request");
          break;
        default:
          alert(msg.error_type);
      }
      break;
    case "ignore_list_names":
      if (msg.names.length === 0) alert("No users ignored");
      else alert(msg.names);
      break;
    case "ignore_list_ids":
      ignore_list_update(msg.ids);
      break;
    case "all_user_activity":
      alert(msg.activity);
      break;
    default:
      console.log("Unsupported message:");
      console.log(msg);
  }
}

conn.onopen = function (event) {
  //Has issue, login screen shows up in period before login registered by server,
  //  should have it wait for response.
  if (localStorage.username != undefined && localStorage.password !== undefined) {
    const msg = {
      type: "login_request",
      username: localStorage.getItem("username"),
      password: localStorage.getItem("password"),
    };
    conn.send(JSON.stringify(msg));
  }
}

conn.onclose = function (event) {
  // Perhaps send msg to chat of this user leaving
  //  Set account status as offine
}

class Client {
  constructor(uuid, username, password) {
    this.uuid = uuid;
    this.username = username;
    this.password = password;
  }
}
let client = null;

function login(msg) {
  client = new Client(
    msg.content.uuid,
    msg.content.username,
    msg.content.password
  );
  console.log("Logged in user: " + client.username); //RM

  localStorage.setItem("uuid", msg.content.uuid);
  localStorage.setItem("username", msg.content.username);
  localStorage.setItem("password", msg.content.password);

  formToChatPage();
}

function logout() {
  client = null;
  ignore_list_update([]);
  localStorage.removeItem("uuid");
  localStorage.removeItem("username");
  localStorage.removeItem("password");
  const msg = {
    type: "logout_request",
  };
  conn.send(JSON.stringify(msg));
}

//Switch between login and signup form displayed
function switchForm() {
  if (login_form.style.display === "none") {
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
msg_btn.addEventListener("click", function (event) {
  event.preventDefault();
  send_msg_request();
});

//Send msg upon enter pressed
//Also handles sending typing status messages
var time = null;
const msg_input = document.getElementById("msg_input");
msg_input.addEventListener("keydown", function (event) {
  if (event.key === "Enter") {
    event.preventDefault();
    send_msg_request();
  }
  else { //Only send typing status notif if last > 5 seconds ago
    if (time) {
      var newtime = new Date();
      if (newtime - time < 5000) return false;
      else time = new Date();
    }
    else time = new Date();

    const msg = {
      type: "typing_status",
    };

    conn.send(JSON.stringify(msg));
  }
});

//Send contents of msg input area
function send_msg_request() {
  if (msg_input.value === "") return;

  let msg;
  if (msg_input.value.startsWith("/")) {
    msg = {
      type: "cmd",
      content: msg_input.value.substring(1),
    };
  }
  else {
    msg = {
      type: "msg",
      content: msg_input.value,
    };
  }

  conn.send(JSON.stringify(msg));
  msg_input.value = "";
}

function addMsgToChat(msg) {
  if (client === null) return;

  let uuid = msg.from_id;
  let username = msg.from;
  let content = msg.content;

  let messages = document.getElementById("messages");
  let newMessage = document.createElement("div");
  newMessage.classList.add("message", uuid);

  let sender = document.createElement("div");
  sender.textContent = username;
  let msg_content = document.createElement("div");
  msg_content.textContent = content;
  newMessage.append(sender);
  newMessage.append(content);

  if (ignoreList.includes(uuid)) newMessage.style.display = "none";
  messages.append(newMessage);
}

const typing_status = document.getElementById("typing_status");
function showTypingStatus(msg) {
  if (ignoreList.includes(msg.from_id)) return;
  let from = msg.from;

  typing_status.textContent = from + " is typing...";
  typing_status.style.display = "flex";

  setTimeout(function () {
    typing_status.style.display = "none";
  }, 5000);
}

// Used to logout and switch display back to login/signup form page when 
//   username area of bottom left corner clicked
document.getElementById("username").addEventListener("click", () => {
  logout();
  chatPageToForm();
})

window.addEventListener("load", () => {
  if (Notification.permission === "default") {
    askNotificationPermission();
  }
});

// Ask for permission when the 'Enable notifications' button is clicked
function askNotificationPermission() {
  // Function to actually ask the permissions
  function handlePermission(permission) {
    // Whatever the user answers, we make sure Chrome stores the information
    if (!Reflect.has(Notification, 'permission')) {
      Notification.permission = permission;
    }
  };

  // Check if the browser supports notifications
  if (!Reflect.has(window, 'Notification')) {
    console.log('This browser does not support notifications.');
  } else {
    if (checkNotificationPromise()) {
      Notification.requestPermission().then(handlePermission);
    } else {
      Notification.requestPermission(handlePermission);
    }
  }
};

// Check whether browser supports the promise version of requestPermission()
// Safari only supports the old callback-based version
function checkNotificationPromise() {
  try {
    Notification.requestPermission().then();
  } catch (e) {
    return false;
  }
  return true;
};

let msg_notif;
function message_notif(msg) {
  if (document.visibilityState === "visible") return;
  if (Notification.permission === "granted" && !ignoreList.includes(msg.from_id)) {
    msg_notif = new Notification(`New message from ${msg.from}`, { body: msg.content });
  }
}

//Close any open notifs when tab opened
document.addEventListener("visibilitychange", () => {
  if (document.visibilityState === "visible") {
    msg_notif.close();
  }
});

let ignoreList = [];
function ignore_list_update(id_list) {
  //Set previous list to visible again
  let i, j;
  for (i = 0; i < ignoreList.length; i++) {
    let messages = document.getElementsByClassName(ignoreList[i]);
    for (j = 0; j < messages.length; j++) {
      messages[j].style.display = "block";
    }
  }
  //Update with new list, making them not visible
  ignoreList = id_list;

  for (i = 0; i < ignoreList.length; i++) {
    let messages = document.getElementsByClassName(ignoreList[i]);
    for (j = 0; j < messages.length; j++) {
      messages[j].style.display = "none";
    }
  }
}