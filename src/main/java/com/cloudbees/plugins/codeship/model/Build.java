package com.cloudbees.plugins.codeship.model;

import java.util.List;
import java.util.Map;

/**
 * Translated from https://github.com/codeship/jet/blob/master/service/external_services.go#externalBuildData
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Build {

    public String path;
    public String dockerfile_path;
    public String image;
    public String context;
    public String dockerfile;
    public BuildConfigure configure;
    public Map<String, String> args;
    public List<String> encrypted_args_file;
    public List<String> encrypted_args;
}
