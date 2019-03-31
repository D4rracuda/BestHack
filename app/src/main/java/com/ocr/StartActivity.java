package com.ocr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class StartActivity extends AppCompatActivity {
    private static final int MY_SCAN_REQUEST_CODE = 100;
    private static final int REQUEST_AUTOTEST = 200;

    private ImageView mResultImage;
    private ImageView mResultCardTypeImage;
    private TextView mResultLabel;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mResultCardTypeImage = (ImageView) findViewById(R.id.result_card_type_image);
        mResultImage = (ImageView) findViewById(R.id.result_image);
        mResultLabel = (TextView) findViewById(R.id.result);


        bissnesCard();
    }

    private void bissnesCard(){
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String outStr = new String();
        Bitmap cardTypeImage = null;

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.cardNumber + "\n";

                resultDisplayStr += "Type: " + scanResult.getCardType() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            outStr += resultDisplayStr;

            Bitmap card = CardIOActivity.getCapturedCardImage(data);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            //File f = saveFile(Objects.requireNonNull(getApplicationContext()).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + "test.txt");
            //String r = extText1(card, Objects.requireNonNull(Objects.requireNonNull(getApplicationContext()).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).toString());
            //Toast.makeText(getApplicationContext(), getAssets() + "", Toast.LENGTH_SHORT).show();
            //String r2 = extText2(card, Objects.requireNonNull(Objects.requireNonNull(getApplicationContext()).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).toString());
            mResultImage.setImageBitmap(card);
            mResultCardTypeImage.setImageBitmap(cardTypeImage);
            //outStr += r + "\n" + r2;

            mResultLabel.setText(outStr);
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }
}
