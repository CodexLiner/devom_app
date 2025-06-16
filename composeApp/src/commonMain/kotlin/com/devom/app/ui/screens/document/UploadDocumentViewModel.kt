package com.devom.app.ui.screens.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devom.Project
import com.devom.app.models.SupportedFiles
import com.devom.models.auth.UserRequestResponse
import com.devom.models.document.CreateDocumentInput
import com.devom.models.document.GetDocumentResponse
import com.devom.network.getUser
import com.devom.utils.Application
import com.devom.utils.network.onResult
import com.devom.utils.network.onResultNothing
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import kotlinx.io.readByteArray

class UploadDocumentViewModel : ViewModel() {
    private val _documents = MutableStateFlow<List<GetDocumentResponse>>(emptyList())
    val documents = _documents

    init {
        getDocuments()
    }

    fun uploadDocument(platformFile: PlatformFile, supportedFiles: SupportedFiles) {
        viewModelScope.launch {
            Project.document.createDocumentUseCase.invoke(
                input = CreateDocumentInput(
                    userId = getUser().userId.toString(),
                    mimeType = "image/${platformFile.extension}",
                    documentType = supportedFiles.type,
                    description = supportedFiles.document,
                    documentName = platformFile.name,
                    file = platformFile.source().buffered().readByteArray()
                )
            ).collect {
                it.onResult {
                    getDocuments()
                    Application.showToast("Document uploaded successfully")
                }
            }
        }
    }

    fun removeDocument(documentId: String) {
        viewModelScope.launch {
            Project.document.removeDocumentUseCase.invoke(documentId).collect {
                it.onResultNothing {
                    getDocuments()
                    Application.showToast("Document removed successfully")
                }
            }
        }
    }


    fun getDocuments() {
        viewModelScope.launch {
            Project.document.getDocumentsUseCase.invoke().collect {
                it.onResult {
                    _documents.value = it.data
                }
            }
        }
    }
}