package ru.Andrey.service.emums;

public enum ServiceComands {
    HELP( "/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String cmd;

    ServiceComands(String cmd){
        this.cmd = cmd;
    }
    @Override
    public String toString(){
        return cmd;
    }

    public boolean equals(String cmd){
        return this.toString().equals(cmd);
    }

}
