package com.greentech.monitorsampah.screen

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )
        Row {
            Text(
                text = "Trash",
                fontSize = 50.sp,
                fontFamily = cinzelFont,
                color = Green,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Track",
                fontSize = 50.sp,
                fontFamily = cinzelFont,
                color = Grey,
                fontWeight = FontWeight.Black,
            )
        }
        val scale = remember {
            androidx.compose.animation.core.Animatable(0.0f)
        }

        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(800, easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
            )
            delay(2000)
            navController.popBackStack()
            navController.navigate("home")
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun SplashScreenPreview() {
    MonitorSampahTheme {
//        SplashScreen(navController = navController)
    }
}
