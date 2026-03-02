package com.dailyshayari

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.ui.theme.*
import com.dailyshayari.util.copyTextToClipboard
import com.dailyshayari.util.isHindi
import com.dailyshayari.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: SearchViewModel = viewModel(factory = ViewModelFactory(context))
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val luxuryText = LocalLuxuryTextColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Search Shayari", 
                            style = MaterialTheme.typography.headlineMedium,
                            color = luxuryText.appTitle
                        ) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = luxuryText.appTitle)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetSearch() }) {
                        Icon(
                            imageVector = Icons.Rounded.RestartAlt, 
                            contentDescription = "Reset", 
                            tint = luxuryText.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by keywords...", color = luxuryText.secondary.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = luxuryText.secondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = luxuryText.secondary)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = luxuryText.secondary.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.performSearch()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }),
                singleLine = true
            )

            // Search Button
            Button(
                onClick = {
                    viewModel.performSearch()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Text("Search", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            // Categories
            Text("Browse Categories", style = MaterialTheme.typography.titleMedium, color = luxuryText.body)
            val categories = listOf("All", "Love", "Sad", "Motivation", "Friendship", "Gita Lines", "Quotes")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategorySelect(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = Color.Black,
                            labelColor = luxuryText.secondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = luxuryText.secondary.copy(alpha = 0.3f),
                            selectedBorderColor = GoldPrimary
                        )
                    )
                }
            }

            // Recent Searches
            if (recentSearches.isNotEmpty()) {
                Text("Recent Searches", style = MaterialTheme.typography.titleMedium, color = luxuryText.body)
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentSearches.forEach { search ->
                        Surface(
                            onClick = { viewModel.onRecentSearchClick(search) },
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(14.dp), tint = luxuryText.secondary)
                                Text(search, style = MaterialTheme.typography.bodySmall, color = luxuryText.secondary)
                            }
                        }
                    }
                }
            }

            // Search Results Title
            Text(
                if (searchResults.isEmpty() && searchQuery.isNotEmpty() && !isSearching) "No results found" else "Results",
                style = MaterialTheme.typography.titleMedium,
                color = luxuryText.body
            )

            // Results List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(searchResults) { shayari ->
                    SearchShayariCard(shayari)
                }
            }
        }
    }
}

@Composable
fun SearchShayariCard(shayari: ShayariEntity) {
    val context = LocalContext.current
    val luxuryText = LocalLuxuryTextColors.current
    val palette = remember(shayari.id) { 
        listOf(
            listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF)),
            listOf(Color(0xFF4B79A1), Color(0xFF283E51)),
            listOf(Color(0xFFCC2B5E), Color(0xFF753A88)),
            listOf(Color(0xFF134E5E), Color(0xFF71B280)),
            listOf(Color(0xFF93291E), Color(0xFFED213A))
        ).random() 
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(palette))
            .padding(1.dp) // border effect
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shayari.category.uppercase(),
                    color = GoldPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
                
                // Copy text feature
                IconButton(
                    onClick = { copyTextToClipboard(context, shayari.text) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Copy",
                        tint = luxuryText.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            val isHindi = isHindi(shayari.text)
            val fontFamily = if (isHindi) NotoSansDevanagariFontFamily else PlayfairDisplayFontFamily
            
            Text(
                text = shayari.text,
                color = luxuryText.body,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = fontFamily,
                    lineHeight = 24.sp
                )
            )
        }
    }
}
