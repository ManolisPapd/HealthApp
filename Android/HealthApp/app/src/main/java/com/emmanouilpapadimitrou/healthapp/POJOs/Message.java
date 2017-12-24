package com.emmanouilpapadimitrou.healthapp.POJOs;



public class Message {
    private String content;
    private String name;
    private String time;

    public Message() {
    }

    public Message(String content, String name, String time) {
        this.content = content;
        this.name = name;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
