let message = "";
let username = "";
let password = "";

async function login(event) {
    event.preventDefault();
    
    username = document.getElementById('username').value;
    password = document.getElementById('password').value;

    try {
        const response = await fetch("http://localhost:8080/auth/signin", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
            credentials: 'include' // Important for cookies
        });

        if (response.ok) {
            const result = await response.json();
            message = result.message || "Login erfolgreich!";
            window.location.assign("http://localhost:8080/pages/login.html"); // Redirect on success
        } else {
            const errorText = await response.text();
            message = `Fehler: ${errorText}`;
            document.getElementById('errorMessage').textContent = message;
        }
    } catch (e) {
        message = `Fehler beim Verbinden mit dem Server: ${e.message}`;
        document.getElementById('errorMessage').textContent = message;
    }
}

// Attach to form submit
document.getElementById('login-form').addEventListener('click', login);

// Logout function
function logoutUser() {
    fetch('/auth/logout', {
        method: 'POST',
        credentials: 'include'
    })
    .then(() => {
        window.location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

// Check if user is authenticated
function checkAuthStatus() {
    fetch('/auth', {
        credentials: 'include' // Important for cookies
    })
    .then(response => response.json())
    .then(data => {
        if (data.authenticated) {
            showLoggedInView(data.username);
            console.log("It checked successfully");
        } else {
            showLoggedOutView();
        }
    })
    .catch(() => showLoggedOutView());
}

// Show the appropriate view
function showLoggedInView(username) {
    document.querySelector('.logged-out-view').style.display = 'none';
    const loggedInView = document.querySelector('.logged-in-view');
    loggedInView.style.display = 'block';
    document.getElementById('usernameDisplay').textContent = username || 'User';
}

function showLoggedOutView() {
    document.querySelector('.logged-in-view').style.display = 'none';
    document.querySelector('.logged-out-view').style.display = 'block';
}