package com.github.awasur04.ToastyBets.models.enums;

public enum PermissionLevel {
        DEV(3),
        ADMIN(2),
        NORMAL(1),
        INACTIVE(0);

        private int value;

        PermissionLevel(int value) {
                this.value = value;
        }

        public boolean isGreaterThan(PermissionLevel requiredLevel) {
                return this.value >= requiredLevel.value;
        }
}
