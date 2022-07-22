/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.github.merchantpug.toomanyorigins.networking;

import com.github.merchantpug.toomanyorigins.TooManyOrigins;
import com.github.merchantpug.toomanyorigins.util.TooManyOriginsServerConfig;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TMOPacketsC2S {
    public static void register() {
        if(TooManyOriginsServerConfig.performVersionCheck) {
            ServerLoginConnectionEvents.QUERY_START.register(TMOPacketsC2S::handshake);
            ServerLoginNetworking.registerGlobalReceiver(TMOPackets.HANDSHAKE, TMOPacketsC2S::handleHandshakeReply);
        }
    }

    private static void handleHandshakeReply(MinecraftServer minecraftServer, ServerLoginNetworkHandler serverLoginNetworkHandler, boolean understood, PacketByteBuf packetByteBuf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender) {
        if (understood) {
            int clientSemVerLength = packetByteBuf.readInt();
            int[] clientSemVer = new int[clientSemVerLength];
            boolean mismatch = clientSemVerLength != TooManyOrigins.SEMVER.length;
            for (int i = 0; i < clientSemVerLength; i++) {
                clientSemVer[i] = packetByteBuf.readInt();
                if (i < clientSemVerLength - 1 && clientSemVer[i] != TooManyOrigins.SEMVER[i]) {
                    mismatch = true;
                }
            }
            if (mismatch) {
                StringBuilder clientVersionString = new StringBuilder();
                for (int i = 0; i < clientSemVerLength; i++) {
                    clientVersionString.append(clientSemVer[i]);
                    if (i < clientSemVerLength - 1) {
                        clientVersionString.append(".");
                    }
                }
                serverLoginNetworkHandler.disconnect(Text.translatable("toomanyorigins.gui.version_mismatch", TooManyOrigins.VERSION, clientVersionString));
                return;
            }
            boolean legacyMismatch = false;
            List<MutableText> mismatchedOriginTranslationKeys = new ArrayList<>();

            boolean clientLegacyDragonbornEnabled = packetByteBuf.readBoolean();
            if (TooManyOrigins.legacyDragonbornContentRegistered && !clientLegacyDragonbornEnabled) {
                mismatchedOriginTranslationKeys.add(Text.translatable("origin.toomanyorigins.dragonborn.name"));
                legacyMismatch = true;
            }

            boolean clientLegacyUndeadEnabled = packetByteBuf.readBoolean();
            if (TooManyOrigins.legacyUndeadContentRegistered && !clientLegacyUndeadEnabled) {
                mismatchedOriginTranslationKeys.add(Text.translatable("origin.toomanyorigins.undead.name"));
                legacyMismatch = true;
            }

            boolean clientLegacyWitheredEnabled = packetByteBuf.readBoolean();
            if (TooManyOrigins.legacyWitheredContentRegistered && !clientLegacyWitheredEnabled) {
                mismatchedOriginTranslationKeys.add(Text.translatable("origin.toomanyorigins.withered.name"));
                legacyMismatch = true;
            }

            if (legacyMismatch) {
                MutableText originText = Text.literal("");
                for (int i = 0; i < mismatchedOriginTranslationKeys.size(); i++) {
                    originText.append(mismatchedOriginTranslationKeys.get(i));
                    if (i < mismatchedOriginTranslationKeys.size() - 1) {
                        originText.append(", ");
                    }
                }
                serverLoginNetworkHandler.disconnect(Text.translatable("toomanyorigins.gui.legacy_mismatch", originText));
            }
        } else {
            serverLoginNetworkHandler.disconnect(Text.literal("This server requires you to install the TooManyOrigins mod (v" + TooManyOrigins.VERSION + ") to play."));
        }
    }

    private static void handshake(ServerLoginNetworkHandler serverLoginNetworkHandler, MinecraftServer minecraftServer, PacketSender packetSender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer) {
        packetSender.sendPacket(TMOPackets.HANDSHAKE, PacketByteBufs.empty());
    }
}
