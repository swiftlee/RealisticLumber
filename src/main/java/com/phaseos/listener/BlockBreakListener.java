package com.phaseos.listener;

import com.phaseos.realisticlumber.RealisticLumber;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.logging.Level;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 05/28/2019 | 13:21
 * __________________
 *
 *  [2016] J&M Plugin Development 
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of J&M Plugin Development and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to J&M Plugin Development
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from J&M Plugin Development.
 */
public class BlockBreakListener implements Listener {

    private RealisticLumber plugin;

    public BlockBreakListener(RealisticLumber plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Material type = block.getType();
        if (String.valueOf(type).toLowerCase().contains("log")) {
            Player player = e.getPlayer();
            blockBreak(block, player.getLocation());
        }
    }

    public void blockBreak(Block breakBlock, Location playerLocation) {
        Material breakBlockType = breakBlock.getType();
        Location breakBlockLocation = breakBlock.getLocation();

        int treeId = 0;
        if (String.valueOf(breakBlockType).toLowerCase().contains("log"))
            treeId = 1;

        if (breakBlock.getMetadata("TreeId").iterator().hasNext()) {
            treeId = breakBlock.getMetadata("TreeId").iterator().next().asInt();
        }
        World world = breakBlock.getWorld();
        Vector direction = new Vector(playerLocation.getX() - breakBlockLocation.getX(), 0, playerLocation.getZ() - breakBlockLocation.getZ());
        direction.normalize();
        float angle = direction.angle(new Vector(0, 0, 1));
        // double angle2 = angle * 180 / Math.PI;
        int angle1 = 90;
        if (direction.getX() > 0) {
            if (angle > Math.PI * 1 / 4)
                angle1 = 180;
            if (angle > Math.PI * 3 / 4)
                angle1 = 270;
        } else {
            if (angle > Math.PI * 1 / 4)
                angle1 = 0;
            if (angle > Math.PI * 3 / 4)
                angle1 = 270;
        }
        switch (angle1) {
            case 0:
                direction.setX(0);
                direction.setZ(1);
                break;
            case 90:
                direction.setX(1);
                direction.setZ(0);
                break;
            case 180:
                direction.setX(0);
                direction.setZ(-1);
                break;
            case 270:
                direction.setX(-1);
                direction.setZ(0);
                break;
        }
        // player.sendMessage("angle: "+ angle1 + " " + angle2 + "^ " + angle +
        // " x:" + direction.getX() + " z:" + direction.getZ());
        // player.sendMessage("direction: "+ direction.toString());

        HashMap<Location, Block> tree = new HashMap<>();
        HashMap<Location, Block> solid = new HashMap<>();
        HashMap<Location, Block> search = new HashMap<>();
        search.put(breakBlockLocation, breakBlock);

        // filling tree
        boolean findNext = true;
        int limit = 0;
        while (findNext) {
            findNext = false;
            HashMap<Location, Block> newSearch = new HashMap<>();
            for (java.util.Map.Entry<Location, Block> pairs : search.entrySet()) {
                Location l = pairs.getKey();
                java.util.Map<Location, Block> near = getNearBlocks(l, 1);
                for (java.util.Map.Entry<Location, Block> nearPairs : near.entrySet()) {
                    Location nearLocation = nearPairs.getKey();
                    Block nearBlock = nearPairs.getValue();
                    if (nearBlock.getType().toString().toLowerCase().contains("log")) {
                        if (!tree.containsKey(nearLocation)) {
                            Boolean put = false;
                            if (treeId == 0 && nearBlock.getMetadata("TreeId").isEmpty())
                                put = true;
                            if (treeId == 1)
                                put = true;
                            if (treeId != 0) {
                                if (nearBlock.getMetadata("TreeId").iterator().hasNext() && nearBlock.getMetadata("TreeId").iterator().next().asInt() == treeId) {
                                    put = true;
                                }
                            }
                            if (put) {
                                tree.put(nearLocation, nearBlock);
                                newSearch.put(nearLocation, nearBlock);
                                findNext = true;
                            }
                        }
                    }
                    if (!nearBlock.getType().toString().toLowerCase().contains("log")) {
                        solid.put(nearLocation, nearBlock);
                        // player.sendMessage("SolidBlock : " +
                        // nearBlock.getType());
                    }

                }
            }
            limit++;
            if (limit > 150) {
                plugin.getLogger().log(Level.INFO, "Tree Logs search reached BlockProcessingLimit.");
                break;
            }
            if (findNext) {
                search.clear();
                search.putAll(newSearch);
            }

        }
        tree.remove(breakBlockLocation);
        solid.remove(breakBlockLocation);
        breakBlock.removeMetadata("TreeId", plugin);
        // player.sendMessage("This tree contains " + tree.size() +
        // " blocks, and connected " + solid.size() + " solid blocks");

        // int logsCount = tree.size();

        // defilling tree depends on solid blocks
        search.clear();
        search.putAll(solid);
        findNext = true;
        limit = 0;
        while (findNext) {
            findNext = false;
            HashMap<Location, Block> newSearch = new HashMap<>();
            for (java.util.Map.Entry<Location, Block> pairs : search.entrySet()) {
                Location l = pairs.getKey();
                // if (l == breakBlockLocation) continue;
                java.util.Map<Location, Block> near = getNearBlocks(l, 1);
                for (java.util.Map.Entry<Location, Block> nearPairs : near.entrySet()) {
                    Location nearLocation = nearPairs.getKey();
                    // if (nearLocation == breakBlockLocation) continue;
                    Block nearBlock = nearPairs.getValue();
                    if (nearBlock.getType().toString().toLowerCase().contains("log")) {
                        if (tree.containsKey(nearLocation)) {
                            tree.remove(nearLocation);
                            newSearch.put(nearLocation, nearBlock);
                            findNext = true;
                        }
                    }
                }
            }
            limit++;
            if (limit > 150) {
                plugin.getLogger().log(Level.INFO, "Solid Blocks connections search reached BlockProcessingLimit.");
                break;
            }
            if (findNext) {
                search.clear();
                search.putAll(newSearch);
                newSearch.clear();
            }

        }

        // detecting distance to ground
        int fallingDistance;
        for (fallingDistance = 1; fallingDistance < 50; fallingDistance++) {
            Block newBlock = world.getBlockAt(breakBlockLocation.getBlockX(), breakBlockLocation.getBlockY() - fallingDistance, breakBlockLocation.getBlockZ());
            Material newBlockType = newBlock.getType();
            if (!isLightBlock(newBlockType))
                break;
        }

        HashMap<Location, Integer> clearWay = new HashMap<>();

        // falling tree
        for (java.util.Map.Entry<Location, Block> logPairs : tree.entrySet()) {
            Location newBlockLocation = logPairs.getKey();
            // if (newBlockLocation == breakBlockLocation) continue;
            Block logBlock = logPairs.getValue();
            Material logBlockType = logBlock.getType();
            // byte logBlockData = logBlock.getData();
            MaterialData logBlockData = logBlock.getState().getData();
            logBlock.setType(Material.AIR);
            logBlock.removeMetadata("TreeId", plugin);

            int horizontalDistance = newBlockLocation.getBlockY() - breakBlockLocation.getBlockY() - 1;
            if (horizontalDistance < 0)
                horizontalDistance = 0;
            int verticalDistance = horizontalDistance + fallingDistance;
            // int horisontalOffset=0;
            int horisontalOffset = (int) Math.floor((horizontalDistance) / 1.5);
            float horisontalSpeed = calcSpeed(horizontalDistance, verticalDistance, horisontalOffset);
            // player.sendMessage("horizontalDistance: "+ horizontalDistance +
            // " verticalDistance:" + verticalDistance + " fallingdistance " +
            // fallingDistance);
            if (fallingDistance == 1) {
                switch (horizontalDistance) {
                    case 1:
                        horisontalOffset = 1;
                        horisontalSpeed = 0;
                        break;
                    case 2:
                        horisontalOffset = 1;
                        horisontalSpeed = 0.1191f;
                        break;
                    case 3:
                        horisontalOffset = 1;
                        horisontalSpeed = 0.185f;
                        break;
                    case 4:
                        horisontalOffset = 2;
                        horisontalSpeed = 0.17f;
                        break;
                    case 5:
                        horisontalOffset = 2;
                        horisontalSpeed = 0.22f;
                        break;
                    case 6:
                        horisontalOffset = 3;
                        horisontalSpeed = 0.21f;
                        break;
                    case 7:
                        horisontalOffset = 3;
                        horisontalSpeed = 0.26f;
                        break;
                    case 8:
                        horisontalOffset = 4;
                        horisontalSpeed = 0.241f;
                        break;
                    case 9:
                        horisontalOffset = 4;
                        horisontalSpeed = 0.28f;
                        break;

                }
            }
            if (fallingDistance == 2) {
                switch (horizontalDistance) {
                    case 1:
                        horisontalOffset = 1;
                        horisontalSpeed = 0;
                        break;
                    case 2:
                        horisontalOffset = 1;
                        horisontalSpeed = 0.1f;
                        break;
                    case 5:
                        horisontalOffset = 2;
                        horisontalSpeed = 0.2f;
                        break;

                }
            }
            Vector vOffset = direction.clone().multiply(horisontalOffset);
            newBlockLocation.add(vOffset);
            Block testBlock = world.getBlockAt(newBlockLocation);
            if (isLightBlock(testBlock.getType())) {
                testBlock.breakNaturally();
            } else {
                newBlockLocation.subtract(vOffset);
                horisontalSpeed = calcSpeed(horizontalDistance, verticalDistance, 0);
            }
            FallingBlock blockFalling = world.spawnFallingBlock(newBlockLocation, logBlockType, (byte) 0); //TODO: Find a way to do this without using deprecated methods
            blockFalling.setVelocity(direction.clone().multiply(horisontalSpeed));

            // calc clear falling way
            int minClearVertical = newBlockLocation.getBlockY() - verticalDistance;
            for (int clearY = newBlockLocation.getBlockY(); clearY >= minClearVertical; clearY--) {
                int horisontalClearDistance = (int) Math.ceil(Math.sqrt(horizontalDistance * horizontalDistance - (clearY - minClearVertical) * (clearY - minClearVertical)));
                Location l = new Location(world, newBlockLocation.getBlockX(), clearY, newBlockLocation.getBlockZ());
                if (clearWay.containsKey(l)) {
                    if (clearWay.get(l) < horisontalClearDistance) {
                        clearWay.put(l, horisontalClearDistance);
                    }
                } else {
                    clearWay.put(l, horisontalClearDistance);
                }
            }
        }

        // clear falling way
        for (java.util.Map.Entry<Location, Integer> clearPairs : clearWay.entrySet()) {
            Location clearBlockLocation = clearPairs.getKey();
            int clearDistance = clearPairs.getValue();
            // player.sendMessage("clearDistance: "+ clearDistance + " y:" +
            // clearBlockLocation.getY());
            for (int c = 0; c <= clearDistance; c++) {
                Location tempClearLoc = clearBlockLocation.clone().add(direction.clone().multiply(c));
                Block clearBlock = world.getBlockAt(tempClearLoc);
                if (isLightBlock(clearBlock.getType())) {
                    clearBlock.breakNaturally();
                    clearBlock.removeMetadata("TreeId", plugin);
                }
            }
        }

        // get blocks around tree to find leaves
        HashMap<Location, Block> leaves = new HashMap<>();
        for (java.util.Map.Entry<Location, Block> logPairs : tree.entrySet()) {
            leaves.putAll(getNearBlocks(logPairs.getKey(), 3));
        }

        if (tree.size() == 0) {
            if (breakBlockType.toString().toLowerCase().contains("log")) {
                leaves.putAll(getNearBlocks(breakBlockLocation, 3));
            } else {
                for (int i = 1; i <= 5; i++) {
                    Location tempLocation = breakBlockLocation.clone().add(0, i, 0);
                    leaves.put(tempLocation, world.getBlockAt(tempLocation));
                }
            }
        }

        leaves.remove(breakBlockLocation);

        // falling leaves
        for (java.util.Map.Entry<Location, Block> leavesPairs : leaves.entrySet()) {
            Location leavesLocation = leavesPairs.getKey();
            Block leavesBlock = leavesPairs.getValue();
            Material leavesMaterial = leavesBlock.getType();
            // byte leavesBlockData = leavesBlock.getData();
            MaterialData leavesBlockData = leavesBlock.getState().getData();
            if (!leavesMaterial.toString().toLowerCase().contains("leaves"))
                continue;

            if (treeId == 0 && !leavesBlock.getMetadata("TreeId").isEmpty())
                continue;
            leavesBlock.breakNaturally();
            leavesBlock.removeMetadata("TreeId", plugin);
        }
    }

