package com.example.fakecall

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.fakecall.ui.theme.FakeCallTheme


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

    /*Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Barack Obama",
            style = TextStyle(
                color = Color.Black,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(8.dp)
        )
    }*/


    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.president_barack_obama),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize(),

            )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Barack Obama",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 43.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(8.dp)
            )
        }

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .size(120.dp) // Taille ajustée pour un bouton rond
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.White),
                onClick = {
                    // Action for Answer button
                    textToSpeech?.speak(
                        "Hello it's Barack Obama",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                    exoplayer.pause()
                }
            ) {
                Text("Answer", modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = {
                    // Action for Decline button
                    textToSpeech?.speak("I will call back ", TextToSpeech.QUEUE_FLUSH, null, null)
                    exoplayer.pause()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .size(120.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White),

            ) {
                Text("Decline", modifier = Modifier.padding(8.dp))
            }

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

@Preview
@Composable
fun AppContentPreview() {
    AppContent()
}
