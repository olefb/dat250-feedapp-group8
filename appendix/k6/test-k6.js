import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        // Ramp up to 100 virtual users over 30 seconds
        { duration: '30s', target: 100 },
        // Stay at 100 virtual users for 1 minute
        { duration: '1m', target: 100 },
        // Ramp down to 0 virtual users over 10 seconds
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        // Fail the test if more than 1% of requests have an error
        'http_req_failed': ['rate<0.01'],
        // Fail if 95th percentile response time is > 200ms
        'http_req_duration': ['p(95)<200'],
    },
};

export default function () {
    const API_URL = 'http://localhost:8080/votes';

    const payload = JSON.stringify({
        voterId: null, // Simulate anonymous vote
        optionId: 1,   // Assumes an option with ID 1 exists
        pollId: 1,     // Assumes a poll with ID 1 exists
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // Send the POST request to cast a vote
    const res = http.post(API_URL, payload, params);

    // Check if the request was accepted (202 Accepted)
    check(res, {
        'is status 202': (r) => r.status === 202,
    });

    // Add a small sleep to simulate realistic user behavior
    sleep(1);
}