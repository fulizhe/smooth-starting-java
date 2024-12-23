package com.fulizhe.ssj.propedit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ConfigToHtmlGenerator {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File("D:\\gitRepository\\smooth-starting-java\\core\\src\\main\\resources\\META-INF\\propedit/props.json"));

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!doctype html>\n<html lang=\"en\">\n<head>\n")
                .append("<title>Configuration Wizard</title>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n")
                .append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n")
                .append("</head>\n<body>\n<div class=\"container\">\n")
                .append("<div id=\"smartwizard\">\n<ul class=\"nav nav-progress\">\n");

        JsonNode steps = rootNode.get("steps");
        for (int i = 0; i < steps.size(); i++) {
            JsonNode step = steps.get(i);
            htmlBuilder.append("<li class=\"nav-item\">\n")
                    .append("<a class=\"nav-link\" href=\"#step-").append(i + 1).append("\">\n")
                    .append("<div class=\"num\">").append(i + 1).append("</div>\n")
                    .append(step.get("title").asText()).append("\n</a>\n</li>\n");
        }

        htmlBuilder.append("</ul>\n<div class=\"tab-content\">\n");

        for (int i = 0; i < steps.size(); i++) {
            JsonNode step = steps.get(i);
            htmlBuilder.append("<div id=\"step-").append(i + 1).append("\" class=\"tab-pane\" role=\"tabpanel\">\n")
                    .append("<form id=\"form-").append(i + 1).append("\" class=\"row needs-validation\" novalidate>\n");

            JsonNode fields = step.get("fields");
            for (JsonNode field : fields) {
                String type = field.get("type").asText();
                String id = field.get("id").asText();
                String name = field.get("name").asText();
                htmlBuilder.append("<div class=\"col\">\n")
                        .append("<label for=\"").append(id).append("\" class=\"form-label\">").append(name).append("</label>\n");

                switch (type) {
                    case "checkbox":
                        htmlBuilder.append("<input class=\"form-control\" type=\"checkbox\" id=\"").append(id).append("\" name=\"").append(id).append("\">\n");
                        break;
                    case "text":
                        String defaultValue = field.has("default") ? field.get("default").asText() : "";
                        htmlBuilder.append("<input type=\"text\" class=\"form-control\" id=\"").append(id).append("\" value=\"").append(defaultValue).append("\" required>\n");
                        break;
                    case "select":
                        htmlBuilder.append("<select class=\"form-select\" id=\"").append(id).append("\" required>\n");
                        for (JsonNode option : field.get("options")) {
                            String selected = option.asText().equals(field.get("default").asText()) ? " selected" : "";
                            htmlBuilder.append("<option value=\"").append(option.asText()).append("\"").append(selected).append(">").append(option.asText()).append("</option>\n");
                        }
                        htmlBuilder.append("</select>\n");
                        break;
                }

                htmlBuilder.append("<div class=\"valid-feedback\">Looks good!</div>\n")
                        .append("<div class=\"invalid-feedback\">Please provide a valid ").append(name).append(".</div>\n")
                        .append("</div>\n");
            }

            htmlBuilder.append("</form>\n</div>\n");
        }

        htmlBuilder.append("</div>\n</div>\n</div>\n")
                .append("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\"></script>\n")
                .append("<script src=\"https://code.jquery.com/jquery-3.6.0.min.js\"></script>\n")
                .append("<script src=\"https://cdn.jsdelivr.net/npm/smartwizard@6/dist/js/jquery.smartWizard.min.js\"></script>\n")
                .append("<script>\n$(function() {\n$('#smartwizard').smartWizard();\n});\n</script>\n")
                .append("</body>\n</html>");
        Files.write(Paths.get("output2222.html"), htmlBuilder.toString().getBytes(), StandardOpenOption.CREATE);

        Files.write(Paths.get(System.getProperty("user.home") + "\\Desktop\\output2222.html"), htmlBuilder.toString().getBytes(), StandardOpenOption.CREATE);

        System.out.println(htmlBuilder.toString());
    }


}