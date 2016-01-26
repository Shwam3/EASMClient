package eastangliamapclient;

import eastangliamapclient.json.JSONParser;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

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
        Map<String, Object> message = (Map<String, Object>) ((Map<String, Object>) JSONParser.parseJSON(jsonMessage)).get("Message");

        TimeoutHandler.lastMessageTime = System.currentTimeMillis();

        switch (MessageType.getType(String.valueOf(message.get("type"))))
        {
            case SEND_ALL:
                Map<String, String> fullMap = (Map<String, String>) message.get("message");
                EastAngliaMapClient.DataMap.putAll(fullMap);
                EastAngliaMapClient.printOut("[WebSocket] Received full map (" + fullMap.size() + ")");

                if (EastAngliaMapClient.frameSignalMap != null)
                    EastAngliaMapClient.frameSignalMap.updateGuiComponents(EastAngliaMapClient.DataMap);

                TimeoutHandler.ready = true;
                break;

            case SEND_UPDATE:
                Map<String, String> updateMap = (Map<String, String>) message.get("message");
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