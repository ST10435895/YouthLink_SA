// routes/auth.js
// Handles registration and login. Passwords are hashed with bcrypt
// before being stored - the database never sees a plain-text password.

const express = require('express');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const pool = require('../config/db');

const router = express.Router();
const SALT_ROUNDS = 10;

// POST /api/auth/register
router.post('/register', async (req, res) => {
  const { first_name, surname, email, password } = req.body;

  if (!first_name || !surname || !email || !password) {
    console.log('Register failed: missing fields');
    return res.status(400).json({ error: 'first_name, surname, email and password are required.' });
  }

  try {
    const [existing] = await pool.query('SELECT user_id FROM Users WHERE email = $1', [email]);
    if (existing.length > 0) {
      console.log(`Register failed: email already in use (${email})`);
      return res.status(409).json({ error: 'An account with this email already exists.' });
    }

    // Encrypt (hash) the password before storing it
    const hashedPassword = await bcrypt.hash(password, SALT_ROUNDS);

    const [rows] = await pool.query(
      'INSERT INTO Users (first_name, surname, email, password) VALUES ($1, $2, $3, $4) RETURNING user_id',
      [first_name, surname, email, hashedPassword]
    );

    console.log(`New user registered: user_id=${rows[0].user_id}`);
    res.status(201).json({ message: 'Registration successful.', user_id: rows[0].user_id });
  } catch (err) {
    console.error('Register error:', err.message);
    res.status(500).json({ error: 'Something went wrong during registration.' });
  }
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ error: 'email and password are required.' });
  }

  try {
    const [rows] = await pool.query('SELECT * FROM Users WHERE email = $1', [email]);
    if (rows.length === 0) {
      console.log(`Login failed: no user with email ${email}`);
      return res.status(401).json({ error: 'Invalid email or password.' });
    }

    const user = rows[0];
    const passwordMatches = await bcrypt.compare(password, user.password);

    if (!passwordMatches) {
      console.log(`Login failed: wrong password for ${email}`);
      return res.status(401).json({ error: 'Invalid email or password.' });
    }

    const token = jwt.sign(
      { user_id: user.user_id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );

    console.log(`Login success: user_id=${user.user_id}`);
    res.json({
      token,
      user: {
        user_id: user.user_id,
        first_name: user.first_name,
        surname: user.surname,
        email: user.email,
        language: user.language
      }
    });
  } catch (err) {
    console.error('Login error:', err.message);
    res.status(500).json({ error: 'Something went wrong during login.' });
  }
});

module.exports = router;
