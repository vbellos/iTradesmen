package com.vbellos.dev.itradesmen.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WorkerDbObj {
    String id,job_id,description;
    Long timestamp;



    public WorkerDbObj(String id, String job_id, String description) {
        this.id = id;
        this.job_id = job_id;
        this.description = description;
    }

    public WorkerDbObj(){}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }



    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
