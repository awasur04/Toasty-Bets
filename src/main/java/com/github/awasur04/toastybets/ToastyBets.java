package com.github.awasur04.toastybets;

import com.github.awasur04.toastybets.managers.GameManager;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

public class ToastyBets {
    public static void main(String[] args) {
        GameManager gm = new GameManager();
        Runtime.getRuntime().addShutdownHook(new Thread(gm::closeProgram));

        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build().login().block();
        client.onDisconnect().block();
        client.updatePresence(Presence.online(Activity.playing("Coming Soon ;)")));
    }
}
