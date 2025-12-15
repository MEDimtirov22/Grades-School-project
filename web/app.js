(function() {
  var STORAGE_USERS = "gt_users_v1";
  var STORAGE_GRADES = "gt_grades_v1";
  var CURRENT_USER = null;

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
    var gs = readJson(STORAGE_GRADES);
    if (gs === null) {
      var seedG = [];
      seedG.push({ id: 1, studentId: 2, course: "Mathematics", value: 5.5 });
      writeJson(STORAGE_GRADES, seedG);
    }
  }

  function getUsers() {
    var u = readJson(STORAGE_USERS);
    if (u === null) {
      return [];
    }
    return u;
  }

  function getGrades() {
    var g = readJson(STORAGE_GRADES);
    if (g === null) {
      return [];
    }
    return g;
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

  function findUserById(id) {
    var users = getUsers();
    for (var i = 0; i < users.length; i++) {
      if (users[i].id === id) {
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

  function nextGradeId() {
    var grades = getGrades();
    var max = 0;
    for (var i = 0; i < grades.length; i++) {
      if (grades[i].id > max) {
        max = grades[i].id;
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

  function addGradeFor(studentName, course, value) {
    if (!studentName || !course) {
      return { ok: false, message: "Student name and course required." };
    }
    var student = findUserByName(studentName);
    if (student === null) {
      return { ok: false, message: "Student not found." };
    }
    if (student.role !== "student") {
      return { ok: false, message: "Provided user is not a student." };
    }
    var gid = nextGradeId();
    var grades = getGrades();
    grades.push({ id: gid, studentId: student.id, course: course, value: Number(value) });
    writeJson(STORAGE_GRADES, grades);
    return { ok: true };
  }

  function updateGrade(id, studentName, course, value) {
    var grades = getGrades();
    var found = false;
    for (var i = 0; i < grades.length; i++) {
      if (grades[i].id === id) {
        found = true;
        var student = findUserByName(studentName);
        if (student === null) {
          return { ok: false, message: "Student not found." };
        }
        if (student.role !== "student") {
          return { ok: false, message: "Provided user is not a student." };
        }
        grades[i].studentId = student.id;
        grades[i].course = course;
        grades[i].value = Number(value);
        break;
      }
    }
    if (!found) {
      return { ok: false, message: "Grade not found." };
    }
    writeJson(STORAGE_GRADES, grades);
    return { ok: true };
  }

  function deleteGrade(id) {
    var grades = getGrades();
    var idx = -1;
    for (var i = 0; i < grades.length; i++) {
      if (grades[i].id === id) {
        idx = i;
        break;
      }
    }
    if (idx === -1) {
      return { ok: false, message: "Grade not found." };
    }
    grades.splice(idx, 1);
    writeJson(STORAGE_GRADES, grades);
    return { ok: true };
  }

  function searchGrades(studentPart, coursePart) {
    var grades = getGrades();
    var users = getUsers();
    var sp = (studentPart || "").toLowerCase();
    var cp = (coursePart || "").toLowerCase();
    var out = [];
    for (var i = 0; i < grades.length; i++) {
      var g = grades[i];
      var studentName = "unknown";
      for (var j = 0; j < users.length; j++) {
        if (users[j].id === g.studentId) {
          studentName = users[j].name;
          break;
        }
      }
      var sm = sp === "" || studentName.toLowerCase().indexOf(sp) !== -1;
      var cm = cp === "" || g.course.toLowerCase().indexOf(cp) !== -1;
      if (sm && cm) {
        out.push({ grade: g, studentName: studentName });
      }
    }
    return out;
  }

  function computeAverages() {
    var grades = getGrades();
    var map = {};
    for (var i = 0; i < grades.length; i++) {
      var g = grades[i];
      if (!map[g.course]) {
        map[g.course] = { sum: 0, count: 0 };
      }
      map[g.course].sum = map[g.course].sum + Number(g.value);
      map[g.course].count = map[g.course].count + 1;
    }
    var out = [];
    for (var k in map) {
      if (map.hasOwnProperty(k)) {
        var avg = 0;
        if (map[k].count > 0) {
          avg = map[k].sum / map[k].count;
        }
        out.push({ course: k, average: avg });
      }
    }
    return out;
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

  function clearChildren(el) {
    while (el.firstChild) {
      el.removeChild(el.firstChild);
    }
  }

  function renderAverages() {
    var area = $("averages-area");
    clearChildren(area);
    var list = computeAverages();
    if (list.length === 0) {
      area.textContent = "No grades yet.";
      return;
    }
    for (var i = 0; i < list.length; i++) {
      var it = document.createElement("div");
      it.className = "result-item";
      it.textContent = list[i].course + " => " + list[i].average.toFixed(2);
      area.appendChild(it);
    }
  }

  function renderResults(items) {
    var area = $("results-area");
    clearChildren(area);
    if (items.length === 0) {
      area.textContent = "No results.";
      return;
    }
    for (var i = 0; i < items.length; i++) {
      (function() {
        var idx = i;
        var el = document.createElement("div");
        el.className = "result-item";

        var left = document.createElement("div");
        left.className = "result-left";
        var grade = items[idx].grade;
        left.textContent = items[idx].studentName + " | " + grade.course + " = " + grade.value;
        el.appendChild(left);

        if (CURRENT_USER !== null && CURRENT_USER.role === "teacher") {
          var actions = document.createElement("div");
          actions.className = "result-actions";

          var btnEdit = document.createElement("button");
          btnEdit.className = "btn-small btn-edit";
          btnEdit.textContent = "Edit";
          btnEdit.addEventListener("click", function() {
            startInlineEdit(el, items[idx]);
          });
          actions.appendChild(btnEdit);

          var btnDelete = document.createElement("button");
          btnDelete.className = "btn-small btn-delete";
          btnDelete.textContent = "Delete";
          btnDelete.addEventListener("click", function() {
            var ok = window.confirm("Delete this grade?");
            if (!ok) {
              return;
            }
            var res = deleteGrade(grade.id);
            if (res.ok) {
              renderAverages();
              var all = searchGrades("", "");
              renderResults(all);
            } else {
              window.alert("Delete failed: " + res.message);
            }
          });
          actions.appendChild(btnDelete);

          el.appendChild(actions);
        }

        area.appendChild(el);
      })();
    }
  }

  function startInlineEdit(containerEl, item) {
    clearChildren(containerEl);
    var grade = item.grade;
    var currentStudentName = item.studentName;
    var currentCourse = grade.course;
    var currentValue = grade.value;

    var form = document.createElement("div");
    form.className = "inline-edit";

    var inStudent = document.createElement("input");
    inStudent.type = "text";
    inStudent.value = currentStudentName;
    inStudent.className = "edit-student";
    form.appendChild(inStudent);

    var inCourse = document.createElement("input");
    inCourse.type = "text";
    inCourse.value = currentCourse;
    inCourse.className = "edit-course";
    form.appendChild(inCourse);

    var inValue = document.createElement("input");
    inValue.type = "number";
    inValue.step = "0.1";
    inValue.min = "2";
    inValue.max = "6";
    inValue.value = currentValue;
    inValue.className = "edit-value";
    form.appendChild(inValue);

    var saveBtn = document.createElement("button");
    saveBtn.className = "btn-small btn-save";
    saveBtn.textContent = "Save";
    form.appendChild(saveBtn);

    var cancelBtn = document.createElement("button");
    cancelBtn.className = "btn-small btn-cancel";
    cancelBtn.textContent = "Cancel";
    form.appendChild(cancelBtn);

    containerEl.appendChild(form);

    var msg = document.createElement("div");
    msg.className = "inline-msg";
    containerEl.appendChild(msg);

    saveBtn.addEventListener("click", function() {
      var newStudent = inStudent.value.trim();
      var newCourse = inCourse.value.trim();
      var newValue = Number(inValue.value);
      if (!newStudent || !newCourse || isNaN(newValue)) {
        msg.textContent = "All fields are required and value must be a number.";
        return;
      }
      var res = updateGrade(grade.id, newStudent, newCourse, newValue);
      if (res.ok) {
        renderAverages();
        var all = searchGrades("", "");
        renderResults(all);
      } else {
        msg.textContent = res.message;
      }
    });

    cancelBtn.addEventListener("click", function() {
      var all = searchGrades("", "");
      renderResults(all);
    });
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
      startAppFor(u);
    });

    $("btn-logout").addEventListener("click", function() {
      show($("auth"));
      hide($("app"));
      $("login-name").value = "";
      $("login-password").value = "";
      $("login-msg").textContent = "";
      $("reg-msg").textContent = "";
      CURRENT_USER = null;
    });

    $("btn-add-grade").addEventListener("click", function() {
      var studentName = $("grade-student").value.trim();
      var course = $("grade-course").value.trim();
      var value = $("grade-value").value;
      var res = addGradeFor(studentName, course, value);
      if (res.ok) {
        $("addgrade-msg").style.color = "green";
        $("addgrade-msg").textContent = "Grade added.";
        $("grade-student").value = "";
        $("grade-course").value = "";
        $("grade-value").value = "";
        renderAverages();
        var all = searchGrades("", "");
        renderResults(all);
      } else {
        $("addgrade-msg").style.color = "#b02a37";
        $("addgrade-msg").textContent = res.message;
      }
    });

    $("btn-search").addEventListener("click", function() {
      var sp = $("search-student").value.trim();
      var cp = $("search-course").value.trim();
      var items = searchGrades(sp, cp);
      renderResults(items);
    });

    $("btn-list-all").addEventListener("click", function() {
      var items = searchGrades("", "");
      renderResults(items);
    });
  }

  function startAppFor(user) {
    CURRENT_USER = user;
    hide($("auth"));
    show($("app"));
    $("login-msg").textContent = "";
    $("reg-msg").textContent = "";
    $("welcome").textContent = "Welcome, " + user.name + " (" + user.role + ")";
    if (user.role === "teacher") {
      show($("teacher-controls"));
    } else {
      hide($("teacher-controls"));
    }
    renderAverages();
    var items = searchGrades("", "");
    renderResults(items);
  }

  ensureSeed();
  bind();
})();