package org.app.carlos.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.ic_app_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun AboutScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF152470))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = painterResource(Res.drawable.ic_app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(top = 32.dp, bottom = 16.dp)
            )

            Text(
                text = "About CarloS App",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Offline car expense tracker.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Version X.Y.Z",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.8f)
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: переход или диалог */ }
                    .padding(vertical = 12.dp)
            )

            Divider(color = Color.White.copy(alpha = 0.3f))

            Text(
                text = "Terms of Use",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: переход или диалог */ }
                    .padding(vertical = 12.dp)
            )
        }
    }
}
