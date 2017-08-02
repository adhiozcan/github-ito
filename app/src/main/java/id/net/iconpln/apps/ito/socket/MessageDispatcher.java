package id.net.iconpln.apps.ito.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

import id.net.iconpln.apps.ito.EventBusProvider;
import id.net.iconpln.apps.ito.helper.Constants;
import id.net.iconpln.apps.ito.helper.JsonNullConverter;
import id.net.iconpln.apps.ito.model.FlagTusbung;
import id.net.iconpln.apps.ito.model.eventbus.ProgressUpdateEvent;
import id.net.iconpln.apps.ito.model.eventbus.TempUploadEvent;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.WoSummary;
import id.net.iconpln.apps.ito.model.WorkOrder;
import id.net.iconpln.apps.ito.model.eventbus.WorkOrderEvent;
import id.net.iconpln.apps.ito.socket.envelope.ErrorMessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.MessageEvent;
import id.net.iconpln.apps.ito.socket.envelope.PingEvent;

/**
 * Created by Ozcan on 26/03/2017.
 */

public class MessageDispatcher {
    private static final String   TAG      = MessageDispatcher.class.getSimpleName();
    private              EventBus eventBus = EventBusProvider.getInstance();
    private String   message;
    private Object[] messages;

    /**
     * Check if messageEvent contains failure connection.
     */
    private boolean isFailureConnection(String responseCode) {
        if (responseCode != null) {
            Log.d(TAG, "Response Code Monitor : " + responseCode);
            if (responseCode.equals(Constants.SOCKET_FAILURE)) {
                eventBus.post(produceErrorMessageEvent("Tidak dapat menghubungi server"));
                return true;
            }
        }
        return false;
    }

    private void loginTreatment(String runFunction, MessageEvent messageEvent) {
        //make exception special for login.
        if (runFunction.equals("login")) {
            eventBus.post(new ErrorMessageEvent(messageEvent.message));
        }
    }

    private void woPelaksanaanTreatment(String runFunction) {
        //make exception special for wopelaksanaan.
        if (runFunction.equals("getwosync")) {
            eventBus.post(produceMessageWorkOrder(new WorkOrder[0], false));
        } else if (runFunction.equals("getwoall")) {
            eventBus.post(produceMessageWorkOrder(new WorkOrder[0], false));
        }
    }

    private void woPelaksanaanUlangTreatment(String runFunction) {
        if (runFunction.equals("getwoallulang")) {
            eventBus.post(produceMessageWorkOrder(new WorkOrder[0], true));
        }
    }

    public void dispatch(String runFunction, MessageEvent messageEvent) {

        if (isFailureConnection(messageEvent.response_code)) {
            eventBus.post(new ErrorMessageEvent("Jaringan terputus"));
            return;
        }

        /**
         * Check if message entities is not null, presume as distinguisher between the response of
         * the request or from the push server.
         */
        if (messageEvent.entities != null) {
            // check if there is no data we've got from response.
            if (messageEvent.response_code.equals("302")) {
                loginTreatment(runFunction, messageEvent);
                woPelaksanaanTreatment(runFunction);
                woPelaksanaanUlangTreatment(runFunction);
                return;
            } else if (messageEvent.entities.length == 0) {
                //eventBus.post(produceErrorMessageEvent("Maaf, ada gangguan pada server, coba beberapa saat lagi"));
                Log.d(TAG, "dispatch: Tidak ada data dari response [402]");
                return;
            }

            if (messageEvent.entities.length == 1) {
                // grab general form, can be use in almost manner.
                message = messageEvent.entities[0].toString();

                // make exception for getwoall
                switch (runFunction) {
                    case "getwoall":
                        messages = messageEvent.entities;
                        break;
                    case "getwoallulang":
                        messages = messageEvent.entities;
                        break;
                }
            } else {
                // special for work order list entitites.
                messages = messageEvent.entities;
            }
        }

        /**
         * Start to corespond between response and subscriber content
         */
        switch (runFunction) {
            case "login":
                eventBus.post(produceMessageUserProfile(message));
                break;
            case "getwototal":
                eventBus.post(produceMessageWoSummary(message));
                break;
            case "getwoall":
                eventBus.post(produceMessageWorkOrder(messages, false));
                break;
            case "getwoallulang":
                eventBus.post(produceMessageWorkOrder(messages, true));
                break;
            case "updateprogressworkorder":
                eventBus.post(produceProgressUpdate(message, false));
                break;
            case "updateulangprogressworkorder":
                eventBus.post(produceProgressUpdate(message, true));
                break;
            case "tempuploadwo":
                eventBus.post(produceTempUploadWo(message));
                break;
            case "getmasterflagtusbung":
                eventBus.post(produceMessageFlagTusbung(messages));
                break;
            case "ping":
                eventBus.post(produceMessagePingEvent(message));
                break;
            default:
                Log.d(TAG, "[Unknown Response] " + messageEvent.toString());
                break;
        }
    }


