package com.dii.ids.application.main.navigation;

import com.dii.ids.application.interfaces.AsyncTaskCallbacksInterface;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;

/**
 * Classe generica di fragment che fanno uso del task asincrono di caricamento delle mappe
 */
public abstract class MapFragment extends BaseFragment implements AsyncTaskCallbacksInterface<MapsDownloaderTask> {

}
