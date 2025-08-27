package tech.jilge.server;

import lombok.Getter;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Getter
public enum HttpImplementedURI {
    INDEX(List.of(URI.create("/login/test/")), new File("dist"));

    private final List<URI> uri;
    private final File targetPackage;

    HttpImplementedURI(List<URI> uri, File targetPackage) {
        this.uri = uri;
        this.targetPackage = targetPackage;
    }

}
