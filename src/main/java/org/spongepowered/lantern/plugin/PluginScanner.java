package org.spongepowered.lantern.plugin;

import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Sets;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.lantern.Sponge;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.zip.ZipFile;

import javax.annotation.Nullable;

final class PluginScanner {

    private static final String PLUGIN_DESCRIPTOR = Type.getDescriptor(Plugin.class);

    private static final String JAVA_HOME = StandardSystemProperty.JAVA_HOME.value();

    private static final String CLASS_EXTENSION = ".class";

    static final DirectoryStream.Filter<Path> CLASS_OR_DIRECTORY = path -> Files.isDirectory(path) || path.endsWith(CLASS_EXTENSION);

    static final DirectoryStream.Filter<Path> ARCHIVE = path -> path.endsWith(".jar") || path.endsWith(".zip");

    private PluginScanner() {
    }

    static Set<String> scanClassPath(URLClassLoader loader) {
        Set<URI> sources = Sets.newHashSet();
        Set<String> plugins = Sets.newHashSet();

        for (URL url : loader.getURLs()) {
            if (!url.getProtocol().equals("file")) {
                Sponge.getLogger().warn("Skipping unsupported classpath source: {}", url);
                continue;
            }

            if (url.getPath().startsWith(JAVA_HOME)) {
                Sponge.getLogger().trace("Skipping JRE classpath entry: {}", url);
                continue;
            }

            URI source;
            try {
                source = url.toURI();
            } catch (URISyntaxException e) {
                Sponge.getLogger().error("Failed to search for classpath plugins in {}", url);
                continue;
            }

            if (sources.add(source)) {
                scanFile(Paths.get(source), plugins);
            }
        }

        Sponge.getLogger().trace("Found {} plugin(s): {}", plugins.size(), plugins);
        return plugins;
    }

    private static Set<String> scanFile(Path file) {
        Set<String> plugins = Sets.newHashSet();
        scanFile(file, plugins);
        Sponge.getLogger().trace("Found {} plugin(s): {}", plugins.size(), plugins);
        return plugins;
    }

    private static void scanFile(Path path, Set<String> plugins) {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                scanDirectory(path, plugins);
            } else {
                scanZip(path, plugins);
            }
        }
    }

    private static void scanDirectory(Path dir, final Set<String> plugins) {
        Sponge.getLogger().trace("Scanning {} for plugins", dir);

        try {
            scanDirectory0(dir, plugins);
        } catch (IOException e) {
            Sponge.getLogger().error("Failed to search for plugins in {}", dir, e);
        }
    }

    private static void scanDirectory0(Path dir, Set<String> plugins) throws IOException {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir, CLASS_OR_DIRECTORY)) {
            for(Path path : stream) {
                if(Files.isDirectory(path)) {
                    // Recurse into subdirectory
                    scanDirectory0(path, plugins);
                } else {
                    // This is a class file
                    Files.newInputStream(path);
                    try(InputStream in = Files.newInputStream(path)) {
                        String plugin = findPlugin(in);
                        if(plugin != null) {
                            plugins.add(plugin);
                        }
                    }
                }
            }
        }
    }

    static Set<String> scanZip(Path file) {
        Set<String> plugins = Sets.newHashSet();
        scanZip(file, plugins);
        Sponge.getLogger().trace("Found {} plugin(s): {}", plugins.size(), plugins);
        return plugins;
    }

    private static void scanZip(Path file, Set<String> plugins) {
        Sponge.getLogger().trace("Scanning {} for plugins", file);

        try {
            if (!ARCHIVE.accept(file)) {
                return;
            }
        } catch (IOException e) {
            return;
        }

        // Open the zip file so we can scan for plugins
        try (ZipFile zip = new ZipFile(file.toFile())) {
            zip.stream()
                    .filter(entry -> !entry.isDirectory())
                    .filter(entry -> !entry.getName().endsWith(CLASS_EXTENSION))
                    .forEach(entry -> {
                        try (InputStream in = zip.getInputStream(entry)) {
                            String plugin = findPlugin(in);
                            if(plugin != null) {
                                plugins.add(plugin);
                            }
                        } catch (IOException ignored) {}
                    });
        } catch (IOException e) {
            Sponge.getLogger().error("Failed to load plugin JAR: {}", file, e);
        }
    }

    @Nullable
    private static String findPlugin(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode node : classNode.visibleAnnotations) {
                if (node.desc.equals(PLUGIN_DESCRIPTOR)) {
                    return classNode.name.replace('/', '.');
                }
            }
        }

        return null;
    }

}
