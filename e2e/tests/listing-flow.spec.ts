import { test, expect, Page } from '@playwright/test';

function uniqueUsername(prefix: string) {
  return `${prefix}_${Date.now() % 1_000_000}`;
}

async function registerAndLogin(page: Page, username: string) {
  await page.goto('/register');
  await page.fill('#username', username);
  await page.fill('#passwordHash', 'aLongEnoughPassword1');
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL('/feed');
}

test.describe('Listing lifecycle', () => {

  test('a user can post a NEED listing and see it on the feed', async ({ page }) => {
    const username = uniqueUsername('e2e_need_user');
    await registerAndLogin(page, username);

    await page.goto('/new-listing');

    await page.click('label[for="typeNeed"]');
    await page.selectOption('#categoryId', { label: 'Blankets' });
    await page.fill('#quantity', '5');
    await page.click('label[for="urgencyHIGH"]');

    const neededBy = new Date();
    neededBy.setDate(neededBy.getDate() + 14);
    await page.fill('#deadline', neededBy.toISOString().slice(0, 10));

    await page.click('button[type="submit"]');

    // Successful create redirects back to the feed ("/feed")
    await expect(page).toHaveURL('/feed');
  });

  test('a NEED and a matching SURPLUS listing trigger the live match toast', async ({ browser }) => {
    // Two separate browser contexts = two separate logged-in users,
    // both watching the same feed at the same time (this is what
    // actually exercises the WebSocket push).
    const neederContext = await browser.newContext();
    const giverContext = await browser.newContext();
    const neederPage = await neederContext.newPage();
    const giverPage = await giverContext.newPage();

    const neederName = uniqueUsername('e2e_needer');
    const giverName = uniqueUsername('e2e_giver');

    await registerAndLogin(neederPage, neederName);
    await registerAndLogin(giverPage, giverName);

    // Needer posts a NEED for Canned Food and stays on the feed to watch for the match.
    await neederPage.goto('/new-listing');
    await neederPage.click('label[for="typeNeed"]');
    await neederPage.selectOption('#categoryId', { label: 'Canned Food' });
    await neederPage.fill('#quantity', '4');
    await neederPage.click('label[for="urgencyMEDIUM"]');
    const neededBy = new Date();
    neededBy.setDate(neededBy.getDate() + 10);
    await neederPage.fill('#deadline', neededBy.toISOString().slice(0, 10));
    await neederPage.click('button[type="submit"]');
    await expect(neederPage).toHaveURL('/feed');

    // Giver posts a SURPLUS in the same category with an availableUntil
    // date after the need's deadline, so the matching engine pairs them.
    await giverPage.goto('/new-listing');
    await giverPage.click('label[for="typeSurplus"]');
    await giverPage.selectOption('#categoryId', { label: 'Canned Food' });
    await giverPage.fill('#quantity', '4');
    const availableUntil = new Date();
    availableUntil.setDate(availableUntil.getDate() + 30);
    await giverPage.fill('#deadline', availableUntil.toISOString().slice(0, 10));
    await giverPage.click('button[type="submit"]');
    await expect(giverPage).toHaveURL('/feed');

    // The needer's feed (still open) should receive the WebSocket push
    // and show the match toast without any manual refresh.
    await expect(neederPage.locator('.sb-match-toast')).toBeVisible({ timeout: 10_000 });
    await expect(neederPage.locator('.sb-match-toast-title')).toHaveText('Match found!');

    await neederContext.close();
    await giverContext.close();
  });

});
