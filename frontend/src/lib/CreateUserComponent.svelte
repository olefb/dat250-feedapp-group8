<script>
  import { apiUrl, ENDPOINTS } from "./config.js";

  let username = $state("");
  let email = $state("");
  let password = $state("");
  let loading = $state(false);
  let error = $state("");
  let createdUser = $state(null);

  async function createUser(event) {
    event.preventDefault();

    if (!username.trim() || !email.trim() || !password.trim()) {
      error = "Username, email, and password are required.";
      return;
    }

    // Basic email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.trim())) {
      error = "Enter a valid email address.";
      return;
    }

    loading = true;
    error = "";

    try {
      const response = await fetch(apiUrl(ENDPOINTS.USERS), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: username.trim(),
          email: email.trim(),
          password: password.trim(),
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.log("Error response:", errorText);
        throw new Error(
          `Failed to create user: ${response.status} - ${errorText}`
        );
      }

      const userData = await response.json();
      createdUser = userData;

      // Clear the form
      username = "";
      email = "";
      password = "";
    } catch (err) {
      console.error("Error creating user:", err);
      error = "Failed to create user. Please try again.";
    } finally {
      loading = false;
    }
  }

  function resetForm() {
    createdUser = null;
    username = "";
    email = "";
    password = "";
    error = "";
  }
</script>

<div class="user-creation-container">
  <h2>Create new user</h2>

  {#if !createdUser}
    <form onsubmit={createUser} class="user-form">
      <div class="form-group">
        <label for="username">Username:</label>
        <input
          id="username"
          type="text"
          bind:value={username}
          placeholder="Enter username"
          disabled={loading}
          required
        />
      </div>

      <div class="form-group">
        <label for="email">Email:</label>
        <input
          id="email"
          type="email"
          bind:value={email}
          placeholder="Enter email address"
          disabled={loading}
          required
        />
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input
          id="password"
          type="password"
          bind:value={password}
          placeholder="Enter a secure password"
          disabled={loading}
          required
        />
      </div>

      {#if error}
        <div class="error-message">{error}</div>
      {/if}

      <button
        type="submit"
        class="create-btn"
        disabled={loading || !username.trim() || !email.trim() || !password.trim()}
      >
        {loading ? "Creating..." : "Create user"}
      </button>
    </form>
  {:else}
    <div class="success-section">
      <div class="success-message">
        <h3>✅ User Created Successfully!</h3>
        <div class="user-details">
          <p><strong>User ID:</strong> {createdUser.id}</p>
          <p><strong>Username:</strong> {createdUser.username}</p>
          <p><strong>Email:</strong> {createdUser.email}</p>
        </div>
        <div class="user-id-highlight">
          <p>
            <strong>User ID:</strong>
            <span class="user-id">{createdUser.id}</span>
          </p>
        </div>
      </div>

      <div class="action-buttons">
        <button onclick={resetForm} class="create-another-btn">
          Create new user
        </button>
      </div>
    </div>
  {/if}
</div>

<style>
  h2, h3 { color: var(--text-color); }
  h3 { margin-top: 0; }
  
  .form-group { margin-bottom: 1rem; }
  .form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
    color: var(--text-color);
  }
  .form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid var(--border-color);
    background-color: var(--input-bg);
    color: var(--text-color);
    border-radius: 6px;
    font-size: 16px;
  }
  .form-group input:focus {
    outline: none;
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary-color) 20%, transparent);
  }
  
  .create-btn { width: 100%; }
  .create-another-btn {
    background-color: var(--primary-color);
    color: #fff;
    border-color: var(--primary-color);
  }

  .error-message {
    background-color: var(--error-bg);
    color: var(--error-color);
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 15px;
    border: 1px solid var(--error-border);
  }
  .success-section { text-align: center; }
  .success-message {
    background-color: var(--success-bg);
    color: var(--success-color);
    padding: 25px;
    border-radius: 8px;
    margin-bottom: 20px;
    border: 1px solid var(--success-border);
  }
  .user-id {
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    padding: 4px 8px;
    border-radius: 4px;
    font-family: monospace;
  }
</style>