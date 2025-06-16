package com.devom.app.ui.screens.document

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.models.OptionsBottomSheetItem
import com.devom.app.models.SupportedFiles
import com.devom.app.theme.greenColor
import com.devom.app.theme.inputColor
import com.devom.app.theme.text_style_lead_text
import com.devom.app.theme.warningColor
import com.devom.app.theme.whiteColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.AsyncImage
import com.devom.app.ui.components.DocumentPicker
import com.devom.app.ui.components.ImageViewer
import com.devom.app.ui.components.OptionsBottomSheet
import com.devom.app.utils.toDevomDocument
import com.devom.models.document.GetDocumentResponse
import com.devom.models.pandit.Media
import org.jetbrains.compose.resources.painterResource
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.ic_arrow_left
import devom_app.composeapp.generated.resources.ic_document_aadhaar
import devom_app.composeapp.generated.resources.ic_document_pan

@Composable
fun UploadDocumentScreen(navController: NavController) {
    val viewModel = viewModel {
        UploadDocumentViewModel()
    }
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = "Documents",
            onNavigationIconClick = { navController.popBackStack() })
        UploadDocumentScreenContent(viewModel)
    }
}

@Composable
private fun UploadDocumentScreenContent(viewModel: UploadDocumentViewModel) {
    val documents = viewModel.documents.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDocuments()
    }
    val showSheet = remember { mutableStateOf(false) }
    var selectedMedia  = remember { mutableStateOf<Media?>(null) }
    val viewImage = remember { mutableStateOf(false) }
    val options = listOf(
        OptionsBottomSheetItem(title = "View"),
        OptionsBottomSheetItem(title = "Delete")
    )

    Column (verticalArrangement = Arrangement.spacedBy(16.dp)){
        LazyColumn(contentPadding = PaddingValues(vertical = 16.dp) , verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(documents.value) {
                DocumentItem(modifier = Modifier.padding(horizontal = 16.dp).clickable {
                    showSheet.value = true
                    selectedMedia.value = Media(documentId = it.documentId.toIntOrNull() ?: 0, documentUrl = it.documentUrl, documentType = it.documentType)
                }, document = it)
            }
            item {
                DocumentPicker(title = "Upload Documents") { platformFile, supportedFiles ->
                    viewModel.uploadDocument(platformFile, supportedFiles)
                }
            }
        }
    }

    OptionsBottomSheet(
        showSheet = showSheet.value,
        options = options,
        onDismiss = { showSheet.value = false },
        onSelect = {
            if (it.title.lowercase() == "delete" && selectedMedia.value != null) {
                viewModel.removeDocument(selectedMedia.value?.documentId.toString())
                showSheet.value = false
            } else viewImage.value = true
            showSheet.value = false
        })

    ImageViewer(viewImage , selectedMedia)

}

@Composable
fun DocumentItem(
    modifier: Modifier = Modifier,
    document: GetDocumentResponse
) {
    Column(
        modifier = modifier
            .background(Color(0xFFF5F9FF), shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
       Box(Modifier.fillMaxWidth().padding(16.dp)) {
           Row(verticalAlignment = Alignment.CenterVertically) {
               DocumentIcon(document)

               DocumentTexts(
                   documentName = getDocumentName(document.documentType),
                   documentId = document.documentId
               )
           }

           VerificationChip(
               status = document.verificationStatus,
               modifier = Modifier.align(Alignment.TopEnd)
           )
       }
        AsyncImage(
            contentScale = ContentScale.Crop,
            model = document.documentUrl.toDevomDocument(),
            modifier = Modifier.fillMaxWidth().heightIn(max = 100.dp)
        )
    }
}


@Composable
private fun DocumentIcon(document: GetDocumentResponse) {
    val icon = when(document.documentType) {
        SupportedFiles.AADHAAR_CARD.type -> painterResource(Res.drawable.ic_document_aadhaar )
        SupportedFiles.PAN_CARD.type -> painterResource(Res.drawable.ic_document_pan)
        else -> painterResource(Res.drawable.ic_document_pan)
    }
    Icon(
        painter = icon,
        contentDescription = null,
        tint = Color.Gray,
        modifier = Modifier
            .padding(end = 12.dp)
            .size(32.dp)
    )
}

@Composable
private fun DocumentTexts(documentName: String, documentId: String) {
    Column {
        Text(
            text = documentName,
            style = text_style_lead_text,
            color = inputColor
        )
//        Text(
//            modifier = Modifier.padding(top = 4.dp),
//            text = documentId,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black
//        )
    }
}

@Composable
private fun VerificationChip(status: String, modifier: Modifier = Modifier) {
    val chipColor = when (status.lowercase()) {
        "verified" -> greenColor
        "pending" -> warningColor
        else -> warningColor
    }

    Box(
        modifier = modifier
            .background(
                color = chipColor,
                shape = RoundedCornerShape(bottomStart = 12.dp, topEnd = 12.dp)
            )
    ) {
        Text(
            text = status,
            color = whiteColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontWeight = FontWeight.W600,
            fontSize = 12.sp
        )
    }
}

private fun getDocumentName(type: String): String {
    return when (type) {
        SupportedFiles.AADHAAR_CARD.type -> SupportedFiles.AADHAAR_CARD.document
        SupportedFiles.PAN_CARD.type -> SupportedFiles.PAN_CARD.document
        SupportedFiles.CERTIFICATE.type -> SupportedFiles.CERTIFICATE.document
        else -> SupportedFiles.OTHER.document
    }
}

