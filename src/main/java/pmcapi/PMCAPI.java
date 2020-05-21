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

public class PMCAPI implements PluginMessageListener {

    String ipSync = "";
    Integer pcSync = 0;
    String[] plSync;
    String uuidSync = "";

    public PMCAPI(Plugin plugin) {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
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

    public void send(Plugin source, Player player, String subchannel, String... argument) {
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

    public void send(Plugin source, String subchannel, String... argument) {
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

    public void sendPlayer(Plugin source, String player, String server) {
        send(source, "Connect" , player, server);
    }

    public String getIP(Plugin source, String player) {
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

    public int getPlayerCount(Plugin source, String server) {
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

    public int getAllPlayerCount(Plugin source) {
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

    public String[] getPlayerList(Plugin source, String server) {
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

    public String[] getAllPlayerList(Plugin source) {
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

    public void sendMessage(Plugin source, String player, String message) {
        send(source, "Message", player, message);
    }

    public void broadcastMessage(Plugin source, String message) {
        send(source, "Message", "ALL", message);
    }

    public void forward(Plugin source, String server, String channel, String data) {
        send(source, channel, server, data);
    }

    public String getUUID(Plugin source, String player) {
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

    public void kickPlayer(Plugin source, String player, String reason) {
        send(source, "KickPlayer", player, reason);
    }
}
