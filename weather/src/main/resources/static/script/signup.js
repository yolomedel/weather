let message = "";
let email = "";
let password = "";
let username = "";
let role = ["user"];

async function handleSignup(event) {
    event.preventDefault();
    
    email = document.getElementById('email').value;
    password = document.getElementById('password').value;
    username = document.getElementById('username').value;

    try {
        const response = await fetch("http://localhost:8080/auth/signup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password, username, role }),
        });

        if (response.ok) {
            message = "Registrierung erfolgreich!";
            // Show success message and redirect
            alert(message);
            window.location.assign("http://localhost:8080/pages/login.html"); 
        } else {
            const error = await response.text();
            message = `Fehler: ${error}`;
            document.getElementById('errorMessage').textContent = message;
        }
    } catch (e) {
        message = `Verbindungsfehler: ${e.message}`;
        document.getElementById('errorMessage').textContent = message;
    }
}

// Attach to form submission
document.getElementById('signup-form').addEventListener('click', handleSignup);