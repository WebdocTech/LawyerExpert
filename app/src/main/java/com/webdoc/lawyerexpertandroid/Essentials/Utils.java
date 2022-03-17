package com.webdoc.lawyerexpertandroid.Essentials;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.snackbar.Snackbar;
import com.webdoc.lawyerexpertandroid.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by WaleedPCC on 7/18/2019.
 */

public class Utils {

    public SweetAlertDialog progressDialog = null;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    public static ProgressDialog progressdialog;
    public Animation animation;
    public Boolean isProgressBarShowing = false;
    public MediaPlayer mediaPlayer;

    public void successSweetDialog(Activity activity, String txt) {
        progressDialog = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE);
        progressDialog.setTitleText("DONE");
        progressDialog.setContentText(txt);
        progressDialog.show();
    }

    public Drawable makeTextDrawable(String str) {
        mDrawableBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .fontSize(48)
                .endConfig()
                .round();

        if (!(str.equalsIgnoreCase("") || (str == null))) {
            TextDrawable drawable = mDrawableBuilder.build(str, mColorGenerator.getColor(str));
            Drawable draw = drawable;
            return draw;
        } else {
            str = "0";
            TextDrawable drawable = mDrawableBuilder.build(str, mColorGenerator.getColor(str));
            Drawable draw = drawable;
            return draw;
        }
    }

    public String latLngToAddress(Activity activity, Double lat, Double lng) {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        return address + "";
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void showSuccessSnakeBar(Activity activity, String msg) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setActionTextColor(activity.getResources().getColor(R.color.white));
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(activity.getResources().getColor(R.color.green));
        snackbar.show();
    }

    public void showErrorSnakeBar(Activity activity, String msg) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setActionTextColor(activity.getResources().getColor(android.R.color.white));
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(activity.getResources().getColor(R.color.red));
        snackbar.show();
    }

    public void startMediaPlayer(Activity activity, int tone) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(activity, Uri.parse("android.resource://com.wmalick.webdocandroidlibrary/" + tone));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMediaPlayer() {
        mediaPlayer.stop();
    }

    public void startProgressBar(Activity activity, String msg) {
        progressdialog = new ProgressDialog(activity);
        progressdialog.setMessage(msg);
        progressdialog.setCancelable(false);
        progressdialog.show();
        isProgressBarShowing = true;
    }

    public void dismissProgressBar() {
        progressdialog.dismiss();
        isProgressBarShowing = false;
    }



}
