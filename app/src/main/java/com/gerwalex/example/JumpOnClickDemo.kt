package com.gerwalex.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gerwalex.library.modifier.jumpOnClick
import com.gerwalex.library.modifier.rememberJumpAnimationState

@Composable
fun JumpOnClickDemo(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🍿",
            fontSize = 96.sp,
            modifier = modifier
                .jumpOnClick(rememberJumpAnimationState(onClick = {}))
        )
        Image(
            painter = painterResource(com.gerwalex.library.R.drawable.demo_image),
            contentDescription = null,
            modifier = modifier
                .size(96.dp)
                .jumpOnClick(rememberJumpAnimationState(onClick = {}))
        )
    }
}
