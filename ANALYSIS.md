# Assignment 4 — analysis


## Environment and commands

- Language: Microsoft OpenJDK 21.0.12
- OS: Windows 10
- CPU: Intel Core i3-5005U 2 GHz
- RAM: 4 GB
- Build: `javac Java/source/*.java`
- Test: `java -cp Java/source AlgorithmsTestRunner`
- Run: `java -cp Java/source Main`

## Algorithm explanations

### 1. LCS — prefix dynamic programming

**State.** `dp[i][j]` is the length of a longest common subsequence of the prefixes `a[0..i)` and `b[0..j)`. Both indices count characters, so the final answer is `dp[a.length()][b.length()]`.

**Base cases.** `dp[0][j] = 0` and `dp[i][0] = 0`: an empty sequence has no nonempty common subsequence.

**Transition.** If the final characters of the two prefixes match, set `dp[i][j] = dp[i-1][j-1] + 1`. Otherwise, the final character of at least one prefix is not used, so take `max(dp[i-1][j], dp[i][j-1])`.

**Order and correctness.** Fill rows and columns in increasing order. Every dependency has a smaller prefix length and is already computed. The transition considers the matching-final-character case or both ways an unmatched final character can be omitted, so it obtains an optimum.

**Reconstruction.** Start at `(n,m)`. On equal characters, append that character and move diagonally. On a mismatch, move to the neighboring state with the larger value. Reverse the collected characters. Ties may be resolved by moving upward; either choice preserves an optimal length.

**Complexity.** `O(nm)` time and `O(nm)` auxiliary memory, including reconstruction. The implementation uses a two-dimensional integer array.

**Boundary test.** `a=""`, `b="abc"` must return length `0` and sequence `""`; detects invalid access to the first row/column.

### 2. Merge — interval dynamic programming

**State.** `dp[l][r]` is the minimum cost to merge all original blocks in the closed interval `[l,r]` into one block. `split[l][r]` stores the final split point.

**Base cases.** `dp[i][i] = 0`, because a single block needs no merge.

**Transition.** The last merge must combine `[l,k]` and `[k+1,r]` for some `l <= k < r`. Its cost is the sum of all original sizes in `[l,r]`. Thus:
`dp[l][r] = min(dp[l][k] + dp[k+1][r] + sum(l,r))`.
Prefix sums calculate each interval sum in constant time.

**Order and correctness.** Process intervals by increasing length. Every candidate uses shorter intervals already solved. Every legal final merge has exactly one split `k`, so minimizing over all split points considers every valid plan.

**Reconstruction.** Recursively reconstruct the left interval, then the right interval, and finally append `[l,k,r]`. This postorder gives a chronological valid plan: both child blocks exist before their parent merge.

**Complexity.** `O(n^3)` time and `O(n^2)` auxiliary memory. Prefix sums use `O(n)` additional memory. The implementation uses `long` for costs.

**Boundary test.** `sizes=[]` returns cost `0`, plan `[]`; detects empty-array indexing. Also test `[2,3,4]`, whose minimum cost is `14`.

### 3. Tree — maximum-weight independent set

**State.** Root the tree at vertex `0`. `take[v]` is the best total weight in the subtree when `v` is selected; `skip[v]` is the best total when `v` is not selected.

**Base cases.** A leaf has `take[v]=weights[v]` and `skip[v]=0`. The empty set is allowed, so negative weights need not be selected.

**Transition.** If `v` is selected, none of its children can be selected: `take[v] = weights[v] + sum(skip[child])`. If `v` is skipped, each child independently chooses its better state: `skip[v] = sum(max(take[child],skip[child]))`.

**Order and correctness.** An iterative traversal records parent-before-child order; process its reverse so all children are solved before their parent. The two cases exhaust the possibilities for selecting `v`, and child subtrees are independent once the parent decision is fixed.

