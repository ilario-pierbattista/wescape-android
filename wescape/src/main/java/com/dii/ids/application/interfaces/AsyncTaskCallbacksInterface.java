package com.dii.ids.application.interfaces;

/**
 * @param <AsyncTaskType>
 * @TODO Potrebbe essere una buona idea spostarla nel package di root
 */
public interface AsyncTaskCallbacksInterface<AsyncTaskType> {
    /**
     * Callback da chiamare nel caso in cui il task asincrono sia andato a buon fine.
     *
     * @param asyncTask Task a cui è riferita la callback
     */
    void onTaskSuccess(AsyncTaskType asyncTask);

    /**
     * Callback da chiamare nel caso in cui il task asincrono non sia andato a buon fine
     *
     * @param asyncTask Task a cui è riferita la callback
     */
    void onTaskError(AsyncTaskType asyncTask);

    /**
     * Callback da chiamare nel caso in cui il task asincrono sia stato cancellato
     *
     * @param asyncTask Task a cui è riferita la callback
     */
    void onTaskCancelled(AsyncTaskType asyncTask);
}
