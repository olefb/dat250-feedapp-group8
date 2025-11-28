import http from 'k6/http';
import { check } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 200 }, // Ramp up to 200 VUs
        { duration: '2m', target: 200 }, // Stay at 200 VUs for 2 minutes
        { duration: '10s', target: 0 },   // Ramp down
    ],
    thresholds: {
        'http_req_failed': ['rate<0.02'],
        'http_req_duration': ['p(95)<500'],
    },
};

export default function () {
    const API_URL = 'http://localhost:8080/votes';

    const payload = JSON.stringify({
        voterId: null,
        optionId: 1,
        pollId: 1,
    });

    const params = { headers: { 'Content-Type': 'application/json' } };
    const res = http.post(API_URL, payload, params);

    check(res, { 'is status 202': (r) => r.status === 202 });
}