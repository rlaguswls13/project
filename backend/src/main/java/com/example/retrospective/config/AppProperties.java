package com.example.retrospective.config;

import java.time.ZoneId;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Github github = new Github();
    private final Copilot copilot = new Copilot();
    private final Retrospective retrospective = new Retrospective();
    private final Seed seed = new Seed();

    public Github getGithub() {
        return github;
    }

    public Copilot getCopilot() {
        return copilot;
    }

    public Retrospective getRetrospective() {
        return retrospective;
    }

    public Seed getSeed() {
        return seed;
    }

    public static class Github {
        private String apiBaseUrl = "https://api.github.com";
        private String owner = "";
        private String repo = "";
        private String token = "";

        public String getApiBaseUrl() {
            return apiBaseUrl;
        }

        public void setApiBaseUrl(String apiBaseUrl) {
            this.apiBaseUrl = apiBaseUrl;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getRepo() {
            return repo;
        }

        public void setRepo(String repo) {
            this.repo = repo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class Copilot {
        private boolean enabled = false;
        private String sdkUrl = "http://localhost:3001";
        private String model = "auto";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSdkUrl() {
            return sdkUrl;
        }

        public void setSdkUrl(String sdkUrl) {
            this.sdkUrl = sdkUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public static class Retrospective {
        private String zone = "Asia/Seoul";

        public ZoneId getZoneId() {
            return ZoneId.of(zone);
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }
    }

    public static class Seed {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}