package org.spongepowered.lantern.plugin;

import java.net.URL;
import java.net.URLClassLoader;

public class LanternClassLoader extends URLClassLoader {

    public LanternClassLoader(URLClassLoader parent) {
        super(parent.getURLs(), parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
