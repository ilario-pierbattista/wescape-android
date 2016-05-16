package com.dii.ids.application.listener;

public interface TaskListener<Result> {
    void onTaskSuccess(Result result);

    void onTaskError();

    void onTaskComplete();

    void onTaskCancelled();
}
