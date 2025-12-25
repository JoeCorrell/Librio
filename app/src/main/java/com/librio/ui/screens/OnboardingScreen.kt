package com.librio.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.librio.ui.theme.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * First-time launch onboarding screen
 * Guides users to set up their profile name and optionally a profile picture
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    currentProfile: UserProfile?,
    onRenameProfile: (UserProfile, String) -> Unit,
    onSetProfilePicture: (UserProfile, String?) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = currentPalette()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val shape16 = cornerRadius(16.dp)
    val shape24 = cornerRadius(24.dp)

    // Profile name input
    var profileName by remember { mutableStateOf(currentProfile?.name ?: "") }
    var hasEditedName by remember { mutableStateOf(false) }

    // Profile picture
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageUri by remember { mutableStateOf<String?>(currentProfile?.profilePicture) }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it.toString()
            currentProfile?.let { profile ->
                onSetProfilePicture(profile, it.toString())
            }
        }
    }

    // Load profile picture bitmap
    LaunchedEffect(selectedImageUri) {
        if (selectedImageUri != null) {
            withContext(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(Uri.parse(selectedImageUri))
                    profileBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                } catch (e: Exception) {
                    profileBitmap = null
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        palette.shade11,
                        palette.shade10,
                        palette.shade9
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App icon/logo area with gradient background
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                palette.shade3,
                                palette.shade4,
                                palette.shade5
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Audiobook,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Welcome text
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.titleLarge,
                color = palette.shade3
            )

            Text(
                text = "LIBRIO",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = palette.shade1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your personal media library",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.shade4
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature highlights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureChip(
                    icon = AppIcons.Audiobook,
                    label = "Audiobooks"
                )
                FeatureChip(
                    icon = AppIcons.Book,
                    label = "E-books"
                )
                FeatureChip(
                    icon = AppIcons.Music,
                    label = "Music"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile setup card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = shape24,
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set up your profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.shade2,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Profile picture circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(palette.shade10)
                            .border(3.dp, palette.accent, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap!!.asImageBitmap(),
                                contentDescription = "Profile picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = AppIcons.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = palette.accent
                                )
                                Text(
                                    text = "Add photo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.shade4
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Name input
                    Text(
                        text = "What should we call you?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.shade3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = profileName,
                        onValueChange = {
                            profileName = it
                            hasEditedName = true
                        },
                        placeholder = {
                            Text(
                                "Enter your name",
                                color = palette.shade6
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = palette.accent,
                            unfocusedBorderColor = palette.shade8,
                            focusedTextColor = palette.shade1,
                            unfocusedTextColor = palette.shade2,
                            cursorColor = palette.accent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = shape16,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Get Started button
                    Button(
                        onClick = {
                            // Save the profile name if changed
                            if (hasEditedName && profileName.isNotBlank() && currentProfile != null) {
                                onRenameProfile(currentProfile, profileName.trim())
                            }
                            onComplete()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.accent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = shape16,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = if (profileName.isBlank() || profileName == "Default") "Skip for now" else "Get Started",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Skip hint
                    if (profileName.isBlank() || profileName == "Default") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "You can change this later in your profile",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.shade5,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun FeatureChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}
