// script.js

// Handle login form submission
document.getElementById("loginForm")?.addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const response = await fetch(
        `http://localhost:8080/api/auth/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      console.log(response)

    if (!response.ok) {
      throw new Error("Invalid credentials");
    }

    const data = await response.json();

console.log(data)
    localStorage.setItem("token", data.token); // Store the JWT token
    window.location.href = "home.html"; // Redirect to the home page
  } catch (error) {
    document.getElementById("error").textContent = "Invalid username or password";
  }
});

// Handle logout button click
document.getElementById("logoutButton")?.addEventListener("click", () => {
    localStorage.removeItem("token"); // Remove the JWT token
    console.log("User logged out.");
    window.location.href = "login.html"; // Redirect to the login page
  });

  // Check if the user is authenticated on the home page
if (window.location.pathname.endsWith("home.html")) {
    const token = localStorage.getItem("token");
    console.log("Token:", token);
  
    if (!token) {
      console.log("No token found. Redirecting to login page.");
      window.location.href = "login.html"; // Redirect to login if no token is found
    }
  }

  // Check if the user is already logged in on the login page
if (window.location.pathname.endsWith("login.html")) {
    const token = localStorage.getItem("token");
    console.log("Token:", token);
  
    if (token) {
      console.log("User is already logged in. Redirecting to home page.");
      window.location.href = "home.html"; // Redirect to home if a token is found
    }
  }