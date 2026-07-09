package com.lightledger.app.ui

import android.content.Context
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.lightledger.app.R
import com.lightledger.app.WidgetActions
import com.lightledger.app.data.ExportResult
import com.lightledger.app.data.ImportResult
import com.lightledger.app.data.model.CategoryEntity
import com.lightledger.app.data.model.LedgerType
import com.lightledger.app.data.model.SettingEntity
import com.lightledger.app.data.model.TransactionEntity
import com.lightledger.app.domain.CategorySuggester
import com.lightledger.app.domain.MoneyFormatter
import com.lightledger.app.ui.theme.LightLedgerTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.floor
import kotlin.math.roundToInt

private enum class MainTab {
    Inbox,
    Ledger,
    Stats,
    Export,
}

private enum class StatsScale(val label: String) {
    Day("日"),
    Month("月"),
    Year("年"),
}

private enum class LedgerDisplayMode(val label: String) {
    Expense("支出"),
    Income("收入"),
}

private enum class TrendChartMode(val label: String) {
    Bar("柱状"),
    Line("折线"),
}

private val Hairline = Color(0xFFE6E6E2)
private val Ink = Color(0xFF050505)
private val InkSoft = Color(0xFF2A2A2A)
private val SoftPaper = Color(0xFFEFEFED)
private val Accent = Color(0xFF050505)
private val AccentSoft = Color(0xFFF1F1EF)
private val TextMuted = Color(0xFF747474)
private val TextFaint = Color(0xFFB8B8B4)
private val TickleBg = Color(0xFFF7F7F5)
private val TickleSurface = Color(0xFFFFFFFF)
private val RadiusXs = 6.dp
private val RadiusSm = 10.dp
private val RadiusMd = 16.dp
private val RadiusLg = 20.dp
private val RadiusXl = 24.dp
private val GapXs = 6.dp
private val GapSm = 10.dp
private val GapMd = 16.dp
private val GapLg = 22.dp
private const val AppPrefsName = "light_ledger_app"
private const val KeyGuideSeen = "guide_seen_v2"
private const val QuickCategoryLimit = 12
private const val QuickCategorySelectableLimit = QuickCategoryLimit - 1
private const val OtherCategoryEntry = "其他"

private object MotionSpec {
    const val HapticsEnabled = true
    const val PressScale = 0.97f
    const val PressDurationMillis = 90
    const val AmountPulseScale = 1.04f
    const val AmountPulseDurationMillis = 160
    const val CategorySwitchDurationMillis = 200
    const val SuccessFeedbackDurationMillis = 700
    const val ErrorShakeDurationMillis = 160
}

private enum class AppHaptic {
    Light,
    Selection,
    Success,
    Warning,
}

@Composable
fun LightLedgerApp(
    viewModel: LightLedgerViewModel,
    launchAction: String? = null,
    onLaunchActionConsumed: () -> Unit = {},
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val lastExport by viewModel.lastExport.collectAsState()
    val lastImport by viewModel.lastImport.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showStartup by remember { mutableStateOf(launchAction == null) }
    var showWidgetCardEditor by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showGuide by remember {
        mutableStateOf(
            launchAction == null &&
                !context.getSharedPreferences(AppPrefsName, Context.MODE_PRIVATE)
                    .getBoolean(KeyGuideSeen, false),
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val expenseCustomNames = remember(categories) {
        categories
            .filter { it.enabled }
            .filter { isExpenseCustomCategory(it.categoryId) }
            .filterNot { isBlockedTestCategory(it.name) }
            .map { categoryCustomDisplayName(it.name) }
            .distinct()
    }
    val incomeCustomNames = remember(categories) {
        categories
            .filter { it.enabled }
            .filter { it.categoryId.startsWith("custom_income_") }
            .filterNot { isBlockedTestCategory(it.name) }
            .map { categoryCustomDisplayName(it.name) }
            .distinct()
    }
    val widgetExpenseCategories = remember(settings, expenseCustomNames) {
        orderedCategoryList(
            settings = settings,
            ledgerType = LedgerType.EXPENSE,
            builtInCategories = expenseBuiltInCategories(),
            customCategories = expenseCustomNames,
        )
    }
    val widgetIncomeCategories = remember(settings, incomeCustomNames) {
        orderedCategoryList(
            settings = settings,
            ledgerType = LedgerType.INCOME,
            builtInCategories = incomeBuiltInCategories(),
            customCategories = incomeCustomNames,
        )
    }

    fun dismissGuide() {
        context.getSharedPreferences(AppPrefsName, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KeyGuideSeen, true)
            .apply()
        showGuide = false
    }

    LaunchedEffect(Unit) {
        if (showStartup) {
            delay(1_200)
            showStartup = false
        }
    }

    LaunchedEffect(launchAction) {
        when (launchAction) {
            WidgetActions.QuickAdd -> selectedTab = MainTab.Inbox.ordinal
            WidgetActions.Ledger -> selectedTab = MainTab.Ledger.ordinal
        }
        if (launchAction != null) onLaunchActionConsumed()
    }

    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (showStartup) {
        StartupSplashScreen()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                MinimalNavigationBar(
                    selectedTab = selectedTab,
                    onSelect = { selectedTab = it },
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                when (MainTab.entries[selectedTab]) {
                    MainTab.Inbox -> InboxScreen(
                        categories = categories,
                        settings = settings,
                        onAddManualTransaction = viewModel::addManualTransaction,
                        onAddCategory = { name, ledgerType -> viewModel.addCategory(name, ledgerType) },
                        onUpdateQuickCategories = viewModel::updateQuickCategories,
                        onUpdateCategoryOrder = viewModel::updateCategoryOrder,
                        onShowGuide = { showGuide = true },
                        onEditWidgetCard = { showWidgetCardEditor = true },
                        modifier = Modifier.fillMaxSize(),
                    )

                    MainTab.Ledger -> LedgerScreen(
                        transactions = transactions,
                        categories = categories,
                        onUpdate = viewModel::updateTransaction,
                        onDelete = viewModel::deleteTransaction,
                        onAddCategory = { name -> viewModel.addCategory(name) },
                        modifier = Modifier.fillMaxSize(),
                    )

                    MainTab.Stats -> StatsScreen(
                        transactions = transactions,
                        modifier = Modifier.fillMaxSize(),
                    )

                    MainTab.Export -> ExportScreen(
                    transactions = transactions,
                    lastExport = lastExport,
                    lastImport = lastImport,
                    onExport = viewModel::exportCsv,
                    onImport = viewModel::importLatestBackup,
                    modifier = Modifier.fillMaxSize(),
                )
                }
            }
        }
        if (showWidgetCardEditor) {
            WidgetCardEditorSheet(
                initialLedgerType = LedgerType.EXPENSE,
                expenseCategories = widgetExpenseCategories,
                incomeCategories = widgetIncomeCategories,
                settings = settings,
                onUpdateWidgetCategories = viewModel::updateWidgetCategories,
                onUpdateWidgetAmounts = viewModel::updateWidgetAmounts,
                onDismiss = { showWidgetCardEditor = false },
            )
        }
        if (showGuide) {
            FirstRunGuideDialog(
                onDismiss = ::dismissGuide,
            )
        }
    }
}

@Composable
private fun StartupSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SplashTickleMark(
                modifier = Modifier.size(74.dp),
            )
            Text(
                text = "tickle",
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                lineHeight = 28.sp,
            )
            Text(
                text = "confirm and log",
                color = Color.White.copy(alpha = 0.76f),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                lineHeight = 10.sp,
            )
        }
        Text(
            text = "produce by liziheng",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 46.dp),
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp,
        )
    }
}

