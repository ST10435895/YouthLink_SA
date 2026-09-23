// routes/events.js
// Events & Youth Programmes feature.

const express = require('express');
const pool = require('../config/db');

const router = express.Router();

// GET /api/events - list upcoming events
router.get('/', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM Event WHERE start_date >= NOW() ORDER BY start_date ASC');
    res.json(rows);
  } catch (err) {
    console.error('Get events error:', err.message);
    res.status(500).json({ error: 'Could not fetch events.' });
  }
});

module.exports = router;
