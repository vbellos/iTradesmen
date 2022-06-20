package com.vbellos.dev.itradesmen.Models;

import java.util.UUID;

public class Job {
    String id,title,icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Job()
    {
        UUID id = UUID.randomUUID();
        this.id = id.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
