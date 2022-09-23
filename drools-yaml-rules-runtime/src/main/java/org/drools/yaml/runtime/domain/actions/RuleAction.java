package org.drools.yaml.runtime.domain.actions;

import java.util.List;

import org.drools.model.Drools;
import org.drools.yaml.api.RulesExecutor;
import org.drools.yaml.api.domain.actions.Action;
import org.drools.yaml.api.domain.actions.AssertFact;
import org.drools.yaml.api.domain.actions.PostEvent;
import org.drools.yaml.api.domain.actions.RetractFact;
import org.drools.yaml.api.domain.actions.RunPlaybook;

public class RuleAction implements Action {

    private AssertFact assert_fact;
    private RetractFact retract_fact;
    private PostEvent post_event;
    private List<RunPlaybook> run_playbook;

    public AssertFact getAssert_fact() {
        return assert_fact;
    }

    public void setAssert_fact(AssertFact assert_fact) {
        this.assert_fact = assert_fact;
    }

    public RetractFact getRetract_fact() {
        return retract_fact;
    }

    public void setRetract_fact(RetractFact retract_fact) {
        this.retract_fact = retract_fact;
    }

    public PostEvent getPost_event() {
        return post_event;
    }

    public void setPost_event(PostEvent post_event) {
        this.post_event = post_event;
    }

    public List<RunPlaybook> getRun_playbook() {
        return run_playbook;
    }

    public void setRun_playbook(List<RunPlaybook> run_playbook) {
        this.run_playbook = run_playbook;
    }

    @Override
    public String toString() {
        return "RuleAction{" +
                "assert_fact=" + assert_fact +
                ", retract_fact=" + retract_fact +
                ", post_event=" + post_event +
                ", run_playbook=" + run_playbook +
                '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        if (assert_fact != null) {
            assert_fact.execute(rulesExecutor, drools);
        }
        if (retract_fact != null) {
            retract_fact.execute(rulesExecutor, drools);
        }
        if (post_event != null) {
            post_event.execute(rulesExecutor, drools);
        }
        if (run_playbook != null) {
            run_playbook.forEach(rp -> rp.execute(rulesExecutor, drools));
        }
    }
}
