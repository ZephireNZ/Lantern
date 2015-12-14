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
package org.spongepowered.lantern.io;

import org.spongepowered.lantern.world.LanternChunk;

import java.io.IOException;

/**
 * Provider of chunk I/O services. Implemented by classes to provide a way of
 * saving and loading chunks to external storage.
 */
public interface ChunkIoService {

    /**
     * Reads a single chunk. The provided chunk must not yet be initialized.
     * @param chunk The GlowChunk to read into.
     * @throws IOException if an I/O error occurs.
     */
    boolean read(LanternChunk chunk) throws IOException;

    /**
     * Writes a single chunk.
     * @param chunk The {@link LanternChunk} to write from.
     * @throws IOException if an I/O error occurs.
     */
    void write(LanternChunk chunk) throws IOException;

    /**
     * Unload the service, performing any cleanup necessary.
     * @throws IOException if an I/O error occurs.
     */
    void unload() throws IOException;

}
