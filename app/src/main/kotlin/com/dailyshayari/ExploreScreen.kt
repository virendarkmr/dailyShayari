
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

sealed class ShayariUiModel {
    abstract val id: Int
    data class Text(override val id: Int, val category: String, val text: String) : ShayariUiModel()
    data class Image(override val id: Int, val imageName: String, val text: String) : ShayariUiModel()
}

val luxuryGold = Color(0xFFC6A75E)
val softWhite = Color(0xFFF5F5F5)

@Composable
fun ExploreScreen(pagerState: PagerState, onNavigate: (Int) -> Unit) {
    var selectedCategory by remember { mutableStateOf<String?>("All") }
    val scrollState = rememberLazyListState()
    val previousVisibleItemIndex = remember(scrollState) {
        mutableStateOf(scrollState.firstVisibleItemIndex)
    }
    val topBarVisible by remember(scrollState) {
        derivedStateOf {
            val isScrollingUp = scrollState.firstVisibleItemIndex > previousVisibleItemIndex.value
            previousVisibleItemIndex.value = scrollState.firstVisibleItemIndex

            if (scrollState.firstVisibleItemIndex == 0) {
                true
            } else {
                !isScrollingUp
            }
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
                            onCategorySelected = { category -> selectedCategory = category }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = { AppBottomBar(pagerState = pagerState, onNavigate = onNavigate) }
    ) { innerPadding ->
        ShayariFeed(
            modifier = Modifier.padding(innerPadding),
            scrollState = scrollState
        )
    }
}

@Composable
fun CategoryChips(selectedCategory: String?, onCategorySelected: (String) -> Unit) {
    val categories = listOf("All", "Love", "Sad", "Motivation", "Friendship", "Gita Lines", "Quotes")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) luxuryGold else Color.Transparent,
                animationSpec = tween(300),
                label = ""
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.Black else luxuryGold,
                animationSpec = tween(300),
                label = ""
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(backgroundColor)
                    .border(1.dp, luxuryGold, RoundedCornerShape(50))
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = textColor,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun ShayariFeed(modifier: Modifier = Modifier, scrollState: LazyListState) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(Int.MAX_VALUE) { index ->
            val item = remember(index) {
                if (index % 3 == 0) {
                    ShayariUiModel.Text(index, "Love", "This is a text shayari.")
                } else {
                    ShayariUiModel.Image(index, "bg_${(index % 20) + 1}", "Some text for the image")
                }
            }

            Box {
                when (item) {
                    is ShayariUiModel.Text -> TextCard(item)
                    is ShayariUiModel.Image -> ImageCard(item)
                }
            }
        }
    }
}

@Composable
fun TextCard(shayari: ShayariUiModel.Text) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.DarkGray.copy(alpha = 0.4f),
                        Color.Black.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = shayari.category,
                color = luxuryGold,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = shayari.text,
                color = softWhite,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
            ActionRow()
        }
    }
}

@Composable
fun ImageCard(shayari: ShayariUiModel.Image) {
    val context = LocalContext.current
    val imageResId = remember(shayari.imageName) {
        context.resources.getIdentifier(shayari.imageName, "drawable", context.packageName)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            AsyncImage(
                model = imageResId,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = shayari.text,
                    color = softWhite,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActionRow()
            }
        }
    }
}

@Composable
fun ActionRow() {
    var isLiked by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(100),
        label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { isLiked = !isLiked }, interactionSource = interactionSource) {
            Icon(
                imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Like",
                tint = if (isLiked) luxuryGold else softWhite.copy(alpha = 0.7f),
                modifier = Modifier.scale(scale)
            )
        }
        IconButton(onClick = { /* TODO: Copy */ }) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = "Copy",
                tint = softWhite.copy(alpha = 0.7f)
            )
        }
        IconButton(onClick = { /* TODO: Share */ }) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = "Share",
                tint = softWhite.copy(alpha = 0.7f)
            )
        }
    }
}
