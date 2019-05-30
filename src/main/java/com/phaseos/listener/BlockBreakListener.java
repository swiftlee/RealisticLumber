package com.phaseos.listener;

import com.phaseos.realisticlumber.RealisticLumber;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    public static Player pubPlayer = null;
    private RealisticLumber plugin;
    private int leafRadius;

    public BlockBreakListener(RealisticLumber plugin) {
        this.plugin = plugin;
    }

    private boolean isLogBlock(Material mat) {
        if ((mat == Material.ACACIA_LOG) || (mat == Material.BIRCH_LOG) || (mat == Material.DARK_OAK_LOG) || (mat == Material.JUNGLE_LOG) || (mat == Material.OAK_LOG) || (mat == Material.SPRUCE_LOG)) {
            return true;
        }
        return false;
    }

    private boolean isLeavesBlock(Material mat) {
        if ((mat == Material.OAK_LEAVES) || (mat == Material.ACACIA_LEAVES) || (mat == Material.BIRCH_LEAVES) || (mat == Material.DARK_OAK_LEAVES) || (mat == Material.JUNGLE_LEAVES) || (mat == Material.SPRUCE_LEAVES)) {
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        if (isLogBlock(block.getType())) {
            event.setCancelled(true);
            if (chop(event.getBlock(), event.getPlayer(), event.getBlock().getWorld())) {
                if ((!plugin.moreDamageToTools) &&
                        (breaksTool(event.getPlayer(), event.getPlayer().getItemInHand()))) {
                    event.getPlayer().getInventory().clear(event.getPlayer().getInventory().getHeldItemSlot());
                }
            } else {
                event.setCancelled(false);
            }
        }
    }

    public boolean chop(Block block, Player player, World world) {
        List<Block> blocks = new LinkedList<>();
        Block highest = getHighestLog(block);
        if (isTree(highest, player, block)) {
            getBlocksToChop(block, highest, blocks);
            if (plugin.logsMoveDown) {
                moveDownLogs(block, blocks, world, player);
            } else {
                popLogs(block, blocks, world, player);
            }
        } else {
            return false;
        }
        return true;
    }

    public void getBlocksToChop(Block block, Block highest, List<Block> blocks) {
        while (block.getY() <= highest.getY()) {
            if (!blocks.contains(block)) {
                blocks.add(block);
            }
            getBranches(block, blocks, block.getRelative(BlockFace.NORTH));
            getBranches(block, blocks, block.getRelative(BlockFace.NORTH_EAST));
            getBranches(block, blocks, block.getRelative(BlockFace.EAST));
            getBranches(block, blocks, block.getRelative(BlockFace.SOUTH_EAST));
            getBranches(block, blocks, block.getRelative(BlockFace.SOUTH));
            getBranches(block, blocks, block.getRelative(BlockFace.SOUTH_WEST));
            getBranches(block, blocks, block.getRelative(BlockFace.WEST));
            getBranches(block, blocks, block.getRelative(BlockFace.NORTH_WEST));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH));
            }
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST));
            }
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST));
            }
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST));
            }
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH));
            }
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST));
            }
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST));
            }
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST))) {
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST));
            }
            if ((block.getData() == 3) || (block.getData() == 7) || (block.getData() == 11) || (block.getData() == 15)) {
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH, 2));
                }
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST, 2));
                }
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST, 2));
                }
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST, 2));
                }
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH, 2));
                }
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST, 2));
                }
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST, 2));
                }
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST, 2))) {
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST, 2));
                }
            }
            if ((blocks.contains(block.getRelative(BlockFace.UP))) || (!isLogBlock(block.getRelative(BlockFace.UP).getType()))) {
                break;
            }
            block = block.getRelative(BlockFace.UP);
        }
    }

    public void getBranches(Block block, List<Block> blocks, Block other) {
        if ((!blocks.contains(other)) && (isLogBlock(other.getType()))) {
            getBlocksToChop(other, getHighestLog(other), blocks);
        }
    }

    public Block getHighestLog(Block block) {
        boolean isLog = true;
        while (isLog) {
            if ((isLogBlock(block.getRelative(BlockFace.UP).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST).getType())) ||
                    (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST).getType()))) {
                if (isLogBlock(block.getRelative(BlockFace.UP).getType())) {
                    block = block.getRelative(BlockFace.UP);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST);
                } else if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST);
                }
            } else {
                isLog = false;
            }
        }
        return block;
    }

    public boolean isTree(Block block, Player player, Block first) {
        if (!plugin.onlyTrees) {
            return true;
        }
        if (plugin.trees.containsKey(player)) {
            Block[] blockarray = (Block[]) plugin.trees.get(player);
            for (int counter = 0; counter < Array.getLength(blockarray); counter++) {
                if (blockarray[counter] == block) {
                    return true;
                }
                if (blockarray[counter] == first) {
                    return true;
                }
            }
        }
        int counter = 0;
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.DOWN).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.NORTH).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.EAST).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.SOUTH).getType())) {
            counter++;
        }
        if (isLeavesBlock(block.getRelative(BlockFace.WEST).getType())) {
            counter++;
        }
        if (counter >= 2) {
            return true;
        }
        if (block.getData() == 1) {
            block = block.getRelative(BlockFace.UP);
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.NORTH).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.EAST).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.SOUTH).getType())) {
                counter++;
            }
            if (isLeavesBlock(block.getRelative(BlockFace.WEST).getType())) {
                counter++;
            }
            if (counter >= 2) {
                return true;
            }
        }
        return false;
    }

    public void popLogs(Block block, List<Block> blocks, World world, Player player) {
        ItemStack item = new ItemStack(Material.STONE, 1, (short) 0, null);
        item.setAmount(1);
        for (int counter = 0; counter < blocks.size(); counter++) {
            block = blocks.get(counter);
            item.setType(block.getType());
            item.setDurability((short) block.getData());
            block.breakNaturally();
            if (plugin.popLeaves) {
                popLeaves(block);
            }
            if ((plugin.moreDamageToTools) &&
                    (breaksTool(player, player.getItemInHand()))) {
                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                if (plugin.interruptIfToolBreaks) {
                    break;
                }
            }
        }
    }

    public void popLeaves(Block block) {
        for (int y = -plugin.leafRadius; y < plugin.leafRadius + 1; y++) {
            for (int x = -plugin.leafRadius; x < plugin.leafRadius + 1; x++) {
                for (int z = -plugin.leafRadius; z < plugin.leafRadius + 1; z++) {
                    Block target = block.getRelative(x, y, z);
                    if (isLeavesBlock(target.getType())) {
                        target.breakNaturally();
                    }
                }
            }
        }
    }

    public void moveDownLogs(Block block, List<Block> blocks, World world, Player player) {
        ItemStack item = new ItemStack(Material.STONE, 1, (short) 0, null);
        item.setAmount(1);

        List<Block> downs = new LinkedList();
        for (int counter = 0; counter < blocks.size(); counter++) {
            block = (Block) blocks.get(counter);
            Block down = block.getRelative(BlockFace.DOWN);
            if ((down.getType() == Material.AIR) || (isLeavesBlock(down.getType()))) {
                down.setType(block.getType());

                block.setType(Material.AIR);
                downs.add(down);
            } else {
                item.setType(block.getType());
                item.setDurability((short) block.getData());
                block.setType(Material.AIR);
                world.dropItem(block.getLocation(), item);
                if ((plugin.moreDamageToTools) &&
                        (breaksTool(player, player.getItemInHand()))) {
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
            }
        }
        for (int counter = 0; counter < downs.size(); counter++) {
            block = downs.get(counter);
            if (isLoneLog(block)) {
                downs.remove(block);
                block.breakNaturally();
                if ((plugin.moreDamageToTools) &&
                        (breaksTool(player, player.getItemInHand()))) {
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
            }
        }
        if (plugin.popLeaves) {
            moveLeavesDown(blocks);
        }
        if (plugin.trees.containsKey(player)) {
            plugin.trees.remove(player);
        }
        if (downs.isEmpty()) {
            return;
        }
        Block[] blockarray = new Block[downs.size()];
        for (int counter = 0; counter < downs.size(); counter++) {
            blockarray[counter] = downs.get(counter);
        }
        plugin.trees.put(player, blockarray);
    }

    public void moveLeavesDown(List<Block> blocks) {
        List<Block> leaves = new LinkedList<>();
        int y = 0;
        Iterator<Block> blockIterator = blocks.iterator();
        Block block;
        while ((blockIterator.hasNext()) && (y < plugin.leafRadius + 1)) {
            block = blockIterator.next();
            y = -plugin.leafRadius;
            for (int x = -plugin.leafRadius; x < plugin.leafRadius + 1; x++) {
                for (int z = -plugin.leafRadius; z < plugin.leafRadius + 1; z++) {
                    if ((isLeavesBlock(block.getRelative(x, y, z).getType())) && (!leaves.contains(block.getRelative(x, y, z)))) {
                        leaves.add(block.getRelative(x, y, z));
                    }
                }
            }
            y++;
        }
        for (Block block : leaves) {
            if (((block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) ||
                    (isLeavesBlock(block.getRelative(BlockFace.DOWN).getType()))) &&
                    ((block.getRelative(BlockFace.DOWN, 2).getType().equals(Material.AIR)) ||
                            (isLeavesBlock(block.getRelative(BlockFace.DOWN, 2).getType())) ||
                            (isLogBlock(block.getRelative(BlockFace.DOWN, 2).getType()))) && (
                    (block.getRelative(BlockFace.DOWN, 3).getType().equals(Material.AIR)) ||
                            (isLeavesBlock(block.getRelative(BlockFace.DOWN, 3).getType())) ||
                            (isLogBlock(block.getRelative(BlockFace.DOWN, 3).getType())))) {
                block.getRelative(BlockFace.DOWN).setType(block.getType());
                block.setType(Material.AIR);
            } else {
                block.breakNaturally();
            }
        }
    }

    public boolean breaksTool(Player player, ItemStack item) {
        if ((item != null) &&
                (isTool(item.getType()))) {
            short damage = item.getDurability();
            if (isAxe(item.getType())) {
                damage = (short) (damage + 1);
            } else {
                damage = (short) (damage + 2);
            }
            if (damage >= item.getType().getMaxDurability()) {
                return true;
            }
            item.setDurability(damage);
        }
        return false;
    }

    public boolean isTool(Material ID) {
        if ((ID == Material.IRON_SHOVEL) || (ID == Material.IRON_PICKAXE) || (ID == Material.IRON_AXE) || (ID == Material.IRON_SWORD) || (ID == Material.WOODEN_SWORD) || (ID == Material.WOODEN_SHOVEL) || (ID == Material.WOODEN_PICKAXE) || (ID == Material.WOODEN_AXE) || (ID == Material.STONE_SWORD) || (ID == Material.STONE_SHOVEL) || (ID == Material.STONE_PICKAXE) || (ID == Material.STONE_AXE) || (ID == Material.DIAMOND_SWORD) || (ID == Material.DIAMOND_SHOVEL) || (ID == Material.DIAMOND_PICKAXE) || (ID == Material.DIAMOND_AXE) || (ID == Material.GOLDEN_SWORD) || (ID == Material.GOLDEN_SHOVEL) || (ID == Material.GOLDEN_PICKAXE) || (ID == Material.GOLDEN_AXE)) {
            return true;
        }
        return false;
    }

    public boolean isAxe(Material ID) {
        if ((ID == Material.WOODEN_AXE) || (ID == Material.STONE_AXE) || (ID == Material.IRON_AXE) || (ID == Material.DIAMOND_AXE) || (ID == Material.GOLDEN_AXE)) {
            return true;
        }
        return false;
    }

    public boolean isLoneLog(Block block) {
        if (isLogBlock(block.getRelative(BlockFace.UP).getType())) {
            return false;
        }
        if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            return false;
        }
        if (hasHorizontalCompany(block)) {
            return false;
        }
        if (hasHorizontalCompany(block.getRelative(BlockFace.UP))) {
            return false;
        }
        if (hasHorizontalCompany(block.getRelative(BlockFace.DOWN))) {
            return false;
        }
        return true;
    }

    public boolean hasHorizontalCompany(Block block) {
        if (isLogBlock(block.getRelative(BlockFace.NORTH).getType())) {
            return true;
        }
        if (isLogBlock(block.getRelative(BlockFace.NORTH_EAST).getType())) {
            return true;
        }
        if (isLogBlock(block.getRelative(BlockFace.EAST).getType())) {
            return true;
        }
        if (isLogBlock(block.getRelative(BlockFace.SOUTH_EAST).getType())) {
            return true;
        }
        if (isLogBlock(block.getRelative(BlockFace.SOUTH).getType())) {
            return true;
        }
        if (isLogBlock(block.getRelative(BlockFace.SOUTH_WEST).getType())) {
            return true;
        }
        if (isLogBlock(block.getRelative(BlockFace.WEST).getType())) {
            return true;
        }
        if (isLogBlock(block.getRelative(BlockFace.NORTH_WEST).getType())) {
            return true;
        }
        return false;
    }

}