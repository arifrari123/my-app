package com.example.protobuf;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ProtobufEngine {
    private ProtobufEngine() {}

    public static List<ProtobufField> decode(byte[] data) {
        List<ProtobufField> fields = new ArrayList<>();
        if (data == null || data.length == 0) return fields;

        int pos = 0;
        int len = data.length;

        while (pos < len) {
            try {
                int tagByte = data[pos++] & 0xFF;
                int fieldNum = tagByte >> 3;
                int wireType = tagByte & 0x07;

                if (fieldNum <= 0) break;

                switch (wireType) {
                    case 0: { // Varint
                        long val = 0;
                        int shift = 0;
                        while (pos < len) {
                            int b = data[pos++] & 0xFF;
                            val |= ((long) (b & 0x7F)) << shift;
                            shift += 7;
                            if ((b & 0x80) == 0) break;
                        }
                        fields.add(new ProtobufField(fieldNum, wireType, val, null));
                        break;
                    }
                    case 1: { // 64-bit
                        if (pos + 8 > len) return fields;
                        byte[] bytes64 = new byte[8];
                        System.arraycopy(data, pos, bytes64, 0, 8);
                        pos += 8;
                        fields.add(new ProtobufField(fieldNum, wireType, 0, bytes64));
                        break;
                    }
                    case 2: { // Length-delimited
                        int length = 0;
                        int shift = 0;
                        while (pos < len) {
                            int b = data[pos++] & 0xFF;
                            length |= (b & 0x7F) << shift;
                            shift += 7;
                            if ((b & 0x80) == 0) break;
                        }
                        if (length < 0 || pos + length > len) return fields;
                        byte[] payload = new byte[length];
                        System.arraycopy(data, pos, payload, 0, length);
                        pos += length;
                        fields.add(new ProtobufField(fieldNum, wireType, 0, payload));
                        break;
                    }
                    case 5: { // 32-bit
                        if (pos + 4 > len) return fields;
                        byte[] bytes32 = new byte[4];
                        System.arraycopy(data, pos, bytes32, 0, 4);
                        pos += 4;
                        fields.add(new ProtobufField(fieldNum, wireType, 0, bytes32));
                        break;
                    }
                    default:
                        // Unsupported wire type
                        return fields;
                }
            } catch (Exception e) {
                break;
            }
        }
        return fields;
    }

    public static List<Long> unpackVarints(byte[] data) {
        List<Long> res = new ArrayList<>();
        if (data == null || data.length == 0) return res;

        int pos = 0;
        int len = data.length;
        while (pos < len) {
            long v = 0;
            int s = 0;
            while (pos < len) {
                int b = data[pos++] & 0xFF;
                v |= ((long) (b & 0x7F)) << s;
                s += 7;
                if ((b & 0x80) == 0) break;
            }
            res.add(v);
        }
        return res;
    }

    public static byte[] encodeVarintField(int fieldNum, long value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeVarint(out, ((long) fieldNum << 3));
        writeVarint(out, value);
        return out.toByteArray();
    }

    public static byte[] encodeStringField(int fieldNum, String str) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeVarint(out, ((long) fieldNum << 3) | 2);
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeVarint(out, bytes.length);
        out.write(bytes, 0, bytes.length);
        return out.toByteArray();
    }

    private static void writeVarint(ByteArrayOutputStream out, long value) {
        long v = value;
        while (true) {
            if ((v & ~0x7FL) == 0) {
                out.write((int) v);
                return;
            } else {
                out.write((int) ((v & 0x7F) | 0x80));
                v >>>= 7;
            }
        }
    }
}
