package com.cloudbees.plugins.codeship.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionedServices {

    public String version;
    public Services services;
}
