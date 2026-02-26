@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.ui.explore.ExploreViewModel
import com.dailyshayari.ui.theme.NotoSansDevanagariFontFamily
import com.dailyshayari.ui.theme.PlayfairDisplayFontFamily
import com.dailyshayari.util.copyTextToClipboard
import com.dailyshayari.util.isHindi
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

val luxuryGold = Color(0xFFC6A75E)
val softWhite = Color(0xFFF5F5F5)

@Composable
fun ExploreScreen(pagerState: PagerState, onNavigate: (Int) -> Unit) {
    val context = LocalContext.current
    val viewModel: ExploreViewModel = viewModel(factory = ViewModelFactory(context))
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val shayaris = viewModel.shayaris.collectAsLazyPagingItems()

    val scrollState = rememberLazyListState()
    val previousVisibleItemIndex = remember(scrollState) { mutableStateOf(scrollState.firstVisibleItemIndex) }
    val topBarVisible by remember(scrollState) {
        derivedStateOf {
            val isScrollingUp = scrollState.firstVisibleItemIndex > previousVisibleItemIndex.value
            previousVisibleItemIndex.value = scrollState.firstVisibleItemIndex
            if (scrollState.firstVisibleItemIndex == 0) true else !isScrollingUp
        }
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = topBarVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                TopAppBar(
                    title = {
                        CategoryChips(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category -> viewModel.selectCategory(category) }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        },
        bottomBar = { AppBottomBar(pagerState = pagerState, onNavigate = onNavigate) }
    ) { innerPadding ->
        ShayariFeed(modifier = Modifier.padding(innerPadding), scrollState = scrollState, shayaris = shayaris)
    }
}

@Composable
fun CategoryChips(selectedCategory: String?, onCategorySelected: (String) -> Unit) {
    val categories = listOf("All", "Love", "Sad", "Motivation", "Friendship", "Gita Lines", "Quotes")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            val backgroundColor by animateColorAsState(targetValue = if (isSelected) luxuryGold else Color.Transparent, animationSpec = tween(300))
            val textColor by animateColorAsState(targetValue = if (isSelected) Color.Black else luxuryGold, animationSpec = tween(300))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(backgroundColor)
                    .border(1.dp, luxuryGold, RoundedCornerShape(50))
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(text = category, color = textColor, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun ShayariFeed(modifier: Modifier = Modifier, scrollState: LazyListState, shayaris: LazyPagingItems<ShayariEntity>) {
    if (shayaris.loadState.refresh is LoadState.Loading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    if (shayaris.itemCount == 0 && shayaris.loadState.append.endOfPaginationReached) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No shayaris found.", color = softWhite) }
        return
    }

    LazyColumn(modifier = modifier, state = scrollState, contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        items(Int.MAX_VALUE) { index ->
            if (shayaris.itemCount > 0) {
                val actualIndex = index % shayaris.itemCount
                shayaris[actualIndex]?.let { shayari -> ShayariCard(shayari = shayari) }
            }
        }
    }
}

private val colorPalettes = listOf(
    listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF)),
    listOf(Color(0xFF4B79A1), Color(0xFF283E51)),
    listOf(Color(0xFFCC2B5E), Color(0xFF753A88)),
    listOf(Color(0xFF134E5E), Color(0xFF71B280)),
    listOf(Color(0xFF93291E), Color(0xFFED213A))
)

@Composable
fun ShayariCard(shayari: ShayariEntity) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    val isImageCard = remember(shayari.id) { shayari.id.hashCode() % 2 == 0 }

    if (isImageCard) {
        var isCapturing by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth().shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))) {
            Capturable(controller = captureController, onCaptured = { imageBitmap, _ ->
                imageBitmap?.let { shareBitmap(context, it.asAndroidBitmap()) }
                isCapturing = false
            }, modifier = Modifier.fillMaxSize()) {

                // Put both image and ActionRow inside Capturable so the ActionRow can be hidden (alpha=0) during capture
                Box(modifier = Modifier.fillMaxSize()) {
                    ImageCardContent(shayari = shayari)

                    ActionRow(
                        shayari = shayari,
                        onShareClick = {
                            coroutineScope.launch {
                                isCapturing = true
                                // allow recomposition to apply alpha=0
                                delay(300)
                                captureController.capture()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .background(Color.Transparent)
                            .alpha(if (isCapturing) 0f else 1f)
                    )
                }
            }
        }
    } else {
        // Text card: keep original look and ActionRow outside the Capturable (so it is included in the visual card)
        val palette = remember(shayari.id) { colorPalettes.random() }
        val cardModifier = run {
            val gradientBrush = remember(palette) { Brush.radialGradient(colors = palette, center = Offset.Zero, radius = 2000f) }
            val patternColor = remember(palette) { palette.last().copy(alpha = 0.08f) }
            Modifier.drawWithContent {
                drawRect(brush = gradientBrush)
                val lineSpacing = 80f
                var y = -200f
                while (y < size.height + 400f) {
                    drawLine(color = patternColor, start = Offset(-200f, y), end = Offset(size.width + 200f, y - 200f), strokeWidth = 1.dp.toPx())
                    y += lineSpacing
                }
                drawContent()
            }
        }

        Column(modifier = Modifier.fillMaxWidth().shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))) {
            Capturable(controller = captureController, onCaptured = { imageBitmap, _ -> imageBitmap?.let { shareBitmap(context, it.asAndroidBitmap()) } }) {
                TextCardContent(shayari = shayari, modifier = cardModifier)
            }

            Column(modifier = Modifier.fillMaxWidth().then(cardModifier)) {
                Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                ActionRow(shayari = shayari, onShareClick = { coroutineScope.launch { captureController.capture() } })
            }
        }
    }
}

