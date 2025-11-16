(function() {
  var STORAGE_USERS = "gt_users_v1";

  function readJson(key) {
    var raw = localStorage.getItem(key);
    if (raw === null) {
      return null;
    }
    try {
      return JSON.parse(raw);
    } catch (e) {
      return null;
    }
  }

  function writeJson(key, obj) {
    localStorage.setItem(key, JSON.stringify(obj));
  }

  function ensureSeed() {
    var us = readJson(STORAGE_USERS);
    if (us === null || us.length === 0) {
      var seed = [];
      seed.push({ id: 1, name: "ivan", role: "teacher", password: "pass" });
      seed.push({ id: 2, name: "petar", role: "student", password: "p" });
      writeJson(STORAGE_USERS, seed);
    }
  }

  function getUsers() {
    var u = readJson(STORAGE_USERS);
    if (u === null) {
      return [];
    }
    return u;
  }

  function findUserByName(name) {
    var users = getUsers();
    for (var i = 0; i < users.length; i++) {
      if (users[i].name === name) {
        return users[i];
      }
    }
    return null;
  }

  function nextUserId() {
    var users = getUsers();
    var max = 0;
    for (var i = 0; i < users.length; i++) {
      if (users[i].id > max) {
        max = users[i].id;
      }
    }
    return max + 1;
  }

  function registerUser(name, role, password) {
    if (!name || !role || !password) {
      return { ok: false, message: "All fields are required." };
    }
    var rl = role.toLowerCase();
    if (rl !== "student" && rl !== "teacher") {
      return { ok: false, message: "Role must be 'student' or 'teacher'." };
    }
    var existing = findUserByName(name);
    if (existing !== null) {
      return { ok: false, message: "User already exists." };
    }
    var id = nextUserId();
    var users = getUsers();
    users.push({ id: id, name: name, role: rl, password: password });
    writeJson(STORAGE_USERS, users);
    return { ok: true, user: { id: id, name: name, role: rl } };
  }

  function loginUser(name, password) {
    if (!name || !password) {
      return null;
    }
    var u = findUserByName(name);
    if (u === null) {
      return null;
    }
    if (u.password !== password) {
      return null;
    }
    return { id: u.id, name: u.name, role: u.role };
  }

  function $(id) {
    return document.getElementById(id);
  }

  function show(element) {
    element.classList.remove("hidden");
  }

  function hide(element) {
    element.classList.add("hidden");
  }

  function bind() {
    var tabLogin = $("tab-login");
    var tabRegister = $("tab-register");
    var loginPanel = $("login-panel");
    var registerPanel = $("register-panel");
    tabLogin.addEventListener("click", function() {
      tabLogin.classList.add("active");
      tabRegister.classList.remove("active");
      show(loginPanel);
      hide(registerPanel);
    });
    tabRegister.addEventListener("click", function() {
      tabRegister.classList.add("active");
      tabLogin.classList.remove("active");
      show(registerPanel);
      hide(loginPanel);
    });

    $("btn-register").addEventListener("click", function() {
      var name = $("reg-name").value.trim();
      var pass = $("reg-password").value;
      var roleEls = document.getElementsByName("role");
      var role = null;
      for (var i = 0; i < roleEls.length; i++) {
        if (roleEls[i].checked) {
          role = roleEls[i].value;
          break;
        }
      }
      if (role === null) {
        $("reg-msg").textContent = "Please select role.";
        return;
      }
      var res = registerUser(name, role, pass);
      if (res.ok) {
        $("reg-msg").style.color = "green";
        $("reg-msg").textContent = "Registered. You may now login.";
        $("reg-name").value = "";
        $("reg-password").value = "";
        for (var i = 0; i < roleEls.length; i++) {
          roleEls[i].checked = false;
        }
      } else {
        $("reg-msg").style.color = "#b02a37";
        $("reg-msg").textContent = res.message;
      }
    });

    $("btn-login").addEventListener("click", function() {
      var name = $("login-name").value.trim();
      var pass = $("login-password").value;
      var u = loginUser(name, pass);
      if (u === null) {
        $("login-msg").textContent = "Invalid credentials.";
        return;
      }
      $("login-msg").style.color = "green";
      $("login-msg").textContent = "Credentials valid.";
    });
  }

  ensureSeed();
  bind();
})();