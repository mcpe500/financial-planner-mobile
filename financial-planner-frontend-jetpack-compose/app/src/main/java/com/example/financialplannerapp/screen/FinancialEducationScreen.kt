package com.example.financialplannerapp.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Main Colors from Bibit Theme
private val BibitGreen = Color(0xFF00B897)
private val BibitLightGreen = Color(0xFFE6F7F3)
val BibitRed = Color(0xFFE94F4F)
val BibitYellow = Color(0xFFFFB020)
val BibitBlue = Color(0xFF3772FF)
val BibitPurple = Color(0xFF6D4AFF)
val BibitBackground = Color(0xFFF8F9FD)
val BibitCardBackground = Color.White
val BibitTextPrimary = Color(0xFF1A1A1A)
val BibitTextSecondary = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialEducationScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edukasi Finansial",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BibitCardBackground
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BibitBackground)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ArtikelKeuanganSection()
            }

            item {
                KalkulatorFinansialSection()
            }

            item {
                ReferensiMateriInvestasiSection()
            }

            item {
                SumberBelajarSection()
            }

            item {
                BukuRekomendasiSection()
            }

            item {
                SdgsFinansialSection()
            }

            // Add space at the bottom for the disclaimer
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Fixed disclaimer at the bottom
        DisclaimerSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = BibitTextPrimary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ArtikelKeuanganSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle("Artikel Keuangan")

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(artikelKeuanganList) { artikel ->
                ArtikelKeuanganCard(artikel)
            }
        }
    }
}

@Composable
fun ArtikelKeuanganCard(artikel: ArtikelKeuangan) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BibitCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Image(
                    painter = painterResource(id = artikel.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = "Download for offline",
                        tint = BibitGreen
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = artikel.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = artikel.excerpt,
                    fontSize = 14.sp,
                    color = BibitTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = BibitGreen
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Baca Selengkapnya")
                }
            }
        }
    }
}

@Composable
fun KalkulatorFinansialSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SectionTitle("Kalkulator Finansial")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BibitCardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                kalkulatorList.forEach { kalkulator ->
                    KalkulatorButton(kalkulator)
                }
            }
        }
    }
}

@Composable
fun KalkulatorButton(kalkulator: KalkulatorFinansial) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(kalkulator.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = kalkulator.icon,
                contentDescription = null,
                tint = kalkulator.iconColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = kalkulator.title,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ReferensiMateriInvestasiSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle("Referensi Materi Investasi")

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(220.dp)
        ) {
            items(referensiMateriList) { materi ->
                ReferensiMateriCard(materi)
            }
        }
    }
}

@Composable
fun ReferensiMateriCard(materi: ReferensiMateri) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BibitCardBackground)
            .clickable { }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(materi.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = materi.icon,
                contentDescription = null,
                tint = materi.iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = materi.title,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "Learn More",
            fontSize = 10.sp,
            color = BibitGreen,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SumberBelajarSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SectionTitle("Sumber Belajar")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BibitCardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                sumberBelajarList.forEach { sumber ->
                    SumberBelajarItem(sumber)
                    if (sumber != sumberBelajarList.last()) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = BibitBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Informasi dari sumber terpercaya. Aplikasi tidak bertanggung jawab atas isi eksternal.",
                    fontSize = 12.sp,
                    color = BibitTextSecondary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun SumberBelajarItem(sumber: SumberBelajar) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(sumber.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = sumber.icon,
                contentDescription = null,
                tint = sumber.iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sumber.title,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = sumber.description,
                fontSize = 12.sp,
                color = BibitTextSecondary
            )
        }

        Icon(
            imageVector = Icons.Outlined.OpenInNew,
            contentDescription = "Open external link",
            tint = BibitGreen
        )
    }
}

@Composable
fun BukuRekomendasiSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle("Buku Rekomendasi")

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bukuRekomendasiList) { buku ->
                BukuRekomendasiCard(buku)
            }
        }
    }
}

