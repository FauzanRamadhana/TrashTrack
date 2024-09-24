package com.greentech.monitorsampah.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.ui.theme.Green
import com.greentech.monitorsampah.ui.theme.Grey
import com.greentech.monitorsampah.ui.theme.MonitorSampahTheme
import kotlinx.coroutines.delay

val cinzelFont = FontFamily(
    Font(R.font.cinzel)
)

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    var startNavigation by remember { mutableStateOf(false) }
    val alpha = remember { Animatable(0f) }
    val slideUp = remember { Animatable(100f) }

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = LinearEasing)
        )
        slideUp.animateTo(
            targetValue = 0f,
            animationSpec = tween(1000, easing = LinearOutSlowInEasing)
        )
        delay(1000)
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(500, easing = LinearEasing)
        )
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha.value)
                .graphicsLayer {
                    translationY = slideUp.value
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(screenWidth * 0.5f),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val textSize = with(density) { minOf(screenWidth * 0.08f, 50.dp).toSp() }
                Text(
                    text = "Trash",
                    style = TextStyle(
                        fontSize = textSize,
                        fontFamily = cinzelFont,
                        fontWeight = FontWeight.ExtraBold,
                        color = Green
                    )
                )
                Text(
                    text = "Track",
                    style = TextStyle(
                        fontSize = textSize,
                        fontFamily = cinzelFont,
                        fontWeight = FontWeight.Black,
                        color = Grey
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MonitorSampahTheme {
        SplashScreen(navController = rememberNavController())
    }
}