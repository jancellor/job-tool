# Test Fixtures

This directory contains HTML fixtures for testing the job scraping functionality without hitting real job boards.

## How to Use

The fixture files are loaded by `FixtureContentProvider` when the `job-tool.mock-boards` property is set to `true`.

### URL Matching

Fixture files are matched to URLs after normalization. The normalization process:
1. Removes protocol (`http://` or `https://`)
2. Removes `www.`
3. Replaces slashes `/` with dashes `-`

The fixture filename (without `.html` extension) must be contained in the normalized URL.

**Examples:**
- URL: `https://www.totaljobs.com/job/106375756/developer`
- Normalized: `totaljobs.com-job-106375756-developer`
- Matches fixture: `totaljobs.com-job-106375756.html` ✓

- URL: `https://www.totaljobs.com/jobs/contract/python?page=1`
- Normalized: `totaljobs.com-jobs-contract-python?page=1`
- Matches fixture: `totaljobs.com-jobs.html` ✓

### Enabling Mock Boards

**In application.properties or application-local.properties:**
```properties
job-tool.mock-boards=true
```

**From command line:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--job-tool.mock-boards=true
```

**From IDE:**
Add VM option: `-Djob-tool.mock-boards=true`

### Adding New Fixtures

1. Save the HTML content from a job board page
2. Normalize the URL (remove protocol and www., replace `/` with `-`)
3. Pick a unique identifying part from the normalized URL
4. Name the file with that pattern and place in `src/main/resources/fixtures/`

**Example:**
- URL: `https://www.cwjobs.co.uk/job/123456/senior-developer`
- Normalized: `cwjobs.co.uk-job-123456-senior-developer`
- Fixture name: `cwjobs.co.uk-job-123456.html` (includes enough to uniquely identify the job)

### Testing

Tests can set the `job-tool.mock-boards` property to automatically use fixture data:

```java
@SpringBootTest(properties = {"job-tool.mock-boards=true"})
class MyTest {
    @Autowired
    private ContentProvider contentProvider;

    @Test
    void test() {
        // contentProvider will return fixture data instead of making real HTTP calls
    }
}
```