@Composable
fun BukuRekomendasiCard(buku: BukuRekomendasi) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(200.dp)
            .clickable { },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BibitCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = buku.coverRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            )

            Text(
                text = buku.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SdgsFinansialSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BibitCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BibitLightGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiNature,
                    contentDescription = null,
                    tint = BibitGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SDGs & Finansial",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pelajari bagaimana investasi dapat mendukung tujuan pembangunan berkelanjutan",
                    fontSize = 14.sp,
                    color = BibitTextSecondary
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Lihat Tag SDG")
            }
        }
    }
}

@Composable
fun DisclaimerSection(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BibitLightGreen.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = BibitYellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "⚠️ Aplikasi ini tidak memberikan saran investasi. Informasi bersifat edukatif. Keputusan investasi adalah tanggung jawab pengguna.",
                fontSize = 12.sp,
                color = BibitTextPrimary
            )
        }
    }
}

// Data Classes and Sample Data
data class ArtikelKeuangan(
    val id: Int,
    val title: String,
    val excerpt: String,
    val imageRes: Int
)

data class KalkulatorFinansial(
    val id: Int,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val backgroundColor: Color,
    val iconColor: Color
)

data class ReferensiMateri(
    val id: Int,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val backgroundColor: Color,
    val iconColor: Color
)

data class SumberBelajar(
    val id: Int,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val backgroundColor: Color,
    val iconColor: Color,
    val url: String
)

data class BukuRekomendasi(
    val id: Int,
    val title: String,
    val author: String,
    val coverRes: Int
)

// Sample Data
val artikelKeuanganList = listOf(
    ArtikelKeuangan(
        id = 1,
        title = "Cara Memulai Investasi dengan Modal Kecil",
        excerpt = "Panduan lengkap untuk memulai investasi meskipun dengan modal yang terbatas.",
        imageRes = android.R.drawable.ic_menu_gallery
    ),
    ArtikelKeuangan(
        id = 2,
        title = "Mengenal Reksa Dana untuk Pemula",
        excerpt = "Penjelasan sederhana tentang reksa dana dan cara kerjanya untuk investor pemula.",
        imageRes = android.R.drawable.ic_menu_gallery
    ),
    ArtikelKeuangan(
        id = 3,
        title = "Tips Mengelola Keuangan di Masa Pandemi",
        excerpt = "Strategi praktis untuk mengelola keuangan pribadi di tengah ketidakpastian ekonomi.",
        imageRes = android.R.drawable.ic_menu_gallery
    ),
    ArtikelKeuangan(
        id = 4,
        title = "Memahami Inflasi dan Dampaknya",
        excerpt = "Penjelasan tentang inflasi dan bagaimana hal ini mempengaruhi keuangan pribadi Anda.",
        imageRes = android.R.drawable.ic_menu_gallery
    )
)

val kalkulatorList = listOf(
    KalkulatorFinansial(
        id = 1,
        title = "Bunga Majemuk",
        icon = Icons.Default.Timeline,
        backgroundColor = BibitLightGreen,
        iconColor = BibitGreen
    ),
    KalkulatorFinansial(
        id = 2,
        title = "Simulasi Cicilan",
        icon = Icons.Default.CreditCard,
        backgroundColor = BibitBlue.copy(alpha = 0.15f),
        iconColor = BibitBlue
    ),
    KalkulatorFinansial(
        id = 3,
        title = "Dana Pensiun",
        icon = Icons.Default.AccountBalance,
        backgroundColor = BibitYellow.copy(alpha = 0.15f),
        iconColor = BibitYellow
    )
)

