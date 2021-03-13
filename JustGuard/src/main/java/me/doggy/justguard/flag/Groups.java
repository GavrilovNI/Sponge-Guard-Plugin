package me.doggy.justguard.flag;

import me.doggy.justguard.utils.ConfigUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public class Groups {

    private static final String GROUP_VALUES = "values";

    private ConfigurationNode root;

    public Groups(ConfigurationNode groupsNode) {
        this.root = groupsNode;
    }

    private ConfigurationNode getGroupNode(String name) {
        return root.getNode(name);
    }

    private boolean containsInGroup(ConfigurationNode groupNode, String name, HashSet<ConfigurationNode> checkedGroups) {

        if(groupNode.isVirtual())
            return false;
        if(!checkedGroups.add(groupNode))
            return false;

        ConfigurationNode valuesNode = groupNode.getNode(GROUP_VALUES);

        if(!valuesNode.isVirtual() && valuesNode.getList(x->x.toString()).contains(name))
            return true;

        HashSet<ConfigurationNode> inheritsFromNodes = ConfigUtils.getAllNodesThisInheritsFrom(groupNode, checkedGroups);

        for(ConfigurationNode inheritsFromNode : inheritsFromNodes) {
            if(containsInGroup(inheritsFromNode, name, checkedGroups))
                return true;
        }

        return false;
    }
    public boolean containsInGroup(String groupName, String value) {
        return containsInGroup(getGroupNode(groupName), value, new HashSet<ConfigurationNode>());
    }

    public Set<String> getGroupNames() {
        return (Set) root.getChildrenMap().keySet();
    }
}
