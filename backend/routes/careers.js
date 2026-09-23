// routes/careers.js
// Career Explorer feature: browse career fields and their details.

const express = require('express');
const pool = require('../config/db');

const router = express.Router();

// GET /api/careers?field= - list careers, optionally filtered by field
router.get('/', async (req, res) => {
  const { field } = req.query;
  try {
    const [rows] = field
      ? await pool.query('SELECT * FROM Career WHERE field = $1', [field])
      : await pool.query('SELECT * FROM Career');
    res.json(rows);
  } catch (err) {
    console.error('Get careers error:', err.message);
    res.status(500).json({ error: 'Could not fetch careers.' });
  }
});

// GET /api/careers/:id
router.get('/:id', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM Career WHERE career_id = $1', [req.params.id]);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'Career not found.' });
    }
    res.json(rows[0]);
  } catch (err) {
    console.error('Get career error:', err.message);
    res.status(500).json({ error: 'Could not fetch career.' });
  }
});

module.exports = router;
