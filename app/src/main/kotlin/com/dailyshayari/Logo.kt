package com.dailyshayari

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val logoResId = remember {
        context.resources.getIdentifier("ic_launcher_foreground", "mipmap", context.packageName)
    }

    if (logoResId != 0) {
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "Logo",
            modifier = modifier
                .size(80.dp)
                .padding(8.dp)
        )
    }
}
