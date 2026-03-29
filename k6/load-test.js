import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  scenarios: {
    normal_load: {
      executor: 'constant-vus',
      vus: 20,
      duration: '30s',
      tags: { scenario: 'normal' },
    },
    peak_load: {
      executor: 'constant-vus',
      vus: 50,
      duration: '30s',
      startTime: '35s',
      tags: { scenario: 'peak' },
    },
    stress_load: {
      executor: 'constant-vus',
      vus: 100,
      duration: '30s',
      startTime: '70s',
      tags: { scenario: 'stress' },
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.05'],
  },
};

export function setup() {
  http.post(`${BASE_URL}/api/v1/auth/register`, JSON.stringify({
    username: 'loadtest_guest',
    password: 'loadtest123',
    role: 'GUEST'
  }), { headers: { 'Content-Type': 'application/json' } });

  const loginRes = http.post(`${BASE_URL}/api/v1/auth/login`, JSON.stringify({
    username: 'loadtest_guest',
    password: 'loadtest123'
  }), { headers: { 'Content-Type': 'application/json' } });

  const token = JSON.parse(loginRes.body).data?.token;
  return { token };
}

export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  // Test 1: Query Listings
  const queryRes = http.get(
    `${BASE_URL}/api/v1/listings?dateFrom=2025-08-01&dateTo=2025-08-10&noOfPeople=2&country=Turkey&city=Istanbul&page=0`,
    { headers }
  );
  check(queryRes, {
    'query listings - status 200': (r) => r.status === 200,
    'query listings - success true': (r) => JSON.parse(r.body).success === true,
  });

  sleep(1);

  // Test 2: Get My Bookings
  const bookingsRes = http.get(`${BASE_URL}/api/v1/bookings/my?page=0`, { headers });
  check(bookingsRes, {
    'my bookings - status 200': (r) => r.status === 200,
  });

  sleep(1);
}
