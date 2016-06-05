package com.dii.ids.application.main.navigation.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.auth.SessionManager;
import com.dii.ids.application.api.auth.wescape.WescapeSessionManager;
import com.dii.ids.application.api.form.UserPositionForm;
import com.dii.ids.application.api.response.UserPositionResponse;
import com.dii.ids.application.api.response.UserResponse;
import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.db.WescapeDatabase;
import com.dii.ids.application.entity.repository.EdgeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.settings.SettingsActivity;
import com.dii.ids.application.navigation.DijkstraSolver;
import com.dii.ids.application.navigation.Graph;
import com.dii.ids.application.navigation.Path;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ContinuousMPSTask extends AsyncTask<Node, Void, Boolean> {
    private static final String TAG = ContinuousMPSTask.class.getName();
    private static final long CONNECTION_TIMEOUT = 1;
    private TaskListener<Path> listener;
    private Exception thrownException;
    private Path minimumPath;
    private SessionManager sessionManager;
    private WescapeService service;
    private Edge excludedEdge;
    private boolean emergency, offline;
    private DatabaseDefinition database;
    private static UserResponse currentUser;
    private static UserPositionResponse currentPosition;

    public ContinuousMPSTask(Context context,
                             TaskListener<Path> listener,
                             Edge excludedEdge,
                             boolean emergency,
                             boolean offline) {
        this.listener = listener;
        this.emergency = emergency;
        this.offline = offline;
        this.excludedEdge = excludedEdge;
        database = FlowManager.getDatabase(WescapeDatabase.class);
        String ipAddress = (PreferenceManager.getDefaultSharedPreferences(context))
                .getString(SettingsActivity.WESCAPE_HOSTNAME,
                        SettingsActivity.WESCAPE_DEFAULT_HOSTNAME);
        this.listener = listener;
        this.sessionManager = new WescapeSessionManager(context, ipAddress);
        this.service = ApiBuilder.buildWescapeService(ipAddress, CONNECTION_TIMEOUT);
    }

    @Override
    protected Boolean doInBackground(Node... params) {
        try {
            Node origin = params[0];
            Node destination = params[1];
            List<Edge> edgesForGraph;

            if(offline) {
                edgesForGraph = EdgeRepository.findAllButOne(excludedEdge);
            } else {
                // Download dei parametri degli archi aggiornati
                try {
                    Call<List<Edge>> call = service.listEdges(sessionManager.getBearer());
                    final Response<List<Edge>> response = call.execute();
                    switch (response.code()) {
                        case HttpURLConnection.HTTP_OK: {
                            // La connessione è andata a buon fine gestione della lista dei nodi
                            edgesForGraph = response.body();
                            if (excludedEdge != null) {
                                edgesForGraph.remove(excludedEdge);
                            }

                            // Lancio il salvataggio asincrono dei lati, in modo da non rallentare
                            // l'esecuzione di dijkstra
                            Transaction transaction = database.beginTransactionAsync(new ITransaction() {
                                @Override
                                public void execute(DatabaseWrapper databaseWrapper) {
                                    for (Edge edge : response.body()) {
                                        edge.save(databaseWrapper);
                                    }
                                }
                            }).build();
                            transaction.execute();
                            break;
                        }
                        default: {
                            throw new ConnectException();
                        }
                    }
                } catch (ConnectException | SocketTimeoutException e) {
                    // Connessione andata male o altri problemi, ripescaggio dei nodi dal db
                    edgesForGraph = EdgeRepository.findAllButOne(excludedEdge);
                }
            }

            // Ricerca della soluzione
            Graph graph = new Graph(edgesForGraph);
            DijkstraSolver solver = new DijkstraSolver();
            solver.startingFrom(origin)
                    .in(graph)
                    .setNormalizationBasis(EdgeRepository.getMaxLength())
                    .setEmergency(emergency);
            minimumPath = solver.search(destination);

            if (!offline) {
                if(ContinuousMPSTask.currentUser == null) {
                    Call<UserResponse> call = service.getCurrentUser(sessionManager.getBearer());
                    Response<UserResponse> response = call.execute();
                    currentUser = response.body();

                    Log.i(TAG, "User request");
                }

                if(minimumPath.size() >= 2) {
                    // Creazione o aggiornamento della posizione
                    Edge firstEdge = (Edge) graph.searchTrunk(minimumPath.getOrigin(), minimumPath.get(1));
                    UserPositionForm positionForm = new UserPositionForm();
                    positionForm.setUser(ContinuousMPSTask.currentUser.getId())
                            .setEdge(firstEdge.getId());

                    if(ContinuousMPSTask.currentPosition == null) {
                        Call<UserPositionResponse> getPosition = service.getCurrentPosition(
                                sessionManager.getBearer(), currentUser.getId());
                        Response<UserPositionResponse> response = getPosition.execute();
                        ContinuousMPSTask.currentPosition = response.body();

                        if(ContinuousMPSTask.currentPosition == null) {
                            Call<UserPositionResponse> createPosition = service.createCurrentPosition(
                                    sessionManager.getBearer(), positionForm);
                            Response<UserPositionResponse> createdPositionResponse = createPosition.execute();
                            ContinuousMPSTask.currentPosition = createdPositionResponse.body();
                        }

                    } else {
                        Call<UserPositionResponse> updatePosition = service.updateCurrentPosition(
                                sessionManager.getBearer(),
                                ContinuousMPSTask.currentUser.getId(),
                                positionForm);
                        Response<UserPositionResponse> response = updatePosition.execute();
                        ContinuousMPSTask.currentPosition = response.body();

                        Log.i(TAG, "update position");
                    }
                } else {
                    Call<UserPositionResponse> deletePosition = service.deleteCurrentPosition(
                            sessionManager.getBearer(),
                            ContinuousMPSTask.currentUser.getId());
                    Response<UserPositionResponse> response = deletePosition.execute();
                    Log.i(TAG, ""+response.code());

                    ContinuousMPSTask.currentPosition = null;
                    ContinuousMPSTask.currentUser = null;
                }
            }


            return minimumPath != null;
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onTaskSuccess(minimumPath);
        } else {
            listener.onTaskError(thrownException);
        }
        listener.onTaskComplete();
    }

    @Override
    protected void onCancelled() {
        listener.onTaskCancelled();
    }
}
