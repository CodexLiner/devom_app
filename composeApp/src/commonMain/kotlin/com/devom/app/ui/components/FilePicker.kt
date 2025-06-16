package com.devom.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devom.app.models.SupportedFiles
import com.devom.app.theme.primaryColor
import com.devom.app.theme.text_style_h3
import com.devom.app.theme.text_style_lead_body_1
import com.devom.app.theme.whiteColor
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.choose_file

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilePickerBottomSheetHost(
    showSheet: Boolean,
    onDismissRequest: () -> Unit,
    allowedDocs: List<SupportedFiles> = listOf(
        SupportedFiles.AADHAAR_CARD,
        SupportedFiles.PAN_CARD,
        SupportedFiles.CERTIFICATE
    ),
    onFilePicked: (PlatformFile, SupportedFiles) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSheet) {
        ModalBottomSheet(
            containerColor = whiteColor,
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(Res.string.choose_file), style = text_style_h3)
                Spacer(modifier = Modifier.height(12.dp))

                allowedDocs.forEach { doc ->
                    ButtonPrimary(
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = primaryColor,
                            contentColor = whiteColor
                        ),
                        fontStyle = text_style_lead_body_1,
                        buttonText = "Pick ${doc.document.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp).height(48.dp),
                        onClick = {
                            coroutineScope.launch {
                                val type = when {
                                    doc.mimeTypes.any { it.startsWith("image/") } -> FileKitType.Image
                                    doc.mimeTypes.any { it.startsWith("video/") } -> FileKitType.Video
                                    else -> FileKitType.File(doc.mimeTypes.toSet())
                                }

                                val file = FileKit.openFilePicker(type = type)
                                file?.let {
                                    onFilePicked(it, doc)
                                    onDismissRequest()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
