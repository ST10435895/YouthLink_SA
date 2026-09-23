// routes/profile.js
// Lets a logged-in user view and update their profile/settings
// (education, skills text, location, language, notification preference).

const express = require('express');
const pool = require('../config/db');
const authenticateToken = require('../middleware/auth');

const router = express.Router();

// GET /api/profile - get the logged-in user's own profile
router.get('/', authenticateToken, async (req, res) => {
  try {
    const [rows] = await pool.query(
      'SELECT user_id, first_name, surname, email, language, location, education_level, experience, notification_preference FROM Users WHERE user_id = $1',
      [req.user.user_id]
    );
    if (rows.length === 0) {
      return res.status(404).json({ error: 'User not found.' });
    }
    res.json(rows[0]);
  } catch (err) {
    console.error('Get profile error:', err.message);
    res.status(500).json({ error: 'Could not fetch profile.' });
  }
});

// PUT /api/profile - update settings (language, location, education, notifications)
router.put('/', authenticateToken, async (req, res) => {
  const { language, location, education_level, experience, notification_preference } = req.body;

  try {
    await pool.query(
      `UPDATE Users SET
        language = COALESCE($1, language),
        location = COALESCE($2, location),
        education_level = COALESCE($3, education_level),
        experience = COALESCE($4, experience),
        notification_preference = COALESCE($5, notification_preference)
       WHERE user_id = $6`,
      [language, location, education_level, experience, notification_preference, req.user.user_id]
    );
    console.log(`Profile updated: user_id=${req.user.user_id}`);
    res.json({ message: 'Profile updated successfully.' });
  } catch (err) {
    console.error('Update profile error:', err.message);
    res.status(500).json({ error: 'Could not update profile.' });
  }
});

module.exports = router;
