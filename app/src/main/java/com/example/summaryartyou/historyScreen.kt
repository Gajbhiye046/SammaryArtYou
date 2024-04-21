package com.example.summaryartyou

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(modifier: Modifier = Modifier, navController: NavHostController, viewModel: TextSummaryViewModel) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var searchText by remember { mutableStateOf("") } // Query for SearchBar
    var active by remember { mutableStateOf(false) } // Active state for SearchBar
    val focusManager = LocalFocusManager.current // Hide cursor
    val focusRequester = remember { FocusRequester() } // Show cursor after removing
    var searchResults by remember { mutableStateOf(emptyList<TextSummary>()) }


    if (active == false) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(),
                    title = {
                        Text(
                            text = stringResource(id = R.string.history),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("home") }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { active = true }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_search_24),
                                contentDescription = stringResource(id = R.string.search)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { innerPadding ->
            var reversedList = remember { mutableStateListOf<String>() }

            LaunchedEffect(viewModel.textSummaries) {
                reversedList.clear()
                reversedList.addAll(viewModel.getAllTextSummaries().reversed())
            }

            LazyColumn(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxSize(),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (viewModel.textSummaries.isNotEmpty()) {
                    items(reversedList) { textSummaryId ->
                        val textSummary =
                            viewModel.textSummaries.firstOrNull { it.id == textSummaryId.toString() }
                        if (textSummary != null) {
                            Textbox(
                                modifier = Modifier.fillMaxWidth(),
                                id = textSummary.id,
                                title = textSummary.title,
                                author = textSummary.author,
                                text = textSummary.text,
                                youtubeLink = textSummary.youtubeLink,
                                viewModel = viewModel,
                                search = false
                            )
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Text(
                            text = stringResource(id = R.string.noHistory),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    } else {
        var searchResults = remember { mutableStateListOf<String>() }
        SearchBar(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            query = searchText,
            onQueryChange = {
                searchText = it
                searchResults.clear()
                searchResults.addAll(viewModel.searchTextSummary(searchText).reversed())
            },
            onSearch = {
                focusManager.clearFocus()
            },
            active = active,
            onActiveChange = {
                active = it
            },
            placeholder = {
                Text(text = stringResource(id = R.string.search))
            },
            leadingIcon = {
                IconButton(onClick = { navController.navigate("history") }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if(searchText == "" || searchText.isNullOrEmpty()) {
                            navController.navigate("history")
                        }else{
                            searchText = ""
                            focusRequester.requestFocus()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
        ) {
            if (searchText.isNotEmpty() && searchResults.isNullOrEmpty()) {
                Text(
                    text = stringResource(id = R.string.nothingFound),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { textSummaryId ->
                        val textSummary =
                            viewModel.textSummaries.firstOrNull { it.id == textSummaryId.toString() }
                        if (textSummary != null) {
                            Textbox(
                                modifier = Modifier.fillMaxWidth(),
                                id = textSummary.id,
                                title = textSummary.title,
                                author = textSummary.author,
                                text = textSummary.text,
                                youtubeLink = textSummary.youtubeLink,
                                viewModel = viewModel,
                                search = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Textbox(modifier: Modifier = Modifier, id: String, title: String?, author: String?, text: String?, youtubeLink: Boolean, viewModel: TextSummaryViewModel, search: Boolean) {
    val haptics = LocalHapticFeedback.current // Vibrations
    val context = LocalContext.current // Clipboard
    val clipboardManager = ContextCompat.getSystemService(
        context,
        ClipboardManager::class.java
    ) as ClipboardManager
    val dismissState = rememberSwipeToDismissBoxState()
    val contextForToast = LocalContext.current.applicationContext
    if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd){
        Toast.makeText(contextForToast, stringResource(id = R.string.deleted), Toast.LENGTH_SHORT).show()
        viewModel.removeTextSummary(id)
    }
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxSize(),
        //directions = setOf(DismissDirection.StartToEnd),
        backgroundContent = {
            val iconScale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 1.2f else 0.9f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(
                    modifier = Modifier
                        .scale(iconScale)
                        .padding(end = 170.dp)
                        .align(Alignment.Center),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (search) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = modifier
                .padding(top = 15.dp, bottom = 15.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        //viewModel.removeTextSummary(title, author, text, youtubeLink)
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(null, text)
                        )
                    }
                )
        ) {
            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                )
                if (!author.isNullOrEmpty()) {
                    Row {
                        Text(
                            text = author,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(
                                    top = 4.dp,
                                    start = 12.dp,
                                    end = 12.dp
                                )
                        )
                        if (youtubeLink) {
                            Icon(
                                painter = painterResource(id = R.drawable.youtube),
                                contentDescription = null,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = text ?: "",
                style = MaterialTheme.typography.labelLarge,
                modifier = modifier
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 10.dp,
                        bottom = 12.dp
                    )
            )
            var tts: TextToSpeech? by remember { mutableStateOf(null) }
            var isSpeaking by remember { mutableStateOf(false) }
            var isPaused by remember { mutableStateOf(false) }
            var currentPosition by remember { mutableStateOf(0) }
            var utteranceId by remember { mutableStateOf("") }
            val copied = stringResource(id = R.string.copied)
            val transcript = text

            val utteranceProgressListener = object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                    // Is called when an utterance starts
                }

                override fun onDone(utteranceId: String) {
                    // Is called when an utterance is done
                    currentPosition = 0
                    isSpeaking = false
                    isPaused = false
                }

                override fun onError(utteranceId: String) {
                    // Is called when an error occurs
                }

                override fun onRangeStart(utteranceId: String, start: Int, end: Int, frame: Int) {
                    // Is called when a new range of text is being spoken
                    currentPosition = end
                }
            }
            tts?.setOnUtteranceProgressListener(utteranceProgressListener)
            DisposableEffect(Unit) {
                tts = TextToSpeech(context) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        // TTS-Engine successfully initialized
                        Log.d("TTS", "Text-to-Speech engine was successfully initialized.")
                    } else {
                        // Error initializing the TTS-Engine
                        Log.d("TTS", "Error initializing the Text-to-Speech engine.")
                    }
                }
                onDispose {
                    tts?.stop()
                    tts?.shutdown()
                }
            }
            Row {
                IconButton(
                    onClick = {
                        if (isSpeaking) {
                            tts?.stop()
                            isSpeaking = false
                            isPaused = false
                            currentPosition = 0
                        } else {
                            if (transcript != null) {
                                utteranceId = UUID.randomUUID().toString()
                                tts?.speak(transcript, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                                isSpeaking = true
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_volume_up_24),
                        contentDescription = if (isSpeaking) "Beenden" else "Vorlesen"
                    )
                }

                AnimatedVisibility(visible = isSpeaking) {
                    IconButton(
                        onClick = {
                            if (isPaused) {
                                if (transcript != null) {
                                    val remainingText = transcript.substring(currentPosition)
                                    utteranceId = UUID.randomUUID().toString()
                                    tts?.speak(remainingText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                                    isPaused = false
                                }
                            } else {
                                tts?.stop()
                                isPaused = true
                            }
                        }
                    ) {
                        Icon(
                            painter = if (isPaused) {
                                painterResource(id = R.drawable.outline_play_circle_filled_24)
                            } else {
                                painterResource(id = R.drawable.outline_pause_circle_filled_24)
                            },
                            contentDescription = if (isPaused) "Fortsetzen" else "Pausieren"
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(null, text)
                        )
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                            Toast.makeText(context, copied, Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_content_copy_24),
                        contentDescription = "Kopieren"
                    )
                }

                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        val chooserIntent = Intent.createChooser(shareIntent, null)
                        context.startActivity(chooserIntent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Teilen"
                    )
                }
            }
        }
    }
}


