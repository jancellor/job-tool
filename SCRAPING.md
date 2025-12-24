# Web Scraping Discovery Guide

This document describes a context-efficient methodology for discovering how to scrape websites, learned from implementing the Totaljobs scraper.

## The Core Problem

Modern websites can have massive HTML payloads (e.g., 945KB for Totaljobs = 200K+ tokens). Loading the full HTML into context for analysis is extremely wasteful and can quickly exhaust token budgets.

## The Solution: Render Once, Analyze Repeatedly

**Key Insight**: Decouple expensive rendering from cheap analysis.

```
Render (browser) → Save (disk) → Explore (lightweight tools) → Analyze (structured tools) → Implement (production)
```

This approach uses ~10-20K tokens instead of 200K+ for the discovery phase.

---

## Three-Phase Methodology

### Phase 1: Reconnaissance

**Goal**: Determine if the site requires JavaScript rendering.

**Tools**: WebFetch or simple HTTP client

**Steps**:
1. Fetch a sample page URL
2. Check if target data exists in raw HTML
3. Look for indicators:
   - React/Vue/Angular markers (`__PRELOADED_STATE__`, `data-reactroot`, etc.)
   - Empty placeholder divs
   - "Loading..." text
   - JSON data in `<script>` tags

**Example**:
```bash
# Quick check for server-side vs client-side rendering
curl -s "https://example.com/jobs" | grep -o "job-title" | wc -l
# If zero hits but page shows jobs → needs JavaScript rendering
```

**Decision Point**:
- Static HTML → Use plain HTTP client + HTML parser
- JavaScript-rendered → Need browser automation (Playwright/Selenium)

---

### Phase 2: Discovery (Context-Efficient)

**Goal**: Find CSS selectors and data structure without exhausting context.

#### Step 1: Save Rendered HTML Once

Use Playwright/Selenium to render and save to a temporary file:

```java
// In your scraper test
String html = page.content();
Files.writeString(Path.of("/tmp/claude/site.html"), html);
```

**Critical**: This is the ONLY time you render the page during discovery.

#### Step 2: Lightweight Text Exploration

Use bash tools to find patterns without loading full HTML into context:

```bash
# Find elements by class patterns
grep -o 'class="[^"]*job[^"]*"' /tmp/claude/site.html | head -20

# Find specific tags
grep -o '<article[^>]*' /tmp/claude/site.html | head -10

# Find data attributes
grep -oP 'data-[a-z-]+="[^"]*"' /tmp/claude/site.html | sort | uniq

# Extract complete element (container + children)
awk '/<article[^>]*data-at="job-item"/{p=1} p{print} /<\/article>/{if(p){p=0; exit}}' \
    /tmp/claude/site.html > /tmp/claude/single_item.html
```

**Benefits**:
- Fast iteration (milliseconds)
- Minimal context usage (show only relevant snippets)
- Pattern discovery without parsing

#### Step 3: Structured Analysis with Python

Once you've identified container elements, use BeautifulSoup to enumerate fields:

```python
uv run - <<'EOF'
# /// script
# dependencies = [
#   "beautifulsoup4",
#   "lxml",
# ]
# ///

from bs4 import BeautifulSoup

with open('/tmp/claude/site.html', 'r') as f:
    html = f.read()

soup = BeautifulSoup(html, 'lxml')

# Find first item
item = soup.find('article', {'data-at': 'job-item'})

if item:
    # Enumerate all data-at attributes
    print("=== DATA ATTRIBUTES ===")
    for elem in item.find_all(attrs={'data-at': True}):
        data_at = elem.get('data-at')
        if data_at.startswith('job-item'):
            text = elem.get_text(strip=True)[:100]
            href = elem.get('href', '')
            print(f"{data_at}:")
            if href:
                print(f"  href: {href}")
            if text:
                print(f"  text: {text}")
EOF
```

**Benefits**:
- Systematic field discovery
- Small, focused output
- Easy to iterate and refine

#### Step 4: Check for Variations

Analyze multiple items to find optional fields, variations, and edge cases:

```python
# Check first 5 items for variations
items = soup.find_all('article', {'data-at': 'job-item'})[:5]

for i, item in enumerate(items):
    print(f"=== ITEM {i+1} ===")

    # Check optional fields
    salary = item.find(attrs={'data-at': 'job-item-salary'})
    print(f"Has salary: {salary is not None}")

    # Check for multiple elements (e.g., labels)
    labels = item.find_all(attrs={'data-at': 'job-item-label'})
    print(f"Labels: {len(labels)}")
```

**What to look for**:
- Optional vs required fields
- Multiple instances (labels, tags, badges)
- Different formats (annual vs daily salary)
- Edge cases (missing data, "Not specified")

---

### Phase 3: Implementation

**Goal**: Translate discoveries into production code.

#### Data Model First

Create a record/class with ALL discovered fields:

