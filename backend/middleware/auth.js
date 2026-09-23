// middleware/auth.js
// Protects routes by requiring a valid JWT in the Authorization header.
// The Android app must send: Authorization: Bearer <token>

const jwt = require('jsonwebtoken');

function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    console.log('Auth failed: no token provided');
    return res.status(401).json({ error: 'Access token is required.' });
  }

  jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
    if (err) {
      console.log('Auth failed: invalid or expired token');
      return res.status(403).json({ error: 'Invalid or expired token.' });
    }
    req.user = user; // { user_id, email }
    next();
  });
}

module.exports = authenticateToken;
