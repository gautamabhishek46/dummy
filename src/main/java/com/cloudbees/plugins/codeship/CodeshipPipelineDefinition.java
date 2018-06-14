package com.cloudbees.plugins.codeship;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinitionDescriptor;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionOwner;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class CodeshipPipelineDefinition extends FlowDefinition {

    private final String services_yaml;
    private final String steps_yaml;

    public CodeshipPipelineDefinition(String services_yaml, String steps_yaml) {
        this.services_yaml = services_yaml;
        this.steps_yaml = steps_yaml;
    }

    @Override
    public FlowExecution create(FlowExecutionOwner owner, TaskListener listener, List<? extends Action> actions) throws Exception {
        String script = CodeShip.translate(services_yaml, steps_yaml);
        System.out.println(script);
        return new CpsFlowExecution(script, false, owner);
    }

    @Extension
    public static class DescriptorImpl extends FlowDefinitionDescriptor {

        @Override public String getDisplayName() {
            return "CodeShip Pipeline";
        }

        public Collection<? extends SCMDescriptor<?>> getApplicableDescriptors() {
            StaplerRequest req = Stapler.getCurrentRequest();
            Job<?,?> job = req != null ? req.findAncestorObject(Job.class) : null;
            return SCM._for(job);
        }
    }
}
