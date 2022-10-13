package com.jet.learnq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ApaSort {
    String[] alphabet;
    //TODO abc sort fields

    /* Integer[] wordTracker;
    Integer[][] letterTracker;
    String[] words;*/
    HashMap<Integer, Integer> alphabetMap;
    public ApaSort(String[] alphabet) {
        this.alphabet = alphabet;
        alphabetMap = new HashMap<>();
        for (int i = 0; i < alphabet.length; i++) {
            alphabetMap.put((int) alphabet[i].charAt(0), i);
        }
    }

    public List<String> streamSort(ArrayList<String> sort) {
        return sort.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    //TODO if you have enough time do a quick word sort
    /*private String[] qSort(String[] array){
        int centerInd = array.length / 2;

        return array;
    }
    private void recQSort(String[] array, int centerInd){
        int i = 0, j = array.length - 1;
        ArrayList<String> moreThan = new ArrayList<>();
        ArrayList<String> lowerThan = new ArrayList<>();
        while(i < centerInd){
            if(isNotFit(array[i], array[centerInd])){
                moreThan.add(array[i]);
            }
            i++;
        }
        while (j > centerInd){
            if(!isNotFit(array[j], array[centerInd])){
                lowerThan.add(array[j]);
            }
            j++;
        }
        recQSort(moreThan.toArray(new String[0]), moreThan.size() / 2);
        recQSort(lowerThan.toArray(new String[0]), lowerThan.size() / 2);
    }*/
    public String[] combSort(String[] array) {
        int step = array.length - 1;
        while (step >= 1) {
            for (int j = 0; j + step < array.length; j++) {
                if (isNotFit(array[j], array[j + step])) {
                    String bucket = array[j];
                    array[j] = array[j + step];
                    array[j + step] = bucket;
                }
            }
            step /= 1.2473309;
        }
        return array;
    }

    public String[] bubbleSort(String[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (isNotFit(array[j], array[j + 1])) {
                    String bucket = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = bucket;
                }
            }
        }
        return array;
    }

    private boolean isNotFit(String s1, String s2) {
        int k = 0;
        while (true) {
            if (k < s1.length() && k < s2.length()) {
                if (s1.charAt(k) != s2.charAt(k)) {
                    Integer l1 = alphabetMap.get((int) s1.charAt(k));
                    Integer l2 = alphabetMap.get((int) s2.charAt(k));
                    if ((l1 != null && l2 != null)) {
                        if (l1 > l2) {
                            return true;
                        }
                    }
                    break;
                }
                ++k;
            } else {
                break;
            }
        }
        return false;
    }
    //TODO If you have enough time do a abc sort
    /*private void sort(String[] array){
        words = array;
        int maxLen = 0;
        for(String s : words){
            if(s.length() > maxLen){
                maxLen = s.length();
            }
        }
        letterTracker = new Integer[maxLen][alphabet.length];
        recSort(0);
    }
    private void recSort(int letterInWordNumber){
        wordTracker = new Integer[words.length];
        for(int wordIndex = 0; wordIndex < words.length; wordIndex++){
            for(int letterNumber = 0; letterNumber < alphabet.length; letterNumber++){
                if(words[wordIndex].charAt(letterInWordNumber) == alphabet[letterNumber].charAt(0)){
                    if(letterTracker[letterInWordNumber][letterNumber] == null){
                        wordTracker[wordIndex] = 0;
                    }else{
                        wordTracker[wordIndex] = letterTracker[letterInWordNumber][letterNumber];
                    }
                    letterTracker[letterInWordNumber][letterNumber] = wordIndex;
                }
            }
        }
        if(wordTracker.length > letterInWordNumber + 1){
            recSort(++letterInWordNumber);
        }
    }*/
}