```java
public record Job(
    String jobId,        // From id attribute
    String title,        // From data-at selector
    String url,          // From href (make absolute)
    String company,      // Direct text extraction
    String location,     // Direct text extraction
    String salary,       // Can be empty - handle gracefully
    String description,  // May be truncated
    String postedAgo,    // Relative time string
    List<String> labels  // Can be empty list
) {}
```

**Key Principles**:
- Capture raw data as-is (don't parse dates, salaries, etc.)
- Use empty strings/lists for missing data (not null)
- Make all fields explicit (avoid generic maps)

#### Implement Parsers

```java
private List<Job> parseJobs(String html) {
    Document doc = Jsoup.parse(html);
    Elements items = doc.select("article[data-at=job-item]");

    return items.stream()
        .map(this::parseJob)
        .toList();
}

private Job parseJob(Element item) {
    String jobId = item.attr("id");
    String title = extractText(item, "[data-at=job-item-title]");
    // ... extract other fields

    return new Job(jobId, title, url, ...);
}
```

#### Handle Edge Cases

```java
private String extractText(Element parent, String selector) {
    Element elem = parent.selectFirst(selector);
    return elem != null ? elem.text().trim() : "";
}

private List<String> extractLabels(Element parent) {
    return parent.select("[data-at=job-item-label]").stream()
        .map(Element::text)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
}
```

---

## Tool Selection by Phase

| Phase | Tool | Why |
|-------|------|-----|
| Reconnaissance | WebFetch, curl | Fast, minimal context |
| Save HTML | Playwright/Selenium | Handles JavaScript rendering |
| Pattern finding | grep, awk | Instant, zero context overhead |
| Structure analysis | Python + BeautifulSoup | Flexible, iterative exploration |
| Production | Java + Jsoup (or target language) | Type-safe, maintainable |

---

## Common Pitfalls to Avoid

### ❌ DON'T: Load Full HTML into Context

```
# BAD: This uses 200K+ tokens
assistant: "Let me read the entire HTML and analyze it..."
<reads 945KB HTML file>
```

### ✅ DO: Save and Analyze with Targeted Tools

```
# GOOD: Uses ~1K tokens per query
$ grep -A 5 "data-at=\"job-item\"" /tmp/claude/site.html | head -20
```

### ❌ DON'T: Re-render for Each Selector Attempt

Running `mvn test` or re-fetching the page for each iteration is slow and wasteful.

### ✅ DO: Save Once, Iterate Rapidly

Save rendered HTML once, then run 10+ analysis commands against the saved file.

### ❌ DON'T: Skip Variation Analysis

Analyzing only one item can miss optional fields, multiple instances, or edge cases.

### ✅ DO: Check 5-10 Items for Variations

```python
# Check first 10 items for missing salaries, multiple labels, etc.
for item in items[:10]:
    # Check for variations
```

### ❌ DON'T: Couple Related Data Points

```java
// BAD: Using href for both title and URL
String title = extractHref(item, "a.job-link");  // href, not title!
```

### ✅ DO: Separate Concerns

```java
// GOOD: Extract text and href separately
String title = extractText(item, "[data-at=job-item-title]");
String url = extractHref(item, "[data-at=job-item-title]");
```

---

## Workflow Checklist

Use this checklist when scraping a new site:

- [ ] **Reconnaissance**: Determine if JavaScript rendering is needed
- [ ] **Render once**: Use Playwright to save HTML to `/tmp/claude/site.html`
- [ ] **Find containers**: Use grep/awk to locate item wrapper elements
- [ ] **Find fields**: Use Python script to enumerate all data attributes
- [ ] **Check variations**: Analyze 5-10 items for optional fields and edge cases
- [ ] **Design model**: Create record/class with ALL discovered fields
- [ ] **Implement parser**: Write production code with proper error handling
- [ ] **Test edge cases**: Verify empty fields, missing data, multiple instances

---

## Context Usage Comparison

| Approach | Context Used | Time |
|----------|--------------|------|
| Load full HTML | 200K tokens | Fast |
| This methodology | 10-20K tokens | Fast |
| **Savings** | **10x reduction** | Same speed |

---

## Example: Totaljobs Discovery

**What we found**:
- Container: `article[data-at="job-item"]`
- Fields with `data-at` prefix: `job-item-title`, `job-item-company-name`, `job-item-location`, `job-item-salary-info`, `job-item-middle`, `job-item-timeago`, `job-item-top-label`
- Job ID: `id` attribute on article
- Variations: Salary can be missing, labels can be 0-N items

**Tools used**:
1. WebFetch → confirmed React rendering needed
2. Playwright → saved 945KB HTML
3. `grep -o '<article[^>]*'` → found container
4. BeautifulSoup → enumerated all `data-at` attributes
5. Second Python script → checked 5 jobs for variations
6. Jsoup → production implementation

**Context saved**: ~180K tokens during discovery

---

## Key Takeaway

**The most expensive operation (rendering) should be done exactly once. Everything else should operate on the saved result using the lightest tool that gets the job done.**

Render → Save → Grep → Python → Production Code