@Composable
private fun SplashTickleMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val dotRadius = size.minDimension * 0.075f
        drawCircle(
            color = Color.White,
            radius = dotRadius,
            center = Offset(size.width * 0.34f, size.height * 0.23f),
        )
        drawCircle(
            color = Color.White,
            radius = dotRadius,
            center = Offset(size.width * 0.64f, size.height * 0.23f),
        )
        val mark = Path().apply {
            moveTo(size.width * 0.26f, size.height * 0.56f)
            cubicTo(
                size.width * 0.38f,
                size.height * 0.66f,
                size.width * 0.42f,
                size.height * 0.77f,
                size.width * 0.52f,
                size.height * 0.78f,
            )
            cubicTo(
                size.width * 0.66f,
                size.height * 0.56f,
                size.width * 0.76f,
                size.height * 0.38f,
                size.width * 0.88f,
                size.height * 0.31f,
            )
        }
        drawPath(
            path = mark,
            color = Color.White,
            style = Stroke(
                width = size.minDimension * 0.072f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
        drawLine(
            color = Color.White,
            start = Offset(size.width * 0.49f, size.height * 0.88f),
            end = Offset(size.width * 0.67f, size.height * 0.84f),
            strokeWidth = size.minDimension * 0.045f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun FirstRunGuideDialog(
    onDismiss: () -> Unit,
) {
    val pages = remember {
        listOf(
            VisualGuidePage(
                imageRes = R.drawable.guide_widget_add,
            ),
            VisualGuidePage(
                imageRes = R.drawable.guide_widget_direct,
            ),
            VisualGuidePage(
                imageRes = R.drawable.guide_widget_mode,
            ),
            VisualGuidePage(
                imageRes = R.drawable.guide_widget_sync,
            ),
        )
    }
    var pageIndex by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "tickle guide",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Surface(
                        modifier = Modifier.clickable(onClick = onDismiss),
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFFEFEBE5),
                    ) {
                        Text(
                            text = "跳过",
                            modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ink,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Crossfade(
                        targetState = pageIndex,
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                        label = "guideVisual",
                    ) { currentPage ->
                        GuideVisualPanel(
                            imageRes = pages[currentPage].imageRes,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    pages.indices.forEach { index ->
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (index == pageIndex) 28.dp else 9.dp, height = 9.dp),
                            shape = CircleShape,
                            color = if (index == pageIndex) Ink else Color(0xFFE9E5DE),
                            content = {},
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { pageIndex = (pageIndex - 1).coerceAtLeast(0) },
                        enabled = pageIndex > 0,
                        modifier = Modifier
                            .weight(0.82f)
                            .height(52.dp),
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, Hairline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Text(
                            text = "上一页",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Button(
                        onClick = {
                            if (pageIndex == pages.lastIndex) {
                                onDismiss()
                            } else {
                                pageIndex += 1
                            }
                        },
                        modifier = Modifier
                            .weight(1.18f)
                            .height(52.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ink,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(
                            text = if (pageIndex == pages.lastIndex) "开始使用" else "下一步",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

private data class VisualGuidePage(
    val imageRes: Int,
)

@Composable
private fun GuideVisualPanel(
    imageRes: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 200.dp)
                .aspectRatio(792f / 1332f),
        )
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val isHome = title == "tickle"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = if (isHome) 10.dp else 22.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isHome) {
                LogoMark(
                    modifier = Modifier
                        .width(58.dp)
                        .height(62.dp),
                )
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            action?.invoke()
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TutorialButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(36.dp)
            .widthIn(min = 68.dp),
        border = BorderStroke(1.dp, Hairline),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        Text(
            text = "教程",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                lineHeight = 16.sp,
            ),
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun WidgetCardEditorButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(36.dp)
            .widthIn(min = 82.dp),
        border = BorderStroke(1.dp, Hairline),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = PaddingValues(horizontal = 11.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MiniWidgetGlyph(modifier = Modifier.size(15.dp))
            Text(
                text = "小卡片",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                ),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun QuickDateButton(
    label: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(34.dp)
            .widthIn(min = 70.dp),
        border = BorderStroke(1.dp, Hairline),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = PaddingValues(horizontal = 11.dp),
    ) {
        Text(
            text = "$label⌄",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 16.sp,
            ),
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
private fun CategoryHeaderAction(
    label: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    Surface(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(32.dp)
            .widthIn(min = 52.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Hairline),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                ),
                fontWeight = FontWeight.Medium,
                color = TextMuted,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun LogoMark(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.tickle_logo),
        contentDescription = "tickle",
        modifier = modifier,
    )
}

@Composable
private fun MiniWidgetGlyph(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onBackground
    Canvas(modifier = modifier.size(18.dp)) {
        val gap = 3.dp.toPx()
        val cell = (size.minDimension - gap) / 2f
        val radius = CornerRadius(2.2.dp.toPx(), 2.2.dp.toPx())
        repeat(2) { row ->
            repeat(2) { column ->
                drawRoundRect(
                    color = color,
                    topLeft = Offset(column * (cell + gap), row * (cell + gap)),
                    size = Size(cell, cell),
                    cornerRadius = radius,
                )
            }
        }
    }
}

@Composable
private fun MinimalNavigationBar(
    selectedTab: Int,
    onSelect: (Int) -> Unit,
) {
    NavigationBar(
        containerColor = TickleBg,
        tonalElevation = 0.dp,
        modifier = Modifier.background(TickleBg),
    ) {
        MainTab.entries.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onSelect(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onBackground,
                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                icon = {
                    MinimalTabIcon(tab = tab, selected = selectedTab == index)
                },
                label = {
                    Text(
                        when (tab) {
                            MainTab.Inbox -> "记账"
                            MainTab.Ledger -> "流水"
                            MainTab.Stats -> "统计"
                            MainTab.Export -> "备份"
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun MinimalTabIcon(
    tab: MainTab,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(modifier = modifier.size(26.dp)) {
        val strokeWidth = if (selected) 2.2.dp.toPx() else 1.8.dp.toPx()
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (tab) {
            MainTab.Inbox -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(5.dp.toPx(), 8.dp.toPx()),
                    size = Size(16.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                    style = stroke,
                )
                drawLine(color, Offset(8.dp.toPx(), 15.dp.toPx()), Offset(12.dp.toPx(), 18.dp.toPx()), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(18.dp.toPx(), 15.dp.toPx()), Offset(14.dp.toPx(), 18.dp.toPx()), strokeWidth, StrokeCap.Round)
            }

            MainTab.Ledger -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(7.dp.toPx(), 4.dp.toPx()),
                    size = Size(12.dp.toPx(), 18.dp.toPx()),
                    cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
                    style = stroke,
                )
                drawLine(color, Offset(10.dp.toPx(), 9.dp.toPx()), Offset(16.dp.toPx(), 9.dp.toPx()), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(10.dp.toPx(), 14.dp.toPx()), Offset(16.dp.toPx(), 14.dp.toPx()), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(10.dp.toPx(), 19.dp.toPx()), Offset(14.dp.toPx(), 19.dp.toPx()), strokeWidth, StrokeCap.Round)
            }

            MainTab.Stats -> {
                drawLine(color, Offset(5.dp.toPx(), 21.dp.toPx()), Offset(21.dp.toPx(), 21.dp.toPx()), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(8.dp.toPx(), 17.dp.toPx()), Offset(8.dp.toPx(), 21.dp.toPx()), strokeWidth + 1.dp.toPx(), StrokeCap.Round)
                drawLine(color, Offset(13.dp.toPx(), 11.dp.toPx()), Offset(13.dp.toPx(), 21.dp.toPx()), strokeWidth + 1.dp.toPx(), StrokeCap.Round)
                drawLine(color, Offset(18.dp.toPx(), 6.dp.toPx()), Offset(18.dp.toPx(), 21.dp.toPx()), strokeWidth + 1.dp.toPx(), StrokeCap.Round)
            }

            MainTab.Export -> {
                drawLine(color, Offset(13.dp.toPx(), 4.dp.toPx()), Offset(13.dp.toPx(), 16.dp.toPx()), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(9.dp.toPx(), 12.dp.toPx()), Offset(13.dp.toPx(), 16.dp.toPx()), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(17.dp.toPx(), 12.dp.toPx()), Offset(13.dp.toPx(), 16.dp.toPx()), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(7.dp.toPx(), 21.dp.toPx()), Offset(19.dp.toPx(), 21.dp.toPx()), strokeWidth, StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun InboxScreen(
    categories: List<CategoryEntity>,
    settings: List<SettingEntity>,
    onAddManualTransaction: (String, String, String, String, String, Long) -> Unit,
    onAddCategory: (String, String) -> Unit,
    onUpdateQuickCategories: (String, List<String>) -> Unit,
    onUpdateCategoryOrder: (String, List<String>) -> Unit,
    onShowGuide: () -> Unit,
    onEditWidgetCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                ManualRemoteAddCard(
                    categories = categories,
                    settings = settings,
                    onAdd = onAddManualTransaction,
                    onAddCategory = onAddCategory,
                    onUpdateQuickCategories = onUpdateQuickCategories,
                    onUpdateCategoryOrder = onUpdateCategoryOrder,
                    onShowGuide = onShowGuide,
                    onEditWidgetCard = onEditWidgetCard,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ManualRemoteAddCard(
    categories: List<CategoryEntity>,
    settings: List<SettingEntity>,
    onAdd: (String, String, String, String, String, Long) -> Unit,
    onAddCategory: (String, String) -> Unit,
    onUpdateQuickCategories: (String, List<String>) -> Unit,
    onUpdateCategoryOrder: (String, List<String>) -> Unit,
    onShowGuide: () -> Unit,
    onEditWidgetCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expenseCustomNames = remember(categories) {
        categories
            .filter { it.enabled }
            .filter { isExpenseCustomCategory(it.categoryId) }
            .filterNot { isBlockedTestCategory(it.name) }
            .map { categoryCustomDisplayName(it.name) }
            .distinct()
    }
    val incomeCustomNames = remember(categories) {
        categories
            .filter { it.enabled }
            .filter { it.categoryId.startsWith("custom_income_") }
            .filterNot { isBlockedTestCategory(it.name) }
            .map { categoryCustomDisplayName(it.name) }
            .distinct()
    }
    val expenseBuiltInCategories = remember { expenseBuiltInCategories() }
    val incomeBuiltInCategories = remember { incomeBuiltInCategories() }
    val expenseCategories = remember(settings, expenseCustomNames) {
        orderedCategoryList(
            settings = settings,
            ledgerType = LedgerType.EXPENSE,
            builtInCategories = expenseBuiltInCategories,
            customCategories = expenseCustomNames,
        )
    }
    val incomeCategories = remember(settings, incomeCustomNames) {
        orderedCategoryList(
            settings = settings,
            ledgerType = LedgerType.INCOME,
            builtInCategories = incomeBuiltInCategories,
            customCategories = incomeCustomNames,
        )
    }
    val builtInCategoryNames = remember {
        setOf(
            "餐饮",
            "交通",
            "购物",
            "娱乐",
            "日用",
            "日用品",
            "服务",
            "教育",
            "医疗",
            "居住",
            "房租",
            "其他",
            "服饰",
            "运动",
            "旅行",
            "宠物",
            "书籍",
            "数码",
            "美容",
            "保险",
            "人情",
            "礼物",
            "还款",
            "工资",
            "报销",
            "转账",
            "红包",
            "退款",
            "奖金",
            "兼职",
            "理财",
            "生意",
            "分红",
            "利息",
            "补贴",
            "礼金",
        )
    }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var selectedLedgerType by remember { mutableStateOf(LedgerType.EXPENSE) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showQuickCategoryManager by remember { mutableStateOf(false) }
    var customCategory by remember { mutableStateOf("") }
    val view = LocalView.current
    val density = LocalDensity.current
    val amountPulse = remember { Animatable(1f) }
    val amountShake = remember { Animatable(0f) }
    var amountPulseReady by remember { mutableStateOf(false) }
    var recordFeedbackText by remember { mutableStateOf<String?>(null) }
    var recordFeedbackNonce by remember { mutableIntStateOf(0) }
    var errorShakeNonce by remember { mutableIntStateOf(0) }
    val shakeDistancePx = with(density) { 8.dp.toPx() }
    LaunchedEffect(amountText) {
        if (amountPulseReady) {
            amountPulse.snapTo(1f)
            amountPulse.animateTo(
                MotionSpec.AmountPulseScale,
                tween(MotionSpec.AmountPulseDurationMillis / 2),
            )
            amountPulse.animateTo(
                1f,
                tween(MotionSpec.AmountPulseDurationMillis / 2),
            )
        } else {
            amountPulseReady = true
        }
    }
    LaunchedEffect(errorShakeNonce) {
        if (errorShakeNonce > 0) {
            amountShake.snapTo(0f)
            amountShake.animateTo(-shakeDistancePx, tween(MotionSpec.ErrorShakeDurationMillis / 4))
            amountShake.animateTo(shakeDistancePx, tween(MotionSpec.ErrorShakeDurationMillis / 4))
            amountShake.animateTo(-shakeDistancePx * 0.45f, tween(MotionSpec.ErrorShakeDurationMillis / 4))
            amountShake.animateTo(0f, tween(MotionSpec.ErrorShakeDurationMillis / 4))
        }
    }
    LaunchedEffect(recordFeedbackNonce) {
        if (recordFeedbackNonce > 0) {
            delay(MotionSpec.SuccessFeedbackDurationMillis.toLong())
            recordFeedbackText = null
        }
    }
    val pinnedCustomCategories = remember(categories, selectedLedgerType) {
        categories
            .filter { it.enabled }
            .filter {
                if (selectedLedgerType == LedgerType.INCOME) {
                    it.categoryId.startsWith("custom_income_")
                } else {
                    isExpenseCustomCategory(it.categoryId)
                }
            }
            .filter { it.name !in builtInCategoryNames }
            .filterNot { isBlockedTestCategory(it.name) }
            .sortedByDescending { it.displayOrder }
            .map { categoryCustomDisplayName(it.name) }
            .distinct()
    }
    val primaryExpenseCategories = remember {
        expenseBuiltInCategories
            .filterNot { it == OtherCategoryEntry }
            .take(QuickCategorySelectableLimit)
    }
    val primaryIncomeCategories = remember {
        incomeBuiltInCategories
            .filterNot { it == OtherCategoryEntry }
            .take(QuickCategorySelectableLimit)
    }
    val activeCategories = if (selectedLedgerType == LedgerType.INCOME) incomeCategories else expenseCategories
    val selectableActiveCategories = remember(activeCategories) {
        activeCategories.filterNot { it == OtherCategoryEntry }
    }
    var selectedCategory by remember(selectedLedgerType, activeCategories) {
        mutableStateOf(selectableActiveCategories.firstOrNull() ?: activeCategories.firstOrNull() ?: OtherCategoryEntry)
    }
    val amountCents = MoneyFormatter.yuanToCents(amountText)?.coerceAtLeast(0L) ?: 0L
    val amountIsValid = amountCents > 0L
    val canRecord = amountIsValid && selectedCategory != OtherCategoryEntry
    val amountDisplay = MoneyFormatter.centsToDisplay(amountCents)
    val defaultPrimaryCategories = if (selectedLedgerType == LedgerType.INCOME) {
        val builtInCount = (QuickCategorySelectableLimit - pinnedCustomCategories.size).coerceIn(0, QuickCategorySelectableLimit)
        (primaryIncomeCategories.take(builtInCount) + pinnedCustomCategories).distinct()
    } else {
        val builtInCount = (QuickCategorySelectableLimit - pinnedCustomCategories.size).coerceIn(0, QuickCategorySelectableLimit)
        (primaryExpenseCategories.take(builtInCount) + pinnedCustomCategories).distinct()
    }
    val savedPrimaryCategories = remember(settings, selectedLedgerType, selectableActiveCategories) {
        settings
            .firstOrNull { it.key == quickCategorySettingKey(selectedLedgerType) }
            ?.value
            ?.split("|")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() && it in selectableActiveCategories }
            ?.distinct()
            .orEmpty()
    }
    val primarySeed = if (savedPrimaryCategories.isNotEmpty()) {
        savedPrimaryCategories
    } else {
        defaultPrimaryCategories
    }
    val primaryCategories = (primarySeed + selectableActiveCategories.filterNot { it in primarySeed })
        .distinct()
        .take(QuickCategorySelectableLimit)

    fun categoryOrderWithQuick(nextQuickCategories: List<String>): List<String> {
        val cleanQuickCategories = nextQuickCategories.filterNot { it == OtherCategoryEntry }.distinct()
        val removedFromQuick = primaryCategories.filterNot { it in cleanQuickCategories }
        return (
            cleanQuickCategories +
                selectableActiveCategories.filterNot { it in cleanQuickCategories || it in removedFromQuick } +
                removedFromQuick
            ).distinct()
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(GapSm),
    ) {
        MinimalCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(GapMd),
            ) {
            Column(verticalArrangement = Arrangement.spacedBy(GapXs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "快速记账",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            lineHeight = 28.sp,
                        ),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TutorialButton(onClick = onShowGuide)
                        WidgetCardEditorButton(onClick = onEditWidgetCard)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "输入金额，选择分类，3 秒完成。",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                    )
                    QuickDateButton(
                        label = formatQuickDateLabel(selectedDateMillis),
                        onClick = {
                            performAppHaptic(view, AppHaptic.Selection)
                            showDatePicker = true
                        },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftPaper, RoundedCornerShape(999.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                RemoteActionButton(
                    label = "支出",
                    selected = selectedLedgerType == LedgerType.EXPENSE,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                ) {
                    selectedLedgerType = LedgerType.EXPENSE
                    selectedCategory = expenseCategories.firstOrNull { it != OtherCategoryEntry } ?: OtherCategoryEntry
                    showCategoryPicker = false
                }
                RemoteActionButton(
                    label = "收入",
                    selected = selectedLedgerType == LedgerType.INCOME,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                ) {
                    selectedLedgerType = LedgerType.INCOME
                    selectedCategory = incomeCategories.firstOrNull { it != OtherCategoryEntry } ?: OtherCategoryEntry
                    showCategoryPicker = false
                }
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = sanitizeMoneyInput(it) },
                modifier = Modifier
                    .offset { IntOffset(amountShake.value.roundToInt(), 0) }
                    .graphicsLayer {
                        scaleX = amountPulse.value
                        scaleY = amountPulse.value
                    }
                    .fillMaxWidth()
                    .heightIn(min = 96.dp),
                singleLine = true,
                label = { Text("金额") },
                placeholder = { Text("输入金额") },
                prefix = {
                    Text(
                        text = "¥",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 36.sp),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 44.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(RadiusLg),
                colors = minimalTextFieldColors(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "分类",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CategoryHeaderAction(
                        label = "编辑分类",
                        onClick = { showQuickCategoryManager = true },
                    )
                }
            }

            CategoryGrid(
                categories = primaryCategories,
                selectedCategory = selectedCategory,
                onSelect = { selectedCategory = it },
                onOther = { showCategoryPicker = true },
                onManage = { showQuickCategoryManager = true },
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it.take(40) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("商户 / 备注") },
                colors = minimalTextFieldColors(),
                shape = RoundedCornerShape(RadiusLg),
            )

            val recordInteractionSource = remember { MutableInteractionSource() }
            val recordScale = rememberPressScale(recordInteractionSource)
            val recordContainerColor = if (canRecord) MaterialTheme.colorScheme.primary else SoftPaper
            val recordContentColor = if (canRecord) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Button(
                onClick = {
                    if (canRecord) {
                        performAppHaptic(view, AppHaptic.Success)
                        recordFeedbackText = "已记入 ✓"
                        recordFeedbackNonce += 1
                        onAdd(amountText, "", selectedCategory, noteText, selectedLedgerType, selectedDateMillis)
                        amountText = ""
                        noteText = ""
                    } else {
                        performAppHaptic(view, AppHaptic.Warning)
                        recordFeedbackText = if (!amountIsValid) "请输入金额" else "请选择分类"
                        recordFeedbackNonce += 1
                        errorShakeNonce += 1
                    }
                },
                interactionSource = recordInteractionSource,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = recordScale
                        scaleY = recordScale
                    }
                    .fillMaxWidth(),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = recordContainerColor,
                    contentColor = recordContentColor,
                ),
            ) {
                Text(
                    text = recordFeedbackText ?: if (amountIsValid) "记入 $amountDisplay" else "记入",
                    modifier = Modifier.padding(vertical = 9.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            }
        }
    }

    if (showCategoryPicker) {
        val moreCategories = selectableActiveCategories
            .filterNot { it in primaryCategories }
            .distinct()
        fun persistCategoryOrder(nextMoreCategories: List<String>) {
            onUpdateCategoryOrder(
                selectedLedgerType,
                (primaryCategories + nextMoreCategories).distinct(),
            )
        }
        CategoryPickerSheet(
            title = if (selectedLedgerType == LedgerType.EXPENSE) "选择支出类型" else "选择收入类型",
            categories = moreCategories,
            selectedCategory = selectedCategory,
            customCategory = customCategory,
            onCustomCategoryChange = { customCategory = it.take(8) },
            onSelect = { category ->
                selectedCategory = category
                showCategoryPicker = false
            },
            onCategoriesChange = { next ->
                persistCategoryOrder(next)
                if (selectedCategory !in primaryCategories && selectedCategory !in next) {
                    selectedCategory = primaryCategories.firstOrNull()
                        ?: next.firstOrNull()
                        ?: OtherCategoryEntry
                }
            },
            onAddCategory = {
                val clean = customCategory.trim()
                if (clean.isNotBlank() && clean != OtherCategoryEntry) {
                    onAddCategory(clean, selectedLedgerType)
                    selectedCategory = clean
                    onUpdateCategoryOrder(
                        selectedLedgerType,
                        (primaryCategories + moreCategories + clean).distinct(),
                    )
                    customCategory = ""
                    showCategoryPicker = false
                }
            },
            onDismiss = { showCategoryPicker = false },
        )
    }
    if (showDatePicker) {
        MinimalDatePickerDialog(
            title = "记账日期",
            initialMillis = selectedDateMillis,
            onConfirm = {
                selectedDateMillis = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
    if (showQuickCategoryManager) {
        QuickCategoryManagerSheet(
            title = if (selectedLedgerType == LedgerType.EXPENSE) "调整支出类型" else "调整收入类型",
            selectedCategories = primaryCategories,
            availableCategories = selectableActiveCategories,
            customCategory = customCategory,
            onCustomCategoryChange = { customCategory = it.take(8) },
            onAddCustomCategory = { clean, nextPrimary ->
                onAddCategory(clean, selectedLedgerType)
                selectedCategory = clean
                onUpdateQuickCategories(selectedLedgerType, nextPrimary)
                onUpdateCategoryOrder(
                    selectedLedgerType,
                    categoryOrderWithQuick(nextPrimary),
                )
                customCategory = ""
            },
            onUpdate = { next ->
                onUpdateQuickCategories(selectedLedgerType, next)
                onUpdateCategoryOrder(
                    selectedLedgerType,
                    categoryOrderWithQuick(next),
                )
            },
            onDismiss = { showQuickCategoryManager = false },
        )
    }
}

@Composable
private fun CategoryGrid(
    categories: List<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit,
    onOther: () -> Unit,
    onManage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleItems = remember(categories) {
        categories
            .filterNot { it == OtherCategoryEntry }
            .take(QuickCategorySelectableLimit) + OtherCategoryEntry
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        visibleItems.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { category ->
                    val isOtherEntry = category == OtherCategoryEntry
                    CategoryChipButton(
                        label = category,
                        selected = !isOtherEntry && category == selectedCategory,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        onClick = {
                            if (isOtherEntry) {
                                onOther()
                            } else {
                                onSelect(category)
                            }
                        },
                        onLongClick = {
                            if (isOtherEntry) {
                                onOther()
                            } else {
                                onManage()
                            }
                        },
                    )
                }
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryChipButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    val targetContainer = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val targetBorder = if (selected) MaterialTheme.colorScheme.primary else Hairline
    val targetText = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
    val containerColor by animateColorAsState(targetContainer, tween(MotionSpec.CategorySwitchDurationMillis), label = "category-chip-bg")
    val borderColor by animateColorAsState(targetBorder, tween(MotionSpec.CategorySwitchDurationMillis), label = "category-chip-border")
    val textColor by animateColorAsState(targetText, tween(MotionSpec.CategorySwitchDurationMillis), label = "category-chip-text")
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    performAppHaptic(view, AppHaptic.Selection)
                    onClick()
                },
                onLongClick = onLongClick,
            ),
        shape = RoundedCornerShape(RadiusMd),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 9.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryGlyph(
                label = label,
                selected = selected,
                modifier = Modifier.size(17.dp),
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = categoryDisplayName(label),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                ),
                fontWeight = FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun categoryDisplayName(name: String): String {
    return when (name) {
        "日用品" -> "日用"
        "房租" -> "居住"
        "生活缴费" -> "缴费"
        "发红包" -> "红包"
        "其他人情" -> "人情"
        "亲属卡" -> "亲属"
        else -> name
    }
}

private fun appendQuickCategory(
    current: List<String>,
    category: String,
): List<String> {
    val clean = category.trim()
    if (clean.isBlank() || clean == OtherCategoryEntry) return current.take(QuickCategorySelectableLimit)
    val next = (current + clean).distinct()
    return if (next.size <= QuickCategorySelectableLimit) {
        next
    } else {
        (next.take(QuickCategorySelectableLimit - 1) + clean).distinct().take(QuickCategorySelectableLimit)
    }
}

private fun quickCategorySettingKey(ledgerType: String): String {
    return if (ledgerType == LedgerType.INCOME) {
        "quick_categories_income_v2"
    } else {
        "quick_categories_expense_v2"
    }
}

private fun categoryOrderSettingKey(ledgerType: String): String {
    return if (ledgerType == LedgerType.INCOME) {
        "category_order_income_v2"
    } else {
        "category_order_expense_v2"
    }
}

private fun expenseBuiltInCategories(): List<String> {
    return listOf(
        "餐饮",
        "交通",
        "购物",
        "娱乐",
        "日用",
        "服务",
        "教育",
        "医疗",
        "居住",
        "其他",
        "服饰",
        "运动",
        "旅行",
        "宠物",
        "书籍",
        "数码",
        "美容",
        "保险",
        "人情",
        "礼物",
        "还款",
    )
}

private fun incomeBuiltInCategories(): List<String> {
    return listOf("工资", "报销", "转账", "红包", "退款", "奖金", "兼职", "理财", "生意", "分红", "利息", "其他", "补贴", "礼金")
}

private fun orderedCategoryList(
    settings: List<SettingEntity>,
    ledgerType: String,
    builtInCategories: List<String>,
    customCategories: List<String>,
): List<String> {
    val available = (builtInCategories + customCategories)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .filterNot { isBlockedTestCategory(it) }
        .distinct()
    val saved = settings
        .firstOrNull { it.key == categoryOrderSettingKey(ledgerType) }
        ?.value
        ?.split("|")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        ?.distinct()
        .orEmpty()
    if (saved.isEmpty()) return available
    val orderedSaved = saved
        .filter { it in available }
        .distinct()
    return (orderedSaved + available.filterNot { it in orderedSaved })
        .distinct()
        .ifEmpty { available }
}

private fun widgetAmountSettingKey(ledgerType: String): String {
    return if (ledgerType == LedgerType.INCOME) {
        "widget_amounts_income_v2"
    } else {
        "widget_amounts_expense_v2"
    }
}

private fun widgetCategorySettingKey(ledgerType: String): String {
    return if (ledgerType == LedgerType.INCOME) {
        "widget_categories_income_v2"
    } else {
        "widget_categories_expense_v2"
    }
}

private fun defaultWidgetAmountValues(ledgerType: String): List<String> {
    return if (ledgerType == LedgerType.INCOME) {
        listOf("100", "500", "1000", "-100", "5000", "-500")
    } else {
        listOf("1", "5", "10", "-1", "50", "-5")
    }
}

private fun widgetAmountValues(
    settings: List<SettingEntity>,
    ledgerType: String,
): List<String> {
    return settings
        .firstOrNull { it.key == widgetAmountSettingKey(ledgerType) }
        ?.value
        ?.split("|")
        ?.mapNotNull { canonicalWidgetAmountInput(it) }
        ?.distinct()
        ?.take(6)
        .orEmpty()
        .let { values -> (values + defaultWidgetAmountValues(ledgerType)).take(6) }
}

private fun sanitizeWidgetAmountInput(input: String): String {
    val normalized = input.trim()
        .replace(',', '.')
        .replace('，', '.')
        .replace('－', '-')
        .replace('−', '-')
        .replace('—', '-')
        .replace('–', '-')
    val negative = normalized.contains('-')
    val unsigned = normalized.replace("-", "")
    val builder = StringBuilder()
    var hasDot = false
    var integerDigits = 0
    var fractionDigits = 0
    var totalDigits = 0

    unsigned.forEach { char ->
        when {
            char == '.' && !hasDot -> {
                hasDot = true
                if (builder.isEmpty()) builder.append('0')
                builder.append('.')
            }
            char.isDigit() && !hasDot && totalDigits < 4 -> {
                builder.append(char)
                integerDigits += 1
                totalDigits += 1
            }
            char.isDigit() && hasDot && fractionDigits < 1 && totalDigits < 4 -> {
                builder.append(char)
                fractionDigits += 1
                totalDigits += 1
            }
        }
    }
    return when {
        negative && builder.isEmpty() -> "-"
        negative -> "-$builder"
        else -> builder.toString()
    }
}

private fun widgetAmountInputToCents(value: String): Long? {
    val clean = sanitizeWidgetAmountInput(value)
    if (clean.isBlank() || clean == "-" || clean == "." || clean == "-.") return null
    val negative = clean.startsWith("-")
    val unsigned = clean.removePrefix("-")
    val parts = unsigned.split('.')
    if (parts.size > 2) return null
    val integer = parts.getOrNull(0).orEmpty()
    val fraction = parts.getOrNull(1).orEmpty()
    if (integer.isBlank() || integer.length > 4 || !integer.all { it.isDigit() }) return null
    if (fraction.length > 1 || !fraction.all { it.isDigit() }) return null
    if (integer.length + fraction.length > 4) return null
    val yuan = integer.toLongOrNull() ?: return null
    val tenth = fraction.firstOrNull()?.digitToIntOrNull()?.toLong() ?: 0L
    val cents = yuan * 100L + tenth * 10L
    if (cents == 0L) return null
    return if (negative) -cents else cents
}

private fun canonicalWidgetAmountInput(value: String): String? {
    val cents = widgetAmountInputToCents(value) ?: return null
    val absCents = kotlin.math.abs(cents)
    val yuan = absCents / 100
    val tenths = (absCents % 100) / 10
    val amount = if (absCents % 100 == 0L) {
        yuan.toString()
    } else {
        "$yuan.$tenths"
    }
    return if (cents < 0L) "-$amount" else amount
}

private fun widgetCategoryValues(
    settings: List<SettingEntity>,
    ledgerType: String,
    availableCategories: List<String>,
): List<String> {
    val saved = settings
        .firstOrNull { it.key == widgetCategorySettingKey(ledgerType) }
        ?.value
        ?.split("|")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() && it in availableCategories }
        ?.distinct()
        ?.take(4)
        .orEmpty()
    return (saved + availableCategories)
        .distinct()
        .take(4)
}

private fun isBlockedTestCategory(name: String): Boolean {
    return name.trim().lowercase() in setOf("cafe", "café")
}

private fun isExpenseCustomCategory(categoryId: String): Boolean {
    return categoryId.startsWith("custom_expense_") ||
        (categoryId.startsWith("custom_") &&
            !categoryId.startsWith("custom_income_") &&
            !categoryId.startsWith("custom_expense_"))
}

private fun categoryCustomDisplayName(name: String): String {
    return when (name) {
        "日用品" -> "日用"
        "房租" -> "居住"
        else -> name
    }
}

@Composable
private fun CategoryGlyph(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (selected) Color.White else InkSoft
    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = 1.7.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val w = size.width
        val h = size.height
        val c = Offset(w / 2f, h / 2f)
        val key = when (label) {
            "日用品" -> "日用"
            "房租" -> "居住"
            "生活缴费" -> "服务"
            "通讯" -> "数码"
            "公益" -> "服务"
            "发红包" -> "红包"
            "退还" -> "退款"
            "人情" -> "服务"
            "其他人情" -> "服务"
            "亲属卡" -> "红包"
            else -> label
        }
        when (key) {
            "餐饮" -> {
                drawLine(color, Offset(w * 0.28f, h * 0.14f), Offset(w * 0.28f, h * 0.86f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.18f, h * 0.14f), Offset(w * 0.18f, h * 0.38f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.38f, h * 0.14f), Offset(w * 0.38f, h * 0.38f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.56f, h * 0.14f), Offset(w * 0.56f, h * 0.86f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.72f, h * 0.18f), Offset(w * 0.72f, h * 0.86f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.56f, h * 0.18f), Offset(w * 0.72f, h * 0.38f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "交通" -> {
                drawRoundRect(color, Offset(w * 0.18f, h * 0.36f), Size(w * 0.64f, h * 0.32f), CornerRadius(w * 0.08f), style = stroke)
                drawLine(color, Offset(w * 0.30f, h * 0.36f), Offset(w * 0.38f, h * 0.22f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.38f, h * 0.22f), Offset(w * 0.64f, h * 0.22f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.64f, h * 0.22f), Offset(w * 0.72f, h * 0.36f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawCircle(color, radius = w * 0.055f, center = Offset(w * 0.32f, h * 0.72f), style = stroke)
                drawCircle(color, radius = w * 0.055f, center = Offset(w * 0.68f, h * 0.72f), style = stroke)
            }
            "购物" -> {
                drawRoundRect(color, Offset(w * 0.24f, h * 0.34f), Size(w * 0.52f, h * 0.48f), CornerRadius(w * 0.08f), style = stroke)
                drawLine(color, Offset(w * 0.38f, h * 0.34f), Offset(w * 0.38f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.62f, h * 0.34f), Offset(w * 0.62f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawArc(color, 200f, 140f, false, Offset(w * 0.38f, h * 0.14f), Size(w * 0.24f, h * 0.22f), style = stroke)
            }
            "娱乐" -> {
                drawCircle(color, radius = w * 0.34f, center = c, style = stroke)
                drawCircle(color, radius = w * 0.025f, center = Offset(w * 0.40f, h * 0.43f))
                drawCircle(color, radius = w * 0.025f, center = Offset(w * 0.60f, h * 0.43f))
                drawArc(color, 30f, 120f, false, Offset(w * 0.35f, h * 0.42f), Size(w * 0.30f, h * 0.25f), style = stroke)
            }
            "日用" -> {
                drawRoundRect(color, Offset(w * 0.30f, h * 0.24f), Size(w * 0.40f, h * 0.58f), CornerRadius(w * 0.10f), style = stroke)
                drawLine(color, Offset(w * 0.40f, h * 0.18f), Offset(w * 0.60f, h * 0.18f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.44f, h * 0.18f), Offset(w * 0.44f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.56f, h * 0.18f), Offset(w * 0.56f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "服务" -> {
                val heart = Path().apply {
                    moveTo(w * 0.50f, h * 0.78f)
                    cubicTo(w * 0.15f, h * 0.52f, w * 0.20f, h * 0.22f, w * 0.42f, h * 0.30f)
                    cubicTo(w * 0.48f, h * 0.32f, w * 0.50f, h * 0.40f, w * 0.50f, h * 0.40f)
                    cubicTo(w * 0.50f, h * 0.40f, w * 0.52f, h * 0.32f, w * 0.58f, h * 0.30f)
                    cubicTo(w * 0.80f, h * 0.22f, w * 0.85f, h * 0.52f, w * 0.50f, h * 0.78f)
                }
                drawPath(heart, color, style = stroke)
            }
            "教育" -> {
                drawRoundRect(color, Offset(w * 0.16f, h * 0.26f), Size(w * 0.68f, h * 0.46f), CornerRadius(w * 0.05f), style = stroke)
                drawLine(color, Offset(w * 0.50f, h * 0.26f), Offset(w * 0.50f, h * 0.78f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.24f, h * 0.42f), Offset(w * 0.42f, h * 0.42f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.58f, h * 0.42f), Offset(w * 0.76f, h * 0.42f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "医疗" -> {
                drawCircle(color, radius = w * 0.34f, center = c, style = stroke)
                drawLine(color, Offset(w * 0.50f, h * 0.31f), Offset(w * 0.50f, h * 0.69f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.31f, h * 0.50f), Offset(w * 0.69f, h * 0.50f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "居住" -> {
                val roof = Path().apply {
                    moveTo(w * 0.18f, h * 0.48f)
                    lineTo(w * 0.50f, h * 0.20f)
                    lineTo(w * 0.82f, h * 0.48f)
                }
                drawPath(roof, color, style = stroke)
                drawRoundRect(color, Offset(w * 0.28f, h * 0.46f), Size(w * 0.44f, h * 0.34f), CornerRadius(w * 0.05f), style = stroke)
            }
            "工资" -> {
                drawRoundRect(color, Offset(w * 0.24f, h * 0.22f), Size(w * 0.52f, h * 0.58f), CornerRadius(w * 0.08f), style = stroke)
                drawLine(color, Offset(w * 0.34f, h * 0.38f), Offset(w * 0.66f, h * 0.38f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.34f, h * 0.54f), Offset(w * 0.58f, h * 0.54f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.64f, h * 0.66f), style = stroke)
            }
            "报销" -> {
                drawRoundRect(color, Offset(w * 0.24f, h * 0.20f), Size(w * 0.52f, h * 0.62f), CornerRadius(w * 0.06f), style = stroke)
                drawLine(color, Offset(w * 0.34f, h * 0.36f), Offset(w * 0.66f, h * 0.36f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.34f, h * 0.50f), Offset(w * 0.62f, h * 0.50f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.34f, h * 0.64f), Offset(w * 0.56f, h * 0.64f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "转账" -> {
                drawLine(color, Offset(w * 0.24f, h * 0.38f), Offset(w * 0.72f, h * 0.38f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.62f, h * 0.28f), Offset(w * 0.72f, h * 0.38f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.62f, h * 0.48f), Offset(w * 0.72f, h * 0.38f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.76f, h * 0.62f), Offset(w * 0.28f, h * 0.62f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.38f, h * 0.52f), Offset(w * 0.28f, h * 0.62f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.38f, h * 0.72f), Offset(w * 0.28f, h * 0.62f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "红包" -> {
                drawRoundRect(color, Offset(w * 0.22f, h * 0.28f), Size(w * 0.56f, h * 0.46f), CornerRadius(w * 0.08f), style = stroke)
                drawLine(color, Offset(w * 0.22f, h * 0.32f), Offset(w * 0.50f, h * 0.52f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.78f, h * 0.32f), Offset(w * 0.50f, h * 0.52f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.50f, h * 0.56f), style = stroke)
            }
            "退款", "还款" -> {
                drawArc(color, 35f, 270f, false, Offset(w * 0.24f, h * 0.24f), Size(w * 0.52f, h * 0.52f), style = stroke)
                drawLine(color, Offset(w * 0.28f, h * 0.42f), Offset(w * 0.20f, h * 0.28f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.28f, h * 0.42f), Offset(w * 0.40f, h * 0.34f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "奖金" -> {
                val star = Path().apply {
                    moveTo(w * 0.50f, h * 0.18f)
                    lineTo(w * 0.58f, h * 0.42f)
                    lineTo(w * 0.82f, h * 0.42f)
                    lineTo(w * 0.62f, h * 0.56f)
                    lineTo(w * 0.70f, h * 0.80f)
                    lineTo(w * 0.50f, h * 0.65f)
                    lineTo(w * 0.30f, h * 0.80f)
                    lineTo(w * 0.38f, h * 0.56f)
                    lineTo(w * 0.18f, h * 0.42f)
                    lineTo(w * 0.42f, h * 0.42f)
                    close()
                }
                drawPath(star, color, style = stroke)
            }
            "兼职", "生意" -> {
                drawRoundRect(color, Offset(w * 0.20f, h * 0.34f), Size(w * 0.60f, h * 0.42f), CornerRadius(w * 0.08f), style = stroke)
                drawLine(color, Offset(w * 0.38f, h * 0.34f), Offset(w * 0.38f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.62f, h * 0.34f), Offset(w * 0.62f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.38f, h * 0.24f), Offset(w * 0.62f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.20f, h * 0.50f), Offset(w * 0.80f, h * 0.50f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "理财" -> {
                drawLine(color, Offset(w * 0.22f, h * 0.78f), Offset(w * 0.78f, h * 0.78f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.30f, h * 0.68f), Offset(w * 0.30f, h * 0.78f), strokeWidth = stroke.width + 1.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.48f, h * 0.52f), Offset(w * 0.48f, h * 0.78f), strokeWidth = stroke.width + 1.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.66f, h * 0.34f), Offset(w * 0.66f, h * 0.78f), strokeWidth = stroke.width + 1.dp.toPx(), cap = StrokeCap.Round)
            }
            "服饰" -> {
                val shirt = Path().apply {
                    moveTo(w * 0.28f, h * 0.28f)
                    lineTo(w * 0.38f, h * 0.20f)
                    lineTo(w * 0.50f, h * 0.30f)
                    lineTo(w * 0.62f, h * 0.20f)
                    lineTo(w * 0.72f, h * 0.28f)
                    lineTo(w * 0.78f, h * 0.46f)
                    lineTo(w * 0.66f, h * 0.50f)
                    lineTo(w * 0.66f, h * 0.80f)
                    lineTo(w * 0.34f, h * 0.80f)
                    lineTo(w * 0.34f, h * 0.50f)
                    lineTo(w * 0.22f, h * 0.46f)
                    close()
                }
                drawPath(shirt, color, style = stroke)
            }
            "运动" -> {
                drawCircle(color, radius = w * 0.32f, center = c, style = stroke)
                drawArc(color, -70f, 140f, false, Offset(w * 0.20f, h * 0.20f), Size(w * 0.60f, h * 0.60f), style = stroke)
                drawArc(color, 110f, 140f, false, Offset(w * 0.20f, h * 0.20f), Size(w * 0.60f, h * 0.60f), style = stroke)
            }
            "旅行" -> {
                drawRoundRect(color, Offset(w * 0.24f, h * 0.34f), Size(w * 0.52f, h * 0.40f), CornerRadius(w * 0.08f), style = stroke)
                drawLine(color, Offset(w * 0.40f, h * 0.34f), Offset(w * 0.40f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.60f, h * 0.34f), Offset(w * 0.60f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.40f, h * 0.24f), Offset(w * 0.60f, h * 0.24f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.34f, h * 0.78f), style = stroke)
                drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.66f, h * 0.78f), style = stroke)
            }
            "宠物" -> {
                drawCircle(color, radius = w * 0.10f, center = Offset(w * 0.50f, h * 0.58f), style = stroke)
                drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.35f, h * 0.42f), style = stroke)
                drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.47f, h * 0.34f), style = stroke)
                drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.59f, h * 0.34f), style = stroke)
                drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.70f, h * 0.42f), style = stroke)
            }
            "书籍" -> {
                drawRoundRect(color, Offset(w * 0.18f, h * 0.26f), Size(w * 0.64f, h * 0.50f), CornerRadius(w * 0.05f), style = stroke)
                drawLine(color, Offset(w * 0.50f, h * 0.26f), Offset(w * 0.50f, h * 0.76f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.28f, h * 0.42f), Offset(w * 0.42f, h * 0.42f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.58f, h * 0.42f), Offset(w * 0.72f, h * 0.42f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            "数码" -> {
                drawRoundRect(color, Offset(w * 0.32f, h * 0.16f), Size(w * 0.36f, h * 0.68f), CornerRadius(w * 0.08f), style = stroke)
                drawCircle(color, radius = w * 0.02f, center = Offset(w * 0.50f, h * 0.76f))
            }
            "美容" -> {
                drawLine(color, Offset(w * 0.50f, h * 0.18f), Offset(w * 0.50f, h * 0.42f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.38f, h * 0.30f), Offset(w * 0.62f, h * 0.30f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.30f, h * 0.58f), Offset(w * 0.30f, h * 0.76f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.21f, h * 0.67f), Offset(w * 0.39f, h * 0.67f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawCircle(color, radius = w * 0.10f, center = Offset(w * 0.68f, h * 0.64f), style = stroke)
            }
            "保险" -> {
                val shield = Path().apply {
                    moveTo(w * 0.50f, h * 0.18f)
                    lineTo(w * 0.76f, h * 0.30f)
                    lineTo(w * 0.70f, h * 0.62f)
                    cubicTo(w * 0.66f, h * 0.74f, w * 0.56f, h * 0.82f, w * 0.50f, h * 0.86f)
                    cubicTo(w * 0.44f, h * 0.82f, w * 0.34f, h * 0.74f, w * 0.30f, h * 0.62f)
                    lineTo(w * 0.24f, h * 0.30f)
                    close()
                }
                drawPath(shield, color, style = stroke)
            }
            "更多" -> {
                val r = w * 0.035f
                listOf(0.36f, 0.64f).forEach { x ->
                    listOf(0.36f, 0.64f).forEach { y ->
                        drawRoundRect(color, Offset(w * x - r * 2, h * y - r * 2), Size(r * 4, r * 4), CornerRadius(r), style = stroke)
                    }
                }
            }
            else -> {
                drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.32f, h * 0.50f))
                drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.50f, h * 0.50f))
                drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.68f, h * 0.50f))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryPickerSheet(
    title: String,
    categories: List<String>,
    selectedCategory: String,
    customCategory: String,
    onCustomCategoryChange: (String) -> Unit,
    onSelect: (String) -> Unit,
    onCategoriesChange: (List<String>) -> Unit,
    onAddCategory: () -> Unit,
    onDismiss: () -> Unit,
) {
    var editMode by remember { mutableStateOf(false) }
    var draftCategories by remember(categories) { mutableStateOf(categories) }

    fun commitCategories(next: List<String>) {
        val clean = next.filter { it.isNotBlank() }.distinct()
        draftCategories = clean
        onCategoriesChange(clean)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 86.dp)
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Hairline),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 560.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (editMode) title.replace("选择", "调整") else title,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = if (editMode) "完成" else "关闭",
                            modifier = Modifier
                                .clickable {
                                    if (editMode) {
                                        editMode = false
                                    } else {
                                        onDismiss()
                                    }
                                }
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    if (editMode) {
                        Text(
                            text = "长按拖动排序；移出只影响当前列表。",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextFaint,
                        )
                        EditableCategoryGrid(
                            categories = draftCategories,
                            onReorder = { from, to ->
                                if (from != to && from in draftCategories.indices && to in draftCategories.indices) {
                                    commitCategories(
                                        draftCategories.toMutableList().also {
                                            val item = it.removeAt(from)
                                            it.add(to, item)
                                        },
                                    )
                                }
                            },
                            onRemove = { category ->
                                commitCategories(draftCategories.filterNot { it == category })
                            },
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            draftCategories.chunked(4).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    rowItems.forEach { category ->
                                        CategoryOptionButton(
                                            label = category,
                                            selected = category == selectedCategory,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp),
                                            onClick = { onSelect(category) },
                                            onLongClick = { editMode = true },
                                        )
                                    }
                                    repeat(4 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = customCategory,
                            onValueChange = onCustomCategoryChange,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            singleLine = true,
                            placeholder = { Text("自定义类型") },
                            colors = minimalTextFieldColors(),
                            shape = RoundedCornerShape(RadiusMd),
                        )
                        OutlinedButton(
                            onClick = onAddCategory,
                            enabled = customCategory.trim().isNotBlank() &&
                                customCategory.trim() != OtherCategoryEntry,
                            modifier = Modifier
                                .width(96.dp)
                                .height(56.dp),
                            border = BorderStroke(1.dp, Hairline),
                            shape = RoundedCornerShape(RadiusMd),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        ) {
                            Text("添加", maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickCategoryManagerSheet(
    title: String,
    selectedCategories: List<String>,
    availableCategories: List<String>,
    customCategory: String,
    onCustomCategoryChange: (String) -> Unit,
    onAddCustomCategory: (String, List<String>) -> Unit,
    onUpdate: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember(selectedCategories) { mutableStateOf(selectedCategories.take(QuickCategorySelectableLimit)) }

    fun commit(next: List<String>) {
        val clean = next
            .filter { it.isNotBlank() && it != OtherCategoryEntry }
            .distinct()
            .take(QuickCategorySelectableLimit)
        draft = clean
        onUpdate(clean)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 88.dp)
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Hairline),
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 640.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "完成",
                            modifier = Modifier
                                .clickable(onClick = onDismiss)
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = "常用分类",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "${draft.size}/$QuickCategorySelectableLimit",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextFaint,
                        )
                    }
                    Text(
                        text = "长按拖动排序；首页最后一格“其他”会固定打开更多分类。",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )
                    EditableCategoryGrid(
                        categories = draft,
                        onReorder = { from, to ->
                            if (from != to && from in draft.indices && to in draft.indices) {
                                commit(draft.toMutableList().also {
                                    val item = it.removeAt(from)
                                    it.add(to, item)
                                })
                            }
                        },
                        onRemove = { category ->
                            commit(draft.filterNot { it == category })
                        },
                    )

                    val candidates = availableCategories
                        .filterNot { it in draft || it == OtherCategoryEntry }
                        .distinct()
                    Text(
                        text = "可添加",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    if (candidates.isNotEmpty()) {
                        CategoryOptionGrid(
                            categories = candidates,
                            onSelect = { category ->
                                commit(appendQuickCategory(draft, category))
                            },
                        )
                    } else {
                        Text(
                            text = "所有分类都已在首页。",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextFaint,
                        )
                    }

                    Text(
                        text = "自定义分类",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = customCategory,
                            onValueChange = onCustomCategoryChange,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            singleLine = true,
                            placeholder = { Text("输入名称") },
                            colors = minimalTextFieldColors(),
                            shape = RoundedCornerShape(RadiusMd),
                        )
                        OutlinedButton(
                            onClick = {
                                val clean = customCategory.trim()
                                if (clean.isNotBlank() && clean != OtherCategoryEntry) {
                                    val nextDraft = appendQuickCategory(draft, clean)
                                    commit(nextDraft)
                                    onAddCustomCategory(clean, nextDraft)
                                }
                            },
                            enabled = customCategory.trim().isNotBlank() &&
                                customCategory.trim() != OtherCategoryEntry,
                            modifier = Modifier
                                .width(88.dp)
                                .height(54.dp),
                            border = BorderStroke(1.dp, Hairline),
                            shape = RoundedCornerShape(RadiusMd),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        )
                        {
                            Text("加入", maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WidgetCardEditorSheet(
    initialLedgerType: String,
    expenseCategories: List<String>,
    incomeCategories: List<String>,
    settings: List<SettingEntity>,
    onUpdateWidgetCategories: (String, List<String>) -> Unit,
    onUpdateWidgetAmounts: (String, List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var editorLedgerType by remember(initialLedgerType) {
        mutableStateOf(initialLedgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE)
    }
    val activeCategories = if (editorLedgerType == LedgerType.INCOME) incomeCategories else expenseCategories
    val savedCategories = remember(settings, editorLedgerType, activeCategories) {
        widgetCategoryValues(settings, editorLedgerType, activeCategories)
    }
    val savedAmounts = remember(settings, editorLedgerType) {
        widgetAmountValues(settings, editorLedgerType)
    }
    var categoryDraft by remember(editorLedgerType, savedCategories) { mutableStateOf(savedCategories) }
    var amountDraft by remember(editorLedgerType, savedAmounts) { mutableStateOf(savedAmounts) }
    var selectingSlot by remember { mutableIntStateOf(-1) }
    var editingTypeKeys by remember { mutableStateOf(false) }
    val typeKeyMotion = rememberInfiniteTransition()
    val typeKeyWiggle by typeKeyMotion.animateFloat(
        initialValue = -0.55f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    fun normalizedCategories(): List<String> {
        return (categoryDraft + activeCategories)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(4)
    }

    fun cleanAmountInput(input: String): String {
        return sanitizeWidgetAmountInput(input)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 84.dp)
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Hairline),
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 650.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "编辑小卡片",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "关闭",
                            modifier = Modifier
                                .clickable(onClick = onDismiss)
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = "设置桌面小卡片的金额键和类型键。类型键点选后可替换，保存后同步所有规格。",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp, lineHeight = 17.sp),
                        color = TextFaint,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftPaper, RoundedCornerShape(999.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        RemoteActionButton(
                            label = "支出",
                            selected = editorLedgerType == LedgerType.EXPENSE,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                        ) {
                            editorLedgerType = LedgerType.EXPENSE
                            selectingSlot = -1
                            editingTypeKeys = false
                        }
                        RemoteActionButton(
                            label = "收入",
                            selected = editorLedgerType == LedgerType.INCOME,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                        ) {
                            editorLedgerType = LedgerType.INCOME
                            selectingSlot = -1
                            editingTypeKeys = false
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "金额键",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted,
                            )
                            Text(
                                text = "4×2 / 4×3 都使用全部 6 个金额键",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 14.sp),
                                color = TextFaint,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        amountDraft.chunked(3).forEachIndexed { amountRowIndex, rowValues ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                rowValues.forEachIndexed { rowIndex, value ->
                                    val amountIndex = amountRowIndex * 3 + rowIndex
                                    OutlinedTextField(
                                        value = value,
                                        onValueChange = { input ->
                                            amountDraft = amountDraft.toMutableList().also {
                                                if (amountIndex in it.indices) {
                                                    it[amountIndex] = cleanAmountInput(input)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default,
                                        textStyle = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center,
                                        ),
                                        colors = minimalTextFieldColors(),
                                        shape = RoundedCornerShape(RadiusMd),
                                    )
                                }
                                repeat(3 - rowValues.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "类型键",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                        )
                        Text(
                            text = if (editingTypeKeys) "点下方类型替换当前键" else "长按或点选微调",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 14.sp),
                            color = TextFaint,
                            maxLines = 1,
                        )
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        normalizedCategories().forEachIndexed { index, category ->
                            val keyIsActive = editingTypeKeys || selectingSlot >= 0
                            val rotation = if (keyIsActive) {
                                typeKeyWiggle * if (index % 2 == 0) 1f else -1f
                            } else {
                                0f
                            }
                            CategoryOptionButton(
                                label = category,
                                selected = selectingSlot == index,
                                modifier = Modifier
                                    .width(98.dp)
                                    .height(42.dp)
                                    .graphicsLayer {
                                        rotationZ = rotation
                                        val selectedScale = if (selectingSlot == index) 1.035f else 1f
                                        scaleX = selectedScale
                                        scaleY = selectedScale
                                    },
                                onClick = {
                                    selectingSlot = index
                                    editingTypeKeys = true
                                },
                                onLongClick = {
                                    selectingSlot = index
                                    editingTypeKeys = true
                                },
                            )
                        }
                    }

                    if (selectingSlot >= 0) {
                        Text(
                            text = "替换第 ${selectingSlot + 1} 个类型",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextFaint,
                        )
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 132.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            activeCategories.distinct().forEach { category ->
                                CategoryOptionButton(
                                    label = category,
                                    selected = categoryDraft.getOrNull(selectingSlot) == category,
                                    modifier = Modifier
                                        .width(86.dp)
                                        .height(38.dp),
                                    onClick = {
                                        categoryDraft = normalizedCategories().toMutableList().also {
                                            if (selectingSlot in it.indices) {
                                                it[selectingSlot] = category
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onUpdateWidgetCategories(editorLedgerType, normalizedCategories())
                            onUpdateWidgetAmounts(
                                editorLedgerType,
                                amountDraft
                                    .map { it.trim() }
                                    .mapNotNull { value -> canonicalWidgetAmountInput(value) }
                                    .take(6),
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text("保存小卡片", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableCategoryGrid(
    categories: List<String>,
    onReorder: (from: Int, to: Int) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (categories.isEmpty()) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = 4
        val gap = 11.dp
        val cellHeight = 48.dp
        val rows = ((categories.size + columns - 1) / columns).coerceAtLeast(1)
        val cellWidth = (maxWidth - gap * (columns - 1)) / columns
        val gridHeight = cellHeight * rows + gap * (rows - 1).coerceAtLeast(0)
        val density = LocalDensity.current
        val cellWidthPx = with(density) { cellWidth.toPx() }
        val cellHeightPx = with(density) { cellHeight.toPx() }
        val gapPx = with(density) { gap.toPx() }
        var draggingCategory by remember { mutableStateOf<String?>(null) }
        var dragOffset by remember { mutableStateOf(Offset.Zero) }

        fun baseOffsetFor(index: Int): Offset {
            val row = index / columns
            val column = index % columns
            return Offset(
                x = column * (cellWidthPx + gapPx),
                y = row * (cellHeightPx + gapPx),
            )
        }

        fun targetIndexFor(currentIndex: Int, offset: Offset): Int {
            val row = currentIndex / columns
            val column = currentIndex % columns
            val centerX = column * (cellWidthPx + gapPx) + cellWidthPx / 2f + offset.x
            val centerY = row * (cellHeightPx + gapPx) + cellHeightPx / 2f + offset.y
            val targetColumn = floor(centerX / (cellWidthPx + gapPx)).toInt().coerceIn(0, columns - 1)
            val targetRow = floor(centerY / (cellHeightPx + gapPx)).toInt().coerceIn(0, rows - 1)
            return (targetRow * columns + targetColumn).coerceIn(0, categories.lastIndex)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
        ) {
            categories.forEachIndexed { index, category ->
                val row = index / columns
                val column = index % columns
                val isDragging = draggingCategory == category
                val itemOffset = if (isDragging) dragOffset else Offset.Zero
                val targetX = with(density) { (cellWidth + gap).toPx() * column }
                val targetY = with(density) { (cellHeight + gap).toPx() * row }
                val animatedX by animateFloatAsState(
                    targetValue = targetX,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                )
                val animatedY by animateFloatAsState(
                    targetValue = targetY,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                )
                EditableCategoryChip(
                    label = category,
                    isDragging = isDragging,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = animatedX.roundToInt() + itemOffset.x.roundToInt(),
                                y = animatedY.roundToInt() + itemOffset.y.roundToInt(),
                            )
                        }
                        .width(cellWidth)
                        .height(cellHeight)
                        .zIndex(if (isDragging) 2f else 1f)
                        .pointerInput(category, index, categories) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggingCategory = category
                                    dragOffset = Offset.Zero
                                },
                                onDragCancel = {
                                    draggingCategory = null
                                    dragOffset = Offset.Zero
                                },
                                onDragEnd = {
                                    draggingCategory = null
                                    dragOffset = Offset.Zero
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val currentIndex = categories.indexOf(draggingCategory ?: category).takeIf { it >= 0 } ?: index
                                    val nextOffset = dragOffset + dragAmount
                                    val targetIndex = targetIndexFor(currentIndex, nextOffset)
                                    if (targetIndex != currentIndex) {
                                        val baseCurrent = baseOffsetFor(currentIndex)
                                        val baseTarget = baseOffsetFor(targetIndex)
                                        onReorder(currentIndex, targetIndex)
                                        dragOffset = nextOffset + baseCurrent - baseTarget
                                    } else {
                                        dragOffset = nextOffset
                                    }
                                },
                            )
                        },
                    onRemove = { onRemove(category) },
                )
            }
        }
    }
}

@Composable
private fun CategoryOptionGrid(
    categories: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (categories.isEmpty()) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = 4
        val gap = 11.dp
        val cellHeight = 48.dp
        val cellWidth = (maxWidth - gap * (columns - 1)) / columns

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            categories.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                ) {
                    rowItems.forEach { category ->
                        CategoryOptionButton(
                            label = category,
                            selected = false,
                            modifier = Modifier
                                .width(cellWidth)
                                .height(cellHeight),
                            onClick = { onSelect(category) },
                        )
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(
                            modifier = Modifier
                                .width(cellWidth)
                                .height(cellHeight),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableCategoryChip(
    label: String,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
    onRemove: () -> Unit,
) {
    val chipScale by animateFloatAsState(
        targetValue = if (isDragging) 1.04f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "editable-category-scale",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isDragging) Ink else Hairline,
        animationSpec = tween(160),
        label = "editable-category-border",
    )

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = chipScale
                    scaleY = chipScale
                },
            shape = RoundedCornerShape(15.dp),
            color = SoftPaper,
            border = BorderStroke(1.dp, borderColor),
            shadowElevation = if (isDragging) 5.dp else 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryGlyph(label = label, selected = false, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = categoryDisplayName(label),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.5.sp,
                        lineHeight = 14.sp,
                    ),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-6).dp)
                .width(34.dp)
                .height(20.dp)
                .zIndex(3f)
                .clickable(onClick = onRemove),
            shape = RoundedCornerShape(999.dp),
            color = TickleSurface,
            border = BorderStroke(1.dp, Hairline),
            shadowElevation = if (isDragging) 2.dp else 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "移出",
                    color = InkSoft,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 10.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryOptionButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
        ),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else SoftPaper,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else Hairline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryGlyph(
                label = label,
                selected = selected,
                modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = categoryDisplayName(label),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                ),
                fontWeight = FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SectionLabel(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RemoteActionButton(
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    val targetBackground = when {
        selected -> MaterialTheme.colorScheme.primary
        else -> SoftPaper
    }
    val targetTextColor = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onBackground
    }
    val background by animateColorAsState(targetBackground, tween(MotionSpec.CategorySwitchDurationMillis), label = "remote-action-bg")
    val textColor by animateColorAsState(targetTextColor, tween(MotionSpec.CategorySwitchDurationMillis), label = "remote-action-text")
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    performAppHaptic(view, AppHaptic.Selection)
                    onClick()
                },
            ),
        shape = RoundedCornerShape(RadiusMd),
        color = background,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else Hairline),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun centsToEditableAmount(cents: Long): String {
    return MoneyFormatter.centsToCsvAmount(cents)
        .trimEnd('0')
        .trimEnd('.')
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ManualQuickAddCard(
    categories: List<CategoryEntity>,
    onAdd: (String, String, String, String, String) -> Unit,
    onAddCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expenseCategories = remember(categories) {
        val names = categories.filter { it.enabled }.map { it.name }
        (
            listOf(
                "餐饮",
                "交通",
                "服饰",
                "购物",
                "服务",
                "教育",
                "娱乐",
                "运动",
                "生活缴费",
                "旅行",
                "宠物",
                "医疗",
                "保险",
                "公益",
                "发红包",
                "转账",
                "亲属卡",
                "其他人情",
                "其他",
                "退还",
                "礼物",
                "日用品",
                "房租",
                "书籍",
                "数码",
                "美容",
            ) + names
        )
            .distinct()
    }
    val incomeCategories = remember {
        listOf("工资", "报销", "转账", "红包", "退款", "奖金", "兼职", "理财", "生意", "其他")
    }
    var amountText by remember { mutableStateOf("") }
    var merchantOrNote by remember { mutableStateOf("") }
    var selectedLedgerType by remember { mutableStateOf(LedgerType.EXPENSE) }
    var showAllCategories by remember { mutableStateOf(false) }
    var customCategory by remember { mutableStateOf("") }
    val activeCategories = if (selectedLedgerType == LedgerType.INCOME) incomeCategories else expenseCategories
    var selectedCategory by remember(selectedLedgerType, activeCategories) {
        mutableStateOf(activeCategories.firstOrNull() ?: "其他")
    }
    val amountIsValid = remember(amountText) {
        MoneyFormatter.yuanToCents(amountText)?.let { it > 0L } == true
    }

    MinimalCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ConfirmChip(
                    label = "支出",
                    selected = selectedLedgerType == LedgerType.EXPENSE,
                    onClick = { selectedLedgerType = LedgerType.EXPENSE },
                )
                ConfirmChip(
                    label = "收入",
                    selected = selectedLedgerType == LedgerType.INCOME,
                    onClick = { selectedLedgerType = LedgerType.INCOME },
                )
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = sanitizeMoneyInput(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("金额") },
                prefix = { Text("¥") },
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = minimalTextFieldColors(),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                activeCategories.take(if (showAllCategories) activeCategories.size else 6).forEach { category ->
                    ConfirmChip(
                        label = category,
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                    )
                }
                ConfirmChip(
                    label = if (showAllCategories) "收起" else "更多",
                    selected = false,
                    onClick = { showAllCategories = !showAllCategories },
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it.take(8) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("自定义分类") },
                    colors = minimalTextFieldColors(),
                )
                OutlinedButton(
                    onClick = {
                        val clean = customCategory.trim()
                        if (clean.isNotBlank()) {
                            onAddCategory(clean)
                            selectedCategory = clean
                            customCategory = ""
                        }
                    },
                    border = BorderStroke(1.dp, Hairline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                ) {
                    Text("添加")
                }
            }

            OutlinedTextField(
                value = merchantOrNote,
                onValueChange = { merchantOrNote = it.take(24) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("商户/备注") },
                colors = minimalTextFieldColors(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedCategory,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Button(
                    onClick = {
                        if (amountIsValid) {
                            onAdd(amountText, merchantOrNote, selectedCategory, "", selectedLedgerType)
                            amountText = ""
                            merchantOrNote = ""
                        }
                    },
                    enabled = amountIsValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Hairline,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ) {
                    Text("记入")
                }
            }
        }
    }
}

private fun sanitizeMoneyInput(raw: String, maxIntegerDigits: Int = 8): String {
    val filtered = raw.filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    if (firstDot < 0) return filtered.take(maxIntegerDigits)

    val integer = filtered.take(firstDot).take(maxIntegerDigits)
    val decimal = filtered
        .drop(firstDot + 1)
        .filter { it.isDigit() }
        .take(2)
    return "$integer.$decimal"
}

@Composable
private fun ConfirmChip(
    label: String,
    selected: Boolean,
    muted: Boolean = false,
    onClick: () -> Unit,
) {
    val targetContainer = when {
        selected -> MaterialTheme.colorScheme.primary
        muted -> SoftPaper
        else -> MaterialTheme.colorScheme.surface
    }
    val targetLabel = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        muted -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onBackground
    }
    val containerColor by animateColorAsState(targetContainer, tween(180), label = "confirm-chip-bg")
    val labelColor by animateColorAsState(targetLabel, tween(180), label = "confirm-chip-text")
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = containerColor,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else Hairline),
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .widthIn(min = 58.dp)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = labelColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun LedgerScreen(
    transactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    onUpdate: (String, String, String, String, String, String, Long) -> Unit,
    onDelete: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val monthOptions = remember(transactions) { ledgerMonthOptions(transactions) }
    var selectedMonthKey by remember { mutableStateOf(currentLedgerMonthKey()) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(LedgerDisplayMode.Expense) }
    val selectedMonth = monthOptions.firstOrNull { it.key == selectedMonthKey } ?: monthOptions.first()
    val monthTransactions = remember(transactions, selectedMonthKey) {
        transactions
            .filter { ledgerMonthKey(it.datetime) == selectedMonthKey }
            .sortedByDescending { it.datetime }
    }
    val visibleTransactions = remember(monthTransactions, selectedMode) {
        monthTransactions.filter { it.matchesLedgerMode(selectedMode) }
    }
    val dayGroups = remember(visibleTransactions) { ledgerDayGroups(visibleTransactions) }
    val totalIn = visibleTransactions.filter { it.amountCents > 0L }.sumOf { it.amountCents }
    val totalOut = visibleTransactions.filter { it.amountCents < 0L }.sumOf { kotlin.math.abs(it.amountCents) }
    val netTotal = totalIn - totalOut
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

    LaunchedEffect(monthOptions) {
        if (monthOptions.none { it.key == selectedMonthKey }) {
            selectedMonthKey = monthOptions.first().key
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            options = monthOptions,
            selectedKey = selectedMonthKey,
            onSelect = { option ->
                selectedMonthKey = option.key
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false },
        )
    }

    if (editingTransaction != null) {
        EditTransactionDialog(
            transaction = editingTransaction!!,
            categories = categories,
            onDismiss = { editingTransaction = null },
            onSave = { transactionId, merchant, amountText, category, note, ledgerType, datetimeMillis ->
                onUpdate(transactionId, merchant, amountText, category, note, ledgerType, datetimeMillis)
                editingTransaction = null
            },
            onAddCategory = onAddCategory,
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            ScreenHeader(
                title = "流水",
                subtitle = "按时间查看每一笔",
            )
        }

        item {
            LedgerMonthToolbar(
                selectedMonth = selectedMonth,
                selectedMode = selectedMode,
                totalIn = totalIn,
                totalOut = totalOut,
                onMonthClick = { showMonthPicker = true },
                onModeChange = { selectedMode = it },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item {
            LedgerMonthSummaryCard(
                monthLabel = selectedMonth.label,
                netTotal = netTotal,
                totalIn = totalIn,
                totalOut = totalOut,
                count = visibleTransactions.size,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        if (visibleTransactions.isEmpty()) {
            item {
                EmptyHint(
                    title = "${selectedMonth.label} 还没有${selectedMode.label}",
                    body = "切换月份或类型，或者去记账页记一笔。",
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        } else {
            dayGroups.forEach { group ->
                item(key = group.key) {
                    LedgerDayGroupCard(
                        group = group,
                        onEdit = { editingTransaction = it },
                        onDelete = onDelete,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LedgerMonthToolbar(
    selectedMonth: LedgerMonthOption,
    selectedMode: LedgerDisplayMode,
    totalIn: Long,
    totalOut: Long,
    onMonthClick: () -> Unit,
    onModeChange: (LedgerDisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.clickable(onClick = onMonthClick),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    text = "${selectedMonth.label} ▾",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                text = "收入 ${MoneyFormatter.centsToDisplay(totalIn)} · 支出 ${MoneyFormatter.centsToDisplay(totalOut)}",
                modifier = Modifier.animateContentSize(
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LedgerDisplayMode.entries.forEach { mode ->
                ConfirmChip(
                    label = mode.label,
                    selected = selectedMode == mode,
                    onClick = { onModeChange(mode) },
                )
            }
        }
    }
}

@Composable
private fun LedgerMonthSummaryCard(
    monthLabel: String,
    netTotal: Long,
    totalIn: Long,
    totalOut: Long,
    count: Int,
    modifier: Modifier = Modifier,
) {
    MinimalCard(
        modifier = modifier.animateContentSize(
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "$monthLabel 净额",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Crossfade(
                targetState = MoneyFormatter.centsToDisplay(netTotal),
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                label = "ledger-net-total",
            ) { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Crossfade(
                targetState = "流入 ${MoneyFormatter.centsToDisplay(totalIn)} · 流出 ${MoneyFormatter.centsToDisplay(totalOut)} · 共 $count 笔",
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                label = "ledger-summary-meta",
            ) { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LedgerDayGroupCard(
    group: LedgerDayGroup,
    onEdit: (TransactionEntity) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "${group.label} ${group.weekday}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Crossfade(
                targetState = "收入 ${MoneyFormatter.centsToDisplay(group.totalIn)} · 支出 ${MoneyFormatter.centsToDisplay(group.totalOut)}",
                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
                label = "ledger-day-total",
            ) { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        MinimalCard {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                group.transactions.forEachIndexed { index, tx ->
                    LedgerTransactionRow(
                        transaction = tx,
                        onEdit = { onEdit(tx) },
                        onDelete = { onDelete(tx.transactionId) },
                    )
                    if (index != group.transactions.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Hairline),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LedgerTransactionRow(
    transaction: TransactionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val title = transaction.category.ifBlank { "其他" }
    val subtitle = ledgerSubtitle(transaction)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Crossfade(
                targetState = MoneyFormatter.centsToDisplay(transaction.amountCents),
                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
                label = "ledger-row-amount",
            ) { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            TextButton(
                onClick = onDelete,
                contentPadding = ButtonDefaults.TextButtonContentPadding,
            ) {
                Text(
                    text = "删除",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MonthPickerDialog(
    options: List<LedgerMonthOption>,
    selectedKey: String,
    onSelect: (LedgerMonthOption) -> Unit,
    onDismiss: () -> Unit,
) {
    val selectedLabel = options.firstOrNull { it.key == selectedKey }?.label ?: "选择月份"
    MinimalPickerSheet(
        title = "选择月份",
        subtitle = selectedLabel,
        onDismiss = onDismiss,
        modifier = Modifier.heightIn(max = 440.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                PickerListOptionButton(
                    label = option.label,
                    selected = option.key == selectedKey,
                    onClick = { onSelect(option) },
                )
            }
        }
    }
}

private fun ledgerSubtitle(transaction: TransactionEntity): String {
    val merchant = transaction.merchant.trim().takeIf {
        it.isNotBlank() &&
            it != transaction.category &&
            it != "未识别商户" &&
            it != "手动记账"
    }
    return listOfNotNull(
        merchant,
        transaction.note.trim().takeIf { it.isNotBlank() },
        formatLedgerTime(transaction.datetime),
    ).joinToString(" · ")
}

private fun TransactionEntity.matchesLedgerMode(mode: LedgerDisplayMode): Boolean {
    return when (mode) {
        LedgerDisplayMode.Expense -> amountCents < 0L
        LedgerDisplayMode.Income -> amountCents > 0L
    }
}

private fun ledgerMonthOptions(transactions: List<TransactionEntity>): List<LedgerMonthOption> {
    val keys = (transactions.map { ledgerMonthKey(it.datetime) } + currentLedgerMonthKey()).distinct()
    return keys
        .map(::ledgerMonthOption)
        .sortedByDescending { it.startMillis }
}

private fun ledgerMonthOption(key: String): LedgerMonthOption {
    val parts = key.split("-")
    val year = parts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
    val month = parts.getOrNull(1)?.toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
    val start = Calendar.getInstance().apply {
        set(year, month - 1, 1, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    return LedgerMonthOption(
        key = "%04d-%02d".format(Locale.US, year, month),
        label = "${year}年${month}月",
        startMillis = start,
    )
}

private fun ledgerDayGroups(transactions: List<TransactionEntity>): List<LedgerDayGroup> {
    return transactions
        .sortedByDescending { it.datetime }
        .groupBy { ledgerDayKey(it.datetime) }
        .map { (key, values) ->
            val first = values.first()
            LedgerDayGroup(
                key = key,
                label = SimpleDateFormat("M月d日", Locale.CHINA).format(Date(first.datetime)),
                weekday = ledgerWeekday(first.datetime),
                totalIn = values.filter { it.amountCents > 0L }.sumOf { it.amountCents },
                totalOut = values.filter { it.amountCents < 0L }.sumOf { kotlin.math.abs(it.amountCents) },
                transactions = values.sortedByDescending { it.datetime },
            )
        }
}

private fun ledgerMonthKey(millis: Long): String {
    return SimpleDateFormat("yyyy-MM", Locale.CHINA).format(Date(millis))
}

private fun currentLedgerMonthKey(): String {
    return ledgerMonthKey(System.currentTimeMillis())
}

private fun ledgerDayKey(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(millis))
}

private fun formatQuickDateLabel(millis: Long, nowMillis: Long = System.currentTimeMillis()): String {
    val selected = calendarFor(millis)
    val now = calendarFor(nowMillis)
    val yesterday = calendarFor(nowMillis).apply { add(Calendar.DAY_OF_YEAR, -1) }
    return when {
        sameCalendarDay(selected, now) -> "今天"
        sameCalendarDay(selected, yesterday) -> "昨天"
        selected.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
            SimpleDateFormat("M月d日", Locale.CHINA).format(Date(millis))
        }
        else -> SimpleDateFormat("yyyy年M月d日", Locale.CHINA).format(Date(millis))
    }
}

private fun sameCalendarDay(first: Calendar, second: Calendar): Boolean {
    return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
        first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
}

private fun ledgerWeekday(millis: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = millis }
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "周日"
        Calendar.MONDAY -> "周一"
        Calendar.TUESDAY -> "周二"
        Calendar.WEDNESDAY -> "周三"
        Calendar.THURSDAY -> "周四"
        Calendar.FRIDAY -> "周五"
        else -> "周六"
    }
}

private fun formatLedgerTime(millis: Long): String {
    return SimpleDateFormat("HH:mm", Locale.CHINA).format(Date(millis))
}

private data class LedgerMonthOption(
    val key: String,
    val label: String,
    val startMillis: Long,
)

private data class LedgerDayGroup(
    val key: String,
    val label: String,
    val weekday: String,
    val totalIn: Long,
    val totalOut: Long,
    val transactions: List<TransactionEntity>,
)

private fun formatEditDate(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(millis))
}

private fun formatEditTime(millis: Long): String {
    return SimpleDateFormat("HH:mm", Locale.CHINA).format(Date(millis))
}

@Composable
private fun MinimalPickerSheet(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrimInteractionSource = remember { MutableInteractionSource() }
    val sheetInteractionSource = remember { MutableInteractionSource() }
    var isVisible by remember { mutableStateOf(false) }
    val sheetScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.98f,
        animationSpec = tween(160),
        label = "minimal-picker-scale",
    )
    val sheetAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(140),
        label = "minimal-picker-alpha",
    )
    LaunchedEffect(Unit) {
        isVisible = true
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.20f))
                .clickable(
                    interactionSource = scrimInteractionSource,
                    indication = null,
                    onClick = onDismiss,
                ),
        ) {
            val sheetMaxHeight = (maxHeight - 56.dp).coerceAtLeast(320.dp)
            Surface(
                modifier = modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .heightIn(max = sheetMaxHeight)
                    .padding(horizontal = 18.dp, vertical = 28.dp)
                    .navigationBarsPadding()
                    .graphicsLayer {
                        alpha = sheetAlpha
                        scaleX = sheetScale
                        scaleY = sheetScale
                    }
                    .clickable(
                        interactionSource = sheetInteractionSource,
                        indication = null,
                        onClick = {},
                    ),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Hairline),
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Ink,
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    content()
                }
            }
        }
    }
}

@Composable
private fun MinimalDatePickerDialog(
    title: String,
    initialMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var draftMillis by remember(initialMillis) { mutableStateOf(initialMillis) }
    val draftCalendar = remember(draftMillis) { calendarFor(draftMillis) }
    var visibleYear by remember(initialMillis) { mutableIntStateOf(draftCalendar.get(Calendar.YEAR)) }
    var visibleMonth by remember(initialMillis) { mutableIntStateOf(draftCalendar.get(Calendar.MONTH)) }
    val selectedYear = draftCalendar.get(Calendar.YEAR)
    val selectedMonth = draftCalendar.get(Calendar.MONTH)
    val selectedDay = draftCalendar.get(Calendar.DAY_OF_MONTH)
    val visibleStart = statsMillisForDate(visibleYear, visibleMonth, 1)
    val daysInMonth = statsDaysInMonth(visibleYear, visibleMonth)
    val leadingBlanks = calendarFor(visibleStart).get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

    MinimalPickerSheet(
        title = title,
        subtitle = SimpleDateFormat("yyyy年M月d日 EEEE", Locale.CHINA).format(Date(draftMillis)),
        onDismiss = onDismiss,
        modifier = Modifier.heightIn(max = 520.dp),
    ) {
        StatsPickerHeader(
            label = "${visibleYear}年${visibleMonth + 1}月",
            canGoPrevious = true,
            canGoNext = true,
            onPrevious = {
                val previous = statsAddMonths(visibleStart, -1)
                val calendar = calendarFor(previous)
                visibleYear = calendar.get(Calendar.YEAR)
                visibleMonth = calendar.get(Calendar.MONTH)
            },
            onNext = {
                val next = statsAddMonths(visibleStart, 1)
                val calendar = calendarFor(next)
                visibleYear = calendar.get(Calendar.YEAR)
                visibleMonth = calendar.get(Calendar.MONTH)
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        val cells = List(leadingBlanks) { 0 } + (1..daysInMonth).toList()
        cells.chunked(7).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { day ->
                    if (day == 0) {
                        Spacer(modifier = Modifier.weight(1f).height(34.dp))
                    } else {
                        val selected = visibleYear == selectedYear && visibleMonth == selectedMonth && day == selectedDay
                        DatePickerDayButton(
                            label = day.toString(),
                            selected = selected,
                            onClick = { draftMillis = updateDatePart(draftMillis, visibleYear, visibleMonth, day) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f).height(34.dp))
                }
            }
        }
        PickerActionRow(
            onDismiss = onDismiss,
            onConfirm = { onConfirm(draftMillis) },
        )
    }
}

@Composable
private fun DatePickerDayButton(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container by animateColorAsState(
        targetValue = when {
            selected -> Ink
            enabled -> MaterialTheme.colorScheme.surface
            else -> SoftPaper
        },
        animationSpec = tween(MotionSpec.CategorySwitchDurationMillis),
        label = "date-day-bg",
    )
    val content by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.onPrimary
            enabled -> Ink
            else -> TextMuted
        },
        animationSpec = tween(MotionSpec.CategorySwitchDurationMillis),
        label = "date-day-text",
    )
    Surface(
        modifier = modifier
            .height(34.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = container,
        border = BorderStroke(1.dp, if (selected) Ink else Hairline),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PickerListOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container by animateColorAsState(
        targetValue = if (selected) Ink else MaterialTheme.colorScheme.surface,
        animationSpec = tween(MotionSpec.CategorySwitchDurationMillis),
        label = "picker-list-bg",
    )
    val content by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else Ink,
        animationSpec = tween(MotionSpec.CategorySwitchDurationMillis),
        label = "picker-list-text",
    )
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = container,
        border = BorderStroke(1.dp, if (selected) Ink else Hairline),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MinimalTimePickerDialog(
    initialMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialCalendar = remember(initialMillis) { calendarFor(initialMillis) }
    var draftHour by remember(initialMillis) { mutableIntStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
    var draftMinute by remember(initialMillis) { mutableIntStateOf(initialCalendar.get(Calendar.MINUTE)) }
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = (draftHour - 2).coerceAtLeast(0))
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = (draftMinute - 3).coerceAtLeast(0))

    MinimalPickerSheet(
        title = "选择时间",
        subtitle = "%02d:%02d".format(Locale.US, draftHour, draftMinute),
        onDismiss = onDismiss,
        modifier = Modifier.heightIn(max = 500.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TimePickerColumn(
                label = "小时",
                values = (0..23).toList(),
                selectedValue = draftHour,
                listState = hourState,
                onSelect = { draftHour = it },
                modifier = Modifier.weight(1f),
            )
            TimePickerColumn(
                label = "分钟",
                values = (0..59).toList(),
                selectedValue = draftMinute,
                listState = minuteState,
                onSelect = { draftMinute = it },
                modifier = Modifier.weight(1f),
            )
        }
        PickerActionRow(
            onDismiss = onDismiss,
            onConfirm = { onConfirm(updateTimePart(initialMillis, draftHour, draftMinute)) },
        )
    }
}

@Composable
private fun TimePickerColumn(
    label: String,
    values: List<Int>,
    selectedValue: Int,
    listState: LazyListState,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.height(208.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            items(values) { value ->
                TimeOptionButton(
                    label = "%02d".format(Locale.US, value),
                    selected = value == selectedValue,
                    onClick = { onSelect(value) },
                )
            }
        }
    }
}

@Composable
private fun TimeOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val container by animateColorAsState(
        targetValue = if (selected) Ink else MaterialTheme.colorScheme.surface,
        animationSpec = tween(MotionSpec.CategorySwitchDurationMillis),
        label = "time-option-bg",
    )
    val content by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else Ink,
        animationSpec = tween(MotionSpec.CategorySwitchDurationMillis),
        label = "time-option-text",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(RadiusSm),
        color = container,
        border = BorderStroke(1.dp, if (selected) Ink else Hairline),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = content,
            )
        }
    }
}

@Composable
private fun PickerActionRow(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            border = BorderStroke(1.dp, Hairline),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink),
        ) {
            Text("取消")
        }
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Ink,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("确定")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditTransactionDialog(
    transaction: TransactionEntity,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, Long) -> Unit,
    onAddCategory: (String) -> Unit,
) {
    val allCategories = remember(categories, transaction.category) {
        val names = categories.filter { it.enabled }.map { it.name }
        (
            listOf(transaction.category) + names + listOf(
                "餐饮",
                "交通",
                "服饰",
                "购物",
                "服务",
                "教育",
                "娱乐",
                "运动",
                "生活缴费",
                "旅行",
                "宠物",
                "医疗",
                "保险",
                "公益",
                "发红包",
                "转账",
                "亲属卡",
                "其他人情",
                "退还",
                "礼物",
                "日用品",
                "房租",
                "书籍",
                "数码",
                "美容",
                "其他",
            )
        ).distinct()
    }
    var merchant by remember(transaction.transactionId) { mutableStateOf(transaction.merchant) }
    var amountText by remember(transaction.transactionId) {
        mutableStateOf(MoneyFormatter.centsToCsvAmount(kotlin.math.abs(transaction.amountCents)))
    }
    var category by remember(transaction.transactionId) { mutableStateOf(transaction.category) }
    var note by remember(transaction.transactionId) { mutableStateOf(transaction.note) }
    var customCategory by remember(transaction.transactionId) { mutableStateOf("") }
    var ledgerType by remember(transaction.transactionId) {
        mutableStateOf(if (transaction.amountCents >= 0L) LedgerType.INCOME else LedgerType.EXPENSE)
    }
    var selectedDateTime by remember(transaction.transactionId) { mutableStateOf(transaction.datetime) }
    var showDatePicker by remember(transaction.transactionId) { mutableStateOf(false) }
    var showTimePicker by remember(transaction.transactionId) { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("编辑流水") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ConfirmChip(
                        label = "支出",
                        selected = ledgerType == LedgerType.EXPENSE,
                        onClick = { ledgerType = LedgerType.EXPENSE },
                    )
                    ConfirmChip(
                        label = "收入",
                        selected = ledgerType == LedgerType.INCOME,
                        onClick = { ledgerType = LedgerType.INCOME },
                    )
                }
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it.take(40) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("商户") },
                    colors = minimalTextFieldColors(),
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = sanitizeMoneyInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("金额") },
                    colors = minimalTextFieldColors(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DateTimeSelectField(
                        label = "日期",
                        value = formatEditDate(selectedDateTime),
                        modifier = Modifier.weight(1f),
                        onClick = { showDatePicker = true },
                    )
                    DateTimeSelectField(
                        label = "时间",
                        value = formatEditTime(selectedDateTime),
                        modifier = Modifier.weight(1f),
                        onClick = { showTimePicker = true },
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    allCategories.forEach { item ->
                        ConfirmChip(
                            label = item,
                            selected = category == item,
                            onClick = { category = item },
                        )
                    }
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it.take(40) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("备注") },
                    colors = minimalTextFieldColors(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it.take(8) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("新增分类") },
                        colors = minimalTextFieldColors(),
                    )
                    OutlinedButton(
                        onClick = {
                            if (customCategory.isNotBlank()) {
                                val clean = customCategory.trim()
                                onAddCategory(clean)
                                category = clean
                                customCategory = ""
                            }
                        },
                        border = BorderStroke(1.dp, Hairline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onBackground,
                        ),
                    ) {
                        Text("添加")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(transaction.transactionId, merchant, amountText, category, note, ledgerType, selectedDateTime)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
    if (showDatePicker) {
        MinimalDatePickerDialog(
            title = "选择日期",
            initialMillis = selectedDateTime,
            onConfirm = {
                selectedDateTime = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
    if (showTimePicker) {
        MinimalTimePickerDialog(
            initialMillis = selectedDateTime,
            onConfirm = {
                selectedDateTime = it
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@Composable
private fun DateTimeSelectField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(7.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Hairline),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
            )
        }
    }
}

private fun calendarFor(millis: Long): Calendar {
    return Calendar.getInstance().apply { timeInMillis = millis }
}

private fun updateDatePart(millis: Long, year: Int, month: Int, dayOfMonth: Int): Long {
    return calendarFor(millis).apply {
        set(year, month, dayOfMonth)
    }.timeInMillis
}

private fun updateTimePart(millis: Long, hourOfDay: Int, minute: Int): Long {
    return calendarFor(millis).apply {
        set(Calendar.HOUR_OF_DAY, hourOfDay)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsScreen(
    transactions: List<TransactionEntity>,
    modifier: Modifier = Modifier,
) {
    var selectedScale by remember { mutableStateOf(StatsScale.Day) }
    var selectedMode by remember { mutableStateOf(LedgerDisplayMode.Expense) }
    var selectedAnchorMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTrendMode by remember { mutableStateOf(TrendChartMode.Bar) }
    var showPeriodPicker by remember { mutableStateOf(false) }
    val nowMillis = System.currentTimeMillis()
    val period = remember(transactions, selectedScale, selectedAnchorMillis, nowMillis) {
        statsPeriodForScale(transactions, selectedScale, selectedAnchorMillis, nowMillis)
    }
    val summaryTransactions = remember(transactions, period) {
        transactions.filter { it.datetime >= period.startMillis && it.datetime < period.endMillis }
    }
    val summaryModeTransactions = remember(summaryTransactions, selectedMode) {
        summaryTransactions.filter { it.matchesLedgerMode(selectedMode) }
    }
    val trendModeTransactions = remember(transactions, selectedMode, period) {
        transactions.filter {
            it.matchesLedgerMode(selectedMode) &&
                it.datetime >= period.trendStartMillis &&
                it.datetime < period.trendEndMillis
        }
    }
    val totalOut = summaryTransactions.filter { it.amountCents < 0L }.sumOf { kotlin.math.abs(it.amountCents) }
    val totalIn = summaryTransactions.filter { it.amountCents > 0L }.sumOf { it.amountCents }
    val selectedTotal = when (selectedMode) {
        LedgerDisplayMode.Expense -> totalOut
        LedgerDisplayMode.Income -> totalIn
    }
    val selectedCount = summaryModeTransactions.size
    val selectedAverage = if (selectedCount > 0) selectedTotal / selectedCount else 0L
    val buckets = remember(summaryModeTransactions) {
        summaryModeTransactions
            .groupBy { it.category }
            .map { (category, values) ->
                SpendBucket(
                    label = category,
                    amountCents = values.sumOf { kotlin.math.abs(it.amountCents) },
                    count = values.size,
                )
            }
            .sortedByDescending { it.amountCents }
    }
    val trendBuckets = remember(trendModeTransactions, selectedScale, period) {
        spendTrendBuckets(trendModeTransactions, selectedScale, period)
    }
    val periodSelectorLabel = remember(selectedScale, selectedAnchorMillis) {
        statsSelectorLabel(selectedScale, selectedAnchorMillis)
    }
    val periodDisplayLabel = remember(selectedScale, selectedAnchorMillis, selectedMode) {
        statsModePeriodLabel(selectedScale, selectedAnchorMillis, selectedMode)
    }
    val isCurrentPeriod = remember(selectedScale, selectedAnchorMillis, nowMillis) {
        statsIsSamePeriod(selectedAnchorMillis, nowMillis, selectedScale)
    }
    val canGoNext = remember(selectedScale, selectedAnchorMillis, nowMillis) {
        !statsIsFuturePeriod(statsShiftAnchor(selectedAnchorMillis, selectedScale, 1), selectedScale, nowMillis)
    }

    if (showPeriodPicker) {
        StatsPeriodPickerDialog(
            scale = selectedScale,
            anchorMillis = selectedAnchorMillis,
            transactions = transactions,
            nowMillis = nowMillis,
            onConfirm = { selected ->
                selectedAnchorMillis = selected
                showPeriodPicker = false
            },
            onDismiss = { showPeriodPicker = false },
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "统计",
                subtitle = "看清节奏，也看清构成",
            )
        }

        item {
            StatsOverviewCard(
                periodLabel = periodDisplayLabel,
                periodSelectorLabel = periodSelectorLabel,
                selectedScale = selectedScale,
                onScaleChange = { scale ->
                    selectedScale = scale
                    selectedAnchorMillis = statsClampToPresent(selectedAnchorMillis, scale, nowMillis)
                },
                selectedMode = selectedMode,
                onModeChange = { selectedMode = it },
                onPreviousPeriod = {
                    selectedAnchorMillis = statsShiftAnchor(selectedAnchorMillis, selectedScale, -1)
                },
                onNextPeriod = {
                    val next = statsShiftAnchor(selectedAnchorMillis, selectedScale, 1)
                    if (!statsIsFuturePeriod(next, selectedScale, nowMillis)) {
                        selectedAnchorMillis = next
                    }
                },
                canGoNext = canGoNext,
                isCurrentPeriod = isCurrentPeriod,
                currentPeriodLabel = statsCurrentShortcutLabel(selectedScale),
                onCurrentPeriodClick = { selectedAnchorMillis = nowMillis },
                onOpenPeriodPicker = { showPeriodPicker = true },
                primaryAmount = selectedTotal,
                transactionCount = selectedCount,
                averageAmount = selectedAverage,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item {
            MinimalCard(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = trendTitle(selectedScale, selectedMode),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            TrendChartMode.entries.forEach { mode ->
                                ConfirmChip(
                                    label = mode.label,
                                    selected = selectedTrendMode == mode,
                                    onClick = { selectedTrendMode = mode },
                                )
                            }
                        }
                    }
                    Text(
                        text = trendCaption(selectedScale, selectedMode),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (trendModeTransactions.isEmpty()) {
                        EmptyDiagramText()
                    } else {
                        RefinedTrendChart(
                            buckets = trendBuckets,
                            mode = selectedTrendMode,
                        )
                    }
                }
            }
        }

        item {
            MinimalCard(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = "${selectedMode.label}构成",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = selectedScale.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                    }
                    if (buckets.isEmpty()) {
                        EmptyDiagramText()
                    } else {
                        CategoryCompositionDiagram(
                            buckets = buckets,
                            totalCents = selectedTotal,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsOverviewCard(
    periodLabel: String,
    periodSelectorLabel: String,
    selectedScale: StatsScale,
    onScaleChange: (StatsScale) -> Unit,
    selectedMode: LedgerDisplayMode,
    onModeChange: (LedgerDisplayMode) -> Unit,
    onPreviousPeriod: () -> Unit,
    onNextPeriod: () -> Unit,
    canGoNext: Boolean,
    isCurrentPeriod: Boolean,
    currentPeriodLabel: String,
    onCurrentPeriodClick: () -> Unit,
    onOpenPeriodPicker: () -> Unit,
    primaryAmount: Long,
    transactionCount: Int,
    averageAmount: Long,
    modifier: Modifier = Modifier,
) {
    MinimalCard(
        modifier = modifier.animateContentSize(
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatsScale.entries.forEach { scale ->
                    ConfirmChip(
                        label = scale.label,
                        selected = selectedScale == scale,
                        onClick = { onScaleChange(scale) },
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LedgerDisplayMode.entries.forEach { mode ->
                    ConfirmChip(
                        label = mode.label,
                        selected = selectedMode == mode,
                        onClick = { onModeChange(mode) },
                    )
                }
            }
            StatsPeriodNavigator(
                label = periodSelectorLabel,
                canGoNext = canGoNext,
                isCurrentPeriod = isCurrentPeriod,
                currentPeriodLabel = currentPeriodLabel,
                onPrevious = onPreviousPeriod,
                onNext = onNextPeriod,
                onOpenPicker = onOpenPeriodPicker,
                onCurrent = onCurrentPeriodClick,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Crossfade(
                    targetState = periodLabel,
                    animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
                    label = "stats-period-label",
                ) { value ->
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                }
                Crossfade(
                    targetState = MoneyFormatter.centsToDisplay(primaryAmount),
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    label = "stats-primary-total",
                ) { value ->
                    Text(
                        text = value,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatsOverviewMetric(
                    label = "总${selectedMode.label}",
                    value = MoneyFormatter.centsToDisplay(primaryAmount),
                    modifier = Modifier.weight(1f),
                )
                StatsOverviewMetric(
                    label = "笔数",
                    value = "$transactionCount",
                    modifier = Modifier.weight(1f),
                )
                StatsOverviewMetric(
                    label = "单笔均值",
                    value = MoneyFormatter.centsToDisplay(averageAmount),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StatsOverviewMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(AccentSoft, RoundedCornerShape(RadiusSm))
            .animateContentSize(
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            maxLines = 1,
        )
        Crossfade(
            targetState = value,
            animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
            label = "stats-metric-value",
        ) { currentValue ->
            Text(
                text = currentValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun StatsPeriodNavigator(
    label: String,
    canGoNext: Boolean,
    isCurrentPeriod: Boolean,
    currentPeriodLabel: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenPicker: () -> Unit,
    onCurrent: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PeriodStepButton(
                label = "‹",
                enabled = true,
                onClick = onPrevious,
            )
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    )
                    .clickable(onClick = onOpenPicker),
                shape = RoundedCornerShape(RadiusSm),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Hairline),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Crossfade(
                        targetState = label,
                        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
                        label = "stats-period-nav-label",
                    ) { value ->
                        Text(
                            text = value,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            PeriodStepButton(
                label = "›",
                enabled = canGoNext,
                onClick = onNext,
            )
        }
        if (!isCurrentPeriod) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onCurrent,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                ) {
                    Text(
                        text = currentPeriodLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodStepButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(RadiusSm),
        color = if (enabled) MaterialTheme.colorScheme.surface else SoftPaper,
        border = BorderStroke(1.dp, Hairline),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = if (enabled) Ink else TextMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatsPeriodPickerDialog(
    scale: StatsScale,
    anchorMillis: Long,
    transactions: List<TransactionEntity>,
    nowMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var draftMillis by remember(scale, anchorMillis) {
        mutableStateOf(statsClampToPresent(anchorMillis, scale, nowMillis))
    }
    val maxHeight = when (scale) {
        StatsScale.Day -> 560.dp
        StatsScale.Month -> 430.dp
        StatsScale.Year -> 380.dp
    }

    MinimalPickerSheet(
        title = statsPickerTitle(scale),
        subtitle = statsSelectorLabel(scale, draftMillis),
        onDismiss = onDismiss,
        modifier = Modifier.heightIn(max = maxHeight),
    ) {
        when (scale) {
            StatsScale.Day -> DayPeriodPicker(
                draftMillis = draftMillis,
                transactions = transactions,
                nowMillis = nowMillis,
                onDraftChange = { draftMillis = it },
            )

            StatsScale.Month -> MonthPeriodPicker(
                draftMillis = draftMillis,
                transactions = transactions,
                nowMillis = nowMillis,
                onDraftChange = { draftMillis = it },
            )

            StatsScale.Year -> YearPeriodPicker(
                draftMillis = draftMillis,
                transactions = transactions,
                nowMillis = nowMillis,
                onDraftChange = { draftMillis = it },
            )
        }
        PickerActionRow(
            onDismiss = onDismiss,
            onConfirm = { onConfirm(draftMillis) },
        )
    }
}

@Composable
private fun DayPeriodPicker(
    draftMillis: Long,
    transactions: List<TransactionEntity>,
    nowMillis: Long,
    onDraftChange: (Long) -> Unit,
) {
    val draftCalendar = remember(draftMillis) { calendarFor(draftMillis) }
    var visibleYear by remember(draftMillis) { mutableIntStateOf(draftCalendar.get(Calendar.YEAR)) }
    var visibleMonth by remember(draftMillis) { mutableIntStateOf(draftCalendar.get(Calendar.MONTH)) }
    val selectedYear = draftCalendar.get(Calendar.YEAR)
    val selectedMonth = draftCalendar.get(Calendar.MONTH)
    val selectedDay = draftCalendar.get(Calendar.DAY_OF_MONTH)
    val visibleStart = statsMillisForDate(visibleYear, visibleMonth, 1)
    val canGoNext = !statsIsFuturePeriod(statsAddMonths(visibleStart, 1), StatsScale.Month, nowMillis)
    val daysInMonth = statsDaysInMonth(visibleYear, visibleMonth)
    val leadingBlanks = calendarFor(visibleStart).get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StatsPickerHeader(
            label = "${visibleYear}年${visibleMonth + 1}月",
            canGoPrevious = true,
            canGoNext = canGoNext,
            onPrevious = {
                val previous = statsAddMonths(visibleStart, -1)
                val calendar = calendarFor(previous)
                visibleYear = calendar.get(Calendar.YEAR)
                visibleMonth = calendar.get(Calendar.MONTH)
            },
            onNext = {
                val next = statsAddMonths(visibleStart, 1)
                if (!statsIsFuturePeriod(next, StatsScale.Month, nowMillis)) {
                    val calendar = calendarFor(next)
                    visibleYear = calendar.get(Calendar.YEAR)
                    visibleMonth = calendar.get(Calendar.MONTH)
                }
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        val cells = List(leadingBlanks) { 0 } + (1..daysInMonth).toList()
        cells.chunked(7).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { day ->
                    if (day == 0) {
                        Spacer(modifier = Modifier.weight(1f).height(34.dp))
                    } else {
                        val millis = statsMillisForDate(visibleYear, visibleMonth, day)
                        val selected = visibleYear == selectedYear && visibleMonth == selectedMonth && day == selectedDay
                        val enabled = !statsIsFuturePeriod(millis, StatsScale.Day, nowMillis)
                        DatePickerDayButton(
                            label = day.toString(),
                            selected = selected,
                            enabled = enabled,
                            onClick = { onDraftChange(millis) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f).height(34.dp))
                }
            }
        }
    }
}

@Composable
private fun MonthPeriodPicker(
    draftMillis: Long,
    transactions: List<TransactionEntity>,
    nowMillis: Long,
    onDraftChange: (Long) -> Unit,
) {
    val draftCalendar = remember(draftMillis) { calendarFor(draftMillis) }
    var visibleYear by remember(draftMillis) { mutableIntStateOf(draftCalendar.get(Calendar.YEAR)) }
    val selectedYear = draftCalendar.get(Calendar.YEAR)
    val selectedMonth = draftCalendar.get(Calendar.MONTH)
    val monthKeysWithData = remember(transactions) { transactions.map { statsMonthKey(it.datetime) }.toSet() }
    val currentYear = calendarFor(nowMillis).get(Calendar.YEAR)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StatsPickerHeader(
            label = "${visibleYear}年",
            canGoPrevious = true,
            canGoNext = visibleYear < currentYear,
            onPrevious = { visibleYear -= 1 },
            onNext = {
                if (visibleYear < currentYear) {
                    visibleYear += 1
                }
            },
        )
        (0..11).chunked(3).forEach { rowMonths ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowMonths.forEach { month ->
                    val millis = statsMillisForDate(visibleYear, month, 1)
                    StatsPickerOptionButton(
                        label = "${month + 1}月",
                        selected = visibleYear == selectedYear && month == selectedMonth,
                        enabled = !statsIsFuturePeriod(millis, StatsScale.Month, nowMillis),
                        hasData = monthKeysWithData.contains(statsMonthKey(millis)),
                        onClick = { onDraftChange(millis) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun YearPeriodPicker(
    draftMillis: Long,
    transactions: List<TransactionEntity>,
    nowMillis: Long,
    onDraftChange: (Long) -> Unit,
) {
    val selectedYear = calendarFor(draftMillis).get(Calendar.YEAR)
    val years = remember(transactions, nowMillis) { statsSelectableYears(transactions, nowMillis) }
    val yearsWithData = remember(transactions) { transactions.map { statsYearOf(it.datetime) }.toSet() }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        years.chunked(3).forEach { rowYears ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowYears.forEach { year ->
                    val millis = statsMillisForDate(year, Calendar.JANUARY, 1)
                    StatsPickerOptionButton(
                        label = "${year}年",
                        selected = year == selectedYear,
                        enabled = !statsIsFuturePeriod(millis, StatsScale.Year, nowMillis),
                        hasData = yearsWithData.contains(year),
                        onClick = { onDraftChange(millis) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowYears.size) {
                    Spacer(modifier = Modifier.weight(1f).height(44.dp))
                }
            }
        }
    }
}

@Composable
private fun StatsPickerHeader(
    label: String,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PeriodStepButton(label = "‹", enabled = canGoPrevious, onClick = onPrevious)
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        PeriodStepButton(label = "›", enabled = canGoNext, onClick = onNext)
    }
}

@Composable
private fun StatsPickerOptionButton(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    hasData: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = when {
        selected -> Ink
        enabled -> MaterialTheme.colorScheme.surface
        else -> SoftPaper
    }
    val content = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        enabled -> Ink
        else -> TextMuted
    }
    Surface(
        modifier = modifier
            .height(38.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(RadiusSm),
        color = container,
        border = BorderStroke(1.dp, if (selected) Ink else Hairline),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = when {
                            selected && hasData -> MaterialTheme.colorScheme.onPrimary
                            hasData -> Ink
                            else -> Color.Transparent
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun SpendBarDiagram(
    buckets: List<SpendBucket>,
    totalCents: Long,
) {
    CategoryCompositionDiagram(
        buckets = buckets,
        totalCents = totalCents,
    )
}

@Composable
private fun CategoryCompositionDiagram(
    buckets: List<SpendBucket>,
    totalCents: Long,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CompositionRail(
            buckets = buckets,
            totalCents = totalCents,
        )
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            buckets.take(8).forEachIndexed { index, bucket ->
                CategoryRankRow(
                    index = index,
                    bucket = bucket,
                    totalCents = totalCents,
                )
            }
        }
    }
}

@Composable
private fun CompositionRail(
    buckets: List<SpendBucket>,
    totalCents: Long,
    modifier: Modifier = Modifier,
) {
    val topBuckets = buckets.take(5)
    val topTotal = topBuckets.sumOf { it.amountCents }
    val remainder = (totalCents - topTotal).coerceAtLeast(0L)
    val railBuckets = if (remainder > 0L) {
        topBuckets + SpendBucket("其他", remainder, 0)
    } else {
        topBuckets
    }
    val animatedFractions = railBuckets.mapIndexed { index, bucket ->
        val target = if (totalCents <= 0L) {
            0f
        } else {
            (bucket.amountCents.toFloat() / totalCents).coerceIn(0f, 1f)
        }
        val animated by animateFloatAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
            label = "composition-rail-$index",
        )
        animated
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp),
    ) {
        val corner = CornerRadius(size.height / 2f, size.height / 2f)
        val gap = 2.dp.toPx()
        drawRoundRect(
            color = AccentSoft,
            size = size,
            cornerRadius = corner,
        )
        var startX = 0f
        animatedFractions.forEachIndexed { index, fraction ->
            val segmentWidth = (size.width * fraction - gap).coerceAtLeast(0f)
            if (segmentWidth > 0f) {
                drawRoundRect(
                    color = chartColor(index),
                    topLeft = Offset(startX, 0f),
                    size = Size(segmentWidth, size.height),
                    cornerRadius = corner,
                )
            }
            startX += size.width * fraction
        }
    }
}

@Composable
private fun CategoryRankRow(
    index: Int,
    bucket: SpendBucket,
    totalCents: Long,
) {
    val targetFraction = if (totalCents <= 0L) {
        0f
    } else {
        (bucket.amountCents.toFloat() / totalCents).coerceIn(0.01f, 1f)
    }
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "category-rank-$index",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = (index + 1).toString().padStart(2, '0'),
            style = MaterialTheme.typography.labelMedium,
            color = if (index == 0) Ink else TextMuted,
            fontWeight = if (index == 0) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.width(24.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = bucket.label.ifBlank { "其他" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (index == 0) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${bucket.count}笔",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1,
                    )
                }
                Text(
                    text = percentLabel(bucket.amountCents, totalCents),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (index == 0) Ink else TextMuted,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(AccentSoft, RoundedCornerShape(99.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .height(5.dp)
                        .background(chartColor(index), RoundedCornerShape(99.dp)),
                )
            }
        }
        Crossfade(
            targetState = MoneyFormatter.centsToDisplay(bucket.amountCents),
            animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
            label = "category-rank-amount",
            modifier = Modifier.widthIn(min = 74.dp, max = 112.dp),
        ) { value ->
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MinimalBarChart(
    buckets: List<TrendBucket>,
) {
    RefinedTrendChart(
        buckets = buckets,
        mode = TrendChartMode.Bar,
    )
}

@Composable
private fun MinimalLineChart(
    buckets: List<TrendBucket>,
) {
    RefinedTrendChart(
        buckets = buckets,
        mode = TrendChartMode.Line,
    )
}

@Composable
private fun RefinedTrendChart(
    buckets: List<TrendBucket>,
    mode: TrendChartMode,
) {
    val highlightBucket = buckets.firstOrNull { it.selected && it.amountCents > 0L }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (highlightBucket != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrendValueBubble(
                    label = highlightBucket.label,
                    amount = MoneyFormatter.centsToDisplay(highlightBucket.amountCents),
                    modifier = Modifier.widthIn(min = 92.dp, max = 128.dp),
                )
            }
        }
        TrendChartCanvas(
            buckets = buckets,
            mode = mode,
        )
        ChartAxisLabels(buckets)
    }
}

@Composable
private fun TrendChartCanvas(
    buckets: List<TrendBucket>,
    mode: TrendChartMode,
) {
    val maxValue = buckets.maxOfOrNull { it.amountCents }?.takeIf { it > 0L } ?: 1L
    val selectedIndex = buckets.indexOfFirst { it.selected }
    val highlightIndex = if (selectedIndex >= 0) selectedIndex else buckets.indexOfLast { it.amountCents > 0L }
    val animatedFractions = buckets.mapIndexed { index, bucket ->
        val target = if (bucket.amountCents <= 0L) {
            0f
        } else {
            (bucket.amountCents.toFloat() / maxValue).coerceIn(0f, 1f)
        }
        val animated by animateFloatAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
            label = "trend-fraction-$index",
        )
        animated
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .padding(bottom = 8.dp),
    ) {
        val gridColor = Color(0xFFEDEDEA)
        val mutedBar = Color(0xFFCFCFCC)
        val faintBar = Color(0xFFF1F1EE)
        val chartTop = 4.dp.toPx()
        val chartBottom = size.height - 4.dp.toPx()
        val chartHeight = (chartBottom - chartTop).coerceAtLeast(1f)
        val gridCount = 4

        repeat(gridCount) { lineIndex ->
            val y = chartTop + chartHeight * lineIndex / (gridCount - 1)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
            )
        }

        if (mode == TrendChartMode.Bar) {
            val count = buckets.size.coerceAtLeast(1)
            val slotWidth = size.width / count
            val preferredGap = when {
                count <= 8 -> 14.dp.toPx()
                count <= 14 -> 8.dp.toPx()
                else -> 4.dp.toPx()
            }
            val barWidth = (slotWidth - preferredGap).coerceIn(4.dp.toPx(), 18.dp.toPx())
            val barRadius = CornerRadius(barWidth / 2f, barWidth / 2f)

            buckets.forEachIndexed { index, bucket ->
                val centerX = slotWidth * index + slotWidth / 2f
                val x = centerX - barWidth / 2f
                drawRoundRect(
                    color = faintBar,
                    topLeft = Offset(x, chartTop),
                    size = Size(barWidth, chartHeight),
                    cornerRadius = barRadius,
                )
                if (bucket.amountCents > 0L) {
                    val barHeight = (chartHeight * animatedFractions[index]).coerceAtLeast(2.dp.toPx())
                    drawRoundRect(
                        color = if (index == highlightIndex) Ink else mutedBar,
                        topLeft = Offset(x, chartBottom - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = barRadius,
                    )
                }
            }
        } else {
            val step = if (buckets.size <= 1) 0f else size.width / (buckets.size - 1)
            val points = animatedFractions.mapIndexed { index, fraction ->
                val x = if (buckets.size <= 1) size.width / 2f else step * index
                val y = chartBottom - fraction * chartHeight
                Offset(x, y)
            }
            if (points.isNotEmpty()) {
                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
                drawPath(
                    path = path,
                    color = Ink,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
                points.forEachIndexed { index, point ->
                    if (buckets[index].amountCents > 0L) {
                        drawCircle(
                            color = if (index == highlightIndex) Ink else mutedBar,
                            radius = if (index == highlightIndex) 4.5.dp.toPx() else 2.5.dp.toPx(),
                            center = point,
                        )
                        if (index == highlightIndex) {
                            drawCircle(
                                color = TickleSurface,
                                radius = 2.dp.toPx(),
                                center = point,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendValueBubble(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(RadiusSm),
        color = TickleSurface,
        border = BorderStroke(1.dp, Hairline),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Crossfade(
                targetState = amount,
                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
                label = "trend-bubble-amount",
            ) { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun MinimalPieChart(
    buckets: List<SpendBucket>,
    totalCents: Long,
    modifier: Modifier = Modifier,
) {
    CompositionRail(
        buckets = buckets,
        totalCents = totalCents,
        modifier = modifier,
    )
}

@Composable
private fun CategoryLegendRow(
    bucket: SpendBucket,
    totalCents: Long,
    color: Color,
) {
    val percent = percentLabel(bucket.amountCents, totalCents)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape),
            )
            Text(
                text = bucket.label,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = percent,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ChartAxisLabels(
    buckets: List<TrendBucket>,
) {
    if (buckets.isEmpty()) return
    if (buckets.size == 1) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = buckets.first().label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
        }
        return
    }
    val middle = buckets[buckets.lastIndex / 2].label
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = buckets.first().label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
        Text(
            text = middle,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
        Text(
            text = buckets.last().label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
    }
}

@Composable
private fun EmptyDiagramText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 118.dp)
            .background(AccentSoft, RoundedCornerShape(RadiusMd))
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "当前时间范围还没有消费数据。",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center,
        )
    }
}

private fun percentLabel(amountCents: Long, totalCents: Long): String {
    if (totalCents <= 0L || amountCents <= 0L) return "0%"
    val percent = amountCents.toDouble() * 100.0 / totalCents.toDouble()
    return if (percent < 1.0) {
        "<1%"
    } else {
        "${percent.roundToInt()}%"
    }
}

private fun chartColor(index: Int): Color {
    return when (index) {
        0 -> Ink
        1 -> Color(0xFF3A3A3A)
        2 -> Color(0xFF6A6A6A)
        3 -> Color(0xFF969696)
        4 -> Color(0xFFC7C7C4)
        else -> Color(0xFFE0E0DC)
    }
}

@Preview(name = "Stats refined", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun StatsScreenRefinedPreview() {
    LightLedgerTheme {
        Surface(color = TickleBg) {
            StatsScreen(
                transactions = previewStatsTransactions(),
                modifier = Modifier.background(TickleBg),
            )
        }
    }
}

private fun previewStatsTransactions(): List<TransactionEntity> {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    var index = 0
    fun transaction(day: Int, amountCents: Long, category: String, merchant: String): TransactionEntity {
        val datetime = calendarFor(calendar.timeInMillis).apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 9 + (index % 8))
            set(Calendar.MINUTE, (index * 7) % 60)
        }.timeInMillis
        index += 1
        return TransactionEntity(
            transactionId = "preview-$index",
            datetime = datetime,
            amountCents = amountCents,
            merchant = merchant,
            category = category,
            ledgerType = if (amountCents > 0L) LedgerType.INCOME else LedgerType.EXPENSE,
            account = "preview",
            sourceApp = "preview",
            isConsumption = amountCents < 0L,
            rawEventId = null,
            createdAt = datetime,
            updatedAt = datetime,
        )
    }
    return listOf(
        transaction(1, -4680, "早餐", "咖啡"),
        transaction(2, -12800, "交通", "地铁"),
        transaction(4, -23900, "餐饮", "午餐"),
        transaction(6, -8900, "日用", "超市"),
        transaction(8, -42800, "餐饮", "晚餐"),
        transaction(11, -16900, "娱乐", "电影"),
        transaction(12, -7200, "交通", "打车"),
        transaction(16, -35900, "购物", "衣物"),
        transaction(18, -11800, "餐饮", "简餐"),
        transaction(22, -9900, "咖啡", "咖啡店"),
        transaction(24, -26800, "日用", "家居"),
        transaction(27, -18800, "餐饮", "聚餐"),
        transaction(28, 850000, "工资", "工资"),
    )
}

private data class SpendBucket(
    val label: String,
    val amountCents: Long,
    val count: Int,
)

private data class TrendBucket(
    val label: String,
    val amountCents: Long,
    val selected: Boolean = false,
)

private data class StatsPeriod(
    val label: String,
    val startMillis: Long,
    val endMillis: Long,
    val trendStartMillis: Long,
    val trendEndMillis: Long,
    val dayCount: Int,
    val monthCount: Int,
    val years: List<Int>,
    val highlightIndex: Int = 0,
)

private fun compactBucketsForPie(
    buckets: List<SpendBucket>,
    maxSlices: Int = 6,
): List<SpendBucket> {
    if (buckets.size <= maxSlices) return buckets
    val kept = buckets.take(maxSlices - 1)
    val rest = buckets.drop(maxSlices - 1)
    return kept + SpendBucket(
        label = "其他分类",
        amountCents = rest.sumOf { it.amountCents },
        count = rest.sumOf { it.count },
    )
}

private fun legacySpendTrendBuckets(
    transactions: List<TransactionEntity>,
    scale: StatsScale,
    period: StatsPeriod,
): List<TrendBucket> {
    val calendar = Calendar.getInstance()
    val buckets = when (scale) {
        StatsScale.Day -> (1..period.dayCount).map { day ->
            TrendBucket(label = "${day}日", amountCents = 0L)
        }

        StatsScale.Month -> (1..period.monthCount).map { month ->
            TrendBucket(label = "${month}月", amountCents = 0L)
        }

        StatsScale.Year -> period.years.map { year ->
            TrendBucket(label = year.toString(), amountCents = 0L)
        }
    }
    val yearIndex = period.years.withIndex().associate { it.value to it.index }
    val totals = mutableMapOf<Int, Long>()
    transactions.forEach { transaction ->
        calendar.timeInMillis = transaction.datetime
        val key = when (scale) {
            StatsScale.Day -> calendar.get(Calendar.DAY_OF_MONTH) - 1
            StatsScale.Month -> calendar.get(Calendar.MONTH)
            StatsScale.Year -> yearIndex[calendar.get(Calendar.YEAR)] ?: return@forEach
        }
        totals[key] = (totals[key] ?: 0L) + kotlin.math.abs(transaction.amountCents)
    }
    return buckets.mapIndexed { index, bucket ->
        bucket.copy(amountCents = totals[index] ?: 0L)
    }
}

private fun legacyStatsPeriodForScale(
    transactions: List<TransactionEntity>,
    scale: StatsScale,
): StatsPeriod {
    val now = Calendar.getInstance()
    val currentYear = now.get(Calendar.YEAR)
    val currentMonth = now.get(Calendar.MONTH)
    val currentDay = now.get(Calendar.DAY_OF_MONTH)

    return when (scale) {
        StatsScale.Day -> {
            val dayStart = Calendar.getInstance().apply {
                set(currentYear, currentMonth, currentDay, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val monthStart = Calendar.getInstance().apply {
                set(currentYear, currentMonth, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            StatsPeriod(
                label = SimpleDateFormat("M月d日 · 当日消费", Locale.CHINA).format(now.time),
                startMillis = dayStart.timeInMillis,
                endMillis = now.timeInMillis + 1,
                trendStartMillis = monthStart.timeInMillis,
                trendEndMillis = now.timeInMillis + 1,
                dayCount = currentDay,
                monthCount = currentMonth + 1,
                years = listOf(currentYear),
            )
        }

        StatsScale.Month -> {
            val monthStart = Calendar.getInstance().apply {
                set(currentYear, currentMonth, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val yearStart = Calendar.getInstance().apply {
                set(currentYear, Calendar.JANUARY, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            StatsPeriod(
                label = SimpleDateFormat("yyyy年M月 · 本月消费", Locale.CHINA).format(now.time),
                startMillis = monthStart.timeInMillis,
                endMillis = now.timeInMillis + 1,
                trendStartMillis = yearStart.timeInMillis,
                trendEndMillis = now.timeInMillis + 1,
                dayCount = currentDay,
                monthCount = currentMonth + 1,
                years = listOf(currentYear),
            )
        }

        StatsScale.Year -> {
            val years = visibleYearsForStats(transactions, currentYear)
            val yearStart = Calendar.getInstance().apply {
                set(currentYear, Calendar.JANUARY, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val trendStart = Calendar.getInstance().apply {
                set(years.first(), Calendar.JANUARY, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val trendEnd = Calendar.getInstance().apply {
                set(years.last() + 1, Calendar.JANUARY, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            StatsPeriod(
                label = "$currentYear · 本年消费",
                startMillis = yearStart.timeInMillis,
                endMillis = now.timeInMillis + 1,
                trendStartMillis = trendStart.timeInMillis,
                trendEndMillis = trendEnd.timeInMillis,
                dayCount = currentDay,
                monthCount = currentMonth + 1,
                years = years,
            )
        }
    }
}

private fun visibleYearsForStats(
    transactions: List<TransactionEntity>,
    currentYear: Int,
): List<Int> {
    val calendar = Calendar.getInstance()
    val transactionYears = transactions.map {
        calendar.timeInMillis = it.datetime
        calendar.get(Calendar.YEAR)
    }
    val maxYear = maxOf(currentYear, transactionYears.maxOrNull() ?: currentYear)
    val minDataYear = transactionYears.minOrNull() ?: currentYear
    val startYear = maxOf(minDataYear, maxYear - 5)
    return (startYear..maxYear).toList()
}

private fun spendTrendBuckets(
    transactions: List<TransactionEntity>,
    scale: StatsScale,
    period: StatsPeriod,
): List<TrendBucket> {
    val calendar = Calendar.getInstance()
    val buckets = when (scale) {
        StatsScale.Day -> (1..period.dayCount).mapIndexed { index, day ->
            TrendBucket(
                label = "${day}日",
                amountCents = 0L,
                selected = index == period.highlightIndex,
            )
        }

        StatsScale.Month -> (1..period.monthCount).mapIndexed { index, month ->
            TrendBucket(
                label = "${month}月",
                amountCents = 0L,
                selected = index == period.highlightIndex,
            )
        }

        StatsScale.Year -> period.years.mapIndexed { index, year ->
            TrendBucket(
                label = year.toString(),
                amountCents = 0L,
                selected = index == period.highlightIndex,
            )
        }
    }
    val yearIndex = period.years.withIndex().associate { it.value to it.index }
    val totals = mutableMapOf<Int, Long>()
    transactions.forEach { transaction ->
        calendar.timeInMillis = transaction.datetime
        val key = when (scale) {
            StatsScale.Day -> calendar.get(Calendar.DAY_OF_MONTH) - 1
            StatsScale.Month -> calendar.get(Calendar.MONTH)
            StatsScale.Year -> yearIndex[calendar.get(Calendar.YEAR)] ?: return@forEach
        }
        totals[key] = (totals[key] ?: 0L) + kotlin.math.abs(transaction.amountCents)
    }
    return buckets.mapIndexed { index, bucket ->
        bucket.copy(amountCents = totals[index] ?: 0L)
    }
}

private fun statsPeriodForScale(
    transactions: List<TransactionEntity>,
    scale: StatsScale,
    anchorMillis: Long,
    nowMillis: Long,
): StatsPeriod {
    val anchor = calendarFor(statsClampToPresent(anchorMillis, scale, nowMillis))
    val now = calendarFor(nowMillis)
    val anchorYear = anchor.get(Calendar.YEAR)
    val anchorMonth = anchor.get(Calendar.MONTH)
    val anchorDay = anchor.get(Calendar.DAY_OF_MONTH)
    val currentYear = now.get(Calendar.YEAR)
    val currentMonth = now.get(Calendar.MONTH)
    val currentDay = now.get(Calendar.DAY_OF_MONTH)
    val endNow = nowMillis + 1

    return when (scale) {
        StatsScale.Day -> {
            val dayStart = statsMillisForDate(anchorYear, anchorMonth, anchorDay)
            val dayEnd = statsEffectiveEnd(statsAddDays(dayStart, 1), nowMillis)
            val monthStart = statsMillisForDate(anchorYear, anchorMonth, 1)
            val monthEnd = if (anchorYear == currentYear && anchorMonth == currentMonth) {
                endNow
            } else {
                statsAddMonths(monthStart, 1)
            }
            val dayCount = if (anchorYear == currentYear && anchorMonth == currentMonth) {
                currentDay
            } else {
                statsDaysInMonth(anchorYear, anchorMonth)
            }
            StatsPeriod(
                label = "${anchorMonth + 1}月${anchorDay}日 · 当日消费",
                startMillis = dayStart,
                endMillis = dayEnd,
                trendStartMillis = monthStart,
                trendEndMillis = monthEnd,
                dayCount = dayCount,
                monthCount = anchorMonth + 1,
                years = listOf(anchorYear),
                highlightIndex = (anchorDay - 1).coerceIn(0, dayCount - 1),
            )
        }

        StatsScale.Month -> {
            val monthStart = statsMillisForDate(anchorYear, anchorMonth, 1)
            val monthEnd = statsEffectiveEnd(statsAddMonths(monthStart, 1), nowMillis)
            val yearStart = statsMillisForDate(anchorYear, Calendar.JANUARY, 1)
            val yearEnd = if (anchorYear == currentYear) {
                endNow
            } else {
                statsMillisForDate(anchorYear + 1, Calendar.JANUARY, 1)
            }
            val monthCount = if (anchorYear == currentYear) currentMonth + 1 else 12
            StatsPeriod(
                label = "${anchorYear}年${anchorMonth + 1}月 · 月度消费",
                startMillis = monthStart,
                endMillis = monthEnd,
                trendStartMillis = yearStart,
                trendEndMillis = yearEnd,
                dayCount = statsDaysInMonth(anchorYear, anchorMonth),
                monthCount = monthCount,
                years = listOf(anchorYear),
                highlightIndex = anchorMonth.coerceIn(0, monthCount - 1),
            )
        }

        StatsScale.Year -> {
            val years = visibleYearsForStats(transactions, anchorYear, currentYear)
            val yearStart = statsMillisForDate(anchorYear, Calendar.JANUARY, 1)
            val yearEnd = statsEffectiveEnd(statsMillisForDate(anchorYear + 1, Calendar.JANUARY, 1), nowMillis)
            val trendStart = statsMillisForDate(years.first(), Calendar.JANUARY, 1)
            val trendEnd = statsMillisForDate(years.last() + 1, Calendar.JANUARY, 1)
            StatsPeriod(
                label = "${anchorYear}年 · 年度消费",
                startMillis = yearStart,
                endMillis = yearEnd,
                trendStartMillis = trendStart,
                trendEndMillis = trendEnd,
                dayCount = if (anchorYear == currentYear) currentDay else statsDaysInMonth(anchorYear, anchorMonth),
                monthCount = if (anchorYear == currentYear) currentMonth + 1 else 12,
                years = years,
                highlightIndex = years.indexOf(anchorYear).coerceAtLeast(0),
            )
        }
    }
}

private fun visibleYearsForStats(
    transactions: List<TransactionEntity>,
    selectedYear: Int,
    currentYear: Int,
): List<Int> {
    val dataYears = transactions.map { statsYearOf(it.datetime) }
    val minDataYear = dataYears.minOrNull() ?: selectedYear
    val startYear = minOf(minDataYear, selectedYear, currentYear - 5)
    return (startYear..currentYear).toList()
}

private fun statsSelectableYears(
    transactions: List<TransactionEntity>,
    nowMillis: Long,
): List<Int> {
    val currentYear = statsYearOf(nowMillis)
    val dataYears = transactions.map { statsYearOf(it.datetime) }
    val startYear = minOf(dataYears.minOrNull() ?: currentYear, currentYear - 5)
    return (startYear..currentYear).toList()
}

private fun statsPickerTitle(scale: StatsScale): String {
    return when (scale) {
        StatsScale.Day -> "选择日期"
        StatsScale.Month -> "选择月份"
        StatsScale.Year -> "选择年份"
    }
}

private fun statsCurrentShortcutLabel(scale: StatsScale): String {
    return when (scale) {
        StatsScale.Day -> "回到今天"
        StatsScale.Month -> "回到本月"
        StatsScale.Year -> "回到今年"
    }
}

private fun statsSelectorLabel(scale: StatsScale, millis: Long): String {
    val calendar = calendarFor(millis)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return when (scale) {
        StatsScale.Day -> "${year}年${month}月${day}日"
        StatsScale.Month -> "${year}年${month}月"
        StatsScale.Year -> "${year}年"
    }
}

private fun statsModePeriodLabel(scale: StatsScale, millis: Long, mode: LedgerDisplayMode): String {
    val calendar = calendarFor(millis)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return when (scale) {
        StatsScale.Day -> "${month}月${day}日 · 当日${mode.label}"
        StatsScale.Month -> "${year}年${month}月 · 月度${mode.label}"
        StatsScale.Year -> "${year}年 · 年度${mode.label}"
    }
}

private fun statsShiftAnchor(anchorMillis: Long, scale: StatsScale, amount: Int): Long {
    return calendarFor(anchorMillis).apply {
        when (scale) {
            StatsScale.Day -> add(Calendar.DAY_OF_MONTH, amount)
            StatsScale.Month -> {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, amount)
            }
            StatsScale.Year -> {
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.YEAR, amount)
            }
        }
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun statsClampToPresent(anchorMillis: Long, scale: StatsScale, nowMillis: Long): Long {
    return if (statsIsFuturePeriod(anchorMillis, scale, nowMillis)) {
        nowMillis
    } else {
        anchorMillis
    }
}

private fun statsIsFuturePeriod(anchorMillis: Long, scale: StatsScale, nowMillis: Long): Boolean {
    val anchor = calendarFor(anchorMillis)
    val now = calendarFor(nowMillis)
    return when (scale) {
        StatsScale.Day -> statsStartOfDay(anchorMillis) > statsStartOfDay(nowMillis)
        StatsScale.Month -> {
            anchor.get(Calendar.YEAR) > now.get(Calendar.YEAR) ||
                (anchor.get(Calendar.YEAR) == now.get(Calendar.YEAR) && anchor.get(Calendar.MONTH) > now.get(Calendar.MONTH))
        }
        StatsScale.Year -> anchor.get(Calendar.YEAR) > now.get(Calendar.YEAR)
    }
}

private fun statsIsSamePeriod(firstMillis: Long, secondMillis: Long, scale: StatsScale): Boolean {
    val first = calendarFor(firstMillis)
    val second = calendarFor(secondMillis)
    return when (scale) {
        StatsScale.Day -> {
            first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
        }
        StatsScale.Month -> {
            first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.MONTH) == second.get(Calendar.MONTH)
        }
        StatsScale.Year -> first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
    }
}

private fun statsEffectiveEnd(rawEndMillis: Long, nowMillis: Long): Long {
    return minOf(rawEndMillis, nowMillis + 1)
}

private fun statsStartOfDay(millis: Long): Long {
    val calendar = calendarFor(millis)
    return statsMillisForDate(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH),
        day = calendar.get(Calendar.DAY_OF_MONTH),
    )
}

private fun statsMillisForDate(year: Int, month: Int, day: Int): Long {
    return Calendar.getInstance().apply {
        set(year, month, day, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun statsAddDays(millis: Long, amount: Int): Long {
    return calendarFor(millis).apply {
        add(Calendar.DAY_OF_MONTH, amount)
    }.timeInMillis
}

private fun statsAddMonths(millis: Long, amount: Int): Long {
    return calendarFor(millis).apply {
        add(Calendar.MONTH, amount)
    }.timeInMillis
}

private fun statsDaysInMonth(year: Int, month: Int): Int {
    return Calendar.getInstance().apply {
        set(year, month, 1, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.getActualMaximum(Calendar.DAY_OF_MONTH)
}

private fun statsDayKey(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(millis))
}

private fun statsMonthKey(millis: Long): String {
    return SimpleDateFormat("yyyy-MM", Locale.CHINA).format(Date(millis))
}

private fun statsYearOf(millis: Long): Int {
    return calendarFor(millis).get(Calendar.YEAR)
}

private fun trendTitle(scale: StatsScale, mode: LedgerDisplayMode): String {
    return when (scale) {
        StatsScale.Day -> "每日${mode.label}趋势"
        StatsScale.Month -> "每月${mode.label}趋势"
        StatsScale.Year -> "年度${mode.label}趋势"
    }
}

private fun trendCaption(scale: StatsScale, mode: LedgerDisplayMode): String {
    val verb = if (mode == LedgerDisplayMode.Expense) "花得多" else "收入多"
    return when (scale) {
        StatsScale.Day -> "本月按日期汇总，看到哪几天$verb。"
        StatsScale.Month -> "本年按月份汇总，看到月度${mode.label}节奏。"
        StatsScale.Year -> "按年份汇总，看到长期${mode.label}变化。"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExportScreen(
    transactions: List<TransactionEntity>,
    lastExport: ExportResult?,
    lastImport: ImportResult?,
    onExport: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(GapLg),
    ) {
        ScreenHeader(
            title = "备份",
            subtitle = "CSV 明细表会保存到 下载/tickle 文件夹",
        )
        MinimalCard(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(GapMd),
            ) {
                BackupMark()
                Text(
                    text = "数据留在本地，随时导出",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "导出后可用于自己备份，也可以放进表格里慢慢整理。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Button(
                    enabled = transactions.isNotEmpty(),
                    onClick = onExport,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = SoftPaper,
                        disabledContentColor = TextMuted,
                    ),
                ) {
                    Text(
                        text = "导出到 下载/tickle",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                OutlinedButton(
                    onClick = onImport,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Hairline),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                ) {
                    Text(
                        text = "恢复最新备份",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        if (lastExport != null) {
            MinimalCard(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("最近备份 · 下载/tickle", fontWeight = FontWeight.SemiBold)
                    lastExport.paths.forEach { path ->
                        ExportPath(path)
                    }
                }
            }
        } else {
            EmptyHint(
                title = "还没有备份",
                body = "记完至少一笔后，可以在这里导出 CSV 明细表。",
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        if (lastImport != null) {
            MinimalCard(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("最近恢复", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "已导入 ${lastImport.transactionsImported} 笔流水，${lastImport.categoriesImported} 个分类。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ExportPath(lastImport.sourceFile)
                }
            }
        }
    }
}

@Composable
private fun BackupMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(78.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(RadiusLg))
            .border(1.dp, Hairline, RoundedCornerShape(RadiusLg)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(42.dp)) {
            val strokeWidth = 3.dp.toPx()
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val cloud = Path().apply {
                moveTo(size.width * 0.22f, size.height * 0.62f)
                cubicTo(size.width * 0.12f, size.height * 0.62f, size.width * 0.08f, size.height * 0.52f, size.width * 0.12f, size.height * 0.43f)
                cubicTo(size.width * 0.16f, size.height * 0.32f, size.width * 0.29f, size.height * 0.29f, size.width * 0.37f, size.height * 0.36f)
                cubicTo(size.width * 0.42f, size.height * 0.20f, size.width * 0.64f, size.height * 0.18f, size.width * 0.73f, size.height * 0.33f)
                cubicTo(size.width * 0.86f, size.height * 0.34f, size.width * 0.94f, size.height * 0.45f, size.width * 0.91f, size.height * 0.57f)
                cubicTo(size.width * 0.88f, size.height * 0.66f, size.width * 0.79f, size.height * 0.72f, size.width * 0.68f, size.height * 0.72f)
                lineTo(size.width * 0.24f, size.height * 0.72f)
            }
            drawPath(path = cloud, color = Ink, style = stroke)
        }
    }
}

@Composable
private fun ExportPath(path: String) {
    Text(
        text = path,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun MinimalCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RadiusLg),
        border = BorderStroke(1.dp, Hairline),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        content = content,
    )
}

@Composable
private fun rememberPressScale(interactionSource: MutableInteractionSource): Float {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) MotionSpec.PressScale else 1f,
        animationSpec = tween(MotionSpec.PressDurationMillis),
        label = "press-scale",
    )
    return scale
}

private fun performAppHaptic(view: View, haptic: AppHaptic) {
    if (!MotionSpec.HapticsEnabled) return
    val feedback = when (haptic) {
        AppHaptic.Light -> HapticFeedbackConstants.KEYBOARD_TAP
        AppHaptic.Selection -> HapticFeedbackConstants.CLOCK_TICK
        AppHaptic.Success -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.CONTEXT_CLICK
        }
        AppHaptic.Warning -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.REJECT
        } else {
            HapticFeedbackConstants.LONG_PRESS
        }
    }
    view.performHapticFeedback(feedback)
}

@Composable
private fun EmptyHint(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    MinimalCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun minimalTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Hairline,
    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedPlaceholderColor = TextMuted,
    unfocusedPlaceholderColor = TextMuted,
)
