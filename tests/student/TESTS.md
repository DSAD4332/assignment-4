# Assignment 4 — TESTS

These are request fixtures for the eight commands in `CONTRACTS.md`. Each request is a single JSON object, as required by the JSON Lines interface. The expected and actual values below were checked by calling the corresponding `Algorithms` method with the request fields represented as Java maps/lists.

## How to run the local checks

The small Java test runner used for these checks is `AlgorithmsTestRunner.java`.

```bash
javac Java/source/*.java
java -cp Java/source AlgorithmsTestRunner
```

Compilation succeeded. The compiler printed only the unchecked-operation note caused by casting parsed edge arrays in `tree`. All 24 assertions passed. The actual outputs below are the outputs observed in that run. The map/list display from the runner is shown as equivalent JSON for readability.

## Fixtures and results

### 1. LCS

**Fixture `lcs-example`**

Request:
```json
{"task":"lcs","a":"tea_time","b":"team"}
```
Expected:
```json
{"length":4,"sequence":"team"}
```
Actual:
```json
{"length":4,"sequence":"team"}
```
Status: PASS. Detects an implementation that finds only a contiguous substring, or calculates the length but fails to reconstruct a subsequence.

**Fixture `lcs-empty`**

Request:
```json
{"task":"lcs","a":"","b":"abc"}
```
Expected:
```json
{"length":0,"sequence":""}
```
Actual:
```json
{"length":0,"sequence":""}
```
Status: PASS. Detects missing zero-prefix initialization or out-of-bounds reconstruction when one input is empty.

### 2. Adjacent block merge

**Fixture `merge-example`**

Request:
```json
{"task":"merge","sizes":[2,3,4]}
```
Expected:
```json
{"cost":14,"plan":[[0,0,1],[0,1,2]]}
```
Actual:
```json
{"cost":14,"plan":[[0,0,1],[0,1,2]]}
```
Status: PASS. Detects greedy merging of arbitrary smallest blocks, incorrect interval costs, and a plan that merges a parent interval before its child intervals exist.

**Fixture `merge-empty`**

Request:
```json
{"task":"merge","sizes":[]}
```
Expected:
```json
{"cost":0,"plan":[]}
```
Actual:
```json
{"cost":0,"plan":[]}
```
Status: PASS. Detects indexing `dp[0][n-1]` without handling an empty input.

### 3. Tree maximum-weight independent set

**Fixture `tree-example`**

Request:
```json
{"task":"tree","n":3,"weights":[4,9,6],"edges":[[0,1],[1,2]]}
```
Expected:
```json
{"weight":10,"selected":[0,2]}
```
Actual:
```json
{"weight":10,"selected":[0,2]}
```
Status: PASS. Detects selecting adjacent vertices together, or using a locally greedy choice instead of take/skip DP.

**Fixture `tree-negative`**

Request:
```json
{"task":"tree","n":3,"weights":[-5,-2,-9],"edges":[[0,1],[1,2]]}
```
Expected:
```json
{"weight":0,"selected":[]}
```
Actual:
```json
{"weight":0,"selected":[]}
```
Status: PASS. Detects an implementation that incorrectly forces at least one vertex to be selected even though the empty set is allowed.

**Fixture `tree-empty`**

Request:
```json
{"task":"tree","n":0,"weights":[],"edges":[]}
```
Expected:
```json
{"weight":0,"selected":[]}
```
Actual:
```json
{"weight":0,"selected":[]}
```
Status: PASS. Detects root access or array access on an empty tree.

### 4. Naive string matching

**Fixture `naive-overlap`**

Request:
```json
{"task":"naive","text":"aaaaa","pattern":"aaa"}
```
Expected:
```json
{"positions":[0,1,2]}
```
Actual:
```json
{"positions":[0,1,2]}
```
Status: PASS. Detects skipping ahead by the full pattern length after a match, which would lose overlapping occurrences.

**Fixture `naive-empty-pattern`**

Request:
```json
{"task":"naive","text":"ab","pattern":""}
```
Expected:
```json
{"positions":[0,1,2]}
```
Actual:
```json
{"positions":[0,1,2]}
```
Status: PASS. Detects omission of the final text boundary or invalid pattern indexing for an empty pattern.

**Fixture `naive-too-long`**

Request:
```json
{"task":"naive","text":"ab","pattern":"abc"}
```
Expected:
```json
{"positions":[]}
```
Actual:
```json
{"positions":[]}
```
Status: PASS. Detects checking a candidate start when the pattern cannot fit in the text.

### 5. KMP string matching

**Fixture `kmp-overlap`**

Request:
```json
{"task":"kmp","text":"abababa","pattern":"aba"}
```
Expected:
```json
{"positions":[0,2,4]}
```
Actual:
```json
{"positions":[0,2,4]}
```
Status: PASS. Detects resetting the matched-prefix length to zero after a full match instead of falling back to `pi[m-1]`.

**Fixture `kmp-empty-pattern`**

