package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClassSettings
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.viewmodel.ClassTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun getTabIcon(tab: ClassTab): ImageVector = when (tab) {
    ClassTab.DASHBOARD -> Icons.Default.Dashboard
    ClassTab.STUDENTS -> Icons.Default.People
    ClassTab.GROUPS -> Icons.Default.Groups
    ClassTab.SEATING -> Icons.Default.GridView
    ClassTab.MERIT_POINTS -> Icons.Default.Stars
    ClassTab.DISCIPLINE -> Icons.Default.ReportProblem
    ClassTab.HONOR_ROLL -> Icons.Default.EmojiEvents
    ClassTab.WARNINGS -> Icons.Default.NotificationImportant
    ClassTab.EVALUATION -> Icons.Default.FactCheck
    ClassTab.STATISTICS -> Icons.Default.BarChart
    ClassTab.SETTINGS -> Icons.Default.Settings
}

@Composable
fun AppNavigationDrawerSheet(
    currentTab: ClassTab,
    onTabSelected: (ClassTab) -> Unit,
    classSettings: ClassSettings,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(RoyalBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "QUẢN LÍ LỚP 10C3",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBluePrimary
                    )
                    Text(
                        text = "${classSettings.teacherName} • ${classSettings.academicYear}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Navigation Items
            ClassTab.values().forEach { tab ->
                val selected = currentTab == tab
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = tab.title,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = getTabIcon(tab),
                            contentDescription = tab.title,
                            tint = if (selected) RoyalBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = selected,
                    onClick = {
                        onTabSelected(tab)
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = RoyalBluePrimary.copy(alpha = 0.12f),
                        selectedTextColor = RoyalBlueDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Đăng xuất") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Đăng xuất",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                selected = false,
                onClick = onSignOut,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun AppNavigationRail(
    currentTab: ClassTab,
    onTabSelected: (ClassTab) -> Unit,
    onSignOut: () -> Unit
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(RoyalBluePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClassTab.values().forEach { tab ->
                val selected = currentTab == tab
                NavigationRailItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = getTabIcon(tab),
                            contentDescription = tab.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.title.split(" ").firstOrNull() ?: tab.title,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = RoyalBluePrimary,
                        selectedTextColor = RoyalBluePrimary,
                        indicatorColor = RoyalBluePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            IconButton(onClick = onSignOut, modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Đăng xuất",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
