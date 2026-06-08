package org.example.Rough;

import java.util.Comparator;
import java.util.Map;

public class B implements Comparator<Map.Entry<Character,Long>> {


    @Override
    public int compare(Map.Entry<Character, Long> o1, Map.Entry<Character, Long> o2) {
        return o2.getValue().compareTo(o1.getValue());
    }
}
