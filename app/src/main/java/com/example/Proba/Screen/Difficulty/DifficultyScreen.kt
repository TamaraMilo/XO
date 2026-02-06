import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Proba.States.Difficulty

@Composable
fun DifficultyScreen(
    onPick: (Difficulty) -> Unit,
    onBack: () -> Unit
) {
    val bg = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f)
        )
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Izaberi težinu", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onBack) { Text("Nazad") }
            }

            Text(
                text = "Težina menja kako AI bira potez:",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            DifficultyCard(
                title = "Easy",
                subtitle = "AI bira nasumično slobodno polje",
                badge = "Random",
                onClick = { onPick(Difficulty.Easy) }
            )

            DifficultyCard(
                title = "Medium",
                subtitle = "AI pokušava da pobedi ili blokira",
                badge = "Rules",
                onClick = { onPick(Difficulty.Medium) }
            )

            DifficultyCard(
                title = "Hard",
                subtitle = "AI koristi model (sa zaštitom od lošeg poteza)",
                badge = "Model",
                onClick = { onPick(Difficulty.Hard) }
            )

            Spacer(Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                )
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Tip:", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Ako Hard predloži zauzeto polje, aplikacija automatski izabere validan potez.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyCard(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AssistChip(
                onClick = onClick,
                label = { Text(badge) }
            )
        }
    }
}
