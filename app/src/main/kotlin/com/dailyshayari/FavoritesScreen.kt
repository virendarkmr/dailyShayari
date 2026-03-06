
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.dailyshayari.db.FavoriteShayariEntity
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.ui.theme.NotoSansDevanagariFontFamily
import com.dailyshayari.ui.theme.PlayfairDisplayFontFamily
import com.dailyshayari.util.isHindi
import com.dailyshayari.viewmodel.FavoritesViewModel
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val colorPalettes = listOf(
    listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF)),
    listOf(Color(0xFF4B79A1), Color(0xFF283E51)),
    listOf(Color(0xFFCC2B5E), Color(0xFF753A88)),
    listOf(Color(0xFF134E5E), Color(0xFF71B280)),
    listOf(Color(0xFF93291E), Color(0xFFED213A))
)

@Composable
fun FavoritesScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: FavoritesViewModel = viewModel(factory = ViewModelFactory(context))
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    val fullCaptureController = rememberCaptureController()
    var captureTarget by remember { mutableStateOf<FavoriteShayariEntity?>(null) }

    LaunchedEffect(captureTarget) {
        if (captureTarget != null) {
            delay(700)
            fullCaptureController.capture()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Favorites", color = luxuryGold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = luxuryGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (favorites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorites yet.", color = softWhite)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(favorites) { favorite ->
                        FavoriteShayariCard(
                            favorite = favorite,
                            viewModel = viewModel,
                            onRequestFullCapture = { captureTarget = it }
                        )
                    }
                }
            }

            captureTarget?.let { target ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Capturable(
                        controller = fullCaptureController,
                        onCaptured = { imageBitmap, _ ->
                            imageBitmap?.let { shareBitmap(context, it.asAndroidBitmap()) }
                            captureTarget = null
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                    ) {
                        Box(modifier = Modifier.shadow(4.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp)).fillMaxWidth()) {
                            FavoriteImageCardContent(favorite = target)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteShayariCard(
    favorite: FavoriteShayariEntity,
    viewModel: FavoritesViewModel,
    onRequestFullCapture: (FavoriteShayariEntity) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    val isImageCard = favorite.imageUrl != "none"

    if (isImageCard) {
        Box(modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))) {
            Capturable(controller = captureController, onCaptured = { imageBitmap, _ ->
                imageBitmap?.let { shareBitmap(context, it.asAndroidBitmap()) }
            }, modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    FavoriteImageCardContent(favorite = favorite)
                    ActionRow(
                        shayari = ShayariEntity(favorite.id, favorite.text, favorite.category, 0, 0),
                        onShareClick = { onRequestFullCapture(favorite) },
                        onFavoriteClick = { viewModel.toggleFavoriteFromFav(favorite) },
                        isFavoriteFlow = viewModel.isFavorite(favorite.id),
                        modifier = Modifier.align(Alignment.BottomCenter).background(Color.Transparent)
                    )
                }
            }
        }
    } else {
        val palette = remember(favorite.id) { colorPalettes.random() }
        val cardModifier = Modifier.drawWithContent {
            val gradientBrush = Brush.radialGradient(colors = palette, center = Offset.Zero, radius = 2000f)
            drawRect(brush = gradientBrush)
            drawContent()
        }

        Column(modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))) {
            Capturable(controller = captureController, onCaptured = { imageBitmap, _ -> imageBitmap?.let { shareBitmap(context, it.asAndroidBitmap()) } }) {
                FavoriteTextCardContent(favorite = favorite, modifier = cardModifier)
            }
            Column(modifier = Modifier.fillMaxWidth().then(cardModifier)) {
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                ActionRow(
                    shayari = ShayariEntity(favorite.id, favorite.text, favorite.category, 0, 0),
                    onShareClick = { coroutineScope.launch { captureController.capture() } },
                    onFavoriteClick = { viewModel.toggleFavoriteFromFav(favorite) },
                    isFavoriteFlow = viewModel.isFavorite(favorite.id)
                )
            }
        }
    }
}

@Composable
fun FavoriteTextCardContent(favorite: FavoriteShayariEntity, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = favorite.category.replaceFirstChar { it.uppercase() },
            color = luxuryGold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        val isHindi = isHindi(favorite.text)
        val fontFamily = if (isHindi) NotoSansDevanagariFontFamily else PlayfairDisplayFontFamily
        Text(
            text = favorite.text,
            color = softWhite,
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = fontFamily),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun FavoriteImageCardContent(favorite: FavoriteShayariEntity) {
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(favorite.imageUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isHindi = isHindi(favorite.text)
            val fontFamily = if (isHindi) NotoSansDevanagariFontFamily else PlayfairDisplayFontFamily
            Text(
                text = favorite.text,
                color = softWhite,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isHindi) 22.sp else 20.sp,
                    fontFamily = fontFamily,
                    shadow = Shadow(Color.Black.copy(alpha = 0.5f), Offset(0f, 2f), 6f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
