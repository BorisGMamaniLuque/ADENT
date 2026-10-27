package com.adent.identity.service.recovery;

public interface RecoveryDeliveryService {
    void deliver(String channel, String destination, String code, int expiresInMinutes);
}
