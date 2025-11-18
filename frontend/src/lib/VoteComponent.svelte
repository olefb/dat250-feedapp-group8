<script>
  import { authenticatedFetch, apiUrl, ENDPOINTS } from "./config.js";
  import { authStore } from "./authStore";
  let isAuthenticated = $derived($authStore.isAuthenticated);
  let voterId = $derived($authStore.user?.id);
  let poll = $state(null);
  let selectedOption = $state(null);
  let loading = $state(false);
  let error = $state("");
  let hasVoted = $state(false);
  let { pollId = $bindable(), onVoteSuccess } = $props();

  $effect(() => {
    if (pollId) {
      loadPoll();
    } else {
      // Clear state if pollId is cleared/reset
      poll = null;
      selectedOption = null;
    }
  });

  async function loadPoll() {
    if (!pollId.trim()) {
      error = "Enter a poll ID";
      return;
    }

    loading = true;
    error = "";

    try {
      const response = await fetch(apiUrl(ENDPOINTS.POLLS + `/${pollId}`));

      if (!response.ok) {
        throw new Error(`Poll not found: ${response.status}`);
      }

      poll = await response.json();
      selectedOption = null;
      hasVoted = false;
    } catch (err) {
      console.error("Error loading poll:", err);
      error = "Failed to load poll. Please check the poll ID.";
      poll = null;
    } finally {
      loading = false;
    }
  }

  async function submitVote() {
    if (!selectedOption) {
      error = "Select an option";
      return;
    }

    // Determine the ID to send: logged-in ID, or 0L for anonymous
    const finalVoterId = isAuthenticated ? voterId : 0;

    loading = true;
    error = "";

    try {
      const response = await authenticatedFetch(ENDPOINTS.VOTES, {
        method: "POST",
        body: JSON.stringify({
          voterId: finalVoterId,
          optionId: selectedOption.id,
          pollId: poll.id,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `Failed to submit vote: ${response.status} - ${errorText}`
        );
      }

      hasVoted = true;

      if (onVoteSuccess) {
        onVoteSuccess();
      }

    } catch (err) {
      console.error("Error submitting vote:", err);
      error = "Failed to submit vote. Please try again.";
    } finally {
      loading = false;
    }
  }

  function selectOption(option) {
    selectedOption = option;
  }
</script>

<div class="voting-container">
  <h2>Vote on a poll</h2>

  <!-- Poll loading -->
  <div class="poll-loader">
    <p>Current Poll ID: <strong>{pollId || 'None Selected'}</strong></p>
  </div>

  <!-- Error display -->
  {#if error}
    <div class="error-message">{error}</div>
  {/if}

  <!-- Poll display and voting -->
  {#if poll && !hasVoted}
    {#key poll.id}
      <div class="poll-section">
        <h3>{poll.question}</h3>

        <!-- Authentication status message -->
        {#if !isAuthenticated}
          <div class="auth-status-message unauthorized-message">
            You are voting anonymously.
          </div>
        {:else}
          <div class="auth-status-message">
            <p class="logged-in-info">
              Voting as: <strong
                >{$authStore.user.username} (ID: {voterId})</strong
              >
            </p>
          </div>
        {/if}
        
        <!-- Options container -->
        <div class="options-container">
          <h4>Select an option:</h4>
          {#each poll.options as option (option.id)}
            <div
              class="option-item"
              class:selected={selectedOption?.id === option.id}
            >
              <input
                type="radio"
                id="option-{poll.id}-{option.id}"
                name="poll-option-{poll.id}"
                value={option.id}
                checked={selectedOption?.id === option.id}
                onchange={() => selectOption(option)}
              />
              <label for="option-{poll.id}-{option.id}">
                {option.caption}
              </label>
            </div>
          {/each}
        </div>

        <!-- Submit button -->
        <button
          class="submit-vote-btn"
          onclick={submitVote}
          disabled={loading || !selectedOption}
        >
          {loading ? "Submitting..." : "Submit vote"}
        </button>
      </div>
    {/key}
  {/if}

  <!-- Success message -->
  {#if hasVoted}
    <div class="success-message">
      <h3>✅ Vote submitted!</h3>
      <p>{poll.question}</p>
      <p>Your selected option: "{selectedOption.caption}"</p>
      <button
        onclick={() => {
          hasVoted = false;
          poll = null;
          pollId = "";
          voterId = "";
        }}
      >
        Vote on another poll
      </button>
    </div>
  {/if}
</div>

<style>
  .auth-status-message {
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 1.5rem;
    border: 1px solid;
  }
  .unauthorized-message {
    background-color: color-mix(in srgb, var(--primary-color) 15%, transparent);
    border-color: var(--primary-color);
  }
  .logged-in-info {
    font-style: italic;
    color: var(--text-color-muted);
  }
  .option-item {
    display: flex;
    align-items: center;
    padding: 12px;
    margin-bottom: 8px;
    border: 2px solid var(--border-color);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  .option-item:hover {
    border-color: var(--primary-color);
  }
  .option-item.selected {
    border-color: var(--primary-color);
    background-color: var(--selected-bg);
  }
  .submit-vote-btn { width: 100%; margin-top: 1.5rem; }
  .error-message {
    background-color: var(--error-bg);
    color: var(--error-color);
    border: 1px solid var(--error-border);
    padding: 12px;
    border-radius: 6px;
    margin: 10px 0;
  }
  .success-message {
    background-color: var(--success-bg);
    color: var(--success-color);
    border: 1px solid var(--success-border);
    padding: 20px;
    border-radius: 6px;
    margin: 20px 0;
    text-align: center;
  }
</style>