package com.nbsp.translator.models.cloudsight;

import java.util.List;

/**
 * Created by Dimorinny on 24.09.15.
 */
public class CSCheckResultResponse {

    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_NOT_COMPLETED = "not completed";
    public static final String STATUS_TIMEOUT = "timeout";

    private String status;
    private String name;
    private List<String> flags;

    public CSCheckResultResponse(String status, String name, List<String> flags) {
        this.status = status;
        this.name = name;
        this.flags = flags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }
}
