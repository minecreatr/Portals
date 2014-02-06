package com.enjin.druxuscraft.portals;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Portals extends JavaPlugin implements Listener{

    HashMap<String, Location> temp = new HashMap<String, Location>();

    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);
        saveConfig();
    }

    public void onDisable(){
        saveConfig();
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if (player.getItemInHand().getType() == Material.ARROW && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("pointer") && player.isOp()){
            if (e.getAction() == Action.LEFT_CLICK_BLOCK){
                e.setCancelled(true);
                Location loc = e.getClickedBlock().getLocation();
                temp.put("loc1"+player.getName(), loc);
                player.sendMessage("The First position has been set as x: "+loc.getX()+" y: "+loc.getY()+" z: "+loc.getZ());
            }
            else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
                e.setCancelled(true);
                Location loc = e.getClickedBlock().getLocation();
                temp.put("loc2"+player.getName(), loc);
                player.sendMessage("The Second position has been set as x: "+loc.getX()+" y: "+loc.getY()+" z: "+loc.getZ());
            }

        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("newportal")){
            if (!(args.length>2)){
                return false;
            }
            if (temp.get("loc1"+player.getName())==null || temp.get("loc2"+player.getName())==null){
                player.sendMessage("You have not selected point one and/or point two");
                return true;
            }

            Location loc1 = temp.get("loc1"+player.getName());
            Location loc2 = temp.get("loc2"+player.getName());
            this.getConfig().set(args[0]+".portalloc1"+".x", loc1.getX());
            this.getConfig().set(args[0]+".portalloc1"+".y", loc1.getY());
            this.getConfig().set(args[0]+".portalloc1"+".z", loc1.getZ());
            this.getConfig().set(args[0]+".portalloc2"+".x", loc2.getX());
            this.getConfig().set(args[0]+".portalloc2"+".y", loc2.getY());
            this.getConfig().set(args[0]+".portalloc2"+".z", loc2.getZ());
            String output = "";
            for (int i=1; i<args.length;i++){
                output = output +args[i]+" ";
            }
            this.getConfig().set(args[0]+".command", output);
            player.sendMessage("New portal has been created between ("+loc1.getX()+","+loc1.getY()+","+loc1.getZ()+") and ("+loc2.getX()+","+loc2.getY()+","+loc2.getZ()+") with the command set to: /"+output);
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("delportal")){
            if (args.length!=1){
                return false;
            }
            for (String name: this.getConfig().getKeys(false)){
                if (name.equals(args[0])){
                    this.getConfig().set(name, null);
                    player.sendMessage(name + " has been deleted");
                }
            }
            return true;
        }
        return false;
    }

    public boolean isBetween(double n1, double n2, double b){
        if(n1<=b && b<=n2 || n2<=b && b<=n1){
            return true;
        }
        else {
            return false;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Material m = player.getLocation().getBlock().getType();
        if (m == Material.STATIONARY_WATER || m == Material.WATER){
            for (String name : this.getConfig().getKeys(false)){
                if (isBetween(this.getConfig().getDouble(name+".portalloc1.x"), this.getConfig().getDouble(name+".portalloc2.x"), Math.round(player.getLocation().getX()))){
                    if (isBetween(this.getConfig().getDouble(name+".portalloc1.y"), this.getConfig().getDouble(name+".portalloc2.y"), Math.round(player.getLocation().getY()))){
                        if (isBetween(this.getConfig().getDouble(name+".portalloc1.z"), this.getConfig().getDouble(name+".portalloc2.z"), Math.round(player.getLocation().getZ()))){
                            if (player.isOp()==false){
                                player.setOp(true);
                                Bukkit.getServer().dispatchCommand((CommandSender) player, this.getConfig().getString(name + ".command"));
                                player.setOp(false);
                            }
                            else {
                                Bukkit.getServer().dispatchCommand((CommandSender) player, this.getConfig().getString(name + ".command"));
                            }
                        }
                    }
                  
                }
            }
        }
    }
}
