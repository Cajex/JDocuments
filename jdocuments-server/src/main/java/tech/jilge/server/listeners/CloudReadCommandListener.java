package tech.jilge.server.listeners;

import tech.jilge.server.CloudAPI;
import tech.jilge.server.events.CloudTerminalCommandEvent;

public class CloudReadCommandListener {

    public CloudReadCommandListener() {
        CloudAPI.getInstance().getEventBus().registerListener(CloudTerminalCommandEvent.class, event -> {
            if (event.getCommand().equals("stop")) {
                CloudAPI.getInstance().getTerminal().send("Shutdown web server");
                Runtime.getRuntime().exit(0);
            } else {
                CloudAPI.getInstance().getTerminal().send("Command not found");
            }
        });
    }

}
