package com.example.learnq1;

import com.jet.learnq.ArrayOfWordsConverter;
import com.jet.learnq.model.PairDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ArrayOfWordsConverterTest {
    @Test
    public void getDtoFromString() {
        ArrayOfWordsConverter converter = new ArrayOfWordsConverter();
        List<String> array = new ArrayList<>();
        array.add("Privet-hello");
        array.add("Noch-Night");
        array.add("Probel-whitespace,space");
        List<PairDTO> real = converter.getWordDTOsFromStringArray(array);
        for (PairDTO pair : real) {
            System.out.println(pair.getWord());
            for (String str : pair.getTranslations()) {
                System.out.println("translation " + str);
            }
        }
        List<PairDTO> expected = new ArrayList<>();
        expected.add(new PairDTO("Privet", new ArrayList<String>() {{
            add("hello");
        }}));
        expected.add(new PairDTO("Noch", new ArrayList<String>() {{
            add("Night");
        }}));
        expected.add(new PairDTO("Probel", new ArrayList<String>() {{
            add("whitespace");
            add("space");
        }}));
        Assert.assertArrayEquals(real.toArray(), expected.toArray());
    }
}
