package uk.jchancellor.jobtool.fetching;

import uk.jchancellor.jobtool.jobs.Job;

public interface Fetcher {

    boolean canHandle(String url);

    Job fetch(String url);
}
