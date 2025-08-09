package tech.jilge.server.bootstrap;

import lombok.SneakyThrows;
import tech.jilge.server.CloudAPI;

public final class Bootstrap {

    @SneakyThrows
    public static void main(String[] args) {

        CloudAPI.Factory.create();
    }

}
