package com.github.awasur04.toastybets;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;

public class ToastyBets {
    public static void main(String[] args) {
        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build().login().block();
        client.onDisconnect().block();
    }
}
