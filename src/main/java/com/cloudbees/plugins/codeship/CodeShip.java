package com.cloudbees.plugins.codeship;

import com.cloudbees.plugins.codeship.model.Service;
import com.cloudbees.plugins.codeship.model.Services;
import com.cloudbees.plugins.codeship.model.Step;
import com.cloudbees.plugins.codeship.model.Steps;
import com.cloudbees.plugins.codeship.model.VersionedServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convert a CodeShip pipeline (<code>codeship-steps.yaml</code> and al.) into a Jenkins CPS Pipeline script.
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class CodeShip {

    public static String translate(String codeship_services_yaml, String codeship_steps_yaml) throws IOException, InvocationTargetException, IllegalAccessException {

        Yaml parser = new Yaml();
        Map<String,?> map = (Map) parser.load(codeship_services_yaml);

        // see https://documentation.codeship.com/pro/builds-and-configuration/services/#services-file-setup--configuration



        // reproduce logic from https://github.com/codeship/jet/blob/master/service/unmarshal_external_services.go#unmarshalExternalServices
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final VersionedServices versionedServices = mapper.readValue(codeship_services_yaml, VersionedServices.class);

        Services services;

        if (versionedServices.version != null) {
            services = versionedServices.services;
        } else {
            services = mapper.readValue(codeship_services_yaml, Services.class);
        }

        // Write equivalent Jenkins pipeline

        final StringWriter out = new StringWriter();
        final PrintWriter p = new PrintWriter(out);

        // NOTE alternatively, we could produce a PodTemplate here
        p.println("node() {");
        for (Map.Entry<String, Service> e : services.entrySet()) {
            final String name = e.getKey();
            final Service service = e.getValue();

            p.println("    def "+name+"_image = " + service.getImageForPipeline());
        }

        final Steps steps = mapper.readValue(codeship_steps_yaml, Steps.class);


        p.println();

        for (Step step : steps) {

            p.println("    stage('"+step.name+"') {");
            String args = "";
            final Service service = services.get(step.service);
            int indent = 0;
            for (String depends : service.getLinks()) {         // TODO recursively compute list of dependencies in order
                indent(p, indent);
                p.println("        "+depends+"_image.withRun() { "+depends+" ->");
                indent++;
                args += " --link ${"+depends+".id}:"+depends;
            }

            indent(p, indent);
            // docker.run always run detached. So use plain docker CLI here
            p.println("        sh \"docker run -t "+args+" ${"+step.service+"_image.id} "+step.command+"\"");

            for (;indent > 0;) {
                indent(p, indent--);
                p.println("    }");
            }

            p.println("    }");
        }

        p.println("}");
        return out.toString();
    }

    private static void indent(PrintWriter p, int indent) {
        for (int i = 0; i<indent; i++) p.print("    ");
    }


    private static List<String> sortServicesByDependency(Services services) {

        List<String> ordered = new ArrayList<>();
        Map<String, Service> remaining = new HashMap<>(services);

        do {
            Map<String, Service> all = new HashMap<>(remaining);

            NEXT: for (Map.Entry<String, Service> entry : all.entrySet()) {
                final Service service = entry.getValue();
                if (service.links == null) {
                    ordered.add(entry.getKey());
                    remaining.remove(entry.getKey());
                } else {
                    for (String d : service.links) {
                        if (!ordered.contains(d)) {
                            continue NEXT;
                        }
                    }
                    ordered.add(entry.getKey());
                    remaining.remove(entry.getKey());
                }
            }
        } while (!remaining.isEmpty());

        return ordered;
    }
}
