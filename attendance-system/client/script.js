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
  } catch (error) {
    document.getElementById("error").textContent = "Invalid username or password";
  }
});

