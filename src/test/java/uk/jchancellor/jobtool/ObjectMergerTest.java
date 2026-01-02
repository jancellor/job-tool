package uk.jchancellor.jobtool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import uk.jchancellor.jobtool.api.ObjectMerger;
import uk.jchancellor.jobtool.jobs.Job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
@Import(ObjectMerger.class)
class ObjectMergerTest {

    @Autowired
    private ObjectMerger objectMerger;

    @Test
    public void testMerging() {
        Job original = Job.builder().description(null).build();
        Job overrides = Job.builder().description("Desc").build();
        Job mergedJob = objectMerger.merge(original, overrides);
        assertEquals("Desc", mergedJob.getDescription());
        assertNull(original.getDescription());
    }

    @Test
    void testNulls() {
        Job job = Job.builder().build();
        assertNotNull(objectMerger.merge(job, null));
        assertNotNull(objectMerger.merge(null, job));
    }
}
