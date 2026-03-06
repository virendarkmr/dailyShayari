
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.dailyshayari.di.FirebaseModule
import com.dailyshayari.ui.components.AppWatermark
import com.dailyshayari.ui.explore.ExploreViewModel
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay

private const val FULL_CAPTURE_DELAY_MS = 700L

@Composable
fun GoodMorningScreen(pagerState: PagerState, onNavigate: (Int, String?) -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ExploreViewModel = viewModel(factory = ViewModelFactory(context))
    val goodMorningMax by viewModel.goodMorningMax.collectAsState(initial = 5)

    val fullCaptureController = rememberCaptureController()
    var captureTargetImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(captureTargetImageUrl) {
        if (captureTargetImageUrl != null) {
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
                    val imageIndex = (index % goodMorningMax) + 1
                    val imageUrl = "${FirebaseModule.firebaseStorageBaseUrl}/good_morning%2F${imageIndex}.webp?alt=media"
                    
                    GoodMorningImageCard(
                        imageUrl = imageUrl,
                        onShareClick = { captureTargetImageUrl = imageUrl }
                    )
                }
            }

            // Hidden high-quality capture target
            captureTargetImageUrl?.let { url ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Capturable(
                        controller = fullCaptureController,
                        onCaptured = { imageBitmap, _ ->
                            imageBitmap?.let { shareBitmap(context, it.asAndroidBitmap()) }
                            captureTargetImageUrl = null
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
                            GoodMorningCardContent(imageUrl = url)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoodMorningImageCard(imageUrl: String, onShareClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
    ) {
        GoodMorningCardContent(imageUrl = imageUrl)
        
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
fun GoodMorningCardContent(imageUrl: String) {
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
        
        // Overlay for watermark
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))))
        )
        
        AppWatermark(backgroundColor = Color.Black.copy(alpha = 0.3f))
    }
}
