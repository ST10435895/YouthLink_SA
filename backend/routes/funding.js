// routes/funding.js
// Study & Funding feature: bursaries, NSFAS, skills programmes.

const express = require('express');
const pool = require('../config/db');

const router = express.Router();

// GET /api/funding - list all funding/bursary opportunities
router.get('/', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM Funding ORDER BY closing_date ASC');
    res.json(rows);
  } catch (err) {
    console.error('Get funding error:', err.message);
    res.status(500).json({ error: 'Could not fetch funding options.' });
  }
});

module.exports = router;
