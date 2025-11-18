<script>
  import HeaderComponent from "./lib/HeaderComponent.svelte";
  import CreateUser from "./lib/CreateUserComponent.svelte";
  import CreatePoll from "./lib/CreatePollComponent.svelte";
  import VotePoll from "./lib/VoteComponent.svelte";
  import PollResults from "./lib/PollResultsComponent.svelte";
  import PollsList from "./lib/PollsListComponent.svelte";
  import Login from "./lib/LoginComponent.svelte";
  import { authStore } from "./lib/authStore.js";

  let isAuthenticated = $derived($authStore.isAuthenticated);
  let selectedPollId = $state("");
  let resultsComponent;

  function handlePollSelection(pollId) {
    selectedPollId = pollId.toString(); 
  }

  function handleVoteSuccess() {
    // The write is asynchronous. We wait a second to ensure the consumer 
    // has processed the message before we read from the DB/Cache.
    setTimeout(() => {
      resultsComponent?.refresh();
    }, 300);
  }
</script>

<main>
  <HeaderComponent />
  
  <div class="app-layout">
    <!-- Left column: Navigation and Creation -->
    <div class="sidebar">
      <div class="card">
        <PollsList 
          onPollSelect={handlePollSelection} 
          bind:selectedPollId={selectedPollId} 
        />
      </div>
      
      <!-- The login card only appears when logged out -->
      {#if !isAuthenticated}
        <div class="card">
          <Login />
        </div>
      {/if}

      <div class="card">
        <CreatePoll />
      </div>
      <div class="card">
        <CreateUser />
      </div>
    </div>
    
    <!-- Right column: Interaction with the selected poll -->
    <div class="main-content">
      {#if selectedPollId}
        <div class="card">
          <!-- Pass the callback to VotePoll -->
          <VotePoll 
            bind:pollId={selectedPollId} 
            onVoteSuccess={handleVoteSuccess} 
          />
        </div>
        <div class="card">
          <!-- Bind the component instance to our variable -->
          <PollResults 
            bind:this={resultsComponent} 
            bind:pollId={selectedPollId} 
          />
        </div>
      {:else}
        <div class="placeholder-card">
          <h2>Welcome!</h2>
          <p>Please select a poll from the list on the left to get started.</p>
        </div>
      {/if}
    </div>
  </div>
</main>

<style>
  main {
    text-align: center;
    padding: 1rem;
    width: 100%;
    max-width: 1400px;
    margin: 0 auto;
  }
  
  .app-layout {
    display: grid;
    grid-template-columns: minmax(350px, 1fr) 2fr;
    gap: 2rem;
    align-items: start;
    text-align: left;
  }

  .sidebar, .main-content {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .placeholder-card {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 300px;
    background-color: transparent;
    border: 2px dashed var(--placeholder-border);
    border-radius: 12px;
    color: var(--text-color-muted);
    text-align: center;
    padding: 2rem;
  }
</style>