**Reconstruction.** Begin at the root with no selected parent. Select a vertex only when its parent was not selected and `take[v] > skip[v]`; otherwise skip it. Ties may choose skip. Pass the decision to each child. The implementation uses an explicit stack, not recursive DFS, to handle a long chain.

**Complexity.** `O(n)` time and `O(n)` auxiliary memory for adjacency lists, traversal order, DP arrays and reconstruction.

**Boundary test.** `n=0` returns weight `0`, selected `[]`. An all-negative tree must select no vertices; detects implementations that force selection.

### 4. Naive string matching

**Invariant.** At each candidate start `s`, compare the pattern with `text[s..s+m)` from left to right. A start is reported only if all `m` characters match.

**Boundaries.** For a nonempty pattern, the last valid start is `n-m`; starts after it cannot fit. For an empty pattern, every boundary from `0` through `n` is a match. If `m>n`, there are no matches.

**Correctness.** Every possible fitting start is checked, and a start is added only after all pattern characters match. Increasing start order gives strictly increasing output; advancing by one preserves overlapping matches.

**Complexity.** `O(nm+n+1)` time in the worst case, `O(1)` auxiliary memory apart from `O(z+1)` output storage.

**Boundary test.** Text `"aaaaa"`, pattern `"aaa"` returns `[0,1,2]`; detects skipped overlaps. Empty pattern on `"ab"` returns `[0,1,2]`.

### 5. KMP string matching

**Prefix function.** `pi[i]` is the length of the longest proper prefix of `pattern[0..i]` that is also a suffix of that substring.

**Construction.** For each pattern index, fall back to `pi[j-1]` while the next characters disagree. If they agree, extend the current prefix length. This reuses previously computed prefix information.

**Search invariant and fallback.** `j` is the number of pattern characters matched against a suffix of the text scanned so far. On mismatch, set `j=pi[j-1]` rather than restarting at zero. After a full match, report its start and also set `j=pi[j-1]`; this preserves possible overlapping matches.

**Correctness.** The prefix function lists the longest possible proper border after a mismatch. Every discarded alignment is therefore known to be impossible, while the fallback retains every alignment that could still match. A full match is reported, and fallback permits overlaps.

**Boundaries.** Empty pattern returns all `n+1` boundaries before pattern indexing; `m>n` returns no positions.

**Complexity.** `O(n+m+z+1)` time, `O(m+1)` auxiliary memory for the prefix array and `O(z+1)` output memory.

**Boundary test.** Text `"abababa"`, pattern `"aba"` returns `[0,2,4]`; detects incorrect fallback after a full match.

### 6. Rabin–Karp string matching

**Hash.** The implementation uses polynomial hashing with base `31`, modulus `101`, and ASCII character codes. The leftmost character has the highest power. All values are normalized to nonnegative residues.

**Rolling update.** Remove the outgoing character multiplied by `31^(m-1)`, multiply the remaining hash by `31`, then add the incoming character. This updates a window in constant time.

**Collision handling and correctness.** A hash match is only a candidate. The implementation compares all `m` characters before reporting a position, so collisions cannot create false positives. It checks every fitting window and advances one character at a time, preserving overlaps.

**Boundaries.** Handle empty patterns and patterns longer than the text before computing a window hash.

**Complexity.** For `1<=m<=n`, `O(n+m+c*m)` time, where `c` is the number of equal-hash candidate windows. Worst-case time can be quadratic. Auxiliary memory is `O(1)` apart from output.

**Boundary/collision test.** Text `"abdj"`, pattern `"ab"` returns `[0]`. `"ab"` and `"dj"` collide under the specified hash, so this detects missing character verification.

### 7. Suffix array and Kasai LCP

**Suffix-array construction.** Initially, each suffix is ranked by its first character. At doubling width `k`, sort suffix indices by the pair `(rank[i], rank[i+k])`, using `-1` when the second half is past the end. Assign new equivalence-class ranks to sorted suffixes. Double `k` until every suffix has a distinct rank. The output contains only the `n` nonempty suffixes.

