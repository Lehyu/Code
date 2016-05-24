package com.ices.esmobilesdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.excelsecu.security.EsBase64;
import com.excelsecu.slotapi.EsDevice;
import com.excelsecu.slotapi.EsDeviceManager;
import com.excelsecu.slotapi.EsEvent;
import com.excelsecu.slotapi.EsEventListener;
import com.excelsecu.slotapi.EsException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

public class MainActivity extends Activity{
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    public final static  String OPERMSG = "OPERMSG";
    private EsDevice esDevice = null;
    private EsDeviceManager manager = null;
    private int type = EsDevice.TYPE_AUDIO;
    private HeadsetPlugListener headsetPlugListener = null;
    private TextView modeTextView, connectTextView, stopTextView;
    private View menuView;
    private PopupWindow popupWindow;
    private Button optionBtn;
    private ImageView logo;
    private String MSG =  "<?xml version=\"1.0\" encoding=\"utf-8\"?><T><D><M><k>verity id:</k><v>33315S051009</v></M></D></T>";
    // "<?xml version=\"1.0\" encoding=\"utf-8\"?><T><D><M><k>收款人:</k><v>张三</v></M><M><k>收款人账号:</k><v>1234567890123456</v></M><M><k>金额:</k><v>123.23</v></M></D><E><M><k>流水号:</k><v>1234567890</v></M></E></T>";

    //
    private final static int AUDIO_MODE = 0;
    private final static int BLUETOOTH_MODE = 1;
    private int mode = AUDIO_MODE;

    private boolean isShowed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        OperMsg operMsg = (OperMsg) getIntent().getSerializableExtra(OPERMSG);
        if (null == operMsg){
            Toast.makeText(MainActivity.this,
                    "Please check out whether you have selected an operation or not",
                    Toast.LENGTH_LONG).show();
            this.finish();
        }else{


        }*/
        init();
        registerListener();
    }

    private void init(){
        manager = EsDeviceManager.getInstance(this);
        logo = (ImageView) findViewById(R.id.logo);
        optionBtn = (Button) findViewById(R.id.option);

        menuView = LayoutInflater.from(this).inflate(R.layout.menu, null);

        modeTextView = (TextView) menuView.findViewById(R.id.mode);
        connectTextView = (TextView) menuView.findViewById(R.id.connect);
        stopTextView = (TextView) menuView.findViewById(R.id.stop);
        popupWindow = new PopupWindow(menuView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.stop(type);
    }

    private void registerListener(){
        optionBtn.setOnClickListener(listener);
        modeTextView.setOnClickListener(listener);
        connectTextView.setOnClickListener(listener);
        stopTextView.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss();
            switch (v.getId()){
                case R.id.option:
                    if(!isShowed) {
                        showPopupWindow();
                        isShowed = true;
                    }else {
                        isShowed = false;
                    }
                    break;
                case R.id.mode:
                    if (mode == AUDIO_MODE){
                        mode = BLUETOOTH_MODE;
                    }else {
                        mode = AUDIO_MODE;
                    }
                    break;
                case R.id.connect:
                    if (mode == AUDIO_MODE) {
                        connectU(EsDevice.TYPE_AUDIO);
                    }else{
                        connectU(EsDevice.TYPE_BLUETOOTH);
                    }
                    while (esDevice == null || !esDevice.isConnected()){

                    }
                    showVerifyPinDialogForSign();
                    break;
                case R.id.stop:
                    break;
                default:
                    isShowed = false;
                    break;
            }
        }
    };

    private void showPopupWindow() {
        popupWindow.setTouchable(true);
        if (mode == AUDIO_MODE){
            modeTextView.setText("切换至蓝牙模式");
        }else{
            modeTextView.setText("切换至音频模式");
        }
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        popupWindow.showAtLocation(LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null), Gravity.BOTTOM, 0, 0);
    }

    private void connectU(int type){
        manager.setListener(type, new EsEventListener() {
            @Override
            public void onEvent(EsEvent esEvent) {
                switch (esEvent.getType()) {
                    case EsEvent.TYPE_DEVICE_CONNECTED:
                        esDevice = manager.getDevice(esEvent.getDeviceId());
                        showTips("设备已连接");
                        break;
                    case EsEvent.TYPE_DEVICE_CONNECTING:
                        showTips("设备连接中...");
                        break;
                    case EsEvent.TYPE_DEVICE_DISCONNECTED:
                        if (esDevice.getId() == esEvent.getDeviceId()) {
                            showTips("设备已断开连接");
                        }
                        break;
                    case EsEvent.TYPE_ERROR:
                        if (esEvent.getErrorCode() == EsException.ERROR_DRV_BLUETOOTH_DISCOVERABLE_TIMEOUT) {
                            showTips("蓝牙可被发现超时");
                        } else {
                            showTips("连接出错");
                        }
                    default:
                        break;
                }
            }
        });
        manager.start(type);
    }



    private AlertDialog.Builder getBuilder(View view, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", listener);
        return builder;
    }
    private void showVerifyPinDialogForSign(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verifypin, null);
        final EditText pswET = (EditText) dialogView.findViewById(R.id.password);

        AlertDialog dlg =getBuilder(dialogView, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sign(esDevice, MSG, pswET.getText().toString());
            }
        }) .create();
        dlg.show();
    }

    private void sign(final EsDevice device, final String msg, final String psw){
        if (null == device){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int[] certArray = device.getKeyPairList(EsDevice.KEY_SPEC_SIGNATURE);
                    if(certArray.length < 1){
                        showTips("未找到证书");
                        return;
                    }
                    // 校验密码
                    if (!device.verifyPin(EsDevice.PIN_TYPE_USER, psw)) {
                        int[] pinInfo = esDevice.getPinInfo(EsDevice.PIN_TYPE_USER);
                        showTips("校验密码错误，剩余: " + pinInfo[0] + "次");
                    }

                    //签名
                    Log.v("msg", Convert.parseByte2HexStr(msg.getBytes()));
                    Log.v("msg length", ""+msg.getBytes().length);
                    byte[] signature = esDevice.signMessage(certArray[0], EsDevice.HASH_ALG_SHA256, msg, "utf-8");
                    Log.v("old signature", Convert.parseByte2HexStr(signature));
                    Log.v("old signature length", ""+signature.length);

                    //加密
                    //byte[] encodedSign = esDevice.asymEncrypt(certArray[0], signature);
                    byte[] encodedSign = esDevice.asymEncrypt(certArray[0], msg.getBytes());
                    Log.v("encodedSign", Convert.parseByte2HexStr(encodedSign));
                    Log.v("encodedSign length", ""+encodedSign.length);



                    X509Certificate x509cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                            .generateCertificate(new ByteArrayInputStream(esDevice.readCert(certArray[0])));

                    PublicKey publicKey = x509cert.getPublicKey();

                    //解密
                    byte[] newDigest = decode(signature, publicKey);
                    Log.v("decode length", ""+newDigest.length);
                    showLog("new digest", newDigest);

                    Log.v("verity", String.valueOf(verity(x509cert, msg.getBytes(), newDigest)));

                } catch (EsException e) {
                    e.printStackTrace();
                    Log.v("errorcode", String.format("%d", new Object[]{Integer.valueOf(e.getErrorCode())}));
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private boolean verity(X509Certificate certificate, byte[] decodedText, byte[] digest) throws UnsupportedEncodingException {
        Signature signature;
        try {
            signature = Signature.getInstance(certificate.getSigAlgName(), "BC");
            Log.v("provider", signature.getProvider().toString());
            signature.initVerify(certificate);
            signature.update(decodedText);
            return signature.verify(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] decode(byte[] encodedText, PublicKey key){
        try {
            //Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            int length = encodedText.length;
            int offset = 0;
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i = 0;
            while (length - offset > 0){
                byte[] cache ;
                if (length - offset > blockSize){
                    cache = cipher.doFinal(encodedText, offset, blockSize);
                }else {
                    cache = cipher.doFinal(encodedText, offset, length - offset);

                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i*blockSize;
            }
            return out.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 提示用户输入密码, 然后签名
     */
    private void showVerifyPinDialogForEncryto() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verifypin, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.password);
        // 创建对话框
        AlertDialog dlg = getBuilder(dialogView, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doEncryptDecrypt(esDevice, MSG, editText.getText().toString());
            }
        }).create();
        dlg.show();
    }


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
                    for(int i: certArray){
                        Log.v("key", ""+i);
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

                    Log.v("enc", Convert.parseByte2HexStr(cipherData));
                    showTips("加密后数据:\n" + Convert.parseByte2HexStr(cipherData));


                    byte[] plainData = esDevice.asymDecrypt(keyPairIndex, cipherData);

                    Log.v("dec", Convert.parseByte2HexStr(plainData));
                    showTips("解密后数据:\n" + Convert.parseByte2HexStr(plainData));
                } catch (EsException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showTips(final String msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLog(String TAG, byte[] msg) throws UnsupportedEncodingException {
        Log.v(TAG, Convert.parseByte2HexStr(msg));
    }
}
