package com.ahmadabuhasan.pointofsales.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ahmadabuhasan.pointofsales.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class Utils {

    // Interstitials are preloaded and then shown at a natural transition
    // point (when the user leaves the screen) instead of the moment they
    // finish loading. Showing an ad on top of a screen the user is already
    // interacting with produces accidental clicks, which count as invalid
    // traffic even though they come from real users.
    private static final String ADS_PREFS = "ads_frequency";
    private static final String KEY_LAST_INTERSTITIAL = "last_interstitial_at";

    // Minimum gap between two interstitials, so reopening a screen a few
    // times in a row does not fire one ad per visit.
    private static final long INTERSTITIAL_MIN_INTERVAL_MS = 3 * 60 * 1000L;

    private static InterstitialAd loadedAd;
    private static boolean isLoading;

    /**
     * Preloads an interstitial if the cooldown has elapsed and none is
     * already cached. Safe to call from onCreate(); it never shows an ad.
     */
    public static void preloadInterstitial(Context context) {
        if (loadedAd != null || isLoading || !cooldownElapsed(context)) {
            return;
        }
        isLoading = true;
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                context.getApplicationContext(),
                context.getString(R.string.admob_interstitial_ads_id),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        isLoading = false;
                        loadedAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoading = false;
                        loadedAd = null;
                        Log.d("Ads", "Interstitial failed to load: " + loadAdError.getMessage());
                    }
                }
        );
    }

    /**
     * Shows a preloaded interstitial, if one is ready and the cooldown has
     * elapsed. Call this when the user is leaving the screen — never while
     * they are still working on it.
     *
     * @return true if an ad was shown, so the caller can defer its own
     *         navigation until the ad is dismissed.
     */
    public static boolean showInterstitialOnExit(Activity activity, Runnable onDismissed) {
        if (loadedAd == null || !cooldownElapsed(activity)) {
            return false;
        }
        InterstitialAd ad = loadedAd;
        loadedAd = null;
        markShown(activity);
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                if (onDismissed != null) {
                    onDismissed.run();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.d("Ads", "Interstitial failed to show: " + adError.getMessage());
                if (onDismissed != null) {
                    onDismissed.run();
                }
            }
        });
        ad.show(activity);
        return true;
    }

    private static boolean cooldownElapsed(Context context) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(ADS_PREFS, Context.MODE_PRIVATE);
        long last = prefs.getLong(KEY_LAST_INTERSTITIAL, 0L);
        return System.currentTimeMillis() - last >= INTERSTITIAL_MIN_INTERVAL_MS;
    }

    private static void markShown(Context context) {
        context.getApplicationContext()
                .getSharedPreferences(ADS_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_LAST_INTERSTITIAL, System.currentTimeMillis())
                .apply();
    }

    // UNICODE 0x23 = #
    public static final byte[] UNICODE_TEXT = new byte[]{0x23, 0x23, 0x23,
            0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23,
            0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23,
            0x23, 0x23, 0x23};

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;


        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}