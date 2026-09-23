// config/db.js
// Creates a reusable PostgreSQL connection pool for the whole app,
// using Render's connection details from environment variables.
//
// The query() wrapper below returns results in the same [rows] shape
// the rest of this app's routes already expect, so route code doesn't
// need to change just because the underlying driver changed.

const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  port: process.env.DB_PORT || 5432,
  ssl: { rejectUnauthorized: false } // Render's Postgres requires SSL
});

module.exports = {
  query: async (text, params = []) => {
    const result = await pool.query(text, params);
    return [result.rows];
  }
};
