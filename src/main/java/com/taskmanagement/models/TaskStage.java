package com.taskmanagement.models;

import com.taskmanagement.exceptions.TaskStageException;

import java.util.Arrays;

public enum TaskStage {
    NOT_TAKEN {
        public String toString() {
            return "Not taken";
        }
    },
    IN_PROGRESS {
        public String toString() {
            return "In progress";
        }
    },

    WAIT_REVIEW {
        public String toString() {
            return "Review";
        }
    },

    COMPLETED {
        public String toString() {
            return "Completed";
        }
    };

    public static TaskStage findByName(final String stage){
        return Arrays.stream(values()).filter(value -> value.toString().equals(stage))
                .findFirst().orElseThrow(() -> new TaskStageException(stage));
    }

}
