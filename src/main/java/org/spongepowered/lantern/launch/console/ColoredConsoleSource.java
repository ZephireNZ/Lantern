package org.spongepowered.lantern.launch.console;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ConsoleSource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ColoredConsoleSource implements ConsoleSource {
    @Override
    public String getName() {
        return "Server";
    }

    @Override
    public void sendMessage(Text... messages) {
        //TODO: Implement
    }

    @Override
    public void sendMessage(Iterable<Text> messages) {
        //TODO: Implement
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
}
