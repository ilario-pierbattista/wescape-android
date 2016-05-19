package com.dii.ids.application.listener;

public interface TaskListener<Result> {
    void onTaskSuccess(Result result);

    void onTaskError(Exception e);

    void onTaskComplete();

    void onTaskCancelled();
}
