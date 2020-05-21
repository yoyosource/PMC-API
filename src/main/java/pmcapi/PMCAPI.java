package pmcapi;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sun.istack.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class PMCAPI extends JavaPlugin implements PluginMessageListener {

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


    }

    private static void send(Plugin source, Player player, String subchannel, String... argument) {
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

    private static void send(Plugin source, String subchannel, String... argument) {
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

    public static void getIP(Plugin source, String player) {
        send(source, "player");
    }
}
