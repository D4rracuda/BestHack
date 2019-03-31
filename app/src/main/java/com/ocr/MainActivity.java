package com.ocr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 103;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 104;
    private TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContent = (TextView) findViewById(R.id.content);

        findViewById(R.id.credit_card_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_BANK_CARD);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });

        initAccessTokenWithAkSk();
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(
                new OnResultListener<AccessToken>() {
                    @Override
                    public void onResult(AccessToken result) {


                        initLicense();

                        Log.d("MainActivity", "onResult: " + result.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Начальная аутентификация прошла успешно", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(OCRError error) {
                        error.printStackTrace();
                        Log.e("MainActivity", "onError: " + error.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Начальная аутентификация не удалась, пожалуйста, проверьте ключ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, getApplicationContext(),
                "oH6tqEsBX2PSW2OViQyd2yYA",
                "A36f7UrseglvtH9jGP5u7bU9uGxIjZ31");
    }

    private void initLicense() {
        CameraNativeHelper.init(this, OCR.getInstance().getLicense(),
                new CameraNativeHelper.CameraNativeInitCallback() {
                    @Override
                    public void onError(int errorCode, Throwable e) {
                        final String msg;
                        switch (errorCode) {
                            case CameraView.NATIVE_SOLOAD_FAIL:
                                msg = "Загрузка не удалась, пожалуйста, убедитесь, что есть часть пользовательского интерфейса apk";
                                break;
                            case CameraView.NATIVE_AUTH_FAIL:
                                msg = "Не удалось получить авторизованный локальный маркер контроля качества";
                                break;
                            case CameraView.NATIVE_INIT_FAIL:
                                msg = "Местный контроль качества";
                                break;
                            default:
                                msg = String.valueOf(errorCode);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "Ошибка инициализации локального контроля качества, причина ошибки： " + msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_BANK_CARD.equals(contentType)) {
                        recCreditCard(filePath);
                    }
                }
            }
        }
        if (requestCode == REQUEST_CODE_DRIVING_LICENSE && resultCode == Activity.RESULT_OK) {
            String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
            recDrivingCard(filePath);
        }
        if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
            recVehicleCard(filePath);
        }
    }

    private void recIDCard(String idCardSide, String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        param.setIdCardSide(idCardSide);
        param.setDetectDirection(true);
        param.setImageQuality(40);
        OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {

                    String name = "";
                    String sex = "";
                    String nation = "";
                    String num = "";
                    String address = "";
                    if (result.getName() != null) {
                        name = result.getName().toString();
                    }
                    if (result.getGender() != null) {
                        sex = result.getGender().toString();
                    }
                    if (result.getEthnic() != null) {
                        nation = result.getEthnic().toString();
                    }
                    if (result.getIdNumber() != null) {
                        num = result.getIdNumber().toString();
                    }
                    if (result.getAddress() != null) {
                        address = result.getAddress().toString();
                    }
                    mContent.setText("Имя: " + name + "\n" +
                            "Пол: " + sex + "\n" +
                            "Страна: " + nation + "\n" +
                            "Номер: " + num + "\n" +
                            "Адресс: " + address + "\n");

                }
            }

            @Override
            public void onError(OCRError error) {
                Toast.makeText(MainActivity.this, "Определите ошибки, пожалуйста, проверьте код ошибки журнала", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "onError: " + error.getMessage());
            }
        });
    }

    private void recCreditCard(String filePath) {
        BankCardParams param = new BankCardParams();
        param.setImageFile(new File(filePath));

        OCR.getInstance().recognizeBankCard(param, new OnResultListener<BankCardResult>() {
            @Override
            public void onResult(BankCardResult result) {
                if (result != null) {

                    String type;
                    if (result.getBankCardType() == BankCardResult.BankCardType.Credit) {
                        type = "Кредитная карта";
                    } else if (result.getBankCardType() == BankCardResult.BankCardType.Debit) {
                        type = "Дебетовая карта";
                    } else {
                        type = "Не признается";
                    }
                    mContent.setText("Номер банковской карты: " +
                            (!TextUtils.isEmpty(result.getBankCardNumber()) ? result.getBankCardNumber() : "") + "\n" +
                            "Название банка: " +
                            (!TextUtils.isEmpty(result.getBankName()) ? result.getBankName() : "") + "\n" +
                            "Тип: " +
                            type + "\n");

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    String s = preferences.getString("allCardsIs", null);
                    int sInt = Integer.parseInt(s);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("NumberOfCard" + s, "Номер банковской карты: " +
                            (!TextUtils.isEmpty(result.getBankCardNumber()) ? result.getBankCardNumber() : ""));
                    editor.putString("TypeOfCard" + s, "Тип: " +
                            type);
                    editor.putString("NameOfBanck" + s, "Название банка: " +
                            (!TextUtils.isEmpty(result.getBankName()) ? result.getBankName() : ""));
                    editor.putString("Expire" + s, "Дата: не найдена");
                    editor.putString("Credit" + s, "Тип: топливная карта");
                    editor.putString("allCardsIs", Integer.toString(sInt + 1));
                    editor.apply();

                }
            }

            @Override
            public void onError(OCRError error) {
                Toast.makeText(MainActivity.this, "Определите ошибки, пожалуйста, проверьте код ошибки журнала", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "onError: " + error.getMessage());
            }
        });
    }
    private void recDrivingCard(String filePath) {
        OcrRequestParams param = new OcrRequestParams();


        param.setImageFile(new File(filePath));

        param.putParam("detect_direction", true);

        OCR.getInstance().recognizeDrivingLicense(param, new OnResultListener<OcrResponseResult>() {
            @Override
            public void onResult(OcrResponseResult result) {

                Log.d("MainActivity", result.getJsonRes());
                mContent.setText(result.getJsonRes());
            }

            @Override
            public void onError(OCRError error) {

            }
        });
    }

    private void recVehicleCard(String filePath) {
        OcrRequestParams param = new OcrRequestParams();
        param.setImageFile(new File(filePath));
        OCR.getInstance().recognizeVehicleLicense(param, new OnResultListener<OcrResponseResult>() {
            @Override
            public void onResult(OcrResponseResult result) {
                Log.d("MainActivity", result.getJsonRes());
                mContent.setText(result.getJsonRes());
            }

            @Override
            public void onError(OCRError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        CameraNativeHelper.release();

        OCR.getInstance().release();
        super.onDestroy();

    }
}
