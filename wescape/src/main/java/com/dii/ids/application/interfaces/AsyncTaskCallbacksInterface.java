package com.dii.ids.application.interfaces;

/**
 * Interfaccia delle callback ad un <pre>AsyncTask</pre>
 * <p/>
 * Una classe che implementa questa interfaccia (tipicamente una figlia di <pre>BaseFragment</pre>)
 * creerà e manderà in esecuzione un'oggetto figlio della classe AsyncTask. Alla fine
 * dell'esecuzione di questi ultimi, per esternalizzare la logica di gestione dei risultati prodotti
 * dal task, saranno eseguite queste callback dirattamente dall'istanza della classe che avrà
 * istanziato il task stesso.
 *
 * @param <AsyncTaskType> Classe dell'AsyncTask di cui si stanno implementando i metodi di
 *                        callback.
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
