package com.dailyshayari

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FormatColorText
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.dailyshayari.R
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.ui.components.CategoryChips
import com.dailyshayari.ui.explore.ExploreViewModel
import com.dailyshayari.ui.theme.NotoSansDevanagariFontFamily
import com.dailyshayari.ui.theme.PlayfairDisplayFontFamily
import com.dailyshayari.ui.theme.PoppinsFontFamily
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val colorPalettes = listOf(
    listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF)),
    listOf(Color(0xFF4B79A1), Color(0xFF283E51)),
    listOf(Color(0xFFCC2B5E), Color(0xFF753A88)),
    listOf(Color(0xFF134E5E), Color(0xFF71B280)),
    listOf(Color(0xFF93291E), Color(0xFFED213A)),
    listOf(Color(0xFF00c6ff), Color(0xFF0072ff)),
    listOf(Color(0xFFfe8c00), Color(0xFFf83600)),
    listOf(Color(0xFF52c234), Color(0xFF061700)),
    listOf(Color(0xFF485563), Color(0xFF29323c)),
    listOf(Color(0xFF834d9b), Color(0xFFd04ed6)),
    listOf(Color(0xFF0099f7), Color(0xFFf11712)),
    listOf(Color(0xFFe52d27), Color(0xFFb31217)),
    listOf(Color(0xFF1D2B64), Color(0xFFF8CDDA)),
    listOf(Color(0xFF000000), Color(0xFF434343)),
    listOf(Color(0xFF3a7bd5), Color(0xFF00d2ff)),
    listOf(Color(0xFF673AB7), Color(0xFF512DA8)),
    listOf(Color(0xFFE91E63), Color(0xFFC2185B)),
    listOf(Color(0xFFFF5722), Color(0xFFE64A19)),
    listOf(Color(0xFF4CAF50), Color(0xFF388E3C)),
    listOf(Color(0xFF00BCD4), Color(0xFF0097A7)),
    listOf(Color(0xFFFFEB3B), Color(0xFFFBC02D)),
    listOf(Color(0xFF2196F3), Color(0xFF1976D2)),
    listOf(Color(0xFF607D8B), Color(0xFF455A64)),
    listOf(Color(0xFF795548), Color(0xFF5D4037)),
    listOf(Color(0xFF9E9E9E), Color(0xFF616161)),
    listOf(Color(0xFFFF9800), Color(0xFFF57C00)),
    listOf(Color(0xFF004D40), Color(0xFF00796B)),
    listOf(Color(0xFF303F9F), Color(0xFF3F51B5)),
    listOf(Color(0xFF827717), Color(0xFFAFB42B)),
    listOf(Color(0xFFE65100), Color(0xFFEF6C00)),
    listOf(Color(0xFFBF360C), Color(0xFFD84315)),
    listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)),
    listOf(Color(0xFF01579B), Color(0xFF0277BD)),
    listOf(Color(0xFF4A148C), Color(0xFF6A1B9A)),
    listOf(Color(0xFF880E4F), Color(0xFFAD1457)),
    listOf(Color(0xFFB71C1C), Color(0xFFC62828)),
    listOf(Color(0xFFF44336), Color(0xFFE57373)),
    listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)),
    listOf(Color(0xFF3F51B5), Color(0xFF7986CB)),
    listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
    listOf(Color(0xFF03A9F4), Color(0xFF4FC3F7)),
    listOf(Color(0xFF00BCD4), Color(0xFF4DD0E1)),
    listOf(Color(0xFF009688), Color(0xFF4DB6AC)),
    listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
    listOf(Color(0xFF8BC34A), Color(0xFFAED581)),
    listOf(Color(0xFFCDDC39), Color(0xFFDCE775)),
    listOf(Color(0xFFFFEB3B), Color(0xFFFFF176)),
    listOf(Color(0xFFFFC107), Color(0xFFFFD54F)),
    listOf(Color(0xFFFF9800), Color(0xFFFFB74D)),
    listOf(Color(0xFFFF5722), Color(0xFFFF8A65))
)

enum class CaptureAction {
    SHARE, DOWNLOAD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen() {
    var shayariText by remember { mutableStateOf("Dream big, work hard, stay consistent.") }
    var textColor by remember { mutableStateOf(Color.White) }
    var fontSize by remember { mutableStateOf(24f) }
    var fontFamily by remember { mutableStateOf<FontFamily>(FontFamily.Default) }
    var selectedImage by remember { mutableStateOf<Any?>(null) }
    var selectedAbstractIndex by remember { mutableStateOf<Int?>(0) }
    val viewModel: ExploreViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val captureController = rememberCaptureController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var captureAction by remember { mutableStateOf<CaptureAction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Quote",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = {
                        offsetX = 0f
                        offsetY = 0f
                    }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        captureAction = CaptureAction.SHARE
                        coroutineScope.launch { captureController.capture() }
                    }) {
                        Icon(Icons.Rounded.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        captureAction = CaptureAction.DOWNLOAD
                        coroutineScope.launch { captureController.capture() }
                    }) {
                        Icon(Icons.Rounded.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Preview Area - Takes flexible space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Capturable(
                    controller = captureController,
                    onCaptured = { bitmap, _ ->
                        bitmap?.let {
                            when (captureAction) {
                                CaptureAction.SHARE -> shareBitmap(context, it.asAndroidBitmap())
                                CaptureAction.DOWNLOAD -> saveBitmapToGallery(context, it.asAndroidBitmap())
                                null -> {}
                            }
                        }
                    }
                ) {
                    val abstractModifier = if (selectedAbstractIndex != null) {
                        val index = selectedAbstractIndex!!
                        val palette = colorPalettes[index % colorPalettes.size]
                        val gradientBrush = Brush.radialGradient(colors = palette, center = Offset.Zero, radius = 2000f)
                        val patternColor = palette.last().copy(alpha = 0.12f)
                        Modifier.drawWithContent {
                            drawRect(brush = gradientBrush)
                            val patternType = index % 5
                            when (patternType) {
                                0 -> { // Diagonal lines
                                    val lineSpacing = 80f
                                    var y = -200f
                                    while (y < size.height + 400f) {
                                        drawLine(color = patternColor, start = Offset(-200f, y), end = Offset(size.width + 200f, y - 200f), strokeWidth = 1.dp.toPx())
                                        y += lineSpacing
                                    }
                                }
                                1 -> { // Dots grid
                                    val dotSpacing = 60f
                                    for (x in 0..(size.width / dotSpacing).toInt()) {
                                        for (y in 0..(size.height / dotSpacing).toInt()) {
                                            drawCircle(color = patternColor, radius = 2.dp.toPx(), center = Offset(x * dotSpacing, y * dotSpacing))
                                        }
                                    }
                                }
                                2 -> { // Horizontal lines
                                    val lineSpacing = 60f
                                    var y = 0f
                                    while (y < size.height) {
                                        drawLine(color = patternColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1.dp.toPx())
                                        y += lineSpacing
                                    }
                                }
                                3 -> { // Vertical lines
                                    val lineSpacing = 60f
                                    var x = 0f
                                    while (x < size.width) {
                                        drawLine(color = patternColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1.dp.toPx())
                                        x += lineSpacing
                                    }
                                }
                                4 -> { // Crosshatch
                                    val lineSpacing = 100f
                                    var i = -size.width
                                    while (i < size.width + size.height) {
                                        drawLine(color = patternColor, start = Offset(i, 0f), end = Offset(i + size.height, size.height), strokeWidth = 0.5.dp.toPx())
                                        drawLine(color = patternColor, start = Offset(i, size.height), end = Offset(i + size.height, 0f), strokeWidth = 0.5.dp.toPx())
                                        i += lineSpacing
                                    }
                                }
                            }
                            drawContent()
                        }
                    } else Modifier

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.85f),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().then(abstractModifier)) {
                            selectedImage?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            offsetX += dragAmount.x
                                            offsetY += dragAmount.y
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                TextField(
                                    value = shayariText,
                                    onValueChange = { shayariText = it },
                                    modifier = Modifier
                                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                        .padding(24.dp),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        color = textColor,
                                        fontSize = fontSize.sp,
                                        fontFamily = fontFamily,
                                        textAlign = TextAlign.Center,
                                        lineHeight = (fontSize * 1.3).sp
                                    ),
                                    placeholder = {
                                        Text(
                                            "Type your quote here...",
                                            color = textColor.copy(alpha = 0.5f),
                                            fontSize = fontSize.sp,
                                            fontFamily = fontFamily,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Editor Bottom Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                EditorActionBar(
                    viewModel = viewModel,
                    onImageSelected = {
                        selectedImage = it
                        selectedAbstractIndex = null
                    },
                    onAbstractSelected = {
                        selectedAbstractIndex = it
                        selectedImage = null
                    },
                    onShayariSelected = { shayariText = it.text },
                    onColorSelected = { textColor = it },
                    fontSize = fontSize,
                    onFontSizeChanged = { fontSize = it },
                    onFontFamilySelected = { fontFamily = it }
                )
            }
        }
    }
}

@Composable
fun EditorActionBar(
    viewModel: ExploreViewModel,
    onImageSelected: (Any) -> Unit,
    onAbstractSelected: (Int) -> Unit,
    onShayariSelected: (ShayariEntity) -> Unit,
    onColorSelected: (Color) -> Unit,
    fontSize: Float,
    onFontSizeChanged: (Float) -> Unit,
    onFontFamilySelected: (FontFamily) -> Unit
) {
    var mainTab by remember { mutableStateOf(0) } // 0 for Background, 1 for Text
    var textSubTab by remember { mutableStateOf(0) } // 0 for Library, 1 for Style

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onImageSelected(it) }
        }
    )

    val context = LocalContext.current
    val imageResources = remember {
        (1..19).map {
            context.resources.getIdentifier("bg_$it", "drawable", context.packageName)
        }
    }
    val shayaris = viewModel.shayaris.collectAsLazyPagingItems()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val textColors = listOf(
        Color.White, Color.Black, Color(0xFFC6A75E), // Luxury Gold
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4),
        Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39),
        Color(0xFFFFEB3B), Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722),
        Color(0xFF795548), Color(0xFF9E9E9E), Color(0xFF607D8B),
        Color(0xFFFFD700), Color(0xFFC0C0C0), Color(0xFFCD7F32),
        Color(0xFFE0F7FA), Color(0xFFF1F8E9), Color(0xFFFFF3E0),
        Color(0xFF263238), Color.Companion.Black
    )

    val fontFamilies = listOf(
        "Default" to FontFamily.Default,
        "Serif" to PlayfairDisplayFontFamily,
        "Modern" to PoppinsFontFamily,
        "Hindi" to NotoSansDevanagariFontFamily,
        "Bold" to FontFamily(Font(R.font.poppins_semibold, FontWeight.Bold)),
        "Classic" to FontFamily(Font(R.font.playfairdisplay_variable, FontWeight.Normal)),
        "Light" to FontFamily(Font(R.font.poppins_regular, FontWeight.Normal))
    )

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(top = 8.dp)
    ) {
        // Main Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainTabItem(
                selected = mainTab == 0,
                icon = Icons.Rounded.Image,
                label = "Background",
                modifier = Modifier.weight(1f),
                onClick = { mainTab = 0 }
            )
            MainTabItem(
                selected = mainTab == 1,
                icon = Icons.Rounded.FormatColorText,
                label = "Typography",
                modifier = Modifier.weight(1f),
                onClick = { mainTab = 1 }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

        Box(modifier = Modifier.height(280.dp)) {
            if (mainTab == 0) {
                // Background Selection
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    SectionHeader("Images")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UploadImageButton {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        imageResources.forEach {
                            if (it != 0) {
                                BackgroundThumbnail(it) { onImageSelected(it) }
                            }
                        }
                    }

                    SectionHeader("Abstract Textures")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        colorPalettes.forEachIndexed { index, palette ->
                            AbstractThumbnail(palette) { onAbstractSelected(index) }
                        }
                    }
                }
            } else {
                // Text Editing
                Column {
                    TabRow(
                        selectedTabIndex = textSubTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[textSubTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = textSubTab == 0,
                            onClick = { textSubTab = 0 },
                            text = { Text("Library", style = MaterialTheme.typography.labelLarge) }
                        )
                        Tab(
                            selected = textSubTab == 1,
                            onClick = { textSubTab = 1 },
                            text = { Text("Style", style = MaterialTheme.typography.labelLarge) }
                        )
                    }

                    if (textSubTab == 0) {
                        Column {
                            CategoryChips(
                                selectedCategory = selectedCategory,
                                onCategorySelected = { category -> viewModel.selectCategory(category) }
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(count = shayaris.itemCount) { index ->
                                    val shayari = shayaris[index]
                                    shayari?.let { safeShayari ->
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onShayariSelected(safeShayari) },
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.Transparent
                                        ) {
                                            Text(
                                                text = safeShayari.text,
                                                modifier = Modifier.padding(12.dp),
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 2,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                            )
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                    }
                                }
                            }
                        }
                    } else {
                        // Style Sub-Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            StyleSection(title = "Color") {
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    textColors.forEach { color ->
                                        ColorCircle(color) { onColorSelected(color) }
                                    }
                                }
                            }

                            StyleSection(title = "Size") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.FormatSize, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                    Slider(
                                        value = fontSize,
                                        onValueChange = onFontSizeChanged,
                                        valueRange = 14f..42f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("\${fontSize.toInt()}pt", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            StyleSection(title = "Typeface") {
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    fontFamilies.forEach { (name, family) ->
                                        FontChip(name) { onFontFamilySelected(family) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainTabItem(selected: Boolean, icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = contentColor)
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = contentColor)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
fun BackgroundThumbnail(resId: Int, onClick: () -> Unit) {
    AsyncImage(
        model = resId,
        contentDescription = null,
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun AbstractThumbnail(palette: List<Color>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(palette))
            .clickable(onClick = onClick)
    )
}

@Composable
fun StyleSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
fun ColorCircle(color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick)
    )
}

@Composable
fun FontChip(name: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun UploadImageButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Rounded.AddPhotoAlternate,
                contentDescription = "Upload from Gallery",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gallery",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
