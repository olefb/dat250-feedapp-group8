<script>
  import { apiUrl, ENDPOINTS } from "./config.js";

  let { pollId = $bindable() } = $props();

  let pollResults = $state(null);
  let pollQuestion = $state("");
  let loading = $state(false);
  let error = $state("");

  // Derived state to calculate total votes for percentage calculation
  let totalVotes = $derived(
    pollResults?.reduce((sum, result) => sum + result.voteCount, 0) ?? 0
  );

  let sortedPollResults = $derived(
    pollResults 
      ? [...pollResults].sort((a, b) => b.voteCount - a.voteCount) 
      : []
  );

  $effect(() => {
    if (pollId) {
        loadResults();
    } else {
        // Clear state if selection is cleared
        pollResults = null;
        pollQuestion = "";
        error = "";
    }
  });

  async function loadResults() {
    if (!pollId) return;

    loading = true;
    error = "";

    try {
      const pollResponse = await fetch(apiUrl(ENDPOINTS.POLLS + `/${pollId}`));
      if (!pollResponse.ok) {
        // If 404, it may have been deleted
        throw new Error(`Poll not found`);
      }
      const pollData = await pollResponse.json();
      pollQuestion = pollData.question;

      const resultsResponse = await fetch(
        apiUrl(ENDPOINTS.POLLS + `/${pollId}/results`)
      );

      if (!resultsResponse.ok) {
        throw new Error(`Failed to fetch results`);
      }

      pollResults = await resultsResponse.json();
    } catch (err) {
      console.error("Error loading poll results:", err);
      error = err.message;
      pollResults = null;
    } finally {
      loading = false;
    }
  }

  function getPercentage(count) {
    if (totalVotes === 0) return 0;
    return ((count / totalVotes) * 100).toFixed(1);
  }

  export function refresh() {
    loadResults();
  }
</script>

<div class="results-container">
  <div class="header-row">
    <h2>Poll Results</h2>
    
    <button onclick={loadResults} disabled={loading || !pollId} class="refresh-btn">
      {loading ? "Refreshing..." : "Refresh ⟳"}
    </button>
  </div>

  {#if error}
    <div class="error-message">{error}</div>
  {/if}

  {#if pollResults}
    <div class="results-section">
      <h3>{pollQuestion}</h3>
      <p class="total-votes">Total Votes: <strong>{totalVotes}</strong></p>

      <div class="results-list">
        {#each sortedPollResults as result (result.optionId)}
          <div class="result-bar">
            <div class="option-info">
              <span class="caption">{result.caption}</span>
              <span class="count-percent">
                {result.voteCount} vote{result.voteCount !== 1 ? "s" : ""}
                ({getPercentage(result.voteCount)}%)
              </span>
            </div>
            <div class="bar-visual">
              <div
                class="bar-fill"
                style="width: {getPercentage(result.voteCount)}%;"
              ></div>
            </div>
          </div>
        {/each}
      </div>
    </div>
  {:else if !pollId}
    <p class="no-results">Select a poll from the list to view results.</p>
  {:else if !loading && !error}
    <p class="no-results">No votes recorded yet.</p>
  {/if}
</div>

<style>
  .header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    border-bottom: 1px solid var(--border-color);
    padding-bottom: 0.5rem;
  }
  .header-row h2 {
    border-bottom: none;
    margin: 0;
    padding: 0;
  }
  .refresh-btn {
    background-color: transparent;
    border: 1px solid var(--button-border-color);
    color: var(--primary-color);
    font-size: 0.9rem;
    padding: 0.4em 0.8em;
  }
  .refresh-btn:hover:not(:disabled) {
    background-color: var(--button-bg);
    border-color: var(--primary-color);
  }
  
  .results-section { margin-top: 1rem; text-align: left; }
  .total-votes { color: var(--text-color-muted); margin-bottom: 1rem; }
  
  .result-bar { margin-bottom: 15px; }
  .option-info { display: flex; justify-content: space-between; margin-bottom: 5px; font-weight: bold; font-size: 0.95rem;}
  
  .bar-visual { height: 25px; background-color: var(--bar-bg); border-radius: 4px; overflow: hidden; }
  .bar-fill { height: 100%; background-color: var(--bar-fill); transition: width 0.5s ease-out; }
  
  .error-message {
    background-color: var(--error-bg);
    color: var(--error-color);
    padding: 10px;
    border-radius: 4px;
    margin-bottom: 1rem;
  }
  .no-results {
    color: var(--text-color-muted);
    font-style: italic;
    margin-top: 2rem;
  }
</style>