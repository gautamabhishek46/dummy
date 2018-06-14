package com.cloudbees.plugins.codeship.model;

import java.util.List;

/**
 * Translated from https://github.com/codeship/jet/blob/master/service/external_services.go#ExternalService
 *
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Service {

    public boolean add_docker;
    public Build build;
    public boolean cached;
    public List<String> cap_add;
    public List<String> cap_drop;
    public String command;
    public String cpuset;
    public long spu_shares;
    public String default_cache_branch;
    public List<String> dns;
    public List<String> dns_search;
    public String dockercfg_service;
    public String dockerfile;
    public String domainname;
    public String encrypted_dockercfg_path;
    public List<String> encrypted_env_file;
    public List<String>encrypted_environment;
    public List<String> entrypoint;
    public List<String> env_file;
    public List<String> environment;
    public List<String> expose;
    public List<String> extra_hosts;
    public String hostname;
    public String image;
    public List<String> links;
    public List<String> depends_on;
    public String mem_limit;
    public boolean privileged;
    public List<String> ports;
    public boolean read_only;
    public String restart;
    public List<String> security_opt;
    public String user;
    public List<String> wolumes;
    public List<String> volumes_from;
    public String working_dir;


    public String getImageForPipeline() {
        if (build == null) {
            return "docker.image('"+this.image+"')";
        } else {
            return "docker.build(image: '" + this.build.image + "')"; // TODO add support for build parameters
        }
    }

    public List<String> getLinks() {
        return links != null ? links : depends_on;
    }
}
