package me.doggy.justguard.flag;

import me.doggy.justguard.JustGuard;

import java.util.*;

public class FlagPath implements Iterable<String> {

    private ArrayList<String> path;

    public FlagPath(List<String> path) {
        this.path = new ArrayList<>(path);
    }
    public FlagPath() {
        this(new ArrayList<String>());
    }
    public FlagPath(String ... str) {
        this(Arrays.asList(str));
    }
    public FlagPath(FlagPath other) {
        this(other.path);
    }

    public static FlagPath parse(String str) {
        return new FlagPath(str.split("\\."));
    }

    public int length() {
        return path.size();
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public FlagPath add(int index, List<String> path) {
        this.path.addAll(index, new ArrayList<>(path));
        return this;
    }
    public FlagPath add(int index, String ... path) {
        this.add(index, Arrays.asList(path));
        return this;
    }
    public FlagPath add(int index, FlagPath other) {
        this.add(index, other.path);
        return this;
    }
    public FlagPath add(List<String> path) {
        this.add(this.path.size(), path);
        return this;
    }
    public FlagPath add(String ... path) {
        this.add(this.path.size(), path);
        return this;
    }
    public FlagPath add(FlagPath other) {
        this.add(this.path.size(), other.path);
        return this;
    }
    public FlagPath addInFront(List<String> path) {
        this.add(0, path);
        return this;
    }
    public FlagPath addInFront(String ... path) {
        this.add(0, path);
        return this;
    }
    public FlagPath addInFront(FlagPath other) {
        this.add(0, other.path);
        return this;
    }

    public String get(int index) {
        return this.path.get(index);
    }
    public String getFirst() {
        return this.path.get(0);
    }
    public String getLast() {
        return this.path.get(path.size()-1);
    }

    public FlagPath cut(int fromIndex, int toIndex) {
        return new FlagPath(this.path.subList(fromIndex, toIndex));
    }
    public FlagPath cut(int fromIndex) {
        return this.cut(fromIndex, this.path.size());
    }

    public String getFullPath() {
        return String.join(".", this.path);
    }

    @Override
    public Iterator<String> iterator() {
        return this.path.iterator();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + getFullPath() + ")";
    }
}
