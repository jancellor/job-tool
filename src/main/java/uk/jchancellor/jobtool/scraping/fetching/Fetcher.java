package uk.jchancellor.jobtool.scraping.fetching;

import uk.jchancellor.jobtool.jobs.Job;

public interface Fetcher {

    boolean canHandle(String url);

    Job fetch(String url);
}
