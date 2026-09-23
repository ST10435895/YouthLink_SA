// routes/notifications.js
// In-app notifications list (real push notifications via Firebase Cloud
// Messaging are added for the final PoE - this covers the in-app list
// and read/unread state for the prototype).

const express = require('express');
const pool = require('../config/db');
const authenticateToken = require('../middleware/auth');

const router = express.Router();

// GET /api/notifications - the logged-in user's notifications
router.get('/', authenticateToken, async (req, res) => {
  try {
    const [rows] = await pool.query(
      'SELECT * FROM Notification WHERE user_id = $1 ORDER BY created_at DESC',
      [req.user.user_id]
    );
    res.json(rows);
  } catch (err) {
    console.error('Get notifications error:', err.message);
    res.status(500).json({ error: 'Could not fetch notifications.' });
  }
});

// PUT /api/notifications/read-all - mark all as read
router.put('/read-all', authenticateToken, async (req, res) => {
  try {
    await pool.query('UPDATE Notification SET is_read = TRUE WHERE user_id = $1', [req.user.user_id]);
    res.json({ message: 'All notifications marked as read.' });
  } catch (err) {
    console.error('Mark notifications read error:', err.message);
    res.status(500).json({ error: 'Could not update notifications.' });
  }
});

module.exports = router;
