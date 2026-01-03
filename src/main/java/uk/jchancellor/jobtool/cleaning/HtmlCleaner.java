package uk.jchancellor.jobtool.cleaning;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class HtmlCleaner {

    public String clean(String html) {
        if (html == null) {
            return null;
        }
        return Jsoup.clean(html, Safelist.basic()).trim();
    }
}
