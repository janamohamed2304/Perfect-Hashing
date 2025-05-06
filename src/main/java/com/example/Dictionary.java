package com.example;

public interface Dictionary {
    boolean insert(String word);
    boolean delete(String word);
    boolean search(String word);
}