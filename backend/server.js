// server.js
// Entry point for the YouthLink SA REST API.
// Run locally with `npm run dev`, or deploy to Render (see README).

require('dotenv').config();
const express = require('express');
const cors = require('cors');

const authRoutes = require('./routes/auth');
const profileRoutes = require('./routes/profile');
const opportunityRoutes = require('./routes/opportunities');
const careerRoutes = require('./routes/careers');
const fundingRoutes = require('./routes/funding');
const eventRoutes = require('./routes/events');
const notificationRoutes = require('./routes/notifications');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Simple request logger - helps show understanding of what the API is doing
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} ${req.method} ${req.originalUrl}`);
  next();
});

// Health check - useful to confirm the API is live once hosted
app.get('/', (req, res) => {
  res.json({ status: 'YouthLink SA API is running.' });
});

app.use('/api/auth', authRoutes);
app.use('/api/profile', profileRoutes);
app.use('/api/opportunities', opportunityRoutes);
app.use('/api/careers', careerRoutes);
app.use('/api/funding', fundingRoutes);
app.use('/api/events', eventRoutes);
app.use('/api/notifications', notificationRoutes);

// Catch-all for unmatched routes
app.use((req, res) => {
  res.status(404).json({ error: 'Route not found.' });
});

// Only start listening when this file is run directly (not when imported by tests)
if (require.main === module) {
  app.listen(PORT, () => {
    console.log(`YouthLink SA API listening on port ${PORT}`);
  });
}

module.exports = app;
