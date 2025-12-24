# Web Scraping: Context-Efficient Discovery

> **Note for AI assistants**: These are guidelines that may help you work out how to scrape pages efficiently, but you don't need to follow them strictly if you have a better approach for the specific situation.

> **WebFetch tip**: The WebFetch tool works particularly well for initial reconnaissance - use it to quickly check if a site is server-rendered or JavaScript-rendered, and to understand the page structure before deciding on an approach.

## Core Principle

**Don't load massive HTML files into context.** A typical job listing page can be 945KB (200K+ tokens). Instead: render once, save to disk, then analyze with lightweight tools.

```
Render (browser) → Save (disk) → Explore (grep/awk) → Analyze (Python) → Implement (Java)
```

This uses ~10-20K tokens instead of 200K+ for discovery.

---

## When This Applies

**Use this approach when**:
- The site is JavaScript-rendered (React, Vue, Angular)
- The HTML payload is large (>100KB)
- You need to iterate on selectors

**Skip this if**:
- The site is server-rendered and grep/curl work fine
- The page is small and simple
- The site has a public API you can use instead

**Quick check**: `curl -s "https://example.com/jobs" | grep -o "job-title" | wc -l`
- If zero hits but page shows jobs → needs JavaScript rendering

---

## The Workflow

### 1. Render and Save Once

```java
// In a test or standalone script
String html = page.content();  // Playwright page
Files.writeString(Path.of("/tmp/claude/site.html"), html);
```

**This is the only time you render during discovery.**

### 2. Find Patterns with grep/awk

```bash
# Find container elements
grep -o '<article[^>]*' /tmp/claude/site.html | head -10

# Find class patterns
grep -o 'class="[^"]*job[^"]*"' /tmp/claude/site.html | head -20

# Extract a single complete item for inspection
awk '/<article[^>]*data-at="job-item"/{p=1} p{print} /<\/article>/{if(p){p=0; exit}}' \
    /tmp/claude/site.html > /tmp/claude/single_item.html
```

**Why**: Instant feedback, minimal context usage.

### 3. Enumerate Fields with Python

```python
uv run - <<'EOF'
# /// script
# dependencies = ["beautifulsoup4", "lxml"]
# ///

from bs4 import BeautifulSoup

with open('/tmp/claude/site.html', 'r') as f:
    html = f.read()

soup = BeautifulSoup(html, 'lxml')
item = soup.find('article', {'data-at': 'job-item'})

if item:
    print("=== DATA ATTRIBUTES ===")
    for elem in item.find_all(attrs={'data-at': True}):
        data_at = elem.get('data-at')
        if data_at.startswith('job-item'):
            text = elem.get_text(strip=True)[:100]
            href = elem.get('href', '')
            print(f"{data_at}: {text[:50]}...")
            if href:
                print(f"  -> {href}")
EOF
```

**Why**: Systematic field discovery without loading full HTML into context.

### 4. Check Variations (5-10 Items)

```python
# Check for optional fields, multiple instances, edge cases
items = soup.find_all('article', {'data-at': 'job-item'})[:10]

for i, item in enumerate(items):
    salary = item.find(attrs={'data-at': 'job-item-salary'})
    labels = item.find_all(attrs={'data-at': 'job-item-label'})
    print(f"Item {i}: Has salary: {salary is not None}, Labels: {len(labels)}")
```

**What to look for**: Optional vs required fields, multiple instances, missing data.

### 5. Implement in Java

```java
private List<Job> parseJobs(String html) {
    Document doc = Jsoup.parse(html);
    Elements items = doc.select("article[data-at=job-item]");
    return items.stream().map(this::parseJob).toList();
}

private Job parseJob(Element item) {
    String title = extractText(item, "[data-at=job-item-title]");
    String url = extractHref(item, "[data-at=job-item-title]");
    // ... extract other fields
    return new Job(jobId, title, url, ...);
}

private String extractText(Element parent, String selector) {
    Element elem = parent.selectFirst(selector);
    return elem != null ? elem.text().trim() : "";
}
```

---

## Key Anti-Patterns to Avoid

### ❌ Don't Load Full HTML into Context
```
# BAD: Uses 200K+ tokens
<reads 945KB HTML file>
```

### ❌ Don't Re-render for Each Iteration
Running tests or re-fetching for each selector attempt is slow.

### ❌ Don't Skip Variation Analysis
Checking only one item misses optional fields and edge cases.

---

## Context Savings

| Approach | Tokens | Notes |
|----------|--------|-------|
| Load full HTML | 200K | Fast but wasteful |
| This approach | 10-20K | Same speed, 10x reduction |

---

## Example: Totaljobs

- **Container**: `article[data-at="job-item"]`
- **Fields**: `job-item-title`, `job-item-company-name`, `job-item-location`, `job-item-salary-info`, etc.
- **Job ID**: `id` attribute on article
- **Variations**: Salary optional, labels 0-N items
- **Tools**: WebFetch → Playwright → grep → Python → Jsoup
- **Context saved**: ~180K tokens

---

## TL;DR

The most expensive operation (rendering) should be done exactly once. Everything else operates on the saved file with the lightest tool that works.
