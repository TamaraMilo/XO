import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Proba.Screen.Game.GameViewModel
import com.example.Proba.States.Difficulty
import kotlinx.coroutines.delay

@Composable
fun GameScreen(difficulty: Difficulty, onBack: () -> Unit) {
    val assets = LocalContext.current.assets
    val vm = remember(difficulty) { GameViewModel(assets, difficulty) }

    // AI delay
    LaunchedEffect(vm.isThinking) {
        if (vm.isThinking) {
            delay(1000) // promeni po želji
            vm.aiMove()
        }
    }

    val bg = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f),
            MaterialTheme.colorScheme.background
        )
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(16.dp)
        ) {
            // --- Glavni UI (igra) ---
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // HEADER
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    )
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Težina: $difficulty", fontWeight = FontWeight.Bold)
                            TextButton(onClick = onBack) { Text("Nazad") }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (vm.isThinking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Text(
                                text = vm.statusText,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // TABLA
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BoardGridString(
                            board = vm.board,
                            enabled = !vm.gameOver && !vm.isThinking,
                            onCellClick = { vm.onHumanClick(it) }
                        )
                    }
                }

                // KONTROLE
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { vm.reset() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Reset") }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Promeni težinu") }
                }
            }

            // --- FULL SCREEN OVERLAY kad je kraj ---
            AnimatedVisibility(
                visible = vm.gameOver,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(120)),
                modifier = Modifier.fillMaxSize()
            ) {
                // scrim preko cele igre
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    // kartica u centru
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Kraj igre",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = vm.statusText,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Želiš novu partiju?",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(6.dp))

                            Button(
                                onClick = { vm.reset() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Igraj opet (Reset)") }

                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Promeni težinu") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardGridString(
    board: String,
    enabled: Boolean,
    onCellClick: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (r in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (c in 0..2) {
                    val i = r * 3 + c
                    val ch = board.getOrNull(i) ?: ' '

                    Button(
                        onClick = { onCellClick(i) },
                        enabled = enabled && ch == ' ',
                        modifier = Modifier.size(92.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        AnimatedContent(
                            targetState = ch,
                            transitionSpec = {
                                (fadeIn(tween(140)) + scaleIn(initialScale = 0.85f, animationSpec = tween(140)))
                                    .togetherWith(
                                        fadeOut(tween(110)) + scaleOut(targetScale = 0.95f, animationSpec = tween(110))
                                    )
                            },
                            label = "cell"
                        ) { value ->
                            Text(
                                text = if (value == ' ') "" else value.toString(),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
