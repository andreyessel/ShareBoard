import { defineConfig, devices } from '@playwright/test';

/**
 * Points at the ShareBoard React app running locally via `npm run dev`
 * in SB_client (default Vite port 5173). Override with BASE_URL if
 * your dev server runs on a different port, or if you're testing
 * against a deployed environment.
 *
 * The backend (SB_server) must also be running on localhost:8080
 * for these flows to actually create/read data.
 */
export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  retries: 0,
  reporter: [['html', { open: 'never' }]],
  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:5173',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
