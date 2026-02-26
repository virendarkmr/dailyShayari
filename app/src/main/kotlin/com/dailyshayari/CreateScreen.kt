package com.dailyshayari

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FormatColorText
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.ui.explore.ExploreViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen() {
    var shayariText by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Int?>(null) }
    var selectedAbstractIndex by remember { mutableStateOf<Int?>(null) }
    val viewModel: ExploreViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

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
                    IconButton(onClick = { /* TODO: Reset action */ }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { /* TODO: Download action */ }) {
                        Icon(Icons.Rounded.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
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
                shayariText = shayariText,
                onShayariChanged = { shayariText = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
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
                    .height(400.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures {
                                    change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                    ) {
                        TextField(
                            value = shayariText,
                            onValueChange = { shayariText = it },
                            modifier = Modifier.padding(16.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            placeholder = { Text("Start typing your shayari...", color = Color.White.copy(alpha = 0.6f)) },
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
}

@Composable
fun EditorActionBar(
    viewModel: ExploreViewModel,
    onImageSelected: (Int) -> Unit,
    onAbstractSelected: (Int) -> Unit,
    onShayariSelected: (ShayariEntity) -> Unit,
    shayariText: String,
    onShayariChanged: (String) -> Unit
) {
    val context = LocalContext.current
    var showBackgrounds by remember { mutableStateOf(true) }
    val imageResources = remember {
        (1..19).map {
            context.resources.getIdentifier("bg_$it", "drawable", context.packageName)
        }
    }
    val shayaris = viewModel.shayaris.collectAsLazyPagingItems()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EditorActionItem(icon = Icons.Rounded.Image, label = "Background", onClick = { showBackgrounds = true })
            EditorActionItem(icon = Icons.Rounded.FormatColorText, label = "Text", onClick = { showBackgrounds = false })
        }
        if (showBackgrounds) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Backgrounds",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    imageResources.forEach {
                        if (it != 0) {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onImageSelected(it) },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Abstract",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    colorPalettes.forEachIndexed { index, palette ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(palette))
                                .clickable { onAbstractSelected(index) }
                        )
                    }
                }
            }
        } else {
            Column {
                TextField(
                    value = shayariText,
                    onValueChange = onShayariChanged,
                    placeholder = { Text("Type your own shayari") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                CategoryChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(count = shayaris.itemCount) { index ->
                        val shayari = shayaris[index]
                        shayari?.let { safeShayari ->
                            Text(
                                text = safeShayari.text,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onShayariSelected(safeShayari) }
                                    .padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditorActionItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}
