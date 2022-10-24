package com.tylerb.dragonvalesandbox.android.view.sandbox

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tylerb.dragonvalesandbox.android.R
import com.tylerb.dragonvalesandbox.android.view.AllDragonsScreen
import com.tylerb.dragonvalesandbox.model.DragonData
import kotlinx.coroutines.launch


@Composable
fun SandboxScreen(
    modifier: Modifier = Modifier,
    viewModel: SandboxViewModel = hiltViewModel()
) {


    val uiState by viewModel.uiState.collectAsState()


    if (uiState.initLoading) {
        CircularProgressIndicator()
    } else {
        // TODO: handle loading and error in SandboxContent
        SandboxContent(
            modifier = modifier,
            dragons = uiState.dragons,
            dragonOne = uiState.dragonOne,
            dragonTwo = uiState.dragonTwo,
            onWhichDragon = viewModel::onWhichDragon,
            onDragonSelected = viewModel::onDragonSelected,
            searchQuery = uiState.searchQuery,
            onSearch = viewModel::onSearch,
            beb = uiState.beb,
            onBebChecked = viewModel::onBebChecked,
            childs = uiState.resultDragons
        )
    }






}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SandboxContent(
    modifier: Modifier = Modifier,
    dragons: List<DragonData>,
    dragonOne: String,
    dragonTwo: String,
    onWhichDragon: (SandboxViewModel.WhichDragon) -> Unit,
    onDragonSelected: (DragonData) -> Unit,
    searchQuery: String,
    onSearch: (String) -> Unit,
    beb: Boolean,
    onBebChecked: (Boolean) -> Unit,
    childs: List<DragonData>
) {

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    BackHandler(bottomSheetState.isVisible) {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }

    Scaffold { contentPadding ->
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                DragonSheet(
                    modifier = Modifier.fillMaxSize(),
                    bottomSheetState = bottomSheetState,
                    search = searchQuery,
                    onSearch = onSearch,
                    dragons = dragons,
                    onDragonSelected = {
                        onDragonSelected(it)
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }
                )
            },
            sheetBackgroundColor = MaterialTheme.colorScheme.background,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            Column(
                modifier = modifier.padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Color.LightGray.copy(alpha = .25f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.breeding_cave),
                    contentDescription = null
                )

                DragonSelectionButton(modifier = Modifier.fillMaxWidth(), dragonOne) {
                    coroutineScope.launch {
                        onWhichDragon(SandboxViewModel.WhichDragon.One)
                        bottomSheetState.show()
                    }
                }

                DragonSelectionButton(modifier = Modifier.fillMaxWidth(), dragonTwo) {
                    coroutineScope.launch {
                        onWhichDragon(SandboxViewModel.WhichDragon.Two)
                        bottomSheetState.show()
                    }
                }

                Row {
                    CheckBoxWithText(
                        text = "Bring 'em Back",
                        isChecked = beb,
                        onCheck = onBebChecked
                    )
                }

                AllDragonsScreen(dragons = childs)

            }
        }
    }
}

@Composable
fun DragonSelectionButton(
    modifier: Modifier = Modifier,
    selectedDragonName: String,
    onClick: () -> Unit
) {

    OutlinedButton(
        modifier = modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            text = selectedDragonName,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ColumnScope.DragonSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState,
    search: String,
    onSearch: (String) -> Unit,
    dragons: List<DragonData>,
    onDragonSelected: (DragonData) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (!bottomSheetState.isVisible) {
        onSearch("")
        focusManager.clearFocus()
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = search,
        onValueChange = onSearch,
        placeholder = { Text(text = "Search") },
        trailingIcon = {
            IconButton(onClick = { onSearch("") }) {

            }
        },
        keyboardActions = KeyboardActions(
            onSearch = { keyboardController?.hide() }
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Search
        )
    )

    LazyColumn(
        modifier = modifier,
    ) {
        items(dragons.size) { i ->
            val dragon = dragons[i]

            if (i == 0) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDragonSelected(dragon)
                    }
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    ),
                text = dragon.name
            )

            if (i == dragons.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }


}

@Composable
fun CheckBoxWithText(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onCheck: (Boolean) -> Unit,
) {

    Row(
        modifier = modifier
            .toggleable(
                value = isChecked,
                onValueChange = { onCheck(it) },
                role = Role.Checkbox
            )
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text
        )
    }

}