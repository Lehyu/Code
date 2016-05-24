
package com.excelsecu.apidemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.excelsecu.slotapi.EsDevice;
import com.excelsecu.slotapi.EsDeviceManager;
import com.excelsecu.slotapi.EsEvent;
import com.excelsecu.slotapi.EsEventListener;
import com.excelsecu.slotapi.EsException;
import com.excelsecu.stdapidemo.R;

import java.io.UnsupportedEncodingException;

public class BluetoothActivity extends Activity {

    private static final String DEFAULT_SIGN_MSG = "<?xml version=\"1.0\" encoding=\"utf-8\"?><T><D><M><k>收款人:</k><v>张三</v></M><M><k>收款人账号:</k><v>1234567890123456</v></M><M><k>金额:</k><v>123.23</v></M></D><E><M><k>流水号:</k><v>1234567890</v></M></E></T>";
    private EsDeviceManager esManager = null;
    private EsDevice esDevice = null;
    private int managerType = EsDevice.TYPE_BLUETOOTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        // 初始化Key的管理器
        esManager = EsDeviceManager.getInstance(this);
        esManager.setListener(managerType, new EsEventListener() {

            @Override
            public void onEvent(EsEvent event) {
                switch (event.getType()) {
                    case EsEvent.TYPE_DEVICE_CONNECTED:
                        esDevice = esManager.getDevice(event.getDeviceId());
                        showTips("设备已连接");
                        break;
                    case EsEvent.TYPE_DEVICE_CONNECTING:
                        showTips("设备接入，正在连接");
                        break;
                    case EsEvent.TYPE_DEVICE_DISCONNECTED:
                        if (esDevice.getId() == event.getDeviceId()) {
                            showTips("设备已断开连接");
                        }
                        break;
                    case EsEvent.TYPE_ERROR:
                        if (event.getErrorCode() == EsException.ERROR_DRV_BLUETOOTH_DISCOVERABLE_TIMEOUT) {
                            showTips("蓝牙可被发现超时，仅已配对的设备可以连上");
                        } else {
                            showTips("连接出错");
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        esManager.start(managerType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        esManager.stop(managerType);
    }

    private void init() {
        Button btnSign = (Button) findViewById(R.id.btn_sign);
        btnSign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showVerifyPinDialogForSign();
            }
        });

        Button btnEncrypt = (Button) findViewById(R.id.btn_encrypto);
        btnEncrypt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showVerifyPinDialogForEncryto();
            }
        });
    }

    /**
     * 提示用户输入密码, 然后签名
     */
    private void showVerifyPinDialogForSign() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verifypin, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.password);
        // 创建对话框
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.verify_pin)
                .setView(dialogView)// 设置自定义对话框样式
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {// 设置监听事件

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 做操作
                                doSign(esDevice, DEFAULT_SIGN_MSG, editText
                                        .getText().toString());
                            }
                        })
                .setNegativeButton(R.string.cancle, null)
                .create();

        dlg.show();
    }

    /**
     * 执行测试工作
     * 
     * @param device
     * @param signMessage
     * @param password
     * @return
     */
    private void doSign(final EsDevice device, final String signMessage, final String password) {
        if (device == null) {
            return;
        }

        // 起一个线程去跑操作，避免阻塞UI
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int[] certArray = device.getKeyPairList(EsDevice.KEY_SPEC_SIGNATURE);
                    if (certArray.length < 1) {
                        showTips("未找到证书");
                        return;
                    }

                    // 校验密码
                    if (!device.verifyPin(EsDevice.PIN_TYPE_USER, password)) {
                        int[] pinInfo = esDevice.getPinInfo(EsDevice.PIN_TYPE_USER);
                        showTips("校验密码错误，剩余: " + pinInfo[0] + "次");
                    }

                    // HashValue签名
                    // byte[] result1 = esDevice.signHashValue(certArray[0],
                    // EsDevice.HASH_ALG_MD5,
                    // new byte[] {
                    // 0, 1, 2, 3, 4, 5, 6, 7
                    // });
                    // showTips(new String(result1));

                    // 报文签名
                    byte[] result2 = esDevice.signMessage(certArray[0], EsDevice.HASH_ALG_SHA256,
                            signMessage, "UTF-8");

                    showTips(new String(result2));
                } catch (EsException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 提示用户输入密码, 然后签名
     */
    private void showVerifyPinDialogForEncryto() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verifypin, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.password);
        // 创建对话框
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.verify_pin)
                .setView(dialogView)// 设置自定义对话框样式
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {// 设置监听事件

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doEncryptDecrypt(esDevice, "123456780", editText.getText()
                                        .toString());
                            }
                        })
                .setNegativeButton(R.string.cancle, null)
                .create();

        dlg.show();
    }

    /**
     * 执行加解密
     * 
     * @param device
     * @param signMessage
     * @param password
     * @return
     */
    private void doEncryptDecrypt(final EsDevice device, final String plainData,
            final String password) {
        if (device == null) {
            return;
        }

        // 起一个线程去跑操作，避免阻塞UI
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int[] certArray = device.getKeyPairList(EsDevice.KEY_SPEC_KEYEXCHANGE);
                    if (certArray.length < 1) {
                        showTips("未找到证书");
                    }

                    // 校验密码
                    if (!device.verifyPin(EsDevice.PIN_TYPE_USER, password)) {
                        int[] pinInfo = esDevice.getPinInfo(EsDevice.PIN_TYPE_USER);
                        showTips("校验密码错误，剩余: " + pinInfo[0] + "次");
                    }
                    int keyPairIndex = certArray[0];
                    // 报文签名
                    byte[] cipherData = esDevice.asymEncrypt(keyPairIndex,
                            plainData.getBytes("utf-8"));

                    showTips("加密后数据:\n" + new String(cipherData));

                    byte[] plainData = esDevice.asymDecrypt(keyPairIndex, cipherData);

                    showTips("解密后数据:\n" + new String(plainData, "UTF-8"));
                } catch (EsException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showTips(final String msg) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(BluetoothActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
