// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.Variable;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommunicationChannels<T> {
    private Multimap<String, Message> publicMessages = LinkedHashMultimap.create();
    private Multimap<String, MessageAwaitingLatchCondition> publicConditions = LinkedHashMultimap.create();

    private Map<T, Multimap<String, Message>> privateMessages = Maps.newHashMap();
    private Map<T, Multimap<String, MessageAwaitingLatchCondition>> privateConditions = Maps.newHashMap();

    private Multimap<String, SecureMessage> secureMessages = LinkedHashMultimap.create();
    private Multimap<String, SecureMessageAwaitingLatchCondition> secureConditions = LinkedHashMultimap.create();

    public void expireOldMessages(long currentTime) {
        expireMessagesFromMultimap(currentTime, publicMessages);

        for (Multimap<String, Message> messageMap : privateMessages.values()) {
            expireMessagesFromMultimap(currentTime, messageMap);
        }

        Iterator<SecureMessage> messageIterator = secureMessages.values().iterator();
        while (messageIterator.hasNext()) {
            SecureMessage message = messageIterator.next();
            if (isExpired(message.message, currentTime)) {
                messageIterator.remove();
            }
        }
    }

    private void expireMessagesFromMultimap(long currentTime, Multimap<String, Message> messageMap) {
        Iterator<Message> messageIterator = messageMap.values().iterator();
        while (messageIterator.hasNext()) {
            Message message = messageIterator.next();
            if (isExpired(message, currentTime)) {
                messageIterator.remove();
            }
        }
    }

    public void addPublicMessage(String channelName, Vector3i locationFrom, float range, String message, long expireOn) {
        Message messageObject = new Message(locationFrom, range, message, expireOn);
        boolean caughtByWaitingCondition = false;
        Iterator<MessageAwaitingLatchCondition> latchIterator = publicConditions.get(channelName).iterator();
        while (latchIterator.hasNext()) {
            MessageAwaitingLatchCondition messageAwaitingLatchCondition = latchIterator.next();
            if (isInRange(messageObject, messageAwaitingLatchCondition.getLocationTo(), messageAwaitingLatchCondition.getRangeTo())) {
                caughtByWaitingCondition = true;

                latchIterator.remove();
                messageAwaitingLatchCondition.release(constructMessageReturnObject(
                        messageObject, messageAwaitingLatchCondition.getLocationTo()));

                // We only dispatch the message to first waiting program
                break;
            }
        }
        if (!caughtByWaitingCondition) {
            publicMessages.put(channelName, messageObject);
        }
    }

    public Map<String, Variable> consumeNextPublicMessage(long currentTime, String channelName, Vector3i locationTo, float range) {
        Multimap<String, Message> messageMultimap = publicMessages;
        Message message = consumeMessageFromMultimap(currentTime, channelName, locationTo, range, messageMultimap);
        return constructMessageReturnObject(message, locationTo);
    }

    public void addPublicMessageCondition(String channelName, MessageAwaitingLatchCondition latchCondition) {
        publicConditions.put(channelName, latchCondition);
    }

    public void removePublicMessageCondition(String channelName, MessageAwaitingLatchCondition latchCondition) {
        publicConditions.remove(channelName, latchCondition);
    }

    public void addPrivateMessage(String channelName, T identity, Vector3i locationFrom, float range, String message, long expireOn) {
        Message messageObject = new Message(locationFrom, range, message, expireOn);
        boolean caughtByWaitingCondition = false;
        Multimap<String, MessageAwaitingLatchCondition> conditionMap = privateConditions.get(identity);
        if (conditionMap != null) {
            Iterator<MessageAwaitingLatchCondition> latchIterator = conditionMap.values().iterator();
            while (latchIterator.hasNext()) {
                MessageAwaitingLatchCondition messageAwaitingLatchCondition = latchIterator.next();
                if (isInRange(messageObject, messageAwaitingLatchCondition.getLocationTo(), messageAwaitingLatchCondition.getRangeTo())) {
                    caughtByWaitingCondition = true;

                    latchIterator.remove();
                    messageAwaitingLatchCondition.release(constructMessageReturnObject(
                            messageObject, messageAwaitingLatchCondition.getLocationTo()));
                    // We only dispatch the message to first waiting program
                    break;
                }
            }
        }
        if (!caughtByWaitingCondition) {
            Multimap<String, Message> identityMap = privateMessages.get(identity);
            if (identityMap == null) {
                identityMap = LinkedHashMultimap.create();
                privateMessages.put(identity, identityMap);
            }
            identityMap.put(channelName, messageObject);
        }
    }

    public Map<String, Variable> consumeNextPrivateMessage(long currentTime, String channelName, T identity,
                                                           Vector3i locationTo, float range) {
        Multimap<String, Message> identityMap = privateMessages.get(identity);
        if (identityMap != null) {
            Message message = consumeMessageFromMultimap(currentTime, channelName, locationTo, range, identityMap);
            return constructMessageReturnObject(message, locationTo);
        }
        return null;
    }

    public void addPrivateMessageCondition(String channelName, T identity, MessageAwaitingLatchCondition latchCondition) {
        Multimap<String, MessageAwaitingLatchCondition> conditionMap = privateConditions.get(identity);
        if (conditionMap == null) {
            conditionMap = LinkedHashMultimap.create();
            privateConditions.put(identity, conditionMap);
        }
        conditionMap.put(channelName, latchCondition);
    }

    public void removePrivateMessageCondition(String channelName, T identity, MessageAwaitingLatchCondition latchCondition) {
        Multimap<String, MessageAwaitingLatchCondition> conditionMap = privateConditions.get(identity);
        if (conditionMap != null) {
            conditionMap.remove(channelName, latchCondition);
        }
    }

    public void addSecureMessage(String channelName, String password, Vector3i locationFrom, float range, String message, long expireOn) {
        Message messageObject = new Message(locationFrom, range, message, expireOn);
        boolean caughtByWaitingCondition = false;
        Iterator<SecureMessageAwaitingLatchCondition> latchIterator = secureConditions.get(channelName).iterator();
        while (latchIterator.hasNext()) {
            SecureMessageAwaitingLatchCondition secureMessageAwaitingLatchCondition = latchIterator.next();
            if (secureMessageAwaitingLatchCondition.getPassword().equals(password)
                    && isInRange(messageObject, secureMessageAwaitingLatchCondition.getLocationTo(),
                    secureMessageAwaitingLatchCondition.getRangeTo())) {
                caughtByWaitingCondition = true;

                latchIterator.remove();
                secureMessageAwaitingLatchCondition.release(constructMessageReturnObject(messageObject,
                        secureMessageAwaitingLatchCondition.getLocationTo()));
                // We only dispatch the message to first waiting program
                break;
            }
        }

        if (!caughtByWaitingCondition) {
            secureMessages.put(channelName, new SecureMessage(password, messageObject));
        }
    }

    public Map<String, Variable> consumeNextSecureMessage(long currentTime, String channelName, String password,
                                                          Vector3i locationTo, float range) {
        Iterator<SecureMessage> messageIterator = secureMessages.get(channelName).iterator();
        while (messageIterator.hasNext()) {
            SecureMessage message = messageIterator.next();
            if (isExpired(message.message, currentTime)) {
                messageIterator.remove();
            } else {
                if (message.password.equals(password)) {
                    if (isInRange(message.message, locationTo, range)) {
                        messageIterator.remove();
                        return constructMessageReturnObject(message.message, locationTo);
                    }
                }
            }
        }
        return null;
    }

    public void addSecureMessageCondition(String channelName, SecureMessageAwaitingLatchCondition secureMessageAwaitingLatchCondition) {
        secureConditions.put(channelName, secureMessageAwaitingLatchCondition);
    }

    public void removeSecureMessageCondition(String channelName, SecureMessageAwaitingLatchCondition secureMessageAwaitingLatchCondition) {
        secureConditions.remove(channelName, secureMessageAwaitingLatchCondition);
    }

    private Message consumeMessageFromMultimap(long currentTime, String channelName, Vector3i locationTo, float range,
                                               Multimap<String, Message> messageMultimap) {
        Iterator<Message> messageIterator = messageMultimap.get(channelName).iterator();
        while (messageIterator.hasNext()) {
            Message message = messageIterator.next();
            if (isExpired(message, currentTime)) {
                messageIterator.remove();
            } else {
                if (isInRange(message, locationTo, range)) {
                    messageIterator.remove();
                    return message;
                }
            }
        }
        return null;
    }

    private boolean isInRange(Message message, Vector3i locationTo, float rangeTo) {
        float distance = distance(message, locationTo);
        return distance <= rangeTo && distance <= message.range;
    }

    private float distance(Message message, Vector3i locationTo) {
        return (float) locationTo.distance(message.from);
    }

    private boolean isExpired(Message message, long currentTime) {
        return message.expireOn <= currentTime;
    }

    private Map<String, Variable> constructMessageReturnObject(Message messageObject, Vector3i locationTo) {
        Map<String, Variable> messageResult = new HashMap<>();
        messageResult.put("message", new Variable(messageObject.message));
        messageResult.put("distance", new Variable(distance(messageObject, locationTo)));
        return messageResult;
    }

    private final class SecureMessage {
        private final String password;
        private final Message message;

        private SecureMessage(String password, Message message) {
            this.password = password;
            this.message = message;
        }
    }

    private final class Message {
        private final Vector3i from;
        private final float range;
        private final String message;
        private final long expireOn;

        private Message(Vector3i from, float range, String message, long expireOn) {
            this.from = from;
            this.range = range;
            this.message = message;
            this.expireOn = expireOn;
        }
    }
}
