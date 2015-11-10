package org.spongepowered.lantern.util;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public final class PathMatchers {

    private PathMatchers() {
    }

    public static PathMatcher create(String pattern) {
        return FileSystems.getDefault().getPathMatcher(pattern);
    }

    public static DirectoryStream.Filter<Path> createFilter(final String pattern) {
        return createFilter(create(pattern));
    }

    public static DirectoryStream.Filter<Path> createFilter(final PathMatcher matcher) {
        return entry -> matcher.matches(entry.getFileName());
    }

}
