import java.util.*;

public class AlgorithmsTestRunner {
    static Map<String,Object> map(Object... kv) {
        Map<String,Object> m = new LinkedHashMap<>();
        for (int i=0;i<kv.length;i+=2) m.put((String)kv[i], kv[i+1]);
        return m;
    }
    static List<Object> list(Object... xs) { return new ArrayList<>(Arrays.asList(xs)); }
    static void check(String name, Object expected, Object actual) {
        if (!Objects.equals(expected, actual))
            throw new AssertionError(name + " FAILED\nexpected=" + expected + "\nactual=" + actual);
        System.out.println("PASS " + name + " | expected=" + expected + " | actual=" + actual);
    }
    static Object field(Map<String,Object> m, String k) { return m.get(k); }
    public static void main(String[] args) {
        check("lcs-example", map("length",4,"sequence","team"), Algorithms.lcs(map("a","tea_time","b","team")));
        check("lcs-empty", map("length",0,"sequence",""), Algorithms.lcs(map("a","","b","abc")));

        check("merge-example", map("cost",14L,"plan",list(list(0,0,1),list(0,1,2))), Algorithms.merge(map("sizes",list(2,3,4))));
        check("merge-empty", map("cost",0L,"plan",list()), Algorithms.merge(map("sizes",list())));

        check("tree-example", map("weight",10L,"selected",list(0,2)), Algorithms.tree(map("n",3,"weights",list(4,9,6),"edges",list(list(0,1),list(1,2)))));
        check("tree-negative", map("weight",0L,"selected",list()), Algorithms.tree(map("n",3,"weights",list(-5,-2,-9),"edges",list(list(0,1),list(1,2)))));
        check("tree-empty", map("weight",0L,"selected",list()), Algorithms.tree(map("n",0,"weights",list(),"edges",list())));

        check("naive-overlap", map("positions",list(0,1,2)), Algorithms.naive(map("text","aaaaa","pattern","aaa")));
        check("naive-empty-pattern", map("positions",list(0,1,2)), Algorithms.naive(map("text","ab","pattern","")));
        check("naive-too-long", map("positions",list()), Algorithms.naive(map("text","ab","pattern","abc")));

        check("kmp-overlap", map("positions",list(0,2,4)), Algorithms.kmp(map("text","abababa","pattern","aba")));
        check("kmp-empty-pattern", map("positions",list(0,1,2)), Algorithms.kmp(map("text","ab","pattern","")));
        check("kmp-too-long", map("positions",list()), Algorithms.kmp(map("text","ab","pattern","abc")));

        check("rk-collision", map("positions",list(0)), Algorithms.rk(map("text","abdj","pattern","ab")));
        check("rk-overlap", map("positions",list(0,1,2)), Algorithms.rk(map("text","aaaaa","pattern","aaa")));
        check("rk-empty-pattern", map("positions",list(0,1,2)), Algorithms.rk(map("text","ab","pattern","")));

        check("suffix-example", map("sa",list(5,3,1,0,4,2),"lcp",list(0,1,3,0,0,2),"repeated","ana"), Algorithms.suffix(map("text","banana")));
        check("suffix-empty", map("sa",list(),"lcp",list(),"repeated",""), Algorithms.suffix(map("text","")));
        check("suffix-no-repeat", map("sa",list(0,1,2),"lcp",list(0,0,0),"repeated",""), Algorithms.suffix(map("text","abc")));

        check("aho-example", map("counts",list(list(2L,2L,2L),list(2L,2L,2L),list(0L,0L,0L))), Algorithms.aho(map("patterns",list("he","she","he"),"texts",list("sheshe","sheshe",""))));
        check("aho-empty-dictionary", map("counts",list(list(),list())), Algorithms.aho(map("patterns",list(),"texts",list("abc",""))));
        check("aho-empty-text", map("counts",list(list(0L,0L))), Algorithms.aho(map("patterns",list("a","bc"),"texts",list(""))));

        // Same fixture through all three search algorithms.
        Map<String,Object> search = map("text","abababaabababa","pattern","aba");
        Object naive = field(Algorithms.naive(search), "positions");
        Object kmp = field(Algorithms.kmp(search), "positions");
        Object rk = field(Algorithms.rk(search), "positions");
        check("search-algorithms-agree", naive, kmp);
        check("search-algorithms-agree-rk", naive, rk);
    }
}
