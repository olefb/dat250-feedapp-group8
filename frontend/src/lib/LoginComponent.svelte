<script>
    import { authStore } from "./authStore.js";
    import { apiUrl, ENDPOINTS } from "./config.js";
    
    let email = $state("");
    let password = $state("");
    let loading = $state(false);
    let error = $state("");

    async function login(event) {
        event.preventDefault();
        if (!email.trim() || !password.trim()) {
            error = "Both email and password are required.";
            return;
        }

        loading = true;
        error = "";

        try {
            const response = await fetch(apiUrl(ENDPOINTS.LOGIN), {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username: email.trim(),
                    password: password.trim(),
                }),
            });

            if (!response.ok) {
                throw new Error("Invalid email or password.");
            }

            const token = response.headers.get('Authorization')?.replace('Bearer ', '');
            const userData = await response.json(); 

            if (!token || !userData.id) {
                throw new Error("Server did not return a valid token or user ID.");
            }

            authStore.login(userData, token);
            email = "";
            password = "";
        } catch (err) {
            console.error("Error logging in:", err);
            error = err.message;
        } finally {
            loading = false;
        }
    }
</script>

<div class="login-container">
    <h2>User Login</h2>
    
    <form onsubmit={login} class="login-form">
        <div class="form-group">
            <label for="login-email">Email:</label>
            <input
                id="login-email"
                type="email"
                bind:value={email}
                placeholder="Enter user email"
                disabled={loading}
                required
            />
        </div>

        <div class="form-group">
            <label for="login-password">Password:</label>
            <input
                id="login-password"
                type="password"
                bind:value={password}
                placeholder="Enter placeholder password"
                disabled={loading}
                required
            />
        </div>

        <button type="submit" class="login-btn" disabled={loading || !email.trim() || !password.trim()}>
            {loading ? "Logging in..." : "Log in"}
        </button>
        
        {#if error}
            <div class="error-message">{error}</div>
        {/if}
    </form>
</div>

<style>
  .login-btn { width: 100%; padding: 12px; }
  .login-form .form-group { margin-bottom: 15px; }
  .login-form label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
  }
  .login-form input {
    width: 100%;
    padding: 10px;
    border: 1px solid var(--border-color);
    background-color: var(--input-bg);
    color: var(--text-color);
    border-radius: 4px;
    box-sizing: border-box;
  }
  .error-message {
    background-color: var(--error-bg);
    color: var(--error-color);
    padding: 10px;
    border-radius: 4px;
    margin-top: 15px;
  }
</style>