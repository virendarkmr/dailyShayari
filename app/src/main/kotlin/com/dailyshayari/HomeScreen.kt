package com.dailyshayari

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context, FirebaseModule.provideFirestore())
    )
    val todaysShayari by viewModel.todaysShayari.collectAsState()

    ShayariTheme {
        val luxuryText = LocalLuxuryTextColors.current
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Shayari Vibes", style = MaterialTheme.typography.headlineLarge, color = luxuryText.appTitle) },
                    navigationIcon = {
                        AppLogo()
                    },
                    actions = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = "Notifications",
                                tint = luxuryText.appTitle
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BottomNavigationItem(Icons.Rounded.Home, "Home", true)
                        BottomNavigationItem(Icons.Rounded.Search, "Explore")
                        BottomNavigationItem(Icons.Rounded.Favorite, "Create")
                        BottomNavigationItem(Icons.Rounded.Search, "Search")
                        BottomNavigationItem(Icons.Rounded.Person, "Profile")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(ScreenPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(GridSpacing)
            ) {
                Text(
                    "Today's Special",
                    style = MaterialTheme.typography.headlineMedium,
                    color = luxuryText.appTitle
                )
                TodaysSpecial(shayaris = todaysShayari)

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
}

@Composable
fun BottomNavigationItem(icon: ImageVector, label: String, selected: Boolean = false) {
    val luxuryText = LocalLuxuryTextColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
    val luxuryText = LocalLuxuryTextColors.current
    val context = LocalContext.current
    val images = remember {
        val allImageIds = (1..19).map {
            context.resources.getIdentifier("bg_$it", "drawable", context.packageName)
        }.filter { it != 0 }

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
                AsyncImage(
                    model = images.getOrElse(page % images.size) { R.drawable.bg_1 },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
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
                        AutoSizeText(
                            text = shayari.text,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 30.sp),
                            color = luxuryText.body,
                            textAlign = TextAlign.Center,
                            maxFontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            minFontSize = 12.sp,
                            maxLines = 10, // A reasonable max line count
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "No shayari available today.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = luxuryText.body,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = "Like",
                            tint = luxuryText.appTitle
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkBorder,
                            contentDescription = "Save",
                            tint = luxuryText.appTitle
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = luxuryText.appTitle
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val currentPage = pagerState.currentPage
            repeat(pagerState.pageCount) { index ->
                val color = if (index == currentPage) GoldPrimary else GoldSoft.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
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
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
    style: TextStyle,
    maxFontSize: TextUnit = style.fontSize,
    minFontSize: TextUnit = 12.sp,
) {
    var scaledTextStyle by remember { mutableStateOf(style.copy(fontSize = maxFontSize)) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        color = color,
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

    val cardSurfaceGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            Color.Black
        )
    )

    val cardGlow = Brush.radialGradient(
        colors = listOf(
            GoldPrimary.copy(alpha = 0.1f),
            Color.Transparent
        ),
        radius = 200f
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardGlow, RoundedCornerShape(28.dp))
            .background(cardSurfaceGradient, RoundedCornerShape(28.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = 0.2f),
                        GoldPrimary.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = luxuryText.body)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = luxuryText.secondary)
            }
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = "Arrow",
                tint = luxuryText.secondary
            )
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(GridSpacing),
            ) {
                rowItems.forEach { categoryInfo ->
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryCard(categoryInfo)
                    }
                }
                if (rowItems.size < 2) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CategoryCard(categoryInfo: CategoryInfo) {
    val luxuryText = LocalLuxuryTextColors.current

    val cardSurfaceGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            Color.Black
        )
    )

    val cardGlow = Brush.radialGradient(
        colors = listOf(
            GoldPrimary.copy(alpha = 0.1f),
            Color.Transparent
        ),
        radius = 200f
    )

    val iconContainerGradient = Brush.verticalGradient(
        colors = listOf(
            GoldPrimary.copy(alpha = 0.3f),
            GoldPrimary.copy(alpha = 0.1f)
        )
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(cardGlow, RoundedCornerShape(28.dp))
            .background(cardSurfaceGradient, RoundedCornerShape(28.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = 0.2f),
                        GoldPrimary.copy(alpha = 0.05f)
                    )
                ),
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
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = GoldPrimary.copy(alpha = 0.3f),
                        ambientColor = Color.Black
                    )
                    .background(iconContainerGradient, RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = GoldPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryInfo.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = GoldPrimary
                )
            }

            Text(
                text = categoryInfo.name,
                style = MaterialTheme.typography.titleMedium,
                color = luxuryText.body
            )
        }
        // Top light highlight
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.7f)
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            GoldPrimary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShayariTheme {
        HomeScreen()
    }
}
