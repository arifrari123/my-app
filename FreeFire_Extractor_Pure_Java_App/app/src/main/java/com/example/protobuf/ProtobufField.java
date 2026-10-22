package com.example.protobuf;

import java.nio.charset.StandardCharsets;

public class ProtobufField {
    private final int fieldNum;
    private final int wireType;
    private final long varintValue;
    private final byte[] rawBytes;

    public ProtobufField(int fieldNum, int wireType, long varintValue, byte[] rawBytes) {
        this.fieldNum = fieldNum;
        this.wireType = wireType;
        this.varintValue = varintValue;
        this.rawBytes = rawBytes != null ? rawBytes : new byte[0];
    }

    public int getFieldNum() { return fieldNum; }
    public int getWireType() { return wireType; }
    public long getVarintValue() { return varintValue; }
    public byte[] getRawBytes() { return rawBytes; }

    public String asString() {
        return new String(rawBytes, StandardCharsets.UTF_8);
    }
}