    /**
     * Produce broadcast for user profile data
     *
     * @param message
     * @return
     */
    private UserProfile produceMessageUserProfile(String message) {
        if (message == null) return new UserProfile();

        Gson gson = new GsonBuilder()
                .setLenient()
                .setPrettyPrinting()
                .create();

        UserProfile userProfile = gson.fromJson(message, UserProfile.class);
        return userProfile;
    }

    /**
     * Produce broadcast for work order data
     *
     * @param messages
     * @return
     */
    private WorkOrderEvent produceMessageWorkOrder(Object[] messages, boolean isUlang) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .registerTypeAdapter(String.class, new JsonNullConverter())
                .create();

        WorkOrder[] woList = gson.fromJson(Arrays.toString(messages), WorkOrder[].class);

        for (WorkOrder _wo : woList) {
            _wo.formatPretty();
            _wo.setWoUlang(isUlang);
            Log.d(TAG, _wo.getNoWo() + "\tUlang : [" + isUlang + "]");
        }

        WorkOrderEvent woEvent = new WorkOrderEvent();
        woEvent.isUlang = isUlang;
        woEvent.workOrders = woList;
        System.out.println(woEvent.workOrders);
        return woEvent;
    }

    /**
     * Produce broadcast for work order summary
     *
     * @param message
     * @return
     */
    private WoSummary produceMessageWoSummary(String message) {
        WoSummary woSummary = new Gson().fromJson(message, WoSummary.class);
        return woSummary;
    }

    /**
     * Produce broadcast for flag tusbung
     *
     * @param messages
     * @return
     */
    private FlagTusbung[] produceMessageFlagTusbung(Object[] messages) {
        FlagTusbung[] flagTusbung = new Gson().fromJson(Arrays.toString(messages), FlagTusbung[].class);
        return flagTusbung;
    }

    /**
     * Produce progress update
     *
     * @param message
     * @return
     */
    private ProgressUpdateEvent produceProgressUpdate(String message, boolean isUlang) {
        ProgressUpdateEvent progressEvent = new Gson().fromJson(message, ProgressUpdateEvent.class);
        progressEvent.isUlang = isUlang;
        return progressEvent;
    }

    /**
     * Produce temp upload wo
     *
     * @param message
     * @return
     */
    private TempUploadEvent produceTempUploadWo(String message) {
        TempUploadEvent tempEvent = new Gson().fromJson(message, TempUploadEvent.class);
        return tempEvent;
    }

    /**
     * Produce broadcast ping containing date
     *
     * @param message
     * @return
     */
    private PingEvent produceMessagePingEvent(String message) {
        PingEvent pingEvent = new Gson().fromJson(message, PingEvent.class);
        return pingEvent;
    }

    /**
     * Produce broadcast error message because of socket failure
     *
     * @return
     */
    private ErrorMessageEvent produceErrorMessageEvent(String message) {
        return new ErrorMessageEvent(message);
    }
}
