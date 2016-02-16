package com.dii.ids.application.main.navigation;

import com.dii.ids.application.interfaces.AsyncTaskCallbacksInterface;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;

public abstract class MapFragment extends BaseFragment implements AsyncTaskCallbacksInterface<MapsDownloaderTask>{

}
