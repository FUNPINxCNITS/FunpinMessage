package com.example.funpinmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

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

                if (messageBody.contains("특정 키워드")) {
                    Toast.makeText(context, "특정 키워드가 포함된 메시지 수신!", Toast.LENGTH_LONG).show();
                    // 여기에 추가적인 처리 로직 구현
                }
            }
        }
    }
}
