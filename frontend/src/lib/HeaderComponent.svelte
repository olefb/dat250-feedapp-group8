<script>
  import { authStore } from "./authStore.js";

  // Reactively get user and authentication status from the store
  let user = $derived($authStore.user);
  let isAuthenticated = $derived($authStore.isAuthenticated);
</script>

<header class="app-header">
  <div class="logo">
    <h1>FeedApp</h1>
  </div>

  {#if isAuthenticated}
    <div class="user-status">
      <span class="welcome-message">
        Welcome, <strong>{user.username}</strong>!
      </span>
      <button onclick={() => authStore.logout()} class="logout-btn">
        Log Out
      </button>
    </div>
  {/if}
</header>

<style>
  .app-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 2rem;
    background-color: var(--card-bg);
    border-bottom: 1px solid var(--border-color);
    box-shadow: var(--card-shadow);
    border-radius: 12px;
    margin-bottom: 2rem;
  }

  .logo h1 {
    margin: 0;
    font-size: 1.8rem;
    color: var(--primary-color);
  }

  .user-status {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .welcome-message {
    font-size: 1rem;
    color: var(--text-color-muted);
  }

  .logout-btn {
    background-color: transparent;
    border-color: var(--error-color);
    color: var(--error-color);
    font-weight: 500;
  }

  .logout-btn:hover {
    background-color: var(--error-bg);
  }
</style>