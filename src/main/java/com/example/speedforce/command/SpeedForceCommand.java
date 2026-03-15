package com.example.speedforce.command;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SpeedForceCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("speedforce")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("grant")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    grantPower(player, 1);
                    context.getSource().sendSuccess(() -> Component.translatable("message.speedforce.granted"), false);
                    return 1;
                })
                .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        int level = IntegerArgumentType.getInteger(context, "level");
                        grantPower(player, level);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.granted_level", level), false);
                        return 1;
                    }))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        grantPower(target, 1);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.granted_to", target.getName().getString()), false);
                        return 1;
                    }))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                        .executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "player");
                            int level = IntegerArgumentType.getInteger(context, "level");
                            grantPower(target, level);
                            context.getSource().sendSuccess(() -> 
                                Component.translatable("message.speedforce.granted_to_level", target.getName().getString(), level), false);
                            return 1;
                        }))))
            .then(Commands.literal("revoke")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    revokePower(player);
                    context.getSource().sendSuccess(() -> Component.translatable("message.speedforce.revoked"), false);
                    return 1;
                })
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        revokePower(target);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.revoked_from", target.getName().getString()), false);
                        return 1;
                    })))
            .then(Commands.literal("info")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    var data = player.getData(ModAttachments.SPEED_PLAYER);
                    context.getSource().sendSuccess(() -> 
                        Component.translatable("message.speedforce.info", 
                            data.hasPower ? "Yes" : "No", 
                            data.speedLevel,
                            data.trailColorR, data.trailColorG, data.trailColorB), false);
                    return 1;
                }))
            .then(Commands.literal("color")
                .requires(source -> source.hasPermission(0))
                .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                    .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                int r = IntegerArgumentType.getInteger(context, "r");
                                int g = IntegerArgumentType.getInteger(context, "g");
                                int b = IntegerArgumentType.getInteger(context, "b");
                                SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                                if (!data.hasPower) {
                                    context.getSource().sendFailure(Component.translatable("message.speedforce.no_power"));
                                    return 0;
                                }
                                data.trailColorR = r;
                                data.trailColorG = g;
                                data.trailColorB = b;
                                player.setData(ModAttachments.SPEED_PLAYER, data);
                                ModNetworking.syncToClient(player);
                                context.getSource().sendSuccess(() -> 
                                    Component.translatable("message.speedforce.color_set", r, g, b), false);
                                return 1;
                            })))))
        );
    }

    private static void grantPower(ServerPlayer player, int level) {
        player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, level));
        ModNetworking.syncToClient(player);
    }

    private static void revokePower(ServerPlayer player) {
        player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(false, 0));
        ModNetworking.syncToClient(player);
    }
}