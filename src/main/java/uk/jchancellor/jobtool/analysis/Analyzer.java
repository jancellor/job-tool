package uk.jchancellor.jobtool.analysis;

import uk.jchancellor.jobtool.jobs.Job;

public interface Analyzer {

    boolean canHandle(Job job);

    Job analyze(Job job);
}
