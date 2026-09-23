-- schema.sql (PostgreSQL version, for Render)
-- Run this once against your Render Postgres database using the
-- 'psql' command Render gives you, or a GUI tool like pgAdmin/DBeaver,
-- to create all tables YouthLink SA needs. Matches the ER diagram
-- from Part 1. Note: the User table is named "Users" here because
-- USER is a reserved word in PostgreSQL.

CREATE TABLE IF NOT EXISTS Users (
  user_id SERIAL PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  surname VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  language VARCHAR(20) DEFAULT 'English',
  location VARCHAR(150),
  education_level VARCHAR(100),
  experience TEXT,
  notification_preference BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS Skill (
  skill_id SERIAL PRIMARY KEY,
  skill_name VARCHAR(100) NOT NULL,
  category VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS UserSkill (
  user_skill_id SERIAL PRIMARY KEY,
  user_id INT NOT NULL REFERENCES Users(user_id) ON DELETE CASCADE,
  skill_id INT NOT NULL REFERENCES Skill(skill_id) ON DELETE CASCADE,
  proficiency_level VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Career (
  career_id SERIAL PRIMARY KEY,
  career_title VARCHAR(150) NOT NULL,
  field VARCHAR(100),
  description TEXT,
  education_required VARCHAR(150),
  experience_required VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS Institution (
  institution_id SERIAL PRIMARY KEY,
  institution_name VARCHAR(150) NOT NULL,
  type VARCHAR(100),
  website VARCHAR(255),
  location VARCHAR(150),
  description TEXT
);

CREATE TABLE IF NOT EXISTS Opportunity (
  opportunity_id SERIAL PRIMARY KEY,
  opportunity_title VARCHAR(150) NOT NULL,
  organisation VARCHAR(150),
  description TEXT,
  opportunity_type VARCHAR(50),
  field VARCHAR(100),
  location VARCHAR(150),
  education_requirement VARCHAR(150),
  experience_requirement VARCHAR(150),
  closing_date DATE,
  application_link VARCHAR(255),
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS Funding (
  funding_id SERIAL PRIMARY KEY,
  funding_title VARCHAR(150) NOT NULL,
  provider VARCHAR(150),
  description TEXT,
  amount VARCHAR(100),
  eligibility TEXT,
  closing_date DATE,
  application_link VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Event (
  event_id SERIAL PRIMARY KEY,
  event_title VARCHAR(150) NOT NULL,
  description TEXT,
  event_type VARCHAR(100),
  location VARCHAR(150),
  start_date TIMESTAMP,
  end_date TIMESTAMP,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS SavedOpportunity (
  saved_id SERIAL PRIMARY KEY,
  user_id INT NOT NULL REFERENCES Users(user_id) ON DELETE CASCADE,
  opportunity_id INT NOT NULL REFERENCES Opportunity(opportunity_id) ON DELETE CASCADE,
  saved_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS Notification (
  notification_id SERIAL PRIMARY KEY,
  user_id INT NOT NULL REFERENCES Users(user_id) ON DELETE CASCADE,
  title VARCHAR(150),
  message TEXT,
  type VARCHAR(50),
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO Career (career_title, field, description, education_required, experience_required) VALUES
('Junior Software Developer', 'Information Technology', 'Builds and maintains software applications.', 'Diploma/Degree in IT or Computer Science', 'None to 1 year'),
('Marketing Assistant', 'Business & Finance', 'Supports marketing campaigns and social media.', 'Matric or Diploma in Marketing', 'None');

INSERT INTO Opportunity (opportunity_title, organisation, description, opportunity_type, field, location, education_requirement, experience_requirement, closing_date, application_link) VALUES
('Data Analyst Intern', 'Deloitte', 'Join our team and gain practical experience in data analysis and reporting.', 'Internship', 'Information Technology', 'Johannesburg, Hybrid', 'Currently studying or recently graduated', 'None', '2026-11-25', 'https://example.com/apply/1'),
('Software Developer Learnership', 'Capitec Bank', 'A 12-month learnership for aspiring developers.', 'Learnership', 'Information Technology', 'Cape Town', 'Matric with Maths', 'None', '2026-10-20', 'https://example.com/apply/2');

INSERT INTO Funding (funding_title, provider, description, amount, eligibility, closing_date, application_link) VALUES
('NSFAS 2027 Applications', 'NSFAS', 'Apply for funding for your tertiary studies.', 'Full tuition + allowances', 'South African citizens, household income under threshold', '2027-01-30', 'https://www.nsfas.org.za'),
('University of Pretoria Bursary', 'University of Pretoria', 'Available for South African students.', 'Varies', 'Academic merit', '2026-08-31', 'https://example.com/up-bursary');

INSERT INTO Event (event_title, description, event_type, location, start_date, end_date) VALUES
('Career Expo Johannesburg', 'A career fair featuring top employers.', 'Career Event', 'Sandton Convention Centre', '2026-11-15 09:00:00', '2026-11-15 17:00:00'),
('UCT Open Day', 'Explore courses and campus life.', 'Open Day', 'University of Cape Town', '2026-11-24 09:00:00', '2026-11-24 15:00:00');