Request:
```json
{"task":"kmp","text":"ab","pattern":""}
```
Expected:
```json
{"positions":[0,1,2]}
```
Actual:
```json
{"positions":[0,1,2]}
```
Status: PASS. Detects pattern indexing before the empty-pattern case is handled.

**Fixture `kmp-too-long`**

Request:
```json
{"task":"kmp","text":"ab","pattern":"abc"}
```
Expected:
```json
{"positions":[]}
```
Actual:
```json
{"positions":[]}
```
Status: PASS. Detects failure to reject a pattern longer than the text before search begins.

### 6. Rabin–Karp string matching

**Fixture `rk-collision`**

Request:
```json
{"task":"rk","text":"abdj","pattern":"ab"}
```
Expected:
```json
{"positions":[0]}
```
Actual:
```json
{"positions":[0]}
```
Status: PASS. Detects reporting a hash match as a real occurrence without comparing the actual characters. With base 31 and modulus 101, the window `dj` collides with pattern `ab` but is not a match.

**Fixture `rk-overlap`**

Request:
```json
{"task":"rk","text":"aaaaa","pattern":"aaa"}
```
Expected:
```json
{"positions":[0,1,2]}
```
Actual:
```json
{"positions":[0,1,2]}
```
Status: PASS. Detects an incorrect rolling-window step that skips overlapping windows.

**Fixture `rk-empty-pattern`**

Request:
```json
{"task":"rk","text":"ab","pattern":""}
```
Expected:
```json
{"positions":[0,1,2]}
```
Actual:
```json
{"positions":[0,1,2]}
```
Status: PASS. Detects attempting to calculate a rolling hash or highest power for a zero-length pattern.

### 7. Suffix array and Kasai LCP

**Fixture `suffix-example`**

Request:
```json
{"task":"suffix","text":"banana"}
```
Expected:
```json
{"sa":[5,3,1,0,4,2],"lcp":[0,1,3,0,0,2],"repeated":"ana"}
```
Actual:
```json
{"sa":[5,3,1,0,4,2],"lcp":[0,1,3,0,0,2],"repeated":"ana"}
```
Status: PASS. Detects incorrect suffix ordering, off-by-one LCP indexing, or finding only non-overlapping repeated substrings.

**Fixture `suffix-empty`**

Request:
```json
{"task":"suffix","text":""}
```
Expected:
```json
{"sa":[],"lcp":[],"repeated":""}
```
Actual:
```json
{"sa":[],"lcp":[],"repeated":""}
```
Status: PASS. Detects adding an empty suffix/sentinel or accessing the first suffix when the input is empty.

**Fixture `suffix-no-repeat`**

Request:
```json
{"task":"suffix","text":"abc"}
```
Expected:
```json
{"sa":[0,1,2],"lcp":[0,0,0],"repeated":""}
```
Actual:
```json
{"sa":[0,1,2],"lcp":[0,0,0],"repeated":""}
```
Status: PASS. Detects returning an arbitrary character when every adjacent LCP is zero.

### 8. Aho–Corasick

**Fixture `aho-example`**

Request:
```json
{"task":"aho","patterns":["he","she","he"],"texts":["sheshe","sheshe",""]}
```
Expected:
```json
{"counts":[[2,2,2],[2,2,2],[0,0,0]]}
```
Actual:
```json
{"counts":[[2,2,2],[2,2,2],[0,0,0]]}
```
Status: PASS. Detects failure to count suffix-pattern matches (`he` inside `she`), failure to preserve duplicate dictionary positions, or failure to reset visits between independent texts.

**Fixture `aho-empty-dictionary`**

Request:
```json
{"task":"aho","patterns":[],"texts":["abc",""]}
```
Expected:
```json
{"counts":[[],[]]}
```
Actual:
```json
{"counts":[[],[]]}
```
Status: PASS. Detects omission of an empty result row for each text when the dictionary has no patterns.

**Fixture `aho-empty-text`**

Request:
```json
{"task":"aho","patterns":["a","bc"],"texts":[""]}
```
Expected:
```json
{"counts":[[0,0]]}
```
Actual:
```json
{"counts":[[0,0]]}
```
Status: PASS. Detects missing zero counts for a text with no characters.

### Cross-check: three exact-search algorithms

**Fixture `search-algorithms-agree`**

Request used for all three methods:
```json
{"text":"abababaabababa","pattern":"aba"}
```
Expected positions from the naive baseline:
```json
[0,2,4,7,9,11]
```
Actual Naive:
```json
[0,2,4,7,9,11]
```
Actual KMP:
```json
[0,2,4,7,9,11]
```
Actual Rabin–Karp:
```json
[0,2,4,7,9,11]
```
Status: PASS. Detects disagreement between the optimized matchers and the straightforward baseline on a shared input.

## Test run summary

| Metric | Result |
|---|---:|
| Java compilation | Successful |
| Individual algorithm fixtures | 22 passed |
| Cross-algorithm comparisons | 2 passed |
| Total assertions | 24 passed |
| Failed assertions | 0 |

These are functional correctness checks on representative examples and boundary cases. They do not replace the required large-input tests or the timing experiment in `ANALYSIS.md`.
