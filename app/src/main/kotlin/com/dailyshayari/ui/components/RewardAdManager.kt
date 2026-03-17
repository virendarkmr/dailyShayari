package com.dailyshayari.ui.components

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAdManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null
    private val prefs: SharedPreferences = context.getSharedPreferences("reward_prefs", Context.MODE_PRIVATE)

    fun loadAd(onAdLoaded: () -> Unit = {}) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", // Test Reward Ad ID
            adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    onAdLoaded()
                }
            })
    }

    fun showAd(activity: Activity, onRewardEarned: () -> Unit) {
        if (isRewardActive()) {
            onRewardEarned()
            return
        }

        rewardedAd?.let { ad ->
            ad.show(activity, OnUserEarnedRewardListener {
                saveRewardTime()
                onRewardEarned()
                loadAd() // Load next ad
            })
        } ?: run {
            // If ad not loaded, load it and allow action for now to not block user
            loadAd()
            onRewardEarned()
        }
    }

    private fun saveRewardTime() {
        prefs.edit().putLong("last_reward_time", System.currentTimeMillis()).apply()
    }

    fun isRewardActive(): Boolean {
        val lastTime = prefs.getLong("last_reward_time", 0)
        val oneHourInMillis = 60 * 60 * 1000
        return (System.currentTimeMillis() - lastTime) < oneHourInMillis
    }
}
