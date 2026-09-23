// routes/opportunities.js
// Search/filter opportunities, view details, and save/unsave opportunities.

const express = require('express');
const pool = require('../config/db');
const authenticateToken = require('../middleware/auth');

const router = express.Router();

// GET /api/opportunities?keyword=&field=&location=&type=
router.get('/', async (req, res) => {
  const { keyword, field, location, type } = req.query;
  let sql = 'SELECT * FROM Opportunity WHERE 1=1';
  const params = [];

  if (keyword) {
    params.push(`%${keyword}%`);
    sql += ` AND (opportunity_title ILIKE $${params.length} OR organisation ILIKE $${params.length})`;
  }
  if (field) {
    params.push(field);
    sql += ` AND field = $${params.length}`;
  }
  if (location) {
    params.push(`%${location}%`);
    sql += ` AND location ILIKE $${params.length}`;
  }
  if (type) {
    params.push(type);
    sql += ` AND opportunity_type = $${params.length}`;
  }
  sql += ' ORDER BY closing_date ASC';

  try {
    const [rows] = await pool.query(sql, params);
    res.json(rows);
  } catch (err) {
    console.error('Search opportunities error:', err.message);
    res.status(500).json({ error: 'Could not fetch opportunities.' });
  }
});

// GET /api/opportunities/saved/me - list the logged-in user's saved opportunities
// NOTE: this route is defined BEFORE /:id so Express doesn't treat "saved" as an :id value.
router.get('/saved/me', authenticateToken, async (req, res) => {
  try {
    const [rows] = await pool.query(
      `SELECT o.* FROM SavedOpportunity s
       JOIN Opportunity o ON s.opportunity_id = o.opportunity_id
       WHERE s.user_id = $1 ORDER BY s.saved_at DESC`,
      [req.user.user_id]
    );
    res.json(rows);
  } catch (err) {
    console.error('Get saved opportunities error:', err.message);
    res.status(500).json({ error: 'Could not fetch saved opportunities.' });
  }
});

// GET /api/opportunities/:id - details for one opportunity
router.get('/:id', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM Opportunity WHERE opportunity_id = $1', [req.params.id]);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'Opportunity not found.' });
    }
    res.json(rows[0]);
  } catch (err) {
    console.error('Get opportunity error:', err.message);
    res.status(500).json({ error: 'Could not fetch opportunity.' });
  }
});

// POST /api/opportunities/:id/save - save an opportunity for the logged-in user
router.post('/:id/save', authenticateToken, async (req, res) => {
  try {
    await pool.query(
      'INSERT INTO SavedOpportunity (user_id, opportunity_id) VALUES ($1, $2)',
      [req.user.user_id, req.params.id]
    );
    console.log(`Opportunity ${req.params.id} saved by user_id=${req.user.user_id}`);
    res.status(201).json({ message: 'Opportunity saved.' });
  } catch (err) {
    console.error('Save opportunity error:', err.message);
    res.status(500).json({ error: 'Could not save opportunity.' });
  }
});

// DELETE /api/opportunities/:id/save - unsave
router.delete('/:id/save', authenticateToken, async (req, res) => {
  try {
    await pool.query(
      'DELETE FROM SavedOpportunity WHERE user_id = $1 AND opportunity_id = $2',
      [req.user.user_id, req.params.id]
    );
    res.json({ message: 'Opportunity removed from saved list.' });
  } catch (err) {
    console.error('Unsave opportunity error:', err.message);
    res.status(500).json({ error: 'Could not remove saved opportunity.' });
  }
});

module.exports = router;
