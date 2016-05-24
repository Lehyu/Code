package com.ices.uaegis;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.excelsecu.security.EsBase64;
import com.excelsecu.slotapi.EsDevice;
import com.excelsecu.slotapi.EsDeviceManager;
import com.excelsecu.slotapi.EsEvent;
import com.excelsecu.slotapi.EsEventListener;
import com.excelsecu.slotapi.EsException;
import com.ices.uaegis.com.ices.utility.AsyncExecutor;
import com.ices.uaegis.com.ices.utility.InputDialog;
import com.ices.uaegis.com.ices.utility.NoButtonMessageDialog;


import java.io.ByteArrayInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends BaseActivity implements View.OnClickListener, EsEventListener {
    private static final String DEFAULT_SIGN_MSG = "<?xml version=\"1.0\" encoding=\"utf-8\"?><T><D><M><k>收款人:</k><v>张三</v></M><M><k>收款人账号:</k><v>1234567890123456</v></M><M><k>金额:</k><v>123.23</v></M></D><E><M><k>流水号:</k><v>1234567890</v></M></E></T>";
    private static final int MAX_PWD_LENGTH = 12;
    private static final int MIN_PWD_LENGTH = 6;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TIME = 300;
    private int deviceType = EsDevice.TYPE_BLUETOOTH;
    private EsDevice esDevice;
    private EsDeviceManager esMgr;
    private boolean isBTConnected = false;
    private TextView modifyPwdView;
    private TextView signHashValueView;
    private TextView signatureView;
    private TextView stateView;
    private TextView viewCertView;

    private AsyncExecutor<String> getSNAsyncExecutor = new AsyncExecutor<String>() {
        public String asyncExecute(){
            if (!MainActivity.this.isBTConnected)
                return null;
            try {
                String str = MainActivity.this.esDevice.getMediaId();
                return str;
            }
            catch (EsException localEsException) {
                localEsException.printStackTrace();
                MainActivity localMainActivity = MainActivity.this;
                Object[] arrayOfObject = new Object[1];
                arrayOfObject[0] = Integer.valueOf(localEsException.getErrorCode());
                localMainActivity.showTips(String.format("获取Sn错误: 0x%08", arrayOfObject));
            }
            return "";
        }

        @Override
        public void executeComplete(String paramAnonymousString) {
            MainActivity.this.closeProgress();
            if (paramAnonymousString != null)
                MainActivity.this.stateView.setText(String.format("设备已连接[SN:%s]", new Object[] { paramAnonymousString }));
        }

        public void executePrepare() {
            MainActivity.this.showProgress();
        }
    };

    private AsyncExecutor<Certificate> viewCertAsyncExecutor = new AsyncExecutor<Certificate>() {
        public Certificate asyncExecute() {
            if (!MainActivity.this.isBTConnected){
                return null;
            }
            while (true) {
                try {
                    int[] arrayOfInt = MainActivity.this.esDevice.getKeyPairList(2);
                    if ((arrayOfInt.length > 0) && (MainActivity.this.isBTConnected)) {
                        int i = arrayOfInt[0];
                        byte[] arrayOfByte = MainActivity.this.esDevice.readCert(i);
                        try {
                            Certificate localCertificate = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(arrayOfByte));
                            return localCertificate;
                        }
                        catch (CertificateException localCertificateException) {
                            localCertificateException.printStackTrace();
                            return null;
                        }
                    }
                }
                catch (EsException localEsException) {
                    localEsException.printStackTrace();
                }
            }
        }

        public void executeComplete(Certificate paramAnonymousCertificate) {
            MainActivity.this.closeProgress();
            String str = "获取证书信息失败";
            if (paramAnonymousCertificate != null){
                str = paramAnonymousCertificate.toString();
            }
            MainActivity.this.showMessageDialog(null, str);
        }

        public void executePrepare() {
            MainActivity.this.showProgress();
        }
    };


    private boolean checkPassword(String psw) {
        int i = psw.length();
        boolean bool;
        String str;
        if (i < MIN_PWD_LENGTH) {
            bool = false;
            str = "密码长度不能小于6";
        } else if (i > MAX_PWD_LENGTH){
            str = "密码长度不能大于12";
            bool = false;
        }else {
            bool = true;
            str = null;
        }
        if (!bool){
            showMessageDialog(null, str);
        }
        return bool;
    }

    private void doSignHasnValue(String paramString) {
        asyncExecute(new SignatureExecutor(paramString, true));
    }

    private void doSignature(String paramString) {
        asyncExecute(new SignatureExecutor(paramString, false));
    }

    private boolean doVerify(final String psw) {
        if (!this.isBTConnected) {
            return false;
        }
        try{
            int[] arrayOfInt = this.esDevice.getPinInfo(EsDevice.PIN_TYPE_USER);
            if (arrayOfInt[0] < 1) {
                showMessageDialog(null, "设备已锁定");
                return false;
            }
            if ((arrayOfInt[0] <= 2) && (arrayOfInt[0] < 2)) {
                Toast.makeText(this, "请按设备上的<确认>按键", Toast.LENGTH_LONG).show();
            }
            boolean flag = this.esDevice.verifyPin(EsDevice.PIN_TYPE_USER, psw);
            return flag;
        } catch (EsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void enableButtons(boolean enabled) {
        this.stateView.setEnabled(enabled);
        //this.modifyPwdView.setEnabled(enabled);
        //this.viewCertView.setEnabled(enabled);
        this.signatureView.setEnabled(enabled);
        this.signHashValueView.setEnabled(enabled);
    }


    private void showTips(String paramString) {
        Toast.makeText(this, paramString, Toast.LENGTH_LONG).show();
    }

    private void updateState(int paramInt, Object obj) {
        EsEvent event = (EsEvent) obj;
        String str;
        if (paramInt == 3){
            this.isBTConnected = false;
            closeProgress();
            enableButtons(false);
            this.stateView.setText("未连接设备");
            return;
        }
        if (paramInt == 1) {
            showProgress();
            this.stateView.setText("正在连接设备");
            enableButtons(false);
            return;
        }
        if (this.deviceType == EsDevice.TYPE_BLUETOOTH) {
            str = "蓝牙";
            setTitle(getString(R.string.title_name, new Object[]{str}));
            Log.d(TAG, "update state: " + paramInt);
            this.esDevice = this.esMgr.getDevice(event.getDeviceId());
            this.isBTConnected = true;
            closeProgress();
            enableButtons(true);
            this.stateView.setText("设备已连接");
            asyncExecute(this.getSNAsyncExecutor);
        }else{
            str = "音频";
        }

    }

    public void handleMessage(int paramInt, Object paramObject)
    {
        super.handleMessage(paramInt, paramObject);
        updateState(paramInt, paramObject);
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.main_btn_switch:
                openOptionsMenu();
                return;
            /*
            case R.id.main_btn_view_cert:
                asyncExecute(this.viewCertAsyncExecutor);
                return;
                */
            case R.id.main_btn_signature:
                showInputDialog("签名", "请输入密码", "", "请输入6-12个数字或字母", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        if (((paramAnonymousDialogInterface instanceof InputDialog)) && (paramAnonymousInt == -1)) {
                            String str = ((InputDialog)paramAnonymousDialogInterface).getText();
                            if ((MainActivity.this.checkPassword(str.trim())) && (MainActivity.this.isBTConnected)) {
                                MainActivity.this.doSignature(str);
                            }
                        }
                    }
                });
                return;
            case R.id.main_btn_conn_state:
                asyncExecute(this.getSNAsyncExecutor);
                return;
            case R.id.main_btn_signature_one:
                showInputDialog("签名", "请输入密码", null, "请输入6-12个数字或字母", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        if (((paramAnonymousDialogInterface instanceof InputDialog)) && (paramAnonymousInt == -1)) {
                            String str = ((InputDialog) paramAnonymousDialogInterface).getText();
                            if ((MainActivity.this.checkPassword(str.trim())) && (MainActivity.this.isBTConnected)) {
                                MainActivity.this.doSignHasnValue(str);
                            }
                        }
                    }
                });
                break;
            default:
                return;
        }

    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContent(R.layout.activity_main);
        this.stateView = ((TextView)findViewById(R.id.main_btn_conn_state));
        //this.modifyPwdView = ((TextView)findViewById(R.id.main_btn_modify_pwd));
        //this.viewCertView = ((TextView)findViewById(R.id.main_btn_view_cert));
        this.signatureView = ((TextView)findViewById(R.id.main_btn_signature));
        this.signHashValueView = ((TextView)findViewById(R.id.main_btn_signature_one));
        assignClickListener(this, new int[]{R.id.main_btn_conn_state,
                R.id.main_btn_switch, /*R.id.main_btn_modify_pwd, R.id.main_btn_view_cert,*/
                R.id.main_btn_signature, R.id.main_btn_signature_one});
        //setTitleBarLeftItem(R.layout.btn_goback, this);
        this.esMgr = EsDeviceManager.getInstance(this);
        this.esMgr.setListener(this.deviceType, this);
        updateState(3, null);
        this.esMgr.start(this.deviceType, TIME);
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.main, paramMenu);
        return super.onCreateOptionsMenu(paramMenu);
    }

    protected void onDestroy() {
        this.esMgr.setListener(this.deviceType, null);
        this.esMgr.stop(this.deviceType);
        super.onDestroy();
    }

    public void onEvent(EsEvent paramEsEvent) {
        postMessage(paramEsEvent.getType(), paramEsEvent, this);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_start:
                this.esMgr.start(this.deviceType, TIME);
                Toast.makeText(this, "监听已打开，等待接入", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_stop:
                this.esMgr.stop(this.deviceType);
                Toast.makeText(this, "监听已关闭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_switch:
                if (this.esDevice != null){
                    this.esMgr.stop(this.deviceType);
                    this.esDevice = null;

                }
                String str = "";
                if (this.deviceType == EsDevice.TYPE_BLUETOOTH){
                    this.deviceType = EsDevice.TYPE_AUDIO;
                    str = "音频";
                }else{
                    this.deviceType = EsDevice.TYPE_BLUETOOTH;
                    str = "蓝牙";
                }
                this.esMgr = EsDeviceManager.getInstance(this);
                this.esMgr.setListener(this.deviceType, this);
                Toast.makeText(this, "已切换至" + str + "模式", Toast.LENGTH_SHORT).show();
                menuItem.setTitle(getString(R.string.title_name, new Object[]{str}));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public boolean onPrepareOptionsMenu(Menu paramMenu) {
        MenuItem localMenuItem = paramMenu.getItem(0);
        String str = "";
        if (this.deviceType != EsDevice.TYPE_BLUETOOTH){
            str = "音频";
        }else {
            str = "蓝牙";
        }
        localMenuItem.setTitle(getString(R.string.action_switch, new Object[]{str}));
        return super.onPrepareOptionsMenu(paramMenu);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    private class SignatureExecutor implements AsyncExecutor<String> {
        private NoButtonMessageDialog dialog = new NoButtonMessageDialog(MainActivity.this);
        private boolean isSignHashValue = false;
        private String pwd;

        public SignatureExecutor(String psw, boolean isSignHashValue) {
            this.pwd = psw;
            this.isSignHashValue = isSignHashValue;
        }

        public String asyncExecute() {
            if (!MainActivity.this.isBTConnected) {
                return null;
            }
            try {
                int[] certArray = MainActivity.this.esDevice.getKeyPairList(2);
                if (certArray.length < 1) {
                    return "未找到证书";
                }
                if (!MainActivity.this.doVerify(this.pwd)) {
                    int[] arrayOfInt2 = MainActivity.this.esDevice.getPinInfo(1);
                    return "校验密码错误，剩余: " + arrayOfInt2[0] + "次";
                }
                if (this.isSignHashValue){
                    byte[] hashValue = MainActivity.this.esDevice.signHashValue(certArray[0], EsDevice.HASH_ALG_SHA256, DEFAULT_SIGN_MSG.getBytes());
                    return EsBase64.encodeToString(hashValue, 0);
                }else{
                    //加密
                    byte[] encodedText = esDevice.asymEncrypt(certArray[0], DEFAULT_SIGN_MSG.getBytes());
                    //签名
                    byte[] signedText = MainActivity.this.esDevice.signMessage(certArray[0], EsDevice.HASH_ALG_SHA1, DEFAULT_SIGN_MSG, "UTF-8");


                    X509Certificate x509cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                            .generateCertificate(new ByteArrayInputStream(esDevice.readCert(certArray[0])));

                    PublicKey publicKey = x509cert.getPublicKey();
                    //解密
                    byte[] decodedText = decode(encodedText, publicKey);
                    verity(x509cert, decodedText, signedText);
                    return EsBase64.encodeToString(signedText, 0);
                }
            } catch (EsException localEsException) {
                localEsException.printStackTrace();
                MainActivity.this.showTips(String.format("签名出错，错误码0x%08", Integer.valueOf(localEsException.getErrorCode())));
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            return "";
        }

        public void executeComplete(String paramString) {
            this.dialog.cancel();
            if (!TextUtils.isEmpty(paramString))
                MainActivity.this.showMessageDialog(null, paramString);
        }

        public void executePrepare()
        {
            if (this.isSignHashValue)
            {
                this.dialog.setMessage("正在进行签名操作,请等待");
                this.dialog.show();
                return;
            }
            this.dialog.setMessage("将要进行签名操作，请点击设备上的“确认”按钮");
            this.dialog.show();
        }
    }
    private boolean verity(X509Certificate certificate, byte[] decodedText, byte[] signText){
        Log.v("verity", EsBase64.encodeToString(decodedText, 0)+"\n"+EsBase64.encodeToString(signText, 0));
        Signature signature = null;
        try {
            signature = Signature.getInstance(certificate.getSigAlgName());
            signature.initVerify(certificate);
            signature.update(decodedText);
            return signature.verify(signText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] decode(byte[] encodedText, PublicKey key){
        try {
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encodedText);
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

}