
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.dailyshayari.data.GoodMorningData
import com.dailyshayari.di.FirebaseModule
import com.dailyshayari.ui.components.AppWatermark
import com.dailyshayari.ui.explore.ExploreViewModel
import com.dailyshayari.ui.theme.CinzelFontFamily
import com.dailyshayari.ui.theme.GreatVibesFontFamily
import com.dailyshayari.ui.theme.NotoSansDevanagariFontFamily
import com.dailyshayari.ui.theme.PlayfairDisplayFontFamily
import com.dailyshayari.util.isHindi
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

private const val FULL_CAPTURE_DELAY_MS = 700L

@Composable
fun GoodMorningScreen(pagerState: PagerState, onNavigate: (Int, String?) -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ExploreViewModel = viewModel(factory = ViewModelFactory(context))
    val goodMorningMax by viewModel.goodMorningMax.collectAsState(initial = 5)
    val weekdayMax by viewModel.weekdayMax.collectAsState(initial = 5)

    // Load Good Morning Data
    val gmData = remember {
        try {
            val jsonString = context.resources.openRawResource(R.raw.goodmorning_data).bufferedReader().use { it.readText() }
            Json.decodeFromString<GoodMorningData>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    val calendar = Calendar.getInstance()
    val dayOfWeek = SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.time).lowercase()
    val dateString = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(calendar.time)

    val fullCaptureController = rememberCaptureController()
    var captureTargetData by remember { mutableStateOf<Triple<String, String, Boolean>?>(null) } // URL, Text, isHindi

    LaunchedEffect(captureTargetData) {
        if (captureTargetData != null) {
            delay(FULL_CAPTURE_DELAY_MS)
            fullCaptureController.capture()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Good Morning", color = luxuryGold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = luxuryGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = { AppBottomBar(pagerState = pagerState, onNavigate = onNavigate) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val scrollState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(Int.MAX_VALUE) { index ->
                    val isWeekdayCard = index % 2 == 1
                    val imageUrl: String
                    val displayText: String

                    if (isWeekdayCard) {
                        val imageIndex = (index / 2 % weekdayMax) + 1
                        imageUrl = "${FirebaseModule.firebaseStorageBaseUrl}/good_morning%2F${dayOfWeek}%2F${imageIndex}.webp?alt=media"
                        displayText = gmData?.weekday_blessings?.get(dayOfWeek)?.text ?: "Have a blessed day!"
                    } else {
                        val imageIndex = (index / 2 % goodMorningMax) + 1
                        imageUrl = "${FirebaseModule.firebaseStorageBaseUrl}/good_morning%2F${imageIndex}.webp?alt=media"
                        val messages = gmData?.messages ?: emptyList()
                        displayText = if (messages.isNotEmpty()) messages[index / 2 % messages.size] else "Have a great day!"
                    }
                    
                    GoodMorningImageCard(
                        imageUrl = imageUrl,
                        displayText = displayText,
                        dateString = dateString,
                        itemIndex = index,
                        onShareClick = { captureTargetData = Triple(imageUrl, displayText, isHindi(displayText)) }
                    )
                }
            }

            // Hidden high-quality capture target
            captureTargetData?.let { (url, text, isHindi) ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Capturable(
                        controller = fullCaptureController,
                        onCaptured = { imageBitmap, _ ->
                            imageBitmap?.let { shareBitmap(context, it.asAndroidBitmap()) }
                            captureTargetData = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp))
                                .clip(RoundedCornerShape(24.dp))
                                .fillMaxWidth()
                        ) {
                            GoodMorningCardContent(imageUrl = url, displayText = text, dateString = dateString, isHindiMsg = isHindi, itemIndex = 0) 
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoodMorningImageCard(imageUrl: String, displayText: String, dateString: String, itemIndex: Int, onShareClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
    ) {
        GoodMorningCardContent(imageUrl = imageUrl, displayText = displayText, dateString = dateString, isHindiMsg = isHindi(displayText), itemIndex = itemIndex)
        
        // Share button on top of the image
        IconButton(
            onClick = onShareClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Icon(Icons.Rounded.Share, contentDescription = "Share", tint = Color.White)
        }
    }
}

@Composable
fun GoodMorningCardContent(imageUrl: String, displayText: String, dateString: String, isHindiMsg: Boolean, itemIndex: Int) {
    val fonts = listOf(FontFamily.Cursive, GreatVibesFontFamily, CinzelFontFamily)
    val selectedFont = remember(itemIndex) { fonts[itemIndex % fonts.size] }

    Box(modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Readability Overlay - Darkened for better contrast
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(listOf(
                    Color.Black.copy(alpha = 0.4f), // Darker top
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.6f)  // Darker bottom
                )))
        )
        
        AppWatermark(backgroundColor = Color.Black.copy(alpha = 0.3f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. GOOD MORNING (Gold/Luxury for better contrast)
            Text(
                text = "Good morning",
                style = TextStyle(
                    fontFamily = selectedFont,
                    fontSize = if (selectedFont == CinzelFontFamily) 42.sp else 54.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = luxuryGold, // Changed to luxury gold for readability
                    textAlign = TextAlign.Center,
                    shadow = Shadow(Color.Black.copy(alpha = 0.8f), Offset(3f, 3f), 8f) // Dark shadow
                )
            )

            // 2. Date and Day (Bolder White)
            Text(
                text = dateString,
                modifier = Modifier.padding(top = 2.dp, bottom = 32.dp),
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White, // Changed to White for maximum visibility
                    textAlign = TextAlign.Center,
                    shadow = Shadow(Color.Black.copy(alpha = 0.8f), Offset(2f, 2f), 6f)
                )
            )

            // 3. Message or Blessing Text (Bold White)
            Text(
                text = displayText,
                style = TextStyle(
                    fontFamily = if (isHindiMsg) NotoSansDevanagariFontFamily else PlayfairDisplayFontFamily,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = if (isHindiMsg) FontStyle.Normal else FontStyle.Italic,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp,
                    shadow = Shadow(Color.Black, Offset(2f, 2f), 10f) // Heavy shadow
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
