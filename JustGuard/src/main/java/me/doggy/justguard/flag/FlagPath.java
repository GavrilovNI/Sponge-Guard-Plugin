package me.doggy.justguard.flag;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import me.doggy.justguard.JustGuard;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.CatalogedBy;

import javax.annotation.Nullable;
import java.util.*;

public class FlagPath implements Iterable<String> {

    private List<String> path;

    private FlagPath() { this.path = new ArrayList<>(); }
    private FlagPath(List<String> path) { this.path = new ArrayList<>(path); }
    private FlagPath(String ... path) { this.path = Arrays.asList(path); }

    public static FlagPath parse(String str) {
        return new FlagPath(Arrays.asList(str.split("\\.")));
    }


    public static FlagPath of() { return new FlagPath(); }
    public static FlagPath of(Object ... objects) {
        Builder builder = FlagPath.builder();
        for (Object obj : objects) {
            if(obj instanceof String)
                builder.add((String) obj);
            else if(obj instanceof FlagPath)
                builder.add((FlagPath) obj);
            else if(obj instanceof String[])
                builder.add((String[]) obj);
            else if(obj instanceof Iterable)
                builder.add(Iterables.toArray((Iterable<String>) obj, String.class));
            else if(obj instanceof List)
                builder.add((List) obj);
            else throw new IllegalArgumentException("Object of type '"+obj.getClass().getSimpleName()+"' can't be parsed to FlagPath.");
        }
        return builder.build();
    }


    public static class Builder {
        private FlagPath flagPath;


        private Builder() {
            this.flagPath = new FlagPath(new ArrayList<>());
        }
        private Builder(FlagPath flagPath) {
            this.flagPath = new FlagPath(flagPath.path);
        }

        public Builder add(List<String> path) {
            this.flagPath.path.addAll(new ArrayList<>(path));
            return this;
        }
        public Builder add(String ... path) {
            this.add(Arrays.asList(path));
            return this;
        }
        public Builder add(FlagPath flagPath) {
            this.add(flagPath.path);
            return this;
        }
        public Builder add(int index, List<String> path) {
            this.flagPath.path.addAll(index, new ArrayList<>(path));
            return this;
        }
        public Builder add(int index, String ... path) {
            this.add(index, Arrays.asList(path));
            return this;
        }
        public Builder add(int index, FlagPath flagPath) {
            this.add(index, flagPath.path);
            return this;
        }
        public Builder addFront(List<String> path) {
            this.add(0, path);
            return this;
        }
        public Builder addFront(String ... path) {
            this.add(0, Arrays.asList(path));
            return this;
        }
        public Builder addFront(FlagPath flagPath) {
            this.add(0, flagPath.path);
            return this;
        }

        public Builder remove(int index) {
            flagPath.path.remove(index);
            return this;
        }
        public Builder removeFirst() {
            flagPath.path.remove(0);
            return this;
        }
        public Builder removeLast() {
            flagPath.path.remove(flagPath.length()-1);
            return this;
        }

        public int length() {
            return flagPath.length();
        }


        public FlagPath build() {
            return new FlagPath(this.flagPath.path);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FlagPath.Builder)) {
                return false;
            }
            return this.flagPath.equals(((Builder)obj).flagPath);
        }

        @Override
        public int hashCode() {
            return flagPath.hashCode();
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    public Builder toBuilder() {
        return new Builder(this);
    }

    public FlagPath copy() {
        FlagPath result = new FlagPath(new ArrayList<>(this.path));
        return result;
    }

    public FlagPath cut(int fromIndex, int toIndex) {
        return new FlagPath(this.path.subList(fromIndex, toIndex));
    }
    public FlagPath cut(int fromIndex) {
        return this.cut(fromIndex, this.path.size());
    }

    public int length() {
        return path.size();
    }
    public boolean isEmpty() {
        return path.isEmpty();
    }
    public String get(int index) { return path.get(index); }
    public String getFirst() { return path.get(0); }
    public String getLast() { return path.get(path.size()-1); }
    public String getFullPath() {
        return String.join(".", this.path);
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FlagPath)) {
            return false;
        }
        return this.path.equals(((FlagPath)obj).path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
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
