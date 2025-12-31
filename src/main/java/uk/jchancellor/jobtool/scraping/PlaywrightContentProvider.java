package uk.jchancellor.jobtool.scraping;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConditionalOnExpression("!${job-tool.mock-boards:false}")
@Slf4j
public class PlaywrightContentProvider implements ContentProvider {

    @Value("${job-tool.debug-save-html:false}")
    private boolean debugSaveHtml;

    public String getContent(String url) {
        try (Playwright playwright = Playwright.create();
                Browser browser = playwright.webkit().launch(
                        new BrowserType.LaunchOptions().setHeadless(true));
                BrowserContext context = browser.newContext()) {
            Page page = context.newPage();
            page.navigate(url, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(30000));
            String content = page.content();

            if (debugSaveHtml) {
                saveHtml(url, content);
            }

            return content;
        }
    }

    private void saveHtml(String url, String content) {
        // Normalize URL for filename: remove protocol, remove www., replace slashes with dashes
        String filename = url
                .replaceFirst("^https?://", "")
                .replaceFirst("^www\\.", "")
                .replace("/", "-")
                + ".html";

        Path htmlPath = Paths.get("example-html-files", filename);

        try {
            Files.createDirectories(htmlPath.getParent());
            Files.writeString(htmlPath, content, StandardCharsets.UTF_8);
            log.info("Saved HTML to: {}", filename);
        } catch (IOException e) {
            log.error("Failed to save HTML: {}", filename, e);
        }
    }
}
