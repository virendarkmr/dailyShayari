package com.dailyshayari

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dailyshayari.data.Shayari
import com.dailyshayari.di.FirebaseModule
import com.dailyshayari.ui.theme.*
import com.dailyshayari.viewmodel.HomeViewModel
import com.dailyshayari.viewmodel.HomeViewModelFactory
import java.util.Calendar
import kotlin.random.Random

fun isHindi(text: String): Boolean {
    return text.any { it in '\u0900'..'\u097F' }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(currentScreen: Screen, navigateTo: (Screen) -> Unit) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context, FirebaseModule.provideFirestore())
    )
    val todaysShayari by viewModel.todaysShayari.collectAsState()
    val luxuryText = LocalLuxuryTextColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shayari Vibes", style = MaterialTheme.typography.headlineLarge, color = luxuryText.appTitle) },
                navigationIcon = { AppLogo() },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notifications",
                            tint = luxuryText.appTitle
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = { AppBottomBar(currentScreen = currentScreen, navigateTo = navigateTo) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = ScreenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(GridSpacing)
        ) {
            Text(
                "Today's Special",
                style = MaterialTheme.typography.headlineMedium,
                color = luxuryText.appTitle
            )

            AnimatedVisibility(
                visible = todaysShayari.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(500, easing = FastOutSlowInEasing)
                        )
            ) {
                TodaysSpecial(shayaris = todaysShayari)
            }

            Text(
                "Quick Collections",
                style = MaterialTheme.typography.headlineMedium,
                color = luxuryText.appTitle
            )
            ShayariCard("Good Morning", "124 Messages")
            ShayariCard("Festival Greetings", "85 Messages")

            Text(
                "Browse Categories",
                style = MaterialTheme.typography.headlineMedium,
                color = luxuryText.appTitle
            )
            CategoryGrid()
        }
    }
}

@Composable
fun BottomNavigationItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val luxuryText = LocalLuxuryTextColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) luxuryText.appTitle else luxuryText.secondary
        )
        Text(label, color = if (selected) luxuryText.appTitle else luxuryText.secondary, style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodaysSpecial(shayaris: List<Shayari>) {
    val context = LocalContext.current
    val images = remember {
        val allImageIds = (1..19).map { context.resources.getIdentifier("bg_$it", "drawable", context.packageName) }.filter { it != 0 }
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val seed = year * 1000 + dayOfYear
        allImageIds.shuffled(Random(seed)).take(10)
    }

    val pageCount = if (shayaris.isNotEmpty()) shayaris.size else 1
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val itemWidth = 250.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val contentPadding = (screenWidth - itemWidth) / 2

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = GridSpacing,
            contentPadding = PaddingValues(horizontal = contentPadding)
        ) { page ->
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .height(300.dp)
                    .clip(RoundedCornerShape(28.dp))
            ) {
                val brightness = 0.93f
                val colorMatrix = floatArrayOf(
                    brightness, 0f, 0f, 0f, 0f,
                    0f, brightness, 0f, 0f, 0f,
                    0f, 0f, brightness, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )

                AsyncImage(
                    model = images.getOrElse(page % images.size) { R.drawable.bg_1 },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val shayari = shayaris.getOrNull(page)
                    if (shayari != null) {
                        val fontFamily = if (isHindi(shayari.text)) NotoSansDevanagariFontFamily else PlayfairDisplayFontFamily
                        val textShadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(0f, 2f),
                            blurRadius = 6f
                        )
                        val shayariStyle = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 22.sp,
                            lineHeight = 30.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.3.sp,
                            color = Color.White.copy(alpha = 0.92f),
                            shadow = textShadow
                        )

                        AutoSizeText(
                            text = shayari.text,
                            style = shayariStyle,
                            maxFontSize = 22.sp,
                            minFontSize = 12.sp,
                            maxLines = 10,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = "Like",
                            tint = GoldPrimary,
                            modifier = Modifier.shadow(elevation = 8.dp, spotColor = GoldPrimary, shape = CircleShape)
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = GoldPrimary,
                            modifier = Modifier.shadow(elevation = 8.dp, spotColor = GoldPrimary, shape = CircleShape)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val currentPage = pagerState.currentPage
            repeat(pagerState.pageCount) {
                val color = if (it == currentPage) GoldPrimary else Color.Gray.copy(alpha = 0.4f)
                val scale by animateFloatAsState(
                    targetValue = if (it == currentPage) 1.2f else 1f,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "dotScaleAnimation"
                )
                Box(
                    modifier = Modifier
                        .scale(scale)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
    style: TextStyle,
    maxFontSize: TextUnit = style.fontSize,
    minFontSize: TextUnit = 12.sp,
) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        maxLines = maxLines,
        textAlign = textAlign,
        style = scaledTextStyle,
        onTextLayout = { textLayoutResult ->
            val overflow = textLayoutResult.didOverflowHeight || textLayoutResult.didOverflowWidth
            if (overflow) {
                val newFontSize = scaledTextStyle.fontSize * 0.9f
                if (newFontSize > minFontSize) {
                    scaledTextStyle = scaledTextStyle.copy(fontSize = newFontSize)
                } else {
                    scaledTextStyle = scaledTextStyle.copy(fontSize = minFontSize)
                    readyToDraw = true
                }
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
fun ShayariCard(title: String, subtitle: String) {
    val luxuryText = LocalLuxuryTextColors.current

    val cardSurfaceGradient = Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), Color.Black))
    val cardGlow = Brush.radialGradient(colors = listOf(GoldPrimary.copy(alpha = 0.1f), Color.Transparent), radius = 200f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardGlow, RoundedCornerShape(28.dp))
            .background(cardSurfaceGradient, RoundedCornerShape(28.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(colors = listOf(GoldPrimary.copy(alpha = 0.2f), GoldPrimary.copy(alpha = 0.05f))),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = luxuryText.body)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = luxuryText.secondary)
            }
            Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = "Arrow", tint = luxuryText.secondary)
        }
    }
}

