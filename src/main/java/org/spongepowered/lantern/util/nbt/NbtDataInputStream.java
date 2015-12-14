/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.lantern.util.nbt;

import static org.spongepowered.api.data.DataQuery.of;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * This class reads NBT, or Named Binary Tag streams, and produces a
 * representative {@link DataContainer}.
 */
public class NbtDataInputStream implements Closeable {

    /**
     * The data input stream.
     */
    private final DataInputStream is;

    /**
     * Creates a new NbtDataInputStream, which will source its data
     * from the specified input stream. This assumes the stream is compressed.
     * @param is The input stream.
     * @throws IOException if an I/O error occurs.
     */
    public NbtDataInputStream(InputStream is) throws IOException {
        this(is, true);
    }

    /**
     * Creates a new NbtDataInputStream, which sources its data from the
     * specified input stream. A flag must be passed which indicates if the
     * stream is compressed with GZIP or not.
     * @param is The input stream.
     * @param compressed A flag indicating if the stream is compressed.
     * @throws IOException if an I/O error occurs.
     */
    public NbtDataInputStream(InputStream is, boolean compressed) throws IOException {
        this.is = new DataInputStream(compressed ? new GZIPInputStream(is) : is);
    }

    public DataContainer read() throws IOException {
        // read type
        TagType type = TagType.byIdOrError(is.readUnsignedByte());
        if (type != TagType.COMPOUND) {
            throw new IOException("Root of NBTInputStream was " + type + ", not COMPOUND");
        }

        // for now, throw away name
        int nameLength = is.readUnsignedShort();
        is.skipBytes(nameLength);

        // read tag
        return (DataContainer) readTagPayload(type);
    }

    private DataContainer readCompound() throws IOException {
        DataContainer result = new MemoryDataContainer();

        while (true) {
            // read type
            TagType type = TagType.byIdOrError(is.readUnsignedByte());
            if (type == TagType.END) {
                break;
            }

            // read name
            int nameLength = is.readUnsignedShort();
            byte[] nameBytes = new byte[nameLength];
            is.readFully(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);

            // read tag
            result.set(of(name), readTagPayload(type));
        }

        return result;
    }

    /**
     * Reads the payload of a tag, given the type.
     * @param type The type
     * @return The tag
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("unchecked")
    private Object readTagPayload(TagType type) throws IOException {
        switch (type.getId()) {
            case 1:
                return is.readByte();

            case 2:
                return is.readShort();

            case 3:
                return is.readInt();

            case 4:
                return is.readLong();

            case 5:
                return is.readFloat();

            case 6:
                return is.readDouble();

            case 7:
                int length = is.readInt();
                byte[] bytes = new byte[length];
                is.readFully(bytes);
                return bytes;

            case 8:
                length = is.readShort();
                bytes = new byte[length];
                is.readFully(bytes);
                return new String(bytes, StandardCharsets.UTF_8);

            case 9:
                TagType childType = TagType.byIdOrError(is.readUnsignedByte());
                length = is.readInt();

                List<Object> list = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    list.add(readTagPayload(childType));
                }

                return list;

            case 10:
                return readCompound();

            case 11:
                length = is.readInt();
                int[] ints = new int[length];
                for (int i = 0; i < length; ++i) {
                    ints[i] = is.readInt();
                }
                return ints;

            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
