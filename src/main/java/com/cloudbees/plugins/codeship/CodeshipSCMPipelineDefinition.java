package com.cloudbees.plugins.codeship;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import jenkins.scm.api.SCMFileSystem;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinitionDescriptor;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionOwner;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class CodeshipSCMPipelineDefinition extends FlowDefinition {


    private final SCM scm;

    @DataBoundConstructor
    public CodeshipSCMPipelineDefinition(SCM scm) {
        this.scm = scm;
    }


    @Override
    public FlowExecution create(FlowExecutionOwner owner, TaskListener listener, List<? extends Action> actions) throws Exception {

        Queue.Executable _build = owner.getExecutable();
        if (!(_build instanceof Run)) {
            throw new IOException("can only check out SCM into a Run");
        }
        Run<?,?> build = (Run<?,?>) _build;


        try (SCMFileSystem fs = SCMFileSystem.of(build.getParent(), scm)) {
            if (fs != null) {
                String services = fs.child("codeship-services.yaml").contentAsString();
                String steps = fs.child("codeship-steps.yaml").contentAsString();
                listener.getLogger().println("Obtained codeship pipeline from " + scm.getKey());

                String script = CodeShip.translate(services, steps);

                return new CpsFlowExecution(script, false, owner);
            } else {
                throw new IOException("SCM not supported");
                // FIXME implement full checkout
            }
        }
    }

    @Extension
    public static class DescriptorImpl extends FlowDefinitionDescriptor {

        @Override public String getDisplayName() {
            return "CodeShip Pipeline from SCM";
        }

        public Collection<? extends SCMDescriptor<?>> getApplicableDescriptors() {
            StaplerRequest req = Stapler.getCurrentRequest();
            Job<?,?> job = req != null ? req.findAncestorObject(Job.class) : null;
            return SCM._for(job);
        }
    }
}
