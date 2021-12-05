// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import org.terasology.computer.system.common.ComputerLanguageRegistry;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.config.ModuleConfigManager;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;

import java.util.Collections;

@RegisterSystem(RegisterMode.ALWAYS)
public class WirelessModuleCommonSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    public static final String WIRELESS_MODULE_TYPE = "Wireless";
    private static final int CLEANUP_INTERVAL = 60000; // 60 seconds, probably could be even less often

    @In
    private ComputerLanguageRegistry computerLanguageRegistry;
    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private ModuleConfigManager moduleConfigManager;
    @In
    private Time time;

    private CommunicationChannels<EntityRef> communicationChannels = new CommunicationChannels<>();

    private long lastCleanup;

    @Override
    public void update(float delta) {
        long currentTime = time.getRealTimeInMs();
        if (lastCleanup + CLEANUP_INTERVAL < currentTime) {
            lastCleanup = currentTime;
            communicationChannels.expireOldMessages(time.getGameTimeInMs());
        }
    }

    @Override
    public void preBegin() {
        if (moduleConfigManager.getBooleanVariable("ModularComputers", "registerModule.wireless", true)) {
            computerLanguageRegistry.registerObjectType(
                    "CommunicationChannelBinding",
                    Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null,
                            "An object that tells a method how to access a communication channel."
                                    + "This object is extensively used in <h navigate:"
                                    + DocumentationBuilder.getComputerModulePageId(WIRELESS_MODULE_TYPE)
                                    + ">Wireless communications</h> computer module as a parameter in all methods " +
                                    "that allow to send or receive messages.")));


            int maxMessageExpiry = moduleConfigManager.getIntVariable("ModularComputers", "wireless.maxMessageExpiry", 5000);
            int maxMessageLength = moduleConfigManager.getIntVariable("ModularComputers", "wireless.maxMessageLength", 1024);

            computerModuleRegistry.registerComputerModule(
                    WIRELESS_MODULE_TYPE,
                    new WirelessComputerModule(communicationChannels, time, maxMessageLength, maxMessageExpiry, 256f,
                            WIRELESS_MODULE_TYPE, "Wireless communications"),
                    "This module allows computers to communicate with one another.",
                    null);
        }
    }
}
