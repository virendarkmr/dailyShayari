package com.dailyshayari.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import com.dailyshayari.R

@Composable
fun NativeAdItem() {
    // We use a different background color to distinguish it from shayari cards
    // Shayari cards use dark gradients, so we'll use a slightly lighter grey-brown
    val adBackgroundColor = Color(0xFF3E2723).copy(alpha = 0.5f) 
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(adBackgroundColor)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Sponsored",
                color = Color.Gray,
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    val adView = LayoutInflater.from(context).inflate(R.layout.ad_unified, null) as NativeAdView
                    
                    val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110") // Test Native Ad ID
                        .forNativeAd { nativeAd ->
                            populateNativeAdView(nativeAd, adView)
                        }
                        .withAdListener(object : AdListener() {
                            // Handle failures if needed
                        })
                        .withNativeAdOptions(NativeAdOptions.Builder().build())
                        .build()

                    adLoader.loadAd(AdRequest.Builder().build())
                    adView
                }
            )
        }
    }
}

fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)

    (adView.headlineView as TextView).text = nativeAd.headline
    (adView.bodyView as TextView).text = nativeAd.body
    (adView.callToActionView as Button).text = nativeAd.callToAction

    if (nativeAd.icon == null) {
        adView.iconView?.visibility = android.view.View.GONE
    } else {
        (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
        adView.iconView?.visibility = android.view.View.VISIBLE
    }

    adView.setNativeAd(nativeAd)
}
