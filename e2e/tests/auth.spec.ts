import { test, expect } from '@playwright/test';

function uniqueUsername(prefix: string) {
  return `${prefix}_${Date.now() % 1_000_000}`;
}

test.describe('Authentication', () => {

  test('a new user can register and lands on the feed', async ({ page }) => {
    const username = uniqueUsername('e2e_register');

    await page.goto('/register');

    await page.fill('#username', username);
    await page.fill('#passwordHash', 'aLongEnoughPassword1');
    await page.click('button[type="submit"]');

    // Successful register logs the user in and redirects to "/feed"
    await expect(page).toHaveURL('/feed');
  });

  test('an existing user can log in', async ({ page }) => {
    const username = uniqueUsername('e2e_login');
    const password = 'aLongEnoughPassword1';

    // Register first so we have a known account to log in with.
    await page.goto('/register');
    await page.fill('#username', username);
    await page.fill('#passwordHash', password);
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/feed');

    // Log out isn't wired here, so verify login directly via a fresh
    // navigation to /login instead of relying on a logout button.
    await page.goto('/login');
    await page.fill('#username', username);
    await page.fill('#password', password);
    await page.click('button[type="submit"]');

    await expect(page).toHaveURL('/feed');
  });

  test('login shows an error message on wrong credentials', async ({ page }) => {
    await page.goto('/login');

    await page.fill('#username', 'no_such_user_e2e');
    await page.fill('#password', 'whateverPassword1');
    await page.click('button[type="submit"]');

    await expect(page.locator('.alert-danger')).toContainText('Invalid username or password');
    await expect(page).toHaveURL('/login');
  });

});
