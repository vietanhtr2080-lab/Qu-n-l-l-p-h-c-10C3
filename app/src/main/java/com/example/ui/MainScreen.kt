package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ConductRecord
import com.example.model.Student
import com.example.ui.components.AppNavigationDrawerSheet
import com.example.ui.components.AppNavigationRail
import com.example.ui.components.getTabIcon
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DisciplineBookScreen
import com.example.ui.screens.DisciplineRecordDialog
import com.example.ui.screens.GroupManagementScreen
import com.example.ui.screens.HonorRollScreen
import com.example.ui.screens.MeritPointsScreen
import com.example.ui.screens.MeritRecordDialog
import com.example.ui.screens.MonthlyEvaluationScreen
import com.example.ui.screens.SeatingChartScreen
import com.example.ui.screens.StatisticsReportScreen
import com.example.ui.screens.StudentFormDialog
import com.example.ui.screens.StudentListScreen
import com.example.ui.screens.TeacherSettingsScreen
import com.example.ui.screens.WarningStudentsScreen
import com.example.ui.theme.RoyalBluePrimary
import com.example.viewmodel.ClassTab
import com.example.viewmodel.ClassViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ClassViewModel,
    onSignOut: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val records by viewModel.records.collectAsStateWithLifecycle()
    val seating by viewModel.seating.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val groupFilter by viewModel.selectedGroupFilter.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val filteredStudents by viewModel.filteredStudents.collectAsStateWithLifecycle()
    val evaluations by viewModel.evaluations.collectAsStateWithLifecycle()
    val honorRoll by viewModel.honorRoll.collectAsStateWithLifecycle()
    val warningStudents by viewModel.warningStudents.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Quick Action Dialogs triggered from Dashboard
    var showQuickAddStudentDialog by remember { mutableStateOf(false) }
    var showQuickAddRecordDialog by remember { mutableStateOf(false) }

    // Handle Back Press on Subscreens
    if (currentTab != ClassTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(ClassTab.DASHBOARD)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppNavigationDrawerSheet(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    classSettings = settings,
                    drawerState = drawerState,
                    scope = scope,
                    onSignOut = onSignOut
                )
            }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Wide Screen Navigation Rail
                if (isWideScreen) {
                    AppNavigationRail(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        onSignOut = onSignOut
                    )
                }

                // Main Content
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = currentTab.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Mở danh mục điều hướng"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = RoyalBluePrimary
                            )
                        )
                    },
                    bottomBar = {
                        // Quick 4-tab Bottom Navigation on compact screens
                        if (!isWideScreen) {
                            val primaryTabs = listOf(
                                ClassTab.DASHBOARD,
                                ClassTab.STUDENTS,
                                ClassTab.MERIT_POINTS,
                                ClassTab.DISCIPLINE
                            )
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                primaryTabs.forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.selectTab(tab) },
                                        icon = {
                                            Icon(
                                                imageVector = getTabIcon(tab),
                                                contentDescription = tab.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.title.split(" ").firstOrNull() ?: tab.title,
                                                fontSize = 10.sp
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            ClassTab.DASHBOARD -> DashboardScreen(
                                students = students,
                                records = records,
                                settings = settings,
                                selectedMonth = selectedMonth,
                                onNavigateTab = { viewModel.selectTab(it) },
                                onSeedData = { viewModel.seedSampleData() },
                                onQuickAddStudent = { showQuickAddStudentDialog = true },
                                onQuickAddRecord = { showQuickAddRecordDialog = true }
                            )

                            ClassTab.STUDENTS -> StudentListScreen(
                                students = filteredStudents,
                                searchQuery = searchQuery,
                                selectedGroupFilter = groupFilter,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onGroupFilterChange = { viewModel.setGroupFilter(it) },
                                onSaveStudent = { viewModel.saveStudent(it) },
                                onDeleteStudent = { viewModel.deleteStudent(it) },
                                onAssignRole = { st, newRole, targetGrp ->
                                    viewModel.assignStudentRole(st, newRole, targetGrp)
                                }
                            )

                            ClassTab.GROUPS -> GroupManagementScreen(
                                students = students,
                                records = records,
                                selectedMonth = selectedMonth,
                                onAssignRole = { st, newRole, targetGrp ->
                                    viewModel.assignStudentRole(st, newRole, targetGrp)
                                },
                                onGroupLeaderSubmitMinus = { leader, grp, target, cat, pts, desc, act, date ->
                                    viewModel.groupLeaderSubmitDemeritPoint(
                                        leaderName = leader?.fullName ?: "Tổ trưởng Tổ $grp",
                                        groupNumber = grp,
                                        targetStudent = target,
                                        category = cat,
                                        points = pts,
                                        description = desc,
                                        actionTaken = act,
                                        date = date
                                    )
                                }
                            )

                            ClassTab.SEATING -> SeatingChartScreen(
                                students = students,
                                seating = seating,
                                onAssignSeat = { seatId, row, col, pos, studentId, studentName, groupNum, role ->
                                    viewModel.assignSeat(seatId, row, col, pos, studentId, studentName, groupNum, role)
                                },
                                onClearSeat = { viewModel.clearSeat(it) },
                                onAutoAssignByGroup = { viewModel.autoAssignSeatsByGroup() },
                                onAutoAssignAlphabetical = { viewModel.autoAssignSeatsAlphabetical() },
                                onAutoAssignRandom = { viewModel.autoAssignSeatsRandom() },
                                onAutoAssignFromText = { viewModel.autoAssignFromTextNames(it) },
                                onLoadReferenceSeatingChart = { viewModel.loadReferenceSeatingChart() },
                                onClearAllSeats = { viewModel.clearAllSeats() }
                            )

                            ClassTab.MERIT_POINTS -> MeritPointsScreen(
                                students = students,
                                records = records,
                                selectedMonth = selectedMonth,
                                onSaveRecord = { viewModel.saveRecord(it) },
                                onDeleteRecord = { viewModel.deleteRecord(it) }
                            )

                            ClassTab.DISCIPLINE -> DisciplineBookScreen(
                                students = students,
                                records = records,
                                selectedMonth = selectedMonth,
                                onSaveRecord = { viewModel.saveRecord(it) },
                                onToggleResolved = { viewModel.toggleRecordResolved(it) },
                                onDeleteRecord = { viewModel.deleteRecord(it) }
                            )

                            ClassTab.HONOR_ROLL -> HonorRollScreen(
                                honorList = honorRoll,
                                selectedMonth = selectedMonth
                            )

                            ClassTab.WARNINGS -> WarningStudentsScreen(
                                warningList = warningStudents,
                                selectedMonth = selectedMonth,
                                teacherName = settings.teacherName
                            )

                            ClassTab.EVALUATION -> MonthlyEvaluationScreen(
                                evaluations = evaluations,
                                selectedMonth = selectedMonth
                            )

                            ClassTab.STATISTICS -> StatisticsReportScreen(
                                students = students,
                                records = records,
                                settings = settings,
                                selectedMonth = selectedMonth
                            )

                            ClassTab.SETTINGS -> TeacherSettingsScreen(
                                settings = settings,
                                selectedMonth = selectedMonth,
                                onSaveSettings = { viewModel.saveSettings(it) },
                                onSelectMonth = { viewModel.setSelectedMonth(it) },
                                onSeedData = { viewModel.seedSampleData() },
                                onSignOut = onSignOut
                            )
                        }
                    }
                }
            }
        }
    }

    // Quick Add Student Dialog
    if (showQuickAddStudentDialog) {
        StudentFormDialog(
            student = null,
            onDismiss = { showQuickAddStudentDialog = false },
            onSave = {
                viewModel.saveStudent(it)
                showQuickAddStudentDialog = false
            }
        )
    }

    // Quick Add Record Dialog
    if (showQuickAddRecordDialog) {
        MeritRecordDialog(
            students = students,
            selectedMonth = selectedMonth,
            onDismiss = { showQuickAddRecordDialog = false },
            onSave = {
                viewModel.saveRecord(it)
                showQuickAddRecordDialog = false
            }
        )
    }
}
