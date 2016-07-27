package eastangliamapclient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class EASMWebSocket extends WebSocketClient
{
    public EASMWebSocket()
    {
        super(EastAngliaMapClient.host);
        connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        EastAngliaMapClient.connected = true;
    }

    @Override
    public void onMessage(String jsonMessage)
    {
        JSONObject message = new JSONObject(jsonMessage).getJSONObject("Message");

        TimeoutHandler.lastMessageTime = System.currentTimeMillis();

        switch (MessageType.getType(message.getString("type")))
        {
            case SEND_ALL:
                Map<String, String> fullMap = new HashMap<>();
                JSONObject fullMsg = message.getJSONObject("message");
                for (Iterator<String> iterator = fullMsg.keys(); iterator.hasNext();)
                {
                    String next = iterator.next();
                    fullMap.put(next, fullMsg.getString(next));
                }
                EastAngliaMapClient.DataMap.putAll(fullMap);
                EastAngliaMapClient.printOut("[WebSocket] Received full map (" + fullMap.size() + ")");

                if (EastAngliaMapClient.frameSignalMap != null)
                    EastAngliaMapClient.frameSignalMap.updateGuiComponents(EastAngliaMapClient.DataMap);

                TimeoutHandler.ready = true;
                break;

            case SEND_UPDATE:
                Map<String, String> updateMap = new HashMap<>();
                JSONObject updateMsg = message.getJSONObject("message");
                for (Iterator<String> iterator = updateMsg.keys(); iterator.hasNext();)
                {
                    String next = iterator.next();
                    updateMap.put(next, updateMsg.getString(next));
                }
                EastAngliaMapClient.printOut("[WebSocket] Received update map (" + updateMap.size() + ")");
                EastAngliaMapClient.DataMap.putAll(updateMap);

                if (EastAngliaMapClient.frameSignalMap != null)
                    EastAngliaMapClient.frameSignalMap.updateGuiComponents(updateMap);
                break;

            default:
                EastAngliaMapClient.printErr("[WebSocket] Undefined Message Type: " + message.get("type"));
                break;
        }

        EastAngliaMapClient.frameDataViewer.updateData();
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        EastAngliaMapClient.connected = false;
    }

    @Override
    public void onError(Exception ex)
    {
        ex.printStackTrace();
    }

}