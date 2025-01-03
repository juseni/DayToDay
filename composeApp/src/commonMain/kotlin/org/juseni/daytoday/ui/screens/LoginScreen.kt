package org.juseni.daytoday.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.domain.models.User
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.app_name
import org.juseni.daytoday.resources.auto_logging
import org.juseni.daytoday.resources.ic_close_eye
import org.juseni.daytoday.resources.ic_eye
import org.juseni.daytoday.resources.login
import org.juseni.daytoday.resources.login_information
import org.juseni.daytoday.resources.option_bills
import org.juseni.daytoday.resources.option_income_expenses
import org.juseni.daytoday.resources.options_information
import org.juseni.daytoday.resources.password
import org.juseni.daytoday.resources.remember_me
import org.juseni.daytoday.resources.sing_up
import org.juseni.daytoday.resources.sing_up_information
import org.juseni.daytoday.resources.user
import org.juseni.daytoday.resources.welcome_information
import org.juseni.daytoday.ui.ScreenRoute
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.ErrorLogin
import org.juseni.daytoday.ui.components.FullProgressIndicator
import org.juseni.daytoday.ui.components.LabelledCheckBox
import org.juseni.daytoday.ui.components.LoggingProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.navigateTop
import org.juseni.daytoday.ui.viewmodels.LoginScreenViewModel
import org.juseni.daytoday.ui.viewmodels.LoginState
import org.juseni.daytoday.ui.viewmodels.LoginUiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel = koinViewModel<LoginScreenViewModel>()
) {
    val loginUiState by viewModel.loginUiState.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val autoLogging by viewModel.autoLogging.collectAsState()
    val loginState by viewModel.isLogged.collectAsState()
    val showErrorDialog = remember { mutableStateOf(false) }

    Screen {
        when (loginUiState) {
            is LoginUiState.Loading -> FullProgressIndicator()
            is LoginUiState.Login -> {
                val user = (loginUiState as LoginUiState.Login).user
                val password = (loginUiState as LoginUiState.Login).password
                viewModel.login(user, password)
            }
            is LoginUiState.Success -> LoginContent(
                user = (loginUiState as LoginUiState.Success).user,
                rememberMe = rememberMe,
                autoLogging = autoLogging,
                onRememberMeChange = viewModel::setRememberMe,
                onAutoLoggingChange = viewModel::setAutoLogging,
                onLoginClicked = viewModel::login,
                onSingUpClicked = viewModel::singUp
            )
            is LoginUiState.Error -> { showErrorDialog.value = true }

        }
        when (loginState) {
            LoginState.UNLOGGED -> { /* Nothing to do */ }
            LoginState.LOGGED_IN -> navController.navigateTop(ScreenRoute.HOME_SCREEN)
            LoginState.LOGIN_IN -> LoggingProgressIndicator()
            LoginState.ERROR -> { showErrorDialog.value = true }
        }
        if (showErrorDialog.value) {
            ErrorLogin(onDismiss = {
                viewModel.clearError()
                showErrorDialog.value = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    user: User? = null,
    rememberMe: Boolean = false,
    autoLogging: Boolean = false,
    onRememberMeChange: (Boolean) -> Unit,
    onAutoLoggingChange: (Boolean) -> Unit,
    onLoginClicked: (String, String) -> Unit,
    onSingUpClicked: (User) -> Unit
) {
    var showSingUpContent by remember { mutableStateOf(false) }
    var showLoginContent by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.app_name),
                hasBackButton = false,
                hasEndSessionButton = false
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginButton(
                text = stringResource(Res.string.login_information),
                onClick = {
                    showLoginContent = true
                    showSingUpContent = false
                }
            )

            AnimatedVisibility(showLoginContent) {
                LoginInformation(
                    user = user,
                    rememberMe = rememberMe,
                    autoLogging = autoLogging,
                    buttonText = stringResource(Res.string.login),
                    onRememberMeChange = onRememberMeChange,
                    onAutoLoggingChange = onAutoLoggingChange,
                    onClick = {
                        onLoginClicked(it.user, it.password)
                    }
                )
            }

            LoginButton(
                text = stringResource(Res.string.sing_up_information),
                onClick = {
                    showLoginContent = false
                    showSingUpContent = true
                }
            )

            AnimatedVisibility(showSingUpContent) {
                LoginInformation(
                    rememberMe = rememberMe,
                    autoLogging = autoLogging,
                    buttonText = stringResource(Res.string.sing_up),
                    isSingUp = true,
                    onRememberMeChange = onRememberMeChange,
                    onAutoLoggingChange = onAutoLoggingChange,
                    onClick = {
                        onSingUpClicked(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginButton(
    text: String,
    onClick: () -> Unit
) {
    ElevatedButton(onClick = onClick) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun LoginInformation(
    user: User? = null,
    rememberMe: Boolean = false,
    autoLogging: Boolean = false,
    buttonText: String,
    isSingUp: Boolean = false,
    onClick: (user: User) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onAutoLoggingChange: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val email = remember { mutableStateOf(user?.user ?: "") }
    val password = remember { mutableStateOf(user?.password ?: "") }
    val hasBills = remember { mutableStateOf(false) }
    val hasIncomeExpenses = remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.welcome_information),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = stringResource(Res.string.user)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            trailingIcon = {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                    }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "close circle",
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = stringResource(Res.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            leadingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(
                            if (passwordVisible) Res.drawable.ic_close_eye else Res.drawable.ic_eye
                        ),
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                    }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "close circle",
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isSingUp) {
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.TopCenter),
                    text = stringResource(Res.string.options_information)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    LabelledCheckBox(
                        checked = hasBills.value,
                        onCheckedChange = { hasBills.value = it },
                        label = stringResource(Res.string.option_bills)
                    )

                    LabelledCheckBox(
                        checked = hasIncomeExpenses.value,
                        onCheckedChange = { hasIncomeExpenses.value = it },
                        label = stringResource(Res.string.option_income_expenses)
                    )

                    HorizontalDivider()
                }
            }
        }

        LabelledCheckBox(
            modifier = Modifier.fillMaxWidth(),
            checked = rememberMe,
            onCheckedChange = onRememberMeChange,
            label = stringResource(Res.string.remember_me)
        )

        LabelledCheckBox(
            modifier = Modifier.fillMaxWidth(),
            checked = autoLogging,
            onCheckedChange = onAutoLoggingChange,
            label = stringResource(Res.string.auto_logging)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onClick(
                    User(0, email.value, password.value, hasBills.value, hasIncomeExpenses.value)
                )
            },
            enabled = email.value.isNotEmpty() && password.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = buttonText)
        }
    }
}

