package org.drools.yaml.domain;

import java.util.List;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;
import org.drools.yaml.domain.actions.Action;
import org.drools.yaml.domain.actions.YamlAssertFact;
import org.drools.yaml.domain.actions.YamlPostEvent;
import org.drools.yaml.domain.actions.YamlRetractFact;
import org.drools.yaml.domain.actions.YamlRunPlaybook;

public class YamlRuleAction implements Action {

    private YamlAssertFact assert_fact;
    private YamlRetractFact retract_fact;
    private YamlPostEvent post_event;
    private List<YamlRunPlaybook> run_playbook;

    public YamlAssertFact getAssert_fact() {
        return assert_fact;
    }

    public void setAssert_fact(YamlAssertFact assert_fact) {
        this.assert_fact = assert_fact;
    }

    public YamlRetractFact getRetract_fact() {
        return retract_fact;
    }

    public void setRetract_fact(YamlRetractFact retract_fact) {
        this.retract_fact = retract_fact;
    }

    public YamlPostEvent getPost_event() {
        return post_event;
    }

    public void setPost_event(YamlPostEvent post_event) {
        this.post_event = post_event;
    }

    public List<YamlRunPlaybook> getRun_playbook() {
        return run_playbook;
    }

    public void setRun_playbook(List<YamlRunPlaybook> run_playbook) {
        this.run_playbook = run_playbook;
    }

    @Override
    public String toString() {
        return "YamlRuleAction{" +
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
