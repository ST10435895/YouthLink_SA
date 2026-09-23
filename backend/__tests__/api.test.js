// __tests__/api.test.js
// Basic automated tests for the YouthLink SA API.
// Run with `npm test`. GitHub Actions runs this on every push (see
// .github/workflows/build.yml).
//
// NOTE: These tests mock the database pool so they can run in CI
// without needing a real MySQL server or credentials.

jest.mock('../config/db', () => ({
  query: jest.fn()
}));

const request = require('supertest');
const bcrypt = require('bcrypt');
const pool = require('../config/db');
const app = require('../server');

describe('Health check', () => {
  it('GET / should return a running status', async () => {
    const res = await request(app).get('/');
    expect(res.statusCode).toBe(200);
    expect(res.body.status).toMatch(/running/i);
  });
});

describe('POST /api/auth/register', () => {
  it('rejects registration when required fields are missing', async () => {
    const res = await request(app).post('/api/auth/register').send({ email: 'test@example.com' });
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('error');
  });

  it('registers a new user successfully and hashes the password', async () => {
    pool.query
      .mockResolvedValueOnce([[]]) // no existing user with that email
      .mockResolvedValueOnce([[{ user_id: 1 }]]); // INSERT ... RETURNING user_id

    const res = await request(app).post('/api/auth/register').send({
      first_name: 'Chloe',
      surname: 'Langenhoven',
      email: 'chloe@example.com',
      password: 'SuperSecret123'
    });

    expect(res.statusCode).toBe(201);
    expect(res.body.user_id).toBe(1);

    // The password sent to the DB layer should be a bcrypt hash, not plain text
    const insertedPassword = pool.query.mock.calls[1][1][3];
    expect(insertedPassword).not.toBe('SuperSecret123');
    expect(await bcrypt.compare('SuperSecret123', insertedPassword)).toBe(true);
  });

  it('rejects registration when the email is already in use', async () => {
    pool.query.mockResolvedValueOnce([[{ user_id: 1 }]]);

    const res = await request(app).post('/api/auth/register').send({
      first_name: 'Chloe',
      surname: 'Langenhoven',
      email: 'chloe@example.com',
      password: 'SuperSecret123'
    });

    expect(res.statusCode).toBe(409);
  });
});

describe('POST /api/auth/login', () => {
  it('rejects login with missing credentials', async () => {
    const res = await request(app).post('/api/auth/login').send({});
    expect(res.statusCode).toBe(400);
  });

  it('rejects login for a non-existent user', async () => {
    pool.query.mockResolvedValueOnce([[]]);
    const res = await request(app).post('/api/auth/login').send({
      email: 'nobody@example.com',
      password: 'whatever'
    });
    expect(res.statusCode).toBe(401);
  });

  it('logs in successfully with correct credentials', async () => {
    const hashed = await bcrypt.hash('SuperSecret123', 10);
    pool.query.mockResolvedValueOnce([[{
      user_id: 1,
      first_name: 'Chloe',
      surname: 'Langenhoven',
      email: 'chloe@example.com',
      password: hashed,
      language: 'English'
    }]]);

    const res = await request(app).post('/api/auth/login').send({
      email: 'chloe@example.com',
      password: 'SuperSecret123'
    });

    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('token');
    expect(res.body.user.email).toBe('chloe@example.com');
  });
});

describe('GET /api/opportunities', () => {
  it('returns a list of opportunities', async () => {
    pool.query.mockResolvedValueOnce([[
      { opportunity_id: 1, opportunity_title: 'Data Analyst Intern' }
    ]]);

    const res = await request(app).get('/api/opportunities');
    expect(res.statusCode).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body[0].opportunity_title).toBe('Data Analyst Intern');
  });
});

describe('Protected routes', () => {
  it('rejects a request to a protected route with no token', async () => {
    const res = await request(app).get('/api/profile');
    expect(res.statusCode).toBe(401);
  });
});