data class CategoryInfo(val name: String, val icon: ImageVector)

@Composable
fun CategoryGrid() {
    val categories = listOf(
        CategoryInfo("Love Shayari", Icons.Rounded.Favorite),
        CategoryInfo("Sad Quotes", Icons.Rounded.SentimentDissatisfied),
        CategoryInfo("Inspiration", Icons.Rounded.Bolt),
        CategoryInfo("Friendship", Icons.Rounded.Group),
        CategoryInfo("Good Night", Icons.Rounded.Nightlight),
        CategoryInfo("Attitude", Icons.Rounded.AutoFixHigh)
    )
    Column(verticalArrangement = Arrangement.spacedBy(GridSpacing)) {
        categories.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(GridSpacing)) {
                rowItems.forEach { categoryInfo ->
                    Box(modifier = Modifier.weight(1f)) { CategoryCard(categoryInfo) }
                }
                if (rowItems.size < 2) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun CategoryCard(categoryInfo: CategoryInfo) {
    val luxuryText = LocalLuxuryTextColors.current

    val cardSurfaceGradient = Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), Color.Black))
    val cardGlow = Brush.radialGradient(colors = listOf(GoldPrimary.copy(alpha = 0.1f), Color.Transparent), radius = 200f)
    val iconContainerGradient = Brush.verticalGradient(colors = listOf(GoldPrimary.copy(alpha = 0.3f), GoldPrimary.copy(alpha = 0.1f)))

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(cardGlow, RoundedCornerShape(28.dp))
            .background(cardSurfaceGradient, RoundedCornerShape(28.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(colors = listOf(GoldPrimary.copy(alpha = 0.2f), GoldPrimary.copy(alpha = 0.05f))),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = GoldPrimary.copy(alpha = 0.3f), ambientColor = Color.Black)
                    .background(iconContainerGradient, RoundedCornerShape(16.dp))
                    .border(width = 1.dp, color = GoldPrimary.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = categoryInfo.icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = GoldPrimary)
            }
            Text(text = categoryInfo.name, style = MaterialTheme.typography.titleMedium, color = luxuryText.body)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.7f)
                .height(2.dp)
                .background(brush = Brush.horizontalGradient(colors = listOf(Color.Transparent, GoldPrimary.copy(alpha = 0.3f), Color.Transparent)))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShayariTheme { HomeScreen(currentScreen = Screen.Home, navigateTo = {}) }
}
