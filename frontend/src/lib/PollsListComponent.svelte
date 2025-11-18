<script>
  import { authenticatedFetch, apiUrl, ENDPOINTS } from "./config.js";
  import { authStore } from "./authStore.js";

  let { onPollSelect, selectedPollId = $bindable() } = $props();

  let polls = $state([]);
  let loading = $state(false);
  let error = $state("");

  // Reactive access to the auth store
  let currentUser = $derived($authStore.user);

  async function loadPolls() {
    loading = true;
    error = "";
    try {
      const response = await fetch(apiUrl(ENDPOINTS.POLLS));
      
      if (!response.ok) {
        throw new Error(`Failed to fetch polls: ${response.status}`);
      }

      const data = await response.json();
      polls = data;
    } catch (err) {
      console.error("Error loading polls:", err);
      error = "Failed to load polls. Please ensure the backend is running.";
      polls = [];
    } finally {
      loading = false;
    }
  }

  async function deletePoll(pollId) {
    // Ask for confirmation before performing a destructive action
    if (!confirm("Are you sure you want to delete this poll? This action cannot be undone.")) {
      return;
    }

    error = "";
    try {
      const response = await authenticatedFetch(ENDPOINTS.POLLS + `/${pollId}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        // The backend will send 403 Forbidden if the user is not the owner
        if (response.status === 403) {
            throw new Error("You do not have permission to delete this poll.");
        }
        throw new Error("Failed to delete the poll.");
      }

      // If successful, refresh the list to remove the deleted poll from the UI
      await loadPolls();

    } catch (err) {
      console.error("Error deleting poll:", err);
      error = err.message;
    }
  }
  
  loadPolls();
  
  export { loadPolls };
  
  function selectPoll(pollId) {
    onPollSelect(pollId);
  }
</script>

<div class="poll-list-container">
  <h2>Available Polls</h2>
  <button onclick={loadPolls} disabled={loading} class="refresh-btn">
    {loading ? "Refreshing..." : "Refresh List"}
  </button>

  {#if error}
    <div class="error-message">{error}</div>
  {/if}

  {#if polls.length === 0 && !loading}
    <p class="no-polls">No polls found. Try creating one above.</p>
  {:else}
    <ul class="poll-list">
      {#each polls as poll (poll.id)}
        <li class="poll-item" class:selected={poll.id.toString() === selectedPollId}>
          <div class="poll-info">
            <span class="poll-id">ID: {poll.id}</span>
            <span class="poll-question">{poll.question}</span>
          </div>
          <div class="poll-actions">
            <button onclick={() => selectPoll(poll.id)} class="select-btn">
              View / Vote
            </button>
            {#if currentUser?.id === poll.creatorId}
              <button onclick={() => deletePoll(poll.id)} class="delete-btn">
                Delete
              </button>
            {/if}

          </div>
        </li>
      {/each}
    </ul>
  {/if}
</div>

<style>
  .refresh-btn { margin-bottom: 20px; }
  .poll-list { list-style: none; padding: 0; }
  .poll-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    margin-bottom: 10px;
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 6px;
    transition: all 0.2s;
    cursor: pointer;
  }
  .poll-item:hover {
    border-color: var(--primary-color);
  }
  .poll-item.selected {
    background-color: var(--selected-bg);
    border-color: var(--primary-color);
  }
  .poll-id { font-size: 0.8em; color: var(--text-color-muted); }
  .poll-question { font-weight: bold; font-size: 1.1em; }
  .poll-actions { display: flex; gap: 10px; }
  .select-btn {
    background-color: var(--bar-fill);
    color: white;
    border-color: var(--bar-fill);
  }
  .delete-btn {
    background-color: var(--error-color);
    color: white;
    border-color: var(--error-color);
  }
  .error-message {
    background-color: var(--error-bg);
    color: var(--error-color);
    padding: 10px;
    border-radius: 4px;
    margin-bottom: 15px;
  }
</style>