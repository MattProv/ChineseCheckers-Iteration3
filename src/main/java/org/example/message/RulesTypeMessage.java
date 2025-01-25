package org.example.message;

import org.example.game_logic.RulesType;

public class RulesTypeMessage extends Message {
    private RulesType rulesetType;

    public RulesTypeMessage(RulesType rulesetType) {
        super(MessageType.RULES_TYPE);
        this.rulesetType = rulesetType;
    }

    public RulesType getRulesetType() {
        return rulesetType;
    }

    public void setRulesetType(RulesType rulesetType) {
        this.rulesetType = rulesetType;
    }

    @Override
    public String toString() {
        return "rulesetType='" + rulesetType;
    }
}
