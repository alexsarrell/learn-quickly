package com.example.learnq1;

import com.jet.learnq.ApaSort;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UnitTest {
    @Test
    public void alphabetExtendedBubbleSortFixedStringArrayWithNotNullElements() {
        ApaSort apaSort = new ApaSort("a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z".split(", "));
        String[] expectedArray = new String[]{"aaa", "aab", "aac", "aad", "aba", "bda", "bdb"};
        String[] actualDictionary = apaSort.bubbleSort(new String[]{"aac", "aba", "bda", "bdb", "aaa", "aab", "aad"});
        Assert.assertArrayEquals(expectedArray, actualDictionary);
    }

    @Test
    public void alphabetExtendedCombSortFixedStringArrayWithNotNullElements() {
        ApaSort apaSort = new ApaSort("a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z".split(", "));
        String[] expectedArray = new String[]{"aaa", "aab", "aac", "aad", "aba", "bda", "bdb"};
        String[] actualDictionary = apaSort.combSort(new String[]{"aac", "aba", "bda", "bdb", "aaa", "aab", "aad"});
        Assert.assertArrayEquals(expectedArray, actualDictionary);
    }

    @Test
    public void alphabetStreamSort() {
        ApaSort apaSort = new ApaSort("a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z".split(", "));
        ArrayList<String> expectedArray = new ArrayList<String>() {
            {
                add("aaa");
                add("aab");
                add("aac");
                add("aad");
                add("aba");
                add("bda");
                add("bdb");
            }
        };
        List<String> actualDictionary = apaSort.streamSort(
                new ArrayList<String>() {
                    {
                        add("aac");
                        add("aba");
                        add("bda");
                        add("bdb");
                        add("aaa");
                        add("aab");
                        add("aad");
                    }
                });
        Assert.assertEquals(expectedArray, actualDictionary);
    }
}
