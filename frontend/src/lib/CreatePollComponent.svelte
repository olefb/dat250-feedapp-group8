<script>
  import { authenticatedFetch, apiUrl, ENDPOINTS } from "./config.js";
  import { authStore } from "./authStore";
  let question = $state("");
  let options = $state("");
  let loading = $state(false);
  let error = $state("");
  let createdPoll = $state(null);

  let isAuthenticated = $derived($authStore.isAuthenticated); 
  let userId = $derived($authStore.user?.id);

  function validateOptions(optionsString) {
    const optionList = optionsString
      .split(",")
      .map((opt) => opt.trim())
      .filter((opt) => opt.length > 0);
    return {
      options: optionList,
      isValid: optionList.length >= 2,
      count: optionList.length,
    };
  }

  // Reactive validation for real-time feedback
  let optionValidation = $derived(validateOptions(options));

  async function createPoll(event) {
    event.preventDefault();

    if (!isAuthenticated) {
        error = "You must be logged in to create a poll.";
        return;
    }

    if (!userId || !question.trim() || !options.trim()) {
      error = "All fields are required.";
      return;
    }

    const validation = validateOptions(options);

    if (!validation.isValid) {
      error = `Please provide at least 2 options. You currently have ${validation.count}.`;
      return;
    }

    // Check for duplicate options
    const uniqueOptions = [...new Set(validation.options)];
    if (uniqueOptions.length !== validation.options.length) {
      error = "Please remove duplicate options.";
      return;
    }

    loading = true;
    error = "";

    try {
      const response = await authenticatedFetch(ENDPOINTS.POLLS, {
        method: "POST",
        body: JSON.stringify({
          userId: userId.toString(), // Pass the authenticated user ID
          question: question.trim(),
          options: validation.options,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `Failed to create poll: ${response.status} - ${errorText}`
        );
      }

      const data = await response.json();
      createdPoll = data;

      // Clear form
      question = "";
      options = "";
    } catch (err) {
      console.error("Error creating poll:", err);
      error = "Error creating poll. Please try again.";
    } finally {
      loading = false;
    }
  }

  function resetForm() {
    createdPoll = null;
    // userId = "";
    question = "";
    options = "";
    error = "";
  }
</script>

<div class="create-poll-container">
  <h2>Create new poll</h2>

  {#if !isAuthenticated}
    <div class="unauthorized-message">
        You must be logged in to create a poll. Please log in above.
    </div>
  {:else if !createdPoll}
    <form onsubmit={createPoll} class="poll-form">
      <div class="logged-in-info">
        Creating poll as: <strong>{$authStore.user.username} (ID: {userId})</strong>
      </div>

      <div class="form-group">
        <label for="question">Question:</label>
        <input
          id="question"
          type="text"
          bind:value={question}
          placeholder="Enter your poll question"
          disabled={loading}
          required
        />
      </div>

      <div class="form-group">
        <label for="options">Options (comma separated):</label>
        <input
          id="options"
          type="text"
          bind:value={options}
          placeholder="Option 1, Option 2, Option 3..."
          disabled={loading}
          required
        />
        <div class="options-help">
          <small>
            {#if options.trim()}
              {optionValidation.count} option{optionValidation.count !== 1
                ? "s"
                : ""} detected
              {#if optionValidation.count < 2}
                <span class="warning">⚠️ Need at least 2 options</span>
              {:else}
                <span class="success">✅ Valid</span>
              {/if}
            {:else}
              Separate each option with a comma
            {/if}
          </small>
        </div>

        {#if optionValidation.options.length > 0}
          <div class="options-preview">
            <strong>Preview:</strong>
            <ul>
              {#each optionValidation.options as option, index}
                <li>{index + 1}. {option}</li>
              {/each}
            </ul>
          </div>
        {/if}
      </div>

      {#if error}
        <div class="error-message">{error}</div>
      {/if}

      <button
        type="submit"
        class="create-btn"
        disabled={loading ||
          !optionValidation.isValid ||
          !question.trim()}
      >
        {loading ? "Creating poll..." : "Create poll"}
      </button>
    </form>
  {:else}
    <div class="success-section">
      <div class="success-message">
        <h3>✅ Poll created successfully!</h3>
        <div class="poll-details">
          <p><strong>Poll ID:</strong> {createdPoll.id}</p>
          <p><strong>Question:</strong> {createdPoll.question}</p>
          <p><strong>Options:</strong></p>
          <ul>
            {#each createdPoll.options as option}
              <li>{option.caption}</li>
            {/each}
          </ul>
        </div>
      </div>

      <div class="action-buttons">
        <button onclick={resetForm} class="create-another-btn">
          Create another poll
        </button>
      </div>
    </div>
  {/if}
</div>

<style>
  .unauthorized-message, .logged-in-info {
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 1.5rem;
    border: 1px solid;
    text-align: center;
  }
  .unauthorized-message {
    background-color: color-mix(in srgb, var(--primary-color) 15%, transparent);
    border-color: var(--primary-color);
  }
  .logged-in-info {
    background-color: var(--button-bg);
    border-color: var(--border-color);
    color: var(--text-color-muted);
  }

  .form-group { margin-bottom: 1rem; }
  .form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
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
  .options-help small { color: var(--text-color-muted); }
  .warning { color: var(--error-color); font-weight: bold; }
  .success { color: var(--success-color); font-weight: bold; }

  .options-preview {
    margin-top: 10px;
    padding: 10px;
    background-color: var(--bg-color);
    border-radius: 4px;
    border: 1px solid var(--border-color);
  }
  .options-preview ul { margin: 5px 0 0 0; padding-left: 20px; }

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
  .success-message {
    background-color: var(--success-bg);
    color: var(--success-color);
    padding: 25px;
    border-radius: 8px;
    margin-bottom: 20px;
    border: 1px solid var(--success-border);
  }
  .poll-details { text-align: left; }
</style>