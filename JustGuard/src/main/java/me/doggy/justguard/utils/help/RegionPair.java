package me.doggy.justguard.utils.help;

import javafx.util.Pair;
import me.doggy.justguard.region.Region;

public class RegionPair<R extends Region>
{
    public String name;
    public R region;

    public RegionPair(String name, R region)
    {
        this.name=name;
        this.region=region;
    }

    public Pair<String, R> toPair()
    {
        return new Pair(name, region);
    }
}
