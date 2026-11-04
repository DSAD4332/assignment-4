import java.util.*;

public final class Algorithms {
    private Algorithms() {}

    private static String str(Map<String, Object> in, String key) {
        return (String) in.get(key);
    }

    private static int integer(Object value) {
        return ((Number) value).intValue();
    }

    private static long longValue(Object value) {
        return ((Number) value).longValue();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> list(Map<String, Object> in, String key) {
        return (List<Object>) in.get(key);
    }

    private static Map<String, Object> result(String key, Object value) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put(key, value);
        return out;
    }

    static Map<String,Object> lcs(Map<String,Object> input) {
        String a = str(input, "a"), b = str(input, "b");
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                else
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }

        StringBuilder reversed = new StringBuilder();
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                reversed.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        String sequence = reversed.reverse().toString();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("length", dp[n][m]);
        out.put("sequence", sequence);
        return out;
    }

    static Map<String,Object> merge(Map<String,Object> input) {
        List<Object> raw = list(input, "sizes");
        int n = raw.size();
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++)
            prefix[i + 1] = prefix[i] + longValue(raw.get(i));

        if (n <= 1) {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("cost", 0L);
            out.put("plan", new ArrayList<List<Integer>>());
            return out;
        }

        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];
        for (int len = 2; len <= n; len++) {
            for (int left = 0; left + len <= n; left++) {
                int right = left + len - 1;
                dp[left][right] = Long.MAX_VALUE;
                long intervalSum = prefix[right + 1] - prefix[left];
                for (int k = left; k < right; k++) {
                    long candidate = dp[left][k] + dp[k + 1][right] + intervalSum;
                    if (candidate < dp[left][right]) {
                        dp[left][right] = candidate;
                        split[left][right] = k;
                    }
                }
            }
        }

        List<List<Integer>> plan = new ArrayList<>();
        appendMergePlan(0, n - 1, split, plan);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("cost", dp[0][n - 1]);
        out.put("plan", plan);
        return out;
    }

    private static void appendMergePlan(int left, int right, int[][] split,
                                        List<List<Integer>> plan) {
        if (left >= right) return;
        int k = split[left][right];
        appendMergePlan(left, k, split, plan);
        appendMergePlan(k + 1, right, split, plan);
        plan.add(Arrays.asList(left, k, right));
    }

    static Map<String,Object> tree(Map<String,Object> input) {
        int n = integer(input.get("n"));
        List<Object> rawWeights = list(input, "weights");
        long[] weights = new long[n];
        for (int i = 0; i < n; i++) weights[i] = longValue(rawWeights.get(i));

        List<Object> rawEdges = list(input, "edges");
        List<List<Integer>> graph = new ArrayList<>(n);
        for (int i = 0; i < n; i++) graph.add(new ArrayList<Integer>());
        for (Object edgeObject : rawEdges) {
            List<Object> edge = (List<Object>) edgeObject;
            int u = integer(edge.get(0)), v = integer(edge.get(1));
            graph.get(u).add(v);
            graph.get(v).add(u);
        }

        if (n == 0) {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("weight", 0L);
            out.put("selected", new ArrayList<Integer>());
            return out;
        }

        int[] parent = new int[n];
        Arrays.fill(parent, -2);
        int[] order = new int[n];
        int count = 0;
        int[] stack = new int[n];
        int top = 0;
        stack[top++] = 0;
        parent[0] = -1;

        while (top > 0) {
            int v = stack[--top];
            order[count++] = v;
            for (int u : graph.get(v)) {
                if (u != parent[v]) {
                    parent[u] = v;
                    stack[top++] = u;
                }
            }
        }

        long[] take = new long[n];
        long[] skip = new long[n];
        for (int idx = count - 1; idx >= 0; idx--) {
            int v = order[idx];
            take[v] = weights[v];
            skip[v] = 0L;
            for (int u : graph.get(v)) {
                if (parent[u] == v) {
                    take[v] += skip[u];
                    skip[v] += Math.max(take[u], skip[u]);
                }
            }
        }

        List<Integer> selected = new ArrayList<>();
        int[] nodes = new int[n];
        boolean[] parentTaken = new boolean[n];
        top = 0;
        nodes[top] = 0;
        parentTaken[top++] = false;
        while (top > 0) {
            --top;
            int v = nodes[top];
            boolean pTaken = parentTaken[top];
            boolean choose = !pTaken && take[v] > skip[v];
            if (choose) selected.add(v);
            for (int u : graph.get(v)) {
                if (parent[u] == v) {
                    nodes[top] = u;
                    parentTaken[top++] = choose;
                }
            }
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("weight", Math.max(take[0], skip[0]));
        out.put("selected", selected);
        return out;
    }

    static Map<String,Object> naive(Map<String,Object> input) {
        String text = str(input, "text"), pattern = str(input, "pattern");
        int n = text.length(), m = pattern.length();
        List<Integer> positions = new ArrayList<>();
        if (m == 0) {
            for (int i = 0; i <= n; i++) positions.add(i);
        } else if (m <= n) {
            for (int start = 0; start <= n - m; start++) {
                int j = 0;
                while (j < m && text.charAt(start + j) == pattern.charAt(j)) j++;
                if (j == m) positions.add(start);
            }
        }
        return result("positions", positions);
    }

    static Map<String,Object> kmp(Map<String,Object> input) {
        String text = str(input, "text"), pattern = str(input, "pattern");
        int n = text.length(), m = pattern.length();
        List<Integer> positions = new ArrayList<>();
        if (m == 0) {
            for (int i = 0; i <= n; i++) positions.add(i);
            return result("positions", positions);
        }
        if (m > n) return result("positions", positions);

        int[] pi = new int[m];
        for (int i = 1, j = 0; i < m; i++) {
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j))
                j = pi[j - 1];
            if (pattern.charAt(i) == pattern.charAt(j)) j++;
            pi[i] = j;
        }

        for (int i = 0, j = 0; i < n; i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j))
                j = pi[j - 1];
            if (text.charAt(i) == pattern.charAt(j)) j++;
            if (j == m) {
                positions.add(i - m + 1);
                j = pi[j - 1];
            }
        }
        return result("positions", positions);
    }

    static Map<String,Object> rk(Map<String,Object> input) {
        String text = str(input, "text"), pattern = str(input, "pattern");
        int n = text.length(), m = pattern.length();
        List<Integer> positions = new ArrayList<>();
        if (m == 0) {
            for (int i = 0; i <= n; i++) positions.add(i);
            return result("positions", positions);
        }
        if (m > n) return result("positions", positions);

        final long base = 31L, mod = 101L;
        long highestPower = 1L;
        for (int i = 1; i < m; i++) highestPower = (highestPower * base) % mod;

        long patternHash = 0L, windowHash = 0L;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * base + pattern.charAt(i)) % mod;
            windowHash = (windowHash * base + text.charAt(i)) % mod;
        }

        for (int start = 0; start <= n - m; start++) {
            if (windowHash == patternHash) {
                int j = 0;
                while (j < m && text.charAt(start + j) == pattern.charAt(j)) j++;
                if (j == m) positions.add(start);
            }
            if (start < n - m) {
                long removed = (text.charAt(start) * highestPower) % mod;
                windowHash = (windowHash - removed) % mod;
                if (windowHash < 0) windowHash += mod;
                windowHash = (windowHash * base + text.charAt(start + m)) % mod;
            }
        }
        return result("positions", positions);
    }

    static Map<String,Object> suffix(Map<String,Object> input) {
        String text = str(input, "text");
        int n = text.length();
        int[] sa = new int[n];
        int[] lcp = new int[n];
        if (n == 0) {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("sa", new ArrayList<Integer>());
            out.put("lcp", new ArrayList<Integer>());
            out.put("repeated", "");
            return out;
        }

        Integer[] order = new Integer[n];
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
            rank[i] = text.charAt(i);
        }

        for (int width = 1; width < n; width *= 2) {
            final int w = width;
            final int[] currentRank = rank;
            Arrays.sort(order, (x, y) -> {
                if (currentRank[x] != currentRank[y])
                    return Integer.compare(currentRank[x], currentRank[y]);
                int rx = x + w < n ? currentRank[x + w] : -1;
                int ry = y + w < n ? currentRank[y + w] : -1;
                return Integer.compare(rx, ry);
            });

            int[] nextRank = new int[n];
            nextRank[order[0]] = 0;
            for (int i = 1; i < n; i++) {
                int x = order[i - 1], y = order[i];
                boolean different = rank[x] != rank[y];
                int xSecond = x + width < n ? rank[x + width] : -1;
                int ySecond = y + width < n ? rank[y + width] : -1;
                if (xSecond != ySecond) different = true;
                nextRank[y] = nextRank[x] + (different ? 1 : 0);
            }
            rank = nextRank;
            if (rank[order[n - 1]] == n - 1) break;
            if (width > n / 2) break;
        }

        for (int i = 0; i < n; i++) sa[i] = order[i];

        int[] inverse = new int[n];
        for (int i = 0; i < n; i++) inverse[sa[i]] = i;
        int common = 0;
        for (int i = 0; i < n; i++) {
            int r = inverse[i];
            if (r == 0) {
                lcp[r] = 0;
                continue;
            }
            int j = sa[r - 1];
            while (i + common < n && j + common < n
                    && text.charAt(i + common) == text.charAt(j + common))
                common++;
            lcp[r] = common;
            if (common > 0) common--;
        }

        int bestLength = 0, bestStart = 0;
        for (int i = 1; i < n; i++) {
            if (lcp[i] > bestLength) {
                bestLength = lcp[i];
                bestStart = sa[i];
            }
        }

        List<Integer> saList = new ArrayList<>(n);
        List<Integer> lcpList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            saList.add(sa[i]);
            lcpList.add(lcp[i]);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("sa", saList);
        out.put("lcp", lcpList);
        out.put("repeated", text.substring(bestStart, bestStart + bestLength));
        return out;
    }

    static Map<String,Object> aho(Map<String,Object> input) {
        List<Object> rawPatterns = list(input, "patterns");
        List<Object> rawTexts = list(input, "texts");
        final int alphabet = 95;

        class Node {
            int[] next = new int[alphabet];
            int fail = 0;
            Node() { Arrays.fill(next, -1); }
        }

        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node());
        int[] terminal = new int[rawPatterns.size()];

        for (int p = 0; p < rawPatterns.size(); p++) {
            String pattern = (String) rawPatterns.get(p);
            int state = 0;
            for (int i = 0; i < pattern.length(); i++) {
                int c = pattern.charAt(i) - 32;
                if (nodes.get(state).next[c] == -1) {
                    nodes.get(state).next[c] = nodes.size();
                    nodes.add(new Node());
                }
                state = nodes.get(state).next[c];
            }
            terminal[p] = state;
        }

        int[] bfs = new int[nodes.size()];
        int head = 0, tail = 0;
        for (int c = 0; c < alphabet; c++) {
            int child = nodes.get(0).next[c];
            if (child != -1) {
                nodes.get(child).fail = 0;
                bfs[tail++] = child;
            } else {
                nodes.get(0).next[c] = 0;
            }
        }

        while (head < tail) {
            int v = bfs[head++];
            for (int c = 0; c < alphabet; c++) {
                int child = nodes.get(v).next[c];
                if (child != -1) {
                    nodes.get(child).fail = nodes.get(nodes.get(v).fail).next[c];
                    bfs[tail++] = child;
                } else {
                    nodes.get(v).next[c] = nodes.get(nodes.get(v).fail).next[c];
                }
            }
        }

        List<List<Long>> rows = new ArrayList<>();
        for (Object textObject : rawTexts) {
            String text = (String) textObject;
            long[] visits = new long[nodes.size()];
            int state = 0;
            for (int i = 0; i < text.length(); i++) {
                state = nodes.get(state).next[text.charAt(i) - 32];
                visits[state]++;
            }
            for (int i = tail - 1; i >= 0; i--) {
                int v = bfs[i];
                visits[nodes.get(v).fail] += visits[v];
            }

            List<Long> row = new ArrayList<>(terminal.length);
            for (int p = 0; p < terminal.length; p++)
                row.add(visits[terminal[p]]);
            rows.add(row);
        }
        return result("counts", rows);
    }
}
