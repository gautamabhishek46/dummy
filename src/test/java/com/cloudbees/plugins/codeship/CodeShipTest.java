package com.cloudbees.plugins.codeship;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.RestartableJenkinsRule;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class CodeShipTest {

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void should_run_codeship_pipeline() throws Exception {

        final WorkflowJob p = j.createProject(WorkflowJob.class, "prj");
        p.setDefinition(new CodeshipPipelineDefinition(
                "demo:\n" +
                        "  image: ruby:2.2.1\n" +
                        "  depends_on:\n" +
                        "    - redis\n" +
                        "    - postgres\n" +
                        "redis:\n" +
                        "  image: healthcheck/redis:alpine\n" +
                        "postgres:\n" +
                        "  image: healthcheck/postgres:alpine",

                "- name: ruby\n" +
                        "  service: demo\n" +
                        "  command: ruby --version"));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        j.assertLogContains("ruby 2.2.1", b);
    }

}