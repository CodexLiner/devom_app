package com.devom.app.ui.screens.addbankaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devom.app.models.SupportedFiles
import com.devom.app.theme.backgroundColor
import com.devom.app.ui.components.AppBar
import com.devom.app.ui.components.ButtonPrimary
import com.devom.app.ui.components.DocumentPicker
import com.devom.app.ui.components.TextInputField
import com.devom.utils.Application
import io.github.vinceglb.filekit.source
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import devom_app.composeapp.generated.resources.Account_Number
import devom_app.composeapp.generated.resources.Add_Bank_Account
import devom_app.composeapp.generated.resources.Bank_Name
import devom_app.composeapp.generated.resources.Ifsc_code
import devom_app.composeapp.generated.resources.Res
import devom_app.composeapp.generated.resources.Update
import devom_app.composeapp.generated.resources.enter_full_name
import devom_app.composeapp.generated.resources.ic_arrow_left

@Composable
fun BankAccountScreen(navController: NavController) {
    val viewModel: BankAccountViewModel = viewModel {
        BankAccountViewModel()
    }
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AppBar(
            navigationIcon = painterResource(Res.drawable.ic_arrow_left),
            title = stringResource(Res.string.Add_Bank_Account),
            onNavigationIconClick = { navController.popBackStack() }
        )
        BankAccountScreenContent(navController, viewModel)
    }
}

@Composable
fun ColumnScope.BankAccountScreenContent(
    navController: NavController,
    viewModel: BankAccountViewModel,
) {
    val bankAccount by viewModel.bankAccount.collectAsState()
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextInputField(
                initialValue = bankAccount.accountHolderName, keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ), placeholder = stringResource(Res.string.enter_full_name)
            ) {
                bankAccount.accountHolderName = it
            }
        }

        item {
            TextInputField(
                initialValue = bankAccount.accountNumber, keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ), placeholder = stringResource(Res.string.Account_Number)
            ) {
                bankAccount.accountNumber = it
            }
        }

        item {
            TextInputField(
                initialValue = bankAccount.ifscCode,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ), placeholder = stringResource(Res.string.Ifsc_code)
            ) {
                bankAccount.ifscCode = it
            }
        }

        item {
            TextInputField(
                initialValue = bankAccount.bankName, keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ), placeholder = stringResource(Res.string.Bank_Name)
            ) {
                bankAccount.bankName = it
            }
        }

        item {
            DocumentPicker(
                modifier = Modifier.padding(vertical = 8.dp),
                allowedDocs = listOf(SupportedFiles.IMAGE),
                title = "Select Bank Proof"
            ) { file, type ->
                bankAccount.file = file.source().buffered().readByteArray()
            }
        }

    }
    ButtonPrimary(
        buttonText = stringResource(Res.string.Update),
        modifier = Modifier.navigationBarsPadding()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp).fillMaxWidth().height(58.dp),
        onClick = {
            val isValid = bankAccount.isValid()
            if (isValid.first) {
                viewModel.updateBankAccount(bankAccount)
                navController.popBackStack()
            } else Application.showToast(isValid.second.orEmpty())
        }
    )
}