# YouthLink SA

**Your next opportunity starts here.**

![Backend Tests](https://github.com/ST10435895/YouthLink_SA/actions/workflows/backend-tests.yml/badge.svg)
![Android CI](https://github.com/ST10435895/YouthLink_SA/actions/workflows/android-ci.yml/badge.svg)

YouthLink SA is an Android app built to help young South Africans find their
next step — whether that's a job, an internship, a learnership, a bursary,
career guidance, or a youth development event. It brings together several
things that usually live on separate platforms (SA Youth, LinkedIn, Youth
Jobs SA and others) into a single, easy-to-use app aimed specifically at
South African youth.

This repository contains **both halves** of the OPSC6312 Part 2 submission:
the Android app (`/android`) and the REST API + database it talks to
(`/backend`).

---

## Table of Contents

- [Purpose](#purpose)
- [Features](#features)
- [Design Considerations](#design-considerations)
- [Architecture & Tech Stack](#architecture--tech-stack)
- [Repository Structure](#repository-structure)
- [Backend / REST API](#backend--rest-api)
- [Android App](#android-app)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [GitHub Actions (CI)](#github-actions-ci)
- [Demo Video](#demo-video)
- [AI Tool Usage](#ai-tool-usage)
- [Author](#author)

---

## Purpose

Research done in Part 1 of this project found that young South Africans
looking for their next opportunity are usually forced to jump between
several different platforms — one for jobs, another for professional
networking, another for bursaries — none of which cover the *whole* journey
from school-leaver to employed young professional.

YouthLink SA's purpose is to bring that journey into one app: career
exploration, study and funding options, internships/WIL/learnerships,
entry-level jobs, and youth events, all searchable and filterable in one
place, with a profile a user builds once and reuses everywhere in the app.

## Features

| Feature | Description |
|---|---|
| **Register & Login** | Accounts are created with a hashed (bcrypt) password on the server — the app never stores or transmits a plain-text password after the initial HTTPS request, and the login token is stored on-device using `androidx.security` `EncryptedSharedPreferences`. |
| **Settings** | Users can update their profile (location, education level, experience, language, notification preference) at any time. |
| **Opportunity Search & Filters** | Search opportunities by keyword, and filter by field, type, and location. |
| **Career Exploration** | Browse career information — typical qualifications, experience level, and field — aimed at users who haven't yet decided on a path. |
| **Study & Funding** | Browse bursaries and funding opportunities, with a direct link to apply where available. |
| **Events & Youth Programmes** | Browse career fairs, workshops, open days and other youth events. |
| **Saved Opportunities** | Bookmark opportunities to come back to later. |
| **REST API Integration** | All data is served from a REST API backed by a PostgreSQL database, connected over HTTPS. |

## Design Considerations

- **Single-activity architecture**: one `MainActivity` hosts every screen as
  a `Fragment`, navigated between using the **Navigation Component**. This
  keeps navigation logic centralised in `nav_graph.xml` rather than spread
  across multiple Activities.
- **MVVM**: each screen has a `ViewModel` that owns its network calls and
  exposes state via `LiveData`, wrapped in a small `UiState` sealed class
  (`Loading` / `Success` / `Error`) so every screen handles those three
  states the same, predictable way — this is also what keeps the app from
  crashing on network errors or unexpected responses.
- **Bottom navigation** gives quick access to the app's main feature areas,
  and is deliberately hidden on the authentication screens and the
  detail/settings screens where it doesn't apply.
- **Input validation is testable on its own**: validation logic for
  registration and login lives in a plain Kotlin `ValidationUtils` object
  with no Android framework dependency, specifically so it can be properly
  unit tested (see [Testing](#testing)) rather than only checked manually.
- **Colour palette**: green, gold, red, and blue — drawn from the Part 1
  design document and echoing the South African flag, used consistently
  across headers, buttons, and status text.
- **REST API design**: the backend mirrors the ER diagram designed in
  Part 1 (Users, Opportunity, Career, Institution, Funding, Event,
  SavedOpportunity, Notification, Skill, UserSkill), with JWT-based
  authentication protecting any endpoint that reads or writes
  user-specific data.

## Architecture & Tech Stack

**Android:**
- Language: Kotlin
- UI: Fragments + Material Components, Navigation Component
- Networking: Retrofit2 + OkHttp (logging interceptor) + Gson
- Async: Kotlin Coroutines
- State: ViewModel + LiveData
- Secure storage: `androidx.security:security-crypto` (EncryptedSharedPreferences) for the auth token

**Backend:**
- Node.js + Express
- PostgreSQL (hosted on [Neon](https://neon.tech), a free managed Postgres host)
- bcrypt for password hashing
- jsonwebtoken (JWT) for session tokens
- Jest + Supertest for automated tests

## Repository Structure

```
YouthLink_SA/
├── android/          # The Android app (Kotlin, Android Studio project)
├── backend/          # The REST API (Node.js/Express + PostgreSQL)
└── .github/workflows/
    ├── android-ci.yml       # Builds & tests the Android app
    └── backend-tests.yml    # Runs the backend's Jest test suite
```

## Backend / REST API

The backend lives in [`/backend`](./backend) and exposes endpoints for
authentication, profile management, opportunities (search/filter/save),
careers, funding, and events, backed by a PostgreSQL database.

> **Note on hosting:** during development and for this submission, the API
> is run from a local Node process and exposed publicly via an
> [ngrok](https://ngrok.com) HTTPS tunnel, since free managed hosting
> options available at the time all required payment card details to
> provision a database or web service. The database itself (Neon) is a
> genuinely free, permanently hosted PostgreSQL instance — only the API
> server process runs locally. The API code is written to be
> deployment-ready for any standard Node host (Render, Railway, etc.) with
> no code changes required — only the `API_BASE_URL` value in
> `android/app/build.gradle.kts` needs to change to point at wherever it's
> hosted.

Key files:
- `backend/server.js` — entry point, wires up all routes
- `backend/routes/` — one file per feature (auth, profile, opportunities, careers, funding, events, notifications)
- `backend/models/schema.sql` — PostgreSQL schema + sample data
- `backend/__tests__/api.test.js` — Jest + Supertest unit tests

See inline comments in each route file for endpoint-level detail.

## Android App

The Android app lives in [`/android`](./android). It's a single-activity
app using the Navigation Component, with one `Fragment` + `ViewModel` pair
per screen (Welcome, Login, Register, Home, Career Explorer, Study &
Funding, Events, Saved Opportunities, Opportunity Detail, Settings).

## Getting Started

1. Clone this repo.
2. **Backend:**
   ```bash
   cd backend
   npm install
   cp .env.example .env   # fill in your own database credentials + JWT secret
   npm run dev
   ```
   Run `backend/models/schema.sql` against your PostgreSQL database once to create the tables.
3. **Android:**
   - Open the `android` folder in Android Studio.
   - In `android/app/build.gradle.kts`, update `API_BASE_URL` to point at your running backend (e.g. `http://10.0.2.2:3000/` for an emulator talking to a local server, or your ngrok/hosted URL for a physical device).
   - Sync Gradle and run on an emulator or physical device.

## Testing

**Backend:** `cd backend && npm test` — runs the Jest/Supertest suite against a mocked database layer.

**Android:** `cd android && ./gradlew testDebugUnitTest` — runs unit tests covering `ValidationUtils` (registration/login input validation) and the shared `UiState` wrapper, both pure Kotlin with no Android framework dependency, so they run reliably as plain JVM tests.

## GitHub Actions (CI)

Two workflows run automatically on every push/PR to `main`, each scoped to
only run when its own folder changes:

- **`backend-tests.yml`** — installs dependencies and runs the backend's Jest test suite on a clean Ubuntu runner.
- **`android-ci.yml`** — sets up JDK 17, runs the Android unit tests, builds the debug APK, and uploads both the test report and the APK as workflow artifacts.

This proves both halves of the project build and pass their tests outside
of any one developer's machine, not just "on my computer."

## Demo Video

[Add your unlisted YouTube link here once recorded]

## AI Tool Usage

See [`AI_USAGE.md`](./AI_USAGE.md) for a full write-up (under 500 words) on
how AI tools were used during this assessment.

## Author

**Chloe Jade Langenhoven** (ST10435895) — OPSC6312, Part 2 submission.
