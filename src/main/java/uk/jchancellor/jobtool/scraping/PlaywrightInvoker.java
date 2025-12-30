package uk.jchancellor.jobtool.scraping;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;

public class PlaywrightInvoker {

    public static String getContent(String url) {
        try (Playwright playwright = Playwright.create();
                Browser browser = playwright.webkit().launch(
                        new BrowserType.LaunchOptions().setHeadless(true));
                BrowserContext context = browser.newContext()) {
            Page page = context.newPage();
            page.navigate(url, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(30000));
            return page.content();
        }
    }
}