@Composable
fun TextCardContent(shayari: ShayariEntity, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = shayari.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, color = luxuryGold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
        val isHindi = isHindi(shayari.text)
        val fontFamily = if (isHindi) NotoSansDevanagariFontFamily else PlayfairDisplayFontFamily
        val fontSize = if (isHindi) (MaterialTheme.typography.bodyLarge.fontSize.value + 2).sp else MaterialTheme.typography.bodyLarge.fontSize
        Text(text = shayari.text, color = softWhite, style = MaterialTheme.typography.bodyLarge.copy(fontFamily = fontFamily, fontSize = fontSize), modifier = Modifier.padding(bottom = 16.dp))
    }
}

object DrawableUtils {
    private var imageCount: Int? = null
    fun getBgImageCount(context: android.content.Context): Int {
        if (imageCount != null) return imageCount!!
        var count = 0
        while (context.resources.getIdentifier("bg_${count + 1}", "drawable", context.packageName) != 0) {
            count++
        }
        imageCount = count
        return count
    }
}

@Composable
fun ImageCardContent(shayari: ShayariEntity) {
    val context = LocalContext.current
    val imageCount = remember { DrawableUtils.getBgImageCount(context) }

    if (imageCount == 0) {
        TextCardContent(shayari = shayari)
        return
    }

    val imageResId = remember(shayari.id, shayari.category) {
        val imageIndex = (shayari.id.hashCode().absoluteValue % imageCount) + 1
        context.resources.getIdentifier("bg_$imageIndex", "drawable", context.packageName)
    }

    Box(modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f)) {
        AsyncImage(model = imageResId, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

        Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))

        Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = 56.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            val isHindi = isHindi(shayari.text)
            val fontFamily = if (isHindi) NotoSansDevanagariFontFamily else PlayfairDisplayFontFamily
            val fontSize = if (isHindi) 22.sp else 20.sp
            val textShadow = Shadow(color = Color.Black.copy(alpha = 0.5f), offset = Offset(0f, 2f), blurRadius = 6f)
            Text(text = shayari.text, color = softWhite, style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize, fontFamily = fontFamily, shadow = textShadow, textAlign = TextAlign.Center), modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun ActionRow(shayari: ShayariEntity, onShareClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(targetValue = if (isPressed) 0.8f else 1f, animationSpec = tween(100), label = "")

    Row(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { isLiked = !isLiked }, interactionSource = interactionSource) {
            Icon(imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, contentDescription = "Like", tint = if (isLiked) luxuryGold else softWhite.copy(alpha = 0.7f), modifier = Modifier.scale(scale))
        }
        IconButton(onClick = { copyTextToClipboard(context, shayari.text) }) {
            Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = "Copy", tint = softWhite.copy(alpha = 0.7f))
        }
        IconButton(onClick = onShareClick) {
            Icon(imageVector = Icons.Rounded.Share, contentDescription = "Share", tint = softWhite.copy(alpha = 0.7f))
        }
    }
}
