package pmcapi;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PMCAPI extends JavaPlugin implements PluginMessageListener {

    static String ipSync = "";
    static Integer pcSync = 0;
    static String[] plSync;
    static String uuidSync = "";

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {

            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        String str = "";
        if (subchannel.equals("IP")) {
            ipSync = in.readUTF() + ":" + in.readInt();
            synchronized (ipSync){
                Thread.currentThread().notifyAll();
            }
        }else if(subchannel.equals("PlayerCount")){
            in.readUTF();
            pcSync = in.readInt();
            synchronized (pcSync){
                Thread.currentThread().notifyAll();
            }
        }else if(subchannel.equals("PlayerList")){
            in.readUTF();
            plSync = in.readUTF().split(", ");
            synchronized (plSync){
                Thread.currentThread().notifyAll();
            }
        }else if(subchannel.equals("UUIDOther")){
            uuidSync = in.readUTF();
            synchronized (uuidSync){
                Thread.currentThread().notifyAll();
            }
        }
    }

    public static void send(Plugin source, Player player, String subchannel, String... argument) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subchannel);
        for (String str:
                argument) {
            out.writeUTF(str);
        }
        // If you don't care about the player
        //Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        // Else, specify them

        player.sendPluginMessage(source, "BungeeCord", out.toByteArray());
    }

    public static void send(Plugin source, String subchannel, String... argument) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subchannel);
        for (String str:
             argument) {
            out.writeUTF(str);
        }
        // If you don't care about the player
         Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        // Else, specify them
        //Player player = Bukkit.getPlayerExact("Example");

        player.sendPluginMessage(source, "BungeeCord", out.toByteArray());
    }

    public static void sendPlayer(Plugin source, String player, String server) {
        send(source, "Connect" , player, server);
    }

    public static String getIP(Plugin source, String player) {
        synchronized (ipSync) {
            send(source, "player");
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ipSync;
        }
    }

    public static int getPlayerCount(Plugin source, String server) {
        synchronized (pcSync){
            send(source, "PlayerCount", server);
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return pcSync;
        }
    }

    public static int getAllPlayerCount(Plugin source) {
        synchronized (pcSync){
            send(source, "PlayerCount", "ALL");
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return pcSync;
        }
    }

    public static String[] getPlayerList(Plugin source, String server) {
        synchronized (plSync){
            send(source, "PlayerList", server);
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return plSync;
        }
    }

    public static String[] getAllPlayerList(Plugin source) {
        synchronized (plSync){
            send(source, "PlayerList", "ALL");
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return plSync;
        }
    }

    public static void sendMessage(Plugin source, String player, String message) {
        send(source, "Message", player, message);
    }

    public static void broadcastMessage(Plugin source, String message) {
        send(source, "Message", "ALL", message);
    }

    public static void forward(Plugin source, String server, String channel, String data) {
        send(source, channel, server, data);
    }

    public static String getUUID(Plugin source, String player) {
        synchronized (uuidSync){
            send(source, "UUIDOther", player);
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return uuidSync;
        }
    }

    public static void kickPlayer(Plugin source, String player, String reason) {
        send(source, "KickPlayer", player, reason);
    }
}
