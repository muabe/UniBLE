package com.muabe.unible.client.profile;

import com.muabe.unible.client.CharacterDto;

public class InputCharacteristic {
    GattServiceHub gattServiceHub;

    String currCharacteristic;

    InputCharacteristic(GattServiceHub gattServiceHub){
        this.gattServiceHub = gattServiceHub;
    }

    InputCharacteristic(String currName, GattServiceHub gattServiceHub){
        currCharacteristic = currName;
        this.gattServiceHub = gattServiceHub;
    }

    public InputCharacteristic addCharacteristic(String name, String uuid){
        gattServiceHub.characters.put(name, new CharacterDto(uuid));
        this.currCharacteristic = name;
        return this;
    }

    public InputCharacteristic addDescriptor(String descName, String uuid){
        gattServiceHub.characters.get(currCharacteristic).addDesciptor(descName, uuid);
        return this;
    }
}
