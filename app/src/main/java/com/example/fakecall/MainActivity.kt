package com.example.fakecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.fakecall.ui.theme.FakeCallTheme
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import androidx.compose.ui.Alignment


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FakeCallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    AppContent()
                }


            }

        }
    }

}

@Composable
fun AppContent() {
    var isPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var exoplayer = remember { ExoPlayer.Builder(context).build() }
    val audioSource = remember {
        MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.applesonnerie}")
    }

    // Initialiser TextToSpeech
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        textToSpeech = TextToSpeech(context, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d("TextToSpeech", "TextToSpeech initialized successfully")
            } else {
                Log.e("TextToSpeech", "TextToSpeech initialization failed")
            }
        })

        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    DisposableEffect(isPlaying) {
        // Synthèse vocale lors du changement d'état de lecture
        if (isPlaying && textToSpeech != null) {
            textToSpeech?.speak("Incoming call", TextToSpeech.QUEUE_FLUSH, null, null)
        }
        onDispose {}
    }

    DisposableEffect(Unit) {
        exoplayer.setMediaItem(audioSource)
        exoplayer.prepare()
        exoplayer.play()
        onDispose {
            exoplayer.release()
        }
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.president_barack_obama),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),

        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom

        ) {
            Button(
                onClick = {
                    // Action for Answer button
                    textToSpeech?.speak("Hello it's Barack Obama", TextToSpeech.QUEUE_FLUSH, null, null)
                    exoplayer.pause()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .size(35.dp)
            ) {
                Text("Answer")
            }

            Button(
                onClick = {
                    // Action for Decline button
                    textToSpeech?.speak("I will call you back ", TextToSpeech.QUEUE_FLUSH, null, null)
                    exoplayer.pause()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .size(35.dp)
            ) {
                Text("Decline")
            }

            IconButton(
                onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) exoplayer.play() else exoplayer.pause()
                }
            ) {
                if (isPlaying) {
                    Icon(Icons.Filled.Settings, contentDescription = "Stop")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                }
            }
        }
    }
}

@Preview
@Composable
fun AppContentPreview() {
    AppContent()
}
