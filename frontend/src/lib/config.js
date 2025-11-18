import { authStore } from "./authStore.js";
import { get } from 'svelte/store';

// Get the base URL from environment variables
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Helper function for building API URLs
export function apiUrl(endpoint) {
    return `${API_BASE_URL}${endpoint}`;
}

/**
 * Enhanced fetch wrapper to include the JWT token in the Authorization header.
 */
export async function authenticatedFetch(endpoint, options = {}) {
    let token = get(authStore).token;

    const headers = {
        ...options.headers,
        'Content-Type': 'application/json',
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const newOptions = {
        ...options,
        headers,
    };

    const response = await fetch(apiUrl(endpoint), newOptions);

    // If the server returns 401, log the user out
    if (response.status === 401) {
        authStore.logout();
        throw new Error("Session expired or unauthorized. Please log in again.");
    }

    return response;
}


export const ENDPOINTS = {
    LOGIN: '/login', 
    USERS: '/users',
    POLLS: '/polls',
    VOTES: '/votes'
};