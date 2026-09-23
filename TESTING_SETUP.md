# Adding REST Assured/TestNG + Playwright to ShareBoard

## 1. Where each file goes

Copy these into your existing ShareBoard repo, keeping the same relative paths:

```
SB_server/pom.xml                                                → REPLACES your existing pom.xml
SB_server/testng.xml                                             → NEW, same folder as pom.xml
SB_server/src/test/java/server/shareboard/api/ApiTestSupport.java → NEW
SB_server/src/test/java/server/shareboard/api/AuthApiIT.java      → NEW
SB_server/src/test/java/server/shareboard/api/CategoryApiIT.java  → NEW
SB_server/src/test/java/server/shareboard/api/UserApiIT.java      → NEW
SB_server/src/test/java/server/shareboard/api/ListingApiIT.java   → NEW
SB_server/src/test/java/server/shareboard/api/MatchApiIT.java     → NEW

e2e/                                                              → NEW top-level folder,
                                                                      sibling to SB_client and SB_server
  package.json
  playwright.config.ts
  tests/auth.spec.ts
  tests/listing-flow.spec.ts

.github/workflows/api-tests.yml                                   → NEW, same folder as docker-build.yml
.github/workflows/e2e-tests.yml                                   → NEW, same folder as docker-build.yml
```

Note the `IT` suffix on the new test classes (`AuthApiIT`, not `AuthApiTest`)
— that's deliberate, explained below.

## 2. Why `IT` and not `Test`, and why Failsafe instead of Surefire

Your existing repository/service tests (`ListingJdbcClientRepositoryTest`,
`UserJdbcClientRepositoryTest`, `CategoryJdbcClientRepositoryTest`,
`MatchLineJdbcClientRepositoryTest`, `ListingServiceTest`, `UserServiceTest`)
connect directly to a database and call `set_known_good_state()` — a stored
procedure that only exists in `shareboard_test` (built by
`sql/test-schema.sql`). They run under Maven Surefire, triggered by `mvn test`.

The new REST Assured/TestNG suite is a different kind of test — it hits a
**running instance of the app** over HTTP, needs the real `shareboard`
database (with your actual seed data) rather than the test one, and uses
TestNG instead of JUnit. Wiring it into the same `mvn test`/Surefire run as
your JUnit tests would risk two things: Maven's provider auto-detection
getting confused between JUnit and TestNG, and — more concretely — your
existing tests failing because they'd suddenly be pointed at the wrong
database.

So instead: the new classes are named `AuthApiIT`, `ListingApiIT`, etc.
(the `IT` suffix is a Maven convention), and `pom.xml` adds the
**maven-failsafe-plugin**, bound to the `integration-test`/`verify` phases,
configured to run `testng.xml`. Surefire's default naming pattern doesn't
match `*IT.java`, and Failsafe's default pattern does — so the two suites
stay completely separate by construction. `mvn test` still runs only your
original JUnit tests, unchanged.

`ApiTestSupport.java` is the shared base class (setting REST Assured's base
URL) — it doesn't end in `Test` or `IT` on purpose, so neither plugin tries
to run it directly as a test.

## 3. Running the REST Assured / TestNG suite locally

These are black-box tests — they call a **running** instance of your app
over HTTP, against the real `shareboard` database (not `shareboard_test`).

```bash
# Terminal 1 — start the app against your normal shareboard DB,
# same env vars you already use for local dev
cd SB_server
mvn spring-boot:run

# Terminal 2 — once the app is up, run the API suite
cd SB_server
mvn verify -Dsurefire.skip=true
```

`-Dsurefire.skip=true` matters: `mvn verify` runs the full lifecycle
including the `test` phase, which would otherwise re-run your JUnit tests —
but at this point your terminal's `DB_URL` is pointed at `shareboard`
(for the running app), not `shareboard_test`, so those would fail for an
unrelated reason. The flag skips Surefire and runs only Failsafe (the new
API suite).

Your existing `mvn test` keeps working exactly as before, against
`shareboard_test`, with no flags needed — nothing about that changed.

## 4. Running the Playwright suite

This needs **both** the backend and the frontend running locally, using the
same `shareboard` database as the API suite above:

```bash
# Terminal 1
cd SB_server && mvn spring-boot:run

# Terminal 2
cd SB_client && npm run dev

# Terminal 3 — first time only
cd e2e && npm install && npx playwright install --with-deps chromium

# Terminal 3 — run the tests
cd e2e && npm test
```

`npm test` opens an HTML report on failure (`npm run report` to reopen it
later). The tests assume your Vite dev server is on the default port 5173 —
override with `BASE_URL=http://localhost:XXXX npm test` if yours differs.

## 5. What's covered

- **AuthApiIT** — register (happy path, empty fields, short password,
  duplicate username), login (happy path, wrong password, unknown user,
  empty credentials)
- **CategoryApiIT** — list all, find by id, 404 on unknown id
- **UserApiIT** — find by id, find by username, invalid/nonexistent ids
- **ListingApiIT** — create (NEED + SURPLUS, validation errors), find by
  id/all/by-user, update (ownership + status rules), delete/withdraw,
  match history
- **MatchApiIT** — recent matches with default/custom/out-of-range limits
- **auth.spec.ts** — register flow, login flow, invalid-login error message
- **listing-flow.spec.ts** — posting a NEED listing through the UI, and a
  two-browser-context test that posts a matching NEED + SURPLUS and asserts
  the live WebSocket match toast appears without a page refresh

## 6. Wiring it into CI (GitHub Actions)

Two new workflow files, both go in the same folder as your existing
`docker-build.yml`:

```
ShareBoard/.github/workflows/api-tests.yml   → runs on every push/PR to main
ShareBoard/.github/workflows/e2e-tests.yml   → runs on every push/PR to main
```

**`api-tests.yml`** runs in two phases, matching the local split above:
1. Creates `shareboard_test` from `test-schema.sql` and runs `mvn test` —
   your existing JUnit/Mockito suite, unchanged, same as it runs for you
   locally today.
2. Creates and seeds a separate `shareboard` database, builds and starts
   the app against it, then runs `mvn verify -Dsurefire.skip=true` — the
   new REST Assured/TestNG suite, against the live app.

Both phases' reports (Surefire and Failsafe) upload as a build artifact
regardless of outcome, so a failed run tells you exactly which test failed.

**`e2e-tests.yml`** seeds `shareboard`, builds and starts the backend,
builds and serves the React frontend, then runs the Playwright suite
against both. It never touches `shareboard_test` or runs `mvn test`, so
it's unaffected by the split above. The HTML report uploads as an artifact
on failure with screenshots/traces of what broke.

Both workflows trigger on push and pull request to `main` — so once these
are in place, every PR gets a pass/fail check automatically, which is
exactly the "quality gate in CI/CD" line from the job posting.

One thing worth double-checking once these run for real: `mvn spring-boot:run`
in the background needs a moment to fully come up — the wait loops in both
workflows are generous, but if the CI runner is slow you may need to bump
the retry counts.

## 7. One thing worth fixing separately

`shareboard-key.pem` (an RSA private key, looks like your EC2 SSH key) is
currently committed in your repo's git history. That's worth rotating/
revoking on the AWS side and scrubbing from git history (`git filter-repo`
or BFG Repo-Cleaner), then adding `*.pem` to `.gitignore` — not something to
mention in an interview repo. Happy to walk through that if useful.
