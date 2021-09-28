package com.github.awasur04.toastybets;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

public class ToastyBets {
    public static void main(String[] args) {
        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build().login().block();
        client.onDisconnect().block();
        client.updatePresence(Presence.online(Activity.playing("Coming Soon ;)")));
    }
}
