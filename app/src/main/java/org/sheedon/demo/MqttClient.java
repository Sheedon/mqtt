package org.sheedon.demo;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @Description: java类作用描述
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/2/18 13:26
 */
public class MqttClient implements IMqttActionListener, IMqttMessageListener {
    private static final String TAG = "MQTT_CLIENT";

    private MqttAndroidClient mClient;
    private MqttConnectOptions options;

    private boolean isStartConnect;

    private boolean isSend = false;//mqtt是否在发送中

    private MqttClient() {
        createClient();
    }

    public static final MqttClient getInstance() {
        return MqttClientHolder.INSTANCE;
    }

    private static class MqttClientHolder {
        private static final MqttClient INSTANCE = new MqttClient();
    }

    /**
     * 创建MQTT客户端
     */
    private void createClient() {
        // 创建MqttClient

        String clientId = "yhkhs20181029046";

        if (mClient == null) {
            mClient = new MqttAndroidClient(App.getInstance(), "tcp://yanhang.kmdns.net:3883", clientId);
            mClient.setCallback(new MqttCallbackExtended() {
                // 连接完成
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    subscribeToTopic(reconnect);
                }

                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }

        // 创建配置参数
        if (options == null) {
            options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            // 设置超时时间，单位：秒
            options.setConnectionTimeout(60);
            // 心跳包发送间隔，单位：秒
            options.setKeepAliveInterval(30);
        }


        // 创建连接
        if (!mClient.isConnected() && !isStartConnect) {
            try {
                isStartConnect = true;
                mClient.connect(options, null, this);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 移除监听器
     * 若全部移除则关闭mqtt客户端
     */
    public void destroy() {

        if (mClient != null) {
            try {
                if (mClient.isConnected())
                    mClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mClient = null;
        }
    }


    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.v(TAG, "连接成功");
        DisconnectedBufferOptions options = new DisconnectedBufferOptions();
        options.setBufferEnabled(true);
        options.setBufferSize(100);
        options.setPersistBuffer(false);
        options.setDeleteOldestMessages(false);
        mClient.setBufferOpts(options);
//        subscribeToTopic();
        isStartConnect = false;

        // 通知连接成功
        connectCallback("Connect", "Connect_Success");

        // 开启上传流量记录
//        TrafficCard model = DBManager.getInstance(Factory.app()).getTrafficModel();
//        if (model != null && model.getMonitorFlow() == 1) {
//            new TrafficUtils().startSendService();
//        }

    }



    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.v(TAG, "连接失败:" + exception);
        isStartConnect = false;

        // 通知连接失败
        connectCallback("Connect", "Connect_Failure");
    }

    /**
     * 通知连接成功或失败
     */
    private void connectCallback(String localTopic, String result) {

//        sendMessageArrived(result, "", mListener);

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.v(TAG, "messageArrived: " + topic + " : " + new String(message.getPayload(), "GBK"));
        String data = new String(message.getPayload(), "GBK");
    }

    /**
     * 订阅主题
     */
    private void subscribeToTopic(boolean reconnect) {
        try {

            mClient.subscribe("yh_classify/clouds/recyclable/cmd/yhkhs20181029046", 1, this);
            mClient.subscribe("yh_classify/device/recyclable/data/yhkhs20181029046", 1, this);


//            UpdateAppHelper.updateStatus();
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }


    public void publish(String topic, String message) {
        try {
            mClient.publish(topic, message.getBytes(), 1,
                    false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
