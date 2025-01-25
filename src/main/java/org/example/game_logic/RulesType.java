package org.example.game_logic;

public enum RulesType {
    STANDARD{
        @Override
        public Rules createRules() {
            return new StandardRules();
        }

        @Override
        public String toString() {
            return "Standard";
        }
    },
    CHAOS{
        @Override
        public Rules createRules() {
            return new ChaosRules();
        }

        @Override
        public String toString() {
            return "Chaos";
        }
    };

    public abstract Rules createRules();
}
