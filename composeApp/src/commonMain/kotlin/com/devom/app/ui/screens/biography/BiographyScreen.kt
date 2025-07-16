package com.devom.app.ui.screens.biography

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.devom.app.models.OptionsBottomSheetItem
import com.devom.app.models.SupportedFiles
import com.devom.app.theme.textBlackShade
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DocumentPicker
import com.devom.app.ui.components.ImageViewer
import com.devom.app.ui.components.OptionsBottomSheet
import com.devom.app.ui.components.TagInputField
import com.devom.app.ui.components.TextInputField
import com.devom.app.ui.navigation.Screens
import com.devom.app.utils.toDevomDocument
import com.devom.models.pandit.Media
import com.devom.models.pandit.UpdateBiographyInput
import com.devom.utils.getThumbnail
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.Update
import devom_app.composeapp.generated.resources.expertise
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_video_camera
import devom_app.composeapp.generated.resources.languages_spoken
import devom_app.composeapp.generated.resources.media_galley
import devom_app.composeapp.generated.resources.placeholder
import devom_app.composeapp.generated.resources.placeholder_video
import devom_app.composeapp.generated.resources.preferred_rituals
import devom_app.composeapp.generated.resources.years_of_experience

@Composable
fun BiographyScreen(navController: NavController) {
    val viewModel = viewModel {
        BiographyViewModel()
    }
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Biography",
            onNavigationIconClick = { navController.popBackStack() }
        )
        BiographyScreenScreenContent(viewModel, navController)
    }

    LaunchedEffect(Unit) {
        viewModel.getBiography()
    }
}

@Composable
fun ColumnScope.BiographyScreenScreenContent(
    viewModel: BiographyViewModel,
    navController: NavController,
) {
    val biographyInput = remember {
        mutableStateOf(UpdateBiographyInput())
    }
    val biography = viewModel.biography.collectAsState()
    val showSheet = remember { mutableStateOf(false) }
    var selectedMedia = remember { mutableStateOf<Media?>(null) }
    val viewImage = remember { mutableStateOf(false) }
    val options = listOf(
        OptionsBottomSheetItem(title = "View"),
        OptionsBottomSheetItem(title = "Delete")
    )
    val focus = LocalFocusManager.current

    val isButtonEnable = remember { mutableStateOf(false) }
    LazyColumn(modifier = Modifier.weight(1f)) {
        item {
            BiographyForm(
                viewModel = viewModel, biographyInput = biographyInput,
                onButtonStateChanged = {
                    isButtonEnable.value = it
                }, onRitualsClicked = {
                    navController.navigate(Screens.Rituals.path)
                }
            )
        }

        item {
            DocumentPicker(
                addIconOnly = biography.value?.media.isNullOrEmpty().not(),
                title = stringResource(Res.string.media_galley),
                allowedDocs = listOf(SupportedFiles.IMAGE, SupportedFiles.VIDEO)
            ) { platformFile, supportedFiles ->
                viewModel.uploadDocument(
                    platformFile, supportedFiles
                )
            }
        }

        item {
            LazyVerticalGrid(
                modifier = Modifier.heightIn(max = 3000.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(biography.value?.media.orEmpty()) {
                    MediaItem(model = it.documentUrl, it.documentType) {
                        showSheet.value = true
                        selectedMedia.value = it
                    }
                }
            }
        }
    }

    ButtonPrimary(
        enabled = isButtonEnable.value,
        buttonText = stringResource(Res.string.Update),
        modifier = Modifier.navigationBarsPadding().padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth().height(48.dp),
        onClick = {
            focus.clearFocus()
            viewModel.updateBiography(biographyInput.value)
        }
    )

    OptionsBottomSheet(
        showSheet = showSheet.value,
        options = options,
        onDismiss = { showSheet.value = false },
        onSelect = {
            if (it.title.lowercase() == "delete" && selectedMedia.value != null) {
                viewModel.removeDocument(selectedMedia.value?.documentId.toString())
                showSheet.value = false
            } else {
                viewImage.value = true
            }
            showSheet.value = false
        }
    )
    ImageViewer(viewImage, selectedMedia)
}

@Composable
fun MediaItem(model: String, type: String, onClick: () -> Unit = {}) {
    val videoIcon = remember { mutableStateOf(false) }
    val placeholder = painterResource(
        if (type.lowercase() == SupportedFiles.VIDEO.type)
            Res.drawable.placeholder_video
        else
            Res.drawable.placeholder
    )

    val thumbnail = remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(model) {
        thumbnail.value = model.toDevomDocument().getThumbnail()
    }

    LaunchedEffect(thumbnail.value) {
        Logger.d("thumbnail ${thumbnail.value.toString()}")
    }

    Box(
        modifier = Modifier.height(124.dp).clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        key(thumbnail.value) {
            val modifier = Modifier.fillMaxSize()
            val contentScale = ContentScale.Crop

            if (type.lowercase() == SupportedFiles.VIDEO.type && thumbnail.value != null) {
                thumbnail.value?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "",
                        modifier = modifier,
                        contentScale = contentScale
                    )
                    videoIcon.value = true
                }
            } else AsyncImage(
                model = model.toDevomDocument(),
                contentDescription = "",
                modifier = modifier,
                contentScale = contentScale,
                placeholder = placeholder,
                error = placeholder,
                onSuccess = {
                    videoIcon.value = type.lowercase() == SupportedFiles.VIDEO.type
                },
                onError = {
                    Logger.d("KermitError $it")
                }
            )
        }

        if (videoIcon.value) Image(
            painter = painterResource(Res.drawable.ic_video_camera),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun BiographyForm(
    viewModel: BiographyViewModel,
    biographyInput: MutableState<UpdateBiographyInput>,
    onButtonStateChanged: (Boolean) -> Unit = {},
    onRitualsClicked: () -> Unit = {},
) {
    val biography = viewModel.biography.collectAsState()
    val checkButtonEnable = {
        biographyInput.value.experienceYears.isNotEmpty() &&
                biographyInput.value.languages.isNotEmpty() &&
                biographyInput.value.specialty.isNotEmpty()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
    ) {
        TextInputField(
            initialValue = (biography.value?.experienceYears ?: 0).toString(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = stringResource(Res.string.years_of_experience)
        ) {
            biographyInput.value = biographyInput.value.copy(experienceYears = it)
            onButtonStateChanged(checkButtonEnable())
        }

        TagInputField(
            initialTags = biography.value?.specialty?.trim()?.split(",").orEmpty(),
            placeholder = stringResource(Res.string.expertise),
            onTagsChanged = {
                biographyInput.value.specialty = it.joinToString()
                onButtonStateChanged(checkButtonEnable())

            }
        )

        TagInputField(
            initialTags = biography.value?.languages?.trim()?.split(",").orEmpty(),
            placeholder = stringResource(Res.string.languages_spoken),
            onTagsChanged = {
                biographyInput.value.languages = it.joinToString()
                onButtonStateChanged(checkButtonEnable())
            }
        )

        Text(
            text = stringResource(Res.string.preferred_rituals),
            modifier = Modifier.fillMaxWidth().clickable { onRitualsClicked() },
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.End,
            color = textBlackShade,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp
        )
    }
}
