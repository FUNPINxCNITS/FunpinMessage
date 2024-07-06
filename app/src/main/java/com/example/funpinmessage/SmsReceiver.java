package com.example.funpinmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String sender = sms.getDisplayOriginatingAddress();
                String messageBody = sms.getMessageBody();
                Log.d("SmsReceiver", "메시지 수신!");
                if (messageBody.contains("컬쳐랜드")) {
                    // 정규 표현식을 사용하여 숫자 추출
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(messageBody);

                    if (matcher.find()) {
                        String verificationCode = matcher.group();
                        Toast.makeText(context, "인증: " + verificationCode, Toast.LENGTH_LONG).show();
                        sendVerificationCodeToServer(verificationCode);
                    }
                }
            }
        }
    }

    public void sendVerificationCodeToServer(String verificationCode) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://222.99.172.145:5002/receive_sms");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    String jsonInputString = "{\"verification_code\": \"" + verificationCode + "\"}";

                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d("ServerResponse", response.toString());
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}
