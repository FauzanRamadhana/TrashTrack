package com.greentech.monitorsampah.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.screen.cinzelFont
import com.greentech.monitorsampah.ui.theme.Green
import com.greentech.monitorsampah.ui.theme.Grey
import com.greentech.monitorsampah.ui.theme.GreyShadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashTrackTopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier
            .border(
                2.dp,
                brush = Brush.verticalGradient(listOf(Color.Transparent, GreyShadow)),
                shape = RoundedCornerShape(10.dp)
            ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo TrashTrack",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 5.dp)
                )
                Row {
                    Text(
                        text = "Trash",
                        fontSize = 30.sp,
                        fontFamily = cinzelFont,
                        color = Green,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Track",
                        fontSize = 30.sp,
                        fontFamily = cinzelFont,
                        color = Grey,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }
    )
}
