package com.cloudbees.plugins.codeship.model;

import java.util.List;

/**
 * Translated from https://github.com/codeship/jet/blob/master/step/external_step.go
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Step {

    public String name;
    public String type;
    public String tag;
    public String exclude;
    public String service;
    public List<String> services;
    public String command;
    public List<Step> steps;
    public String image_name;
    public String image_tag;
    public String registry;
    public String encrypted_dockercfg_path;
    public String dockercfg_service;
    public List<Step> on_fail;
}
