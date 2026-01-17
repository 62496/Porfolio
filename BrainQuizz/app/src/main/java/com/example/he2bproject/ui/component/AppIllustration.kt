package com.example.he2bproject.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AppIllustration(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    height: Int = 200
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp),
        contentScale = ContentScale.Fit
    )
}
