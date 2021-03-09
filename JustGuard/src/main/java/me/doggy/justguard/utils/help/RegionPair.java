package me.doggy.justguard.utils.help;

import javafx.util.Pair;
import me.doggy.justguard.region.Region;

public class RegionPair
{
    public String name;
    public Region region;

    public RegionPair(String name, Region region)
    {
        this.name=name;
        this.region=region;
    }

    public Pair<String, Region> toPair()
    {
        return new Pair(name, region);
    }
}
