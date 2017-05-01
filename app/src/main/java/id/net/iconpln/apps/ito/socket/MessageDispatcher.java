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
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.model.WoSummary;
import id.net.iconpln.apps.ito.model.WorkOrder;
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
            if (responseCode.equals(Constants.SOCKET_FAILURE)) {
                eventBus.post(produceErrorMessageEvent("Tidak dapat menghubungi server"));
                return true;
            }
        }
        return false;
    }

    public void dispatch(String runFunction, MessageEvent messageEvent) {

        if (isFailureConnection(messageEvent.response_code))
            return;

        /**
         * Check if message entities is not null, presume as distinguisher between the response of
         * the request or from the push server.
         */
        if (messageEvent.entities != null) {
            // check if there is no data we've got from response.
            if (messageEvent.response_code.equals("302")) {
                Log.d(TAG, "dispatch: Tidak ada data dari response [302]");
                return;
            } else if (messageEvent.entities.length == 0) {
                eventBus.post(produceErrorMessageEvent("Maaf, ada gangguan pada server, coba beberapa saat lagi"));
                Log.d(TAG, "dispatch: Tidak ada data dari response [402]");
                return;
            }

            if (messageEvent.entities.length == 1) {
                // grab general form, can be use in almost manner.
                message = messageEvent.entities[0].toString();
            } else {
                // special for work order list entitites.
                messages = messageEvent.entities;
            }
        } else
            return;

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
            case "getwosync":
                eventBus.post(produceMessageWorkOrder(messages));
                break;
            case "updateprogressworkorder":
                break;
            case "tempuploadwo":
                break;
            case "getmasterflagtusbung":
                eventBus.post(produceMessageFlagTusbung(messages));
                break;
            case "ping":
                // ping message
                eventBus.post(produceMessagePingEvent(message));
                break;
            default:
                Log.d(TAG, "[Unknown Response]");
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
    private WorkOrder[] produceMessageWorkOrder(Object[] messages) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .registerTypeAdapter(String.class, new JsonNullConverter())
                .create();

        WorkOrder[] woList = gson.fromJson(Arrays.toString(messages), WorkOrder[].class);
        for (WorkOrder _wo : woList) {
            _wo.formatPretty();
        }
        return woList;
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
