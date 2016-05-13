package com.dii.ids.application.listener;

public interface ProgressTaskListener<Result, Progress>
        extends TaskListener<Result> {

    void onProgressPublished(Progress progress);
}
