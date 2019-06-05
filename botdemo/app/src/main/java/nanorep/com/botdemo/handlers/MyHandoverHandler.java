package nanorep.com.botdemo.handlers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.integration.core.MessageEvent;
import com.integration.core.StateEvent;
import com.integration.core.UserEvent;
import com.nanorep.convesationui.structure.HandoverHandler;
import com.nanorep.convesationui.structure.components.ComponentType;
import com.nanorep.convesationui.structure.handlers.ChatDelegate;
import com.nanorep.nanoengine.AccountInfo;
import com.nanorep.nanoengine.model.conversation.statement.IncomingStatement;
import com.nanorep.sdkcore.model.ChatStatement;
import com.nanorep.sdkcore.model.SystemStatement;
import com.nanorep.sdkcore.utils.Event;
import com.nanorep.sdkcore.utils.EventListener;
import com.nanorep.sdkcore.utils.NRError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.functions.Function0;

import static com.integration.core.EventsKt.Error;
import static com.integration.core.EventsKt.Message;
import static com.integration.core.EventsKt.UserAction;
import static com.integration.core.StateEvent.Ended;
import static com.integration.core.StateEvent.Started;
import static com.integration.core.UserEvent.ActionLink;
import static com.nanorep.sdkcore.model.StatementModels.StatusOk;
import static com.nanorep.sdkcore.model.StatementModels.StatusPending;
import static com.nanorep.sdkcore.utils.UtilityMethodsKt.runMain;

class MyHandoverHandler extends HandoverHandler {

    private static int responseNumber = 1;

    private String handlerConfiguration;
    private EventListener listener;
    private ChatDelegate chatDelegate;
    private Handler handler = new Handler();

    MyHandoverHandler(@NotNull Context context) {
        super(context);
    }

    @Override
    public void setListener(@Nullable EventListener listener) {
        this.listener = listener;
    }

    @Override
    public void handleEvent(@NotNull String name, @NotNull Event event) {
        switch (name) {

            case UserAction:
                if (event instanceof UserEvent) {
                    UserEvent userEvent = (UserEvent) event;
                    if (userEvent.getAction().equals(ActionLink)) {
                        listener.handleEvent(userEvent.getType(), userEvent);
                    }
                }
                break;

            case Message:

                runMain(event, event1 -> {
                    if (event.getData() instanceof IncomingStatement) {
                        chatDelegate.injectIncoming((IncomingStatement) event.getData());
                    }
                    return null;
                });
                break;

            case Error:
                runMain(event, event1 -> {

                    ChatStatement request = null;

                    try {
                        request = (ChatStatement) event1.getData();
                    } catch (ClassCastException exp) {
                        NRError error = (NRError) event1.getData();
                        if (error != null) {
                            request = ((ChatStatement) error.getData());
                        }
                        Log.e("ClassCastException", "ClassCastException");
                    }

                    if (request != null) {
                        chatDelegate.updateStatus(request.getTimestamp(), StatusPending);
                    }

                    chatDelegate.enableCmp(ComponentType.UserInputCmp, true, null);

                    return null;
                });
                break;

            default:
               listener.handleEvent(event.getType(), event);
               break;
        }

        // If there is any post event function, invoke it after the event handling
        Function0 postEvent = event.getPostEvent();
        if (postEvent != null) {
            postEvent.invoke();
        }
    }

    @Override
    public void startChat(@Nullable AccountInfo accountInfo) {

        chatDelegate = getChatDelegate();

        if (accountInfo != null) {
            byte[] bytes = accountInfo.getInfo();
            handlerConfiguration = new String(bytes);
        }

       handleState(new StateEvent(Started, getScope()));
    }

    @Override
    public void endChat(boolean forceClose) {
        handleState(new StateEvent(Ended, getScope()));
    }

    @Override
    public void post(@NotNull ChatStatement message){
        chatDelegate.injectOutgoing(message);
        chatDelegate.updateStatus(message.getTimestamp(), StatusOk); // can be delayed by the handover provider

        simulateAgentResponse(message.getText());
    }

    private void handleState(StateEvent stateEvent) {

        switch (stateEvent.getState()) {
            case Started:
                Log.e("MainFragment","started handover" );
                runMain(stateEvent, stateEvent1 -> {
                    chatDelegate.injectSystem(new SystemStatement("Started Chat with Handover provider, the handover data is: " + handlerConfiguration, getScope()));
                    chatDelegate.injectIncoming(new IncomingStatement("Hi from handover", getScope()));
                    return null;
                });
                break;

            case Ended:
                Log.e("MainFragment","handover ended" );
                runMain(stateEvent, stateEvent1 -> {
                    chatDelegate.injectIncoming(new IncomingStatement("bye from handover", getScope()));
                    chatDelegate.injectSystem(new SystemStatement("Ended Chat with the Handover provider", getScope()));
                    return null;
                });
                break;
        }

        handleEvent(stateEvent.getType(), stateEvent);
    }


    /***
     * A function used to simulate agent typing indication
     * @param outgoingMessage
     */
    private void simulateAgentResponse(String outgoingMessage) {

        presentTypingIndication(true);

        Runnable runnable = () -> {

            String agentAnswer = "handover response number " + responseNumber++;

            if (outgoingMessage.toLowerCase().equals("url test")) {
                agentAnswer = "<a href=\"https://www.google.com\">Google link</a>";
            }

            presentTypingIndication(false);

            // Event to be sent after the agent response:
            handleEvent(Message, new MessageEvent(new IncomingStatement(agentAnswer, getScope())));

        };

        handler.postDelayed(runnable, 2000);
    }

    private void presentTypingIndication(boolean isTyping) {
        // In order to use the apps custom typing indication use: listener.handleEvent(Track, new TrackingEvent(TrackingEvent.OperatorTyping, getScope(), isTyping));
        chatDelegate.enableCmp(ComponentType.LiveTypingCmp, isTyping, null);
    }
}