    private java.util.Map<Location, Block> getNearBlocks(Location l, int radius) {
        int lx = l.getBlockX();
        int ly = l.getBlockY();
        int lz = l.getBlockZ();
        World w = l.getWorld();
        HashMap<Location, Block> m = new HashMap<>();
        for (int z = lz - radius; z <= lz + radius; z++) {
            for (int x = lx - radius; x <= lx + radius; x++) {
                for (int y = ly - radius; y <= ly + radius; y++) {
                    Location tl = new Location(w, (double) x, (double) y, (double) z);
                    Block b = w.getBlockAt(tl);
                    if (tl != l) {
                        m.put(tl, b);
                    }
                }
            }
        }
        return m;
    }

    float calcSpeed(float horizontalDistance, float verticalDistance, int horisontalOffset) {
        float speed = 0;
        if (verticalDistance > 0) {
            speed = (horizontalDistance - horisontalOffset) / (float) Math.sqrt(2 * (verticalDistance) / 0.064814);
        }
        return speed;
    }

    private boolean isLightBlock(Material m) {
        return m.toString().toLowerCase().contains("leaves") || m == Material.AIR || m == Material.TORCH || m.toString().toLowerCase().contains("grass") || m == Material.RED_MUSHROOM || m == Material.VINE || m == Material.SNOW || m == Material.ARROW || m == Material.COCOA || m == Material.LADDER || m == Material.WEB || m.toString().toLowerCase().contains("sapling") || m == Material.WATER;
    }

}