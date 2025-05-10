package com.ayaan.abhaya.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SosBottomBar(
    onSosClick: () -> Unit,
    onAnonymousSosClick: () -> Unit,
    isEnabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Regular SOS Button
            SosButton(
                onClick = onSosClick,
                icon = Icons.Default.Person,
                text = "SOS",
                modifier = Modifier.weight(1f),
                isEnabled = isEnabled
            )

            // Anonymous SOS Button
            SosButton(
                onClick = onAnonymousSosClick,
                icon = Icons.Default.Warning,
                text = "Anonymous SOS",
                modifier = Modifier.weight(1f),
                isEnabled = isEnabled,
                isAnonymous = true
            )
        }
    }
}

@Composable
private fun SosButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isAnonymous: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isAnonymous)
                MaterialTheme.colorScheme.secondary
            else
                MaterialTheme.colorScheme.error
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
        }
    }
}