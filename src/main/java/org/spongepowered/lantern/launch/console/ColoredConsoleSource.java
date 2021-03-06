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
package org.spongepowered.lantern.launch.console;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ColoredConsoleSource implements ConsoleSource {
    @Override
    public String getName() {
        return "Server";
    }

    @Override
    public MessageSink getMessageSink() {
        return null; //TODO: Implement
    }

    @Override
    public void setMessageSink(MessageSink sink) {
        //TODO: Implement
    }

    @Override
    public String getIdentifier() {
        return getName();
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return null; //TODO: Implement
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return null; //TODO: Implement
    }

    @Override
    public SubjectData getSubjectData() {
        return null; //TODO: Implement
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return null; //TODO: Implement
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return false; //TODO: Implement
    }

    @Override
    public boolean hasPermission(String permission) {
        return false; //TODO: Implement
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return null; //TODO: Implement
    }

    @Override
    public boolean isChildOf(Subject parent) {
        return false; //TODO: Implement
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        return false; //TODO: Implement
    }

    @Override
    public List<Subject> getParents() {
        return null; //TODO: Implement
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        return null; //TODO: Implement
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null; //TODO: Implement
    }

    @Override
    public void sendMessage(Text message) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(Text... messages) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(Iterable<Text> messages) {
        //TODO: Implement
    }
}
