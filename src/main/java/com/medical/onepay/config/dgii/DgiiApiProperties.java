package com.medical.onepay.config.dgii;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dgii.api")
public class DgiiApiProperties {

    private String baseUrl;
    private Endpoints endpoints = new Endpoints();

    @Data
    public static class Endpoints {
        private Auth auth = new Auth();
        private Invoice invoice = new Invoice();
        private Summary summary = new Summary();
        private Inquiry inquiry = new Inquiry();
        private Commercial commercial = new Commercial();
        private Status status = new Status();
        private Tracking tracking = new Tracking();
        private Directory directory = new Directory();
        private String voidOperation;
        private Service service = new Service();
    }

    @Data
    public static class Auth {
        private String seed;
        private String validate;
    }

    @Data
    public static class Invoice {
        private String send;
    }

    @Data
    public static class Summary {
        private String send;
    }

    @Data
    public static class Inquiry {
        private String invoiceSummary;
    }

    @Data
    public static class Commercial {
        private String approval;
    }

    @Data
    public static class Status {
        private String track;
        private String inquiry;
    }

    @Data
    public static class Tracking {
        private String all;
    }

    @Data
    public static class Directory {
        private String test;
        private String prod;
    }

    @Data
    public static class Service {
        private String status;
        private String maintenance;
        private String verification;
    }
}