**Correctness of doubling.** At each round, the pair represents the first `2k` characters (or the available shorter suffix). Sorting these pairs orders prefixes of increasing length. Once all ranks are unique, their order is the lexicographic suffix order.

**Kasai.** Build the inverse suffix-array position for each text index. For each suffix in text order, compare it with its preceding suffix in suffix-array order. Reuse the previous LCP minus one character, because removing the first character from both previously compared suffixes removes at most one common character. Set `lcp[0]=0`.

**Longest repeat.** Any repeated substring is a common prefix of at least two suffixes. In sorted suffix order, the maximum common prefix among any pair is attained by an adjacent pair; therefore the maximum adjacent LCP is the longest repeated substring. Overlaps are allowed.

**Complexity.** Prefix doubling with comparison sorting takes `O(n log^2(n+1))` time; Kasai takes `O(n)`. Auxiliary and output memory are `O(n+1)`. The implementation uses object-array comparison sorting for suffix indices.

**Boundary test.** Empty text returns empty arrays and `""`. Text `"banana"` should have SA `[5,3,1,0,4,2]`, LCP `[0,1,3,0,0,2]`, and a longest repeat `"ana"`.

### 8. Aho–Corasick

**Trie and failure links.** Insert all patterns into one trie. Each state represents a pattern prefix. A failure link points to the longest proper suffix of that prefix that is also a trie prefix. Breadth-first construction ensures the failure state's transitions are ready before a deeper state is processed. Missing transitions are completed using the failure state's transition.

**Counting.** For one text, scan it through the completed automaton and increment a visit count for the state reached after each character. Then process states in reverse BFS order and add each state's visits to its failure state. Consequently, a terminal state receives visits from every state for which that pattern is a suffix, including suffix-pattern matches.

**Duplicates and independent texts.** Store the terminal state for each original pattern position. Duplicate patterns share a terminal state, so they receive equal counts while remaining separate output columns. Allocate a fresh visit array for every text, resetting counts. Empty texts produce zero counts; an empty dictionary produces one empty row per text.

**Correctness.** Every text prefix reaches the state for its longest suffix that is a trie prefix. Failure-link propagation adds that visit to every terminal suffix represented along the failure chain. Thus each occurrence, including overlaps and suffix matches, contributes once to each matching pattern terminal.

**Complexity.** With alphabet size 95, construction is `O(L+95*S+p)` time and `O(95*S+p)` memory. Each text is processed and propagated in `O(|text|+S+p)` time. Output storage is `O(q*(p+1)+1)`.

**Boundary test.** Patterns `["he","she","he"]`, texts `["sheshe","sheshe",""]` must return `[[2,2,2],[2,2,2],[0,0,0]]`; detects missed suffix matches, duplicate handling errors and failure to reset between texts.

## Own tests

Add your actual test cases, expected outputs, actual outputs/logs and the defect each test is intended to detect. At minimum, include the boundary tests described above and the worked examples in `CONTRACTS.md`.

|Command|Input or fixture path|Expected result|Actual result / log|What it checks|
|---|---|---|---|---|
|lcs|`a="tea_time", b="team"`|length 4, sequence `team`|Fill after running|Subsequence reconstruction|
|merge|`sizes=[2,3,4]`|cost 14; plan `[[0,0,1],[0,1,2]]`|Fill after running|Optimal adjacent merges|
|tree|path weights `[4,9,6]`|weight 10; selected `[0,2]`|Fill after running|Take/skip DP|
|naive|text `aaaaa`, pattern `aaa`|`[0,1,2]`|Fill after running|Overlaps|
|kmp|text `abababa`, pattern `aba`|`[0,2,4]`|Fill after running|Prefix fallback|
|rk|text `abdj`, pattern `ab`|`[0]`|Fill after running|Hash collision verification|
|suffix|text `banana`|SA `[5,3,1,0,4,2]`; LCP `[0,1,3,0,0,2]`; repeat `ana`|Fill after running|Suffix order and LCP|
|aho|patterns `he,she,he`; texts `sheshe,sheshe,""`|`[[2,2,2],[2,2,2],[0,0,0]]`|Fill after running|Suffix matches, duplicates, reset|

## Search comparison

Run naive, KMP and RK on identical random and repetitive test inputs at three increasing text sizes, with `n,m<=3000`. Verify output equality before timing. Use at least three measured runs per algorithm, size and case. Record the generator and seed, pattern distribution, exact timing scope, warm-up/JIT policy, raw times, units and median. Do not include compilation in timings.

|Case|n|m|Seed/generator|Algorithm|Run 1|Run 2|Run 3|Median|Correct outputs checked|
|---|---:|---:|---|---|---:|---:|---:|---|
|Fill after measuring||||||||||

Discuss measured observations without treating them as proof of asymptotic complexity. The small modulus `101` intentionally creates collisions and can increase RK verification work. Do not generalize this behavior to all production hash configurations.

## Sources and AI assistance

- Source specification: supplied `CONTRACTS.md`.
- Report structure: supplied `ANALYSIS.md`.
- AI assistance: ChatGPT was used to explain the implementation and make `ANALYSIS.md`.

## Defects detected by the TESTS.md cases

The fixture names below correspond to the request fixtures in `TESTS.md`. Each test is designed to expose a specific implementation defect:

| Fixture | Defect detected |
|---|---|
| `lcs-example` | Treating LCS as a contiguous substring, or returning only the optimum length without reconstructing a valid sequence. |
| `lcs-empty` | Incorrect DP base cases or reconstruction that indexes outside the table when one sequence is empty. |
| `merge-example` | Merging non-adjacent blocks, computing the wrong interval sum, or emitting parent merges before child merges. |
| `merge-empty` | Accessing an interval-DP cell when there are no input blocks. |
| `tree-example` | Selecting adjacent vertices together or replacing the take/skip recurrence with a greedy decision. |
| `tree-negative` | Forcing a vertex into the solution even though the empty set is allowed and has greater weight. |
| `tree-empty` | Assuming a root exists and indexing vertex zero for an empty tree. |
| `naive-overlap` | Advancing by the pattern length after a match and thereby missing overlapping occurrences. |
| `naive-empty-pattern` | Omitting the final boundary or comparing pattern characters when the pattern is empty. |
| `naive-too-long` | Attempting a comparison at a start where the pattern cannot fit. |
| `kmp-overlap` | Resetting KMP's matched-prefix length to zero after a full match instead of using the prefix-function fallback. |
| `kmp-empty-pattern` | Building/indexing the prefix function for an empty pattern instead of returning all text boundaries. |
| `kmp-too-long` | Entering the search when the pattern is longer than the text. |
| `rk-collision` | Treating equal hashes as proof of equality and reporting a false match without character-by-character verification. |
| `rk-overlap` | Advancing the rolling window by more than one position and missing overlapping matches. |
| `rk-empty-pattern` | Calculating a highest power or rolling hash for a zero-length pattern. |
| `suffix-example` | Incorrect suffix rank-pair sorting, incorrect Kasai LCP indexing/reuse, or excluding overlapping repeats. |
| `suffix-empty` | Adding a sentinel/empty suffix to the returned array or accessing a suffix when the text is empty. |
| `suffix-no-repeat` | Returning a nonempty repeated substring even though every adjacent LCP is zero. |
| `aho-example` | Missing suffix-pattern matches, collapsing duplicate output columns, or carrying visit counts from one text into the next. |
| `aho-empty-dictionary` | Failing to emit one empty count row for each text when there are no patterns. |
| `aho-empty-text` | Failing to return a zero count for each dictionary entry when a text is empty. |
| `search-algorithms-agree` | A KMP or Rabin–Karp result that disagrees with the exact naive baseline on the same text and pattern. |
