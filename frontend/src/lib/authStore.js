import { writable } from 'svelte/store';

// Initialize the store with data from localStorage if available
const initialUser = JSON.parse(localStorage.getItem('user')) || null;
const initialToken = localStorage.getItem('token') || null;

const createAuthStore = () => {
    const { subscribe, set } = writable({
        user: initialUser,
        token: initialToken,
        isAuthenticated: !!initialToken
    });

    return {
        subscribe,
        login: (user, token) => {
            localStorage.setItem('user', JSON.stringify(user));
            localStorage.setItem('token', token);
            set({ user, token, isAuthenticated: true });
        },
        logout: () => {
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            set({ user: null, token: null, isAuthenticated: false });
        },
        // Utility to get the current token for custom integrations
        getToken: () => initialToken,
    };
};

export const authStore = createAuthStore();