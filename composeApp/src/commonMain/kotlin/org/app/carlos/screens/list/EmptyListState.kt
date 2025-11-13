package org.app.carlos.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.empty_default
import carlosapp.composeapp.generated.resources.empty_marine
import carlosapp.composeapp.generated.resources.empty_midnight
import carlosapp.composeapp.generated.resources.empty_solaris
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmptyListState(
    selectedThemeId: String?,
    onClearFilters: () -> Unit
) {
    val imageRes = when (selectedThemeId) {
        "default" -> Res.drawable.empty_default
        "midnight" -> Res.drawable.empty_midnight
        "solaris" -> Res.drawable.empty_solaris
        "marine" -> Res.drawable.empty_marine
        else -> Res.drawable.empty_default
    }

    val buttonColor = when (selectedThemeId) {
        "default" -> Color(0xFFFDDB2C)
        "midnight" -> Color(0xFFB421FF)
        "solaris" -> Color(0xFFFFC654)
        "marine" -> Color(0xFF37FFE6)
        else -> Color(0xFFFDDB2C)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Fit
            )
        }

        Button(
            onClick = onClearFilters,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.BottomCenter)
                .height(52.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                ),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Clear filters",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}