package fr.synchrotron.soleil.ica.ci.lib.workflow;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Gregory Boissinot
 */
public class WorkflowTest {

    @Test(expected = NullPointerException.class)
    public void testNullNormalizedStatus() {
        Workflow workflow = new DefaultWorkflow();
        assertNull(workflow.getNormalizedStatus(null));
    }

    @Test(expected = NullPointerException.class)
    public void testNullNextStatus() {
        Workflow workflow = new DefaultWorkflow();
        assertNull(workflow.getNextStatusLabel(null));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructWorkFlowNullName() {
        WorkflowFactory.createWorkFlow(null, new ArrayList<String>());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructWorkFlowNullStatusList() {
        WorkflowFactory.createWorkFlow("A Name", null);
    }

    @Test
    public void testNormalizedStatus() {
        Workflow workflow = new DefaultWorkflow();
        assertNull(workflow.getNormalizedStatus("UNKNOWN_STATUS"));
        assertEquals("BUILD", workflow.getNormalizedStatus("build"));
        assertEquals("BUILD", workflow.getNormalizedStatus("BUILD"));
        assertEquals("INTEGRATION", workflow.getNormalizedStatus("integration"));
        assertEquals("INTEGRATION", workflow.getNormalizedStatus("INTEGRATION"));
        assertEquals("RELEASE", workflow.getNormalizedStatus("release"));
        assertEquals("RELEASE", workflow.getNormalizedStatus("RELEASE"));
    }

    @Test
    public void testBuildWorkflowOneElement() {
        Workflow workflow = WorkflowFactory.createWorkFlow("TEST_WORKFLOW_ONE_ELEMENT", Arrays.asList("BUILD"));
        assertEquals("TEST_WORKFLOW_ONE_ELEMENT", workflow.getName());
        assertEquals("BUILD", workflow.getLatestPromotedStatus());
        assertNull(workflow.getNextStatusLabel("build"));
        assertNull(workflow.getNextStatusLabel("BUILD"));
        assertNull(workflow.getNextStatusLabel("UNKNOWN_STATUS_LABEL"));
    }

    @Test
    public void testBuildWorkflowTwoElements() {
        Workflow workflow = WorkflowFactory.createWorkFlow("TEST_WORKFLOW_TWO_ELEMENTS", Arrays.asList("BUILD", "INTEGRATION"));
        assertEquals("TEST_WORKFLOW_TWO_ELEMENTS", workflow.getName());
        assertEquals("INTEGRATION", workflow.getLatestPromotedStatus());
        assertEquals("INTEGRATION", workflow.getNextStatusLabel("build"));
        assertEquals("INTEGRATION", workflow.getNextStatusLabel("BUILD"));
        assertNull(workflow.getNextStatusLabel("integration"));
        assertNull(workflow.getNextStatusLabel("INTEGRATION"));
        assertNull(workflow.getNextStatusLabel("UNKNOWN_STATUS_LABEL"));
    }

    @Test
    public void testBuildWorkflowThreeElements() {
        Workflow workflow = WorkflowFactory.createWorkFlow("TEST_WORKFLOW_THREE_ELEMENTS", Arrays.asList("BUILD", "INTEGRATION", "RELEASE"));
        assertEquals("TEST_WORKFLOW_THREE_ELEMENTS", workflow.getName());
        assertEquals("RELEASE", workflow.getLatestPromotedStatus());
        assertEquals("INTEGRATION", workflow.getNextStatusLabel("build"));
        assertEquals("INTEGRATION", workflow.getNextStatusLabel("BUILD"));
        assertEquals("RELEASE", workflow.getNextStatusLabel("integration"));
        assertEquals("RELEASE", workflow.getNextStatusLabel("INTEGRATION"));
        assertNull(workflow.getNextStatusLabel("release"));
        assertNull(workflow.getNextStatusLabel("RELEASE"));
        assertNull(workflow.getNextStatusLabel("UNKNOWN_STATUS_LABEL"));
    }
}
