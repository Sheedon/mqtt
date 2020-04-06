package org.sheedon.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MqttClient.getInstance();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "get_manager_list");
                    jsonObject.put("upStartTime", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttClient.getInstance().publish("yh_classify/device/recyclable/data/yhkhs20181029046",
                        jsonObject.toString());
            }
        });
    }
}