val referensiMateriList = listOf(
    ReferensiMateri(
        id = 1,
        title = "Saham",
        icon = Icons.Default.TrendingUp,
        backgroundColor = BibitGreen.copy(alpha = 0.15f),
        iconColor = BibitGreen
    ),
    ReferensiMateri(
        id = 2,
        title = "Obligasi",
        icon = Icons.Default.Description,
        backgroundColor = BibitBlue.copy(alpha = 0.15f),
        iconColor = BibitBlue
    ),
    ReferensiMateri(
        id = 3,
        title = "Reksa Dana",
        icon = Icons.Default.PieChart,
        backgroundColor = BibitPurple.copy(alpha = 0.15f),
        iconColor = BibitPurple
    ),
    ReferensiMateri(
        id = 4,
        title = "Emas",
        icon = Icons.Default.Star,
        backgroundColor = BibitYellow.copy(alpha = 0.15f),
        iconColor = BibitYellow
    ),
    ReferensiMateri(
        id = 5,
        title = "Properti",
        icon = Icons.Default.Home,
        backgroundColor = BibitRed.copy(alpha = 0.15f),
        iconColor = BibitRed
    ),
    ReferensiMateri(
        id = 6,
        title = "P2P",
        icon = Icons.Default.People,
        backgroundColor = BibitGreen.copy(alpha = 0.15f),
        iconColor = BibitGreen
    ),
    ReferensiMateri(
        id = 7,
        title = "Risiko",
        icon = Icons.Default.Warning,
        backgroundColor = BibitYellow.copy(alpha = 0.15f),
        iconColor = BibitYellow
    ),
    ReferensiMateri(
        id = 8,
        title = "Diversifikasi",
        icon = Icons.Default.AccountBalance,
        backgroundColor = BibitBlue.copy(alpha = 0.15f),
        iconColor = BibitBlue
    )
)

val sumberBelajarList = listOf(
    SumberBelajar(
        id = 1,
        title = "Otoritas Jasa Keuangan (OJK)",
        description = "Situs resmi regulator keuangan Indonesia",
        icon = Icons.Default.Security,
        backgroundColor = BibitBlue.copy(alpha = 0.15f),
        iconColor = BibitBlue,
        url = "https://www.ojk.go.id"
    ),
    SumberBelajar(
        id = 2,
        title = "Bursa Efek Indonesia (IDX)",
        description = "Informasi pasar modal Indonesia",
        icon = Icons.Default.BarChart,
        backgroundColor = BibitGreen.copy(alpha = 0.15f),
        iconColor = BibitGreen,
        url = "https://www.idx.co.id"
    ),
    SumberBelajar(
        id = 3,
        title = "Finansialku Blog",
        description = "Artikel edukasi keuangan personal",
        icon = Icons.Default.MenuBook,
        backgroundColor = BibitPurple.copy(alpha = 0.15f),
        iconColor = BibitPurple,
        url = "https://www.finansialku.com"
    )
)

val bukuRekomendasiList = listOf(
    BukuRekomendasi(
        id = 1,
        title = "Rich Dad Poor Dad",
        author = "Robert T. Kiyosaki",
        coverRes = android.R.drawable.ic_menu_gallery
    ),
    BukuRekomendasi(
        id = 2,
        title = "The Psychology of Money",
        author = "Morgan Housel",
        coverRes = android.R.drawable.ic_menu_gallery
    ),
    BukuRekomendasi(
        id = 3,
        title = "Your Money or Your Life",
        author = "Vicki Robin",
        coverRes = android.R.drawable.ic_menu_gallery
    ),
    BukuRekomendasi(
        id = 4,
        title = "The Intelligent Investor",
        author = "Benjamin Graham",
        coverRes = android.R.drawable.ic_menu_gallery
    ),
    BukuRekomendasi(
        id = 5,
        title = "Atomic Habits",
        author = "James Clear",
        coverRes = android.R.drawable.ic_menu_gallery
    )
)

// Preview Functions
@Preview(showBackground = true)
@Composable
fun FinancialEducationScreenPreview() {
    FinancialEducationScreen()
}

@Preview(showBackground = true)
@Composable
fun ArtikelKeuanganSectionPreview() {
    ArtikelKeuanganSection()
}

@Preview(showBackground = true)
@Composable
fun KalkulatorFinansialSectionPreview() {
    KalkulatorFinansialSection()
}

@Preview(showBackground = true)
@Composable
fun ReferensiMateriInvestasiSectionPreview() {
    ReferensiMateriInvestasiSection()
}

@Preview(showBackground = true)
@Composable
fun SumberBelajarSectionPreview() {
    SumberBelajarSection()
}

@Preview(showBackground = true)
@Composable
fun BukuRekomendasiSectionPreview() {
    BukuRekomendasiSection()
}

@Preview(showBackground = true)
@Composable
fun SdgsFinansialSectionPreview() {
    SdgsFinansialSection()
}

@Preview(showBackground = true)
@Composable
fun DisclaimerSectionPreview() {
    DisclaimerSection()
}
