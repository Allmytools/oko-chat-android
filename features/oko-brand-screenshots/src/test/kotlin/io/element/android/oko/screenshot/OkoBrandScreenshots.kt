/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.oko.screenshot

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.element.android.compound.colors.OkoPalette
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.theme.Theme
import io.element.android.features.home.impl.HomeStatePreviewParam
import io.element.android.features.home.impl.HomeView
import io.element.android.features.login.impl.screens.onboarding.OnBoardingView
import io.element.android.features.login.impl.screens.onboarding.anOnBoardingState
import io.element.android.features.messages.impl.MessagesView
import io.element.android.features.messages.impl.aMessagesState
import io.element.android.features.preferences.impl.root.PreferencesRootView
import io.element.android.features.preferences.impl.root.aPreferencesRootState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.emoji.api.picker.NoOpEmojiPickerRenderer
import org.junit.Rule
import org.junit.Test

class OkoLightScreenshots : OkoBrandScreenshots(Theme.Light, NightMode.NOTNIGHT)

class OkoDarkScreenshots : OkoBrandScreenshots(Theme.Dark, NightMode.NIGHT)

abstract class OkoBrandScreenshots(
    private val theme: Theme,
    nightMode: NightMode,
) {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig(screenWidth = 1080, screenHeight = 2160, nightMode = nightMode),
        maxPercentDifference = 0.01,
    )

    @Test
    fun welcome() = snapshot {
        OnBoardingView(
            state = anOnBoardingState(
                productionApplicationName = "OKO",
                customLogoResId = R.drawable.onboarding_logo,
                canCreateAccount = false,
            ),
            onBackClick = {},
            onDeveloperSettingsClick = {},
            onSignInWithQrCode = {},
            onSignIn = {},
            onCreateAccount = {},
            onOAuthDetails = {},
            onNeedLoginPassword = {},
            onLearnMoreClick = {},
            onReportProblem = {},
        )
    }

    @Test
    fun chats() = snapshot {
        HomeView(
            homeState = HomeStatePreviewParam().values.first(),
            onRoomClick = { _, _ -> },
            onSettingsClick = {},
            onAddAccountClick = {},
            onSwitchToAccount = {},
            onSetUpRecoveryClick = {},
            onConfirmRecoveryKeyClick = {},
            onStartChatClick = {},
            onCreateSpaceClick = {},
            onRoomSettingsClick = {},
            onMenuActionClick = {},
            onReportRoomClick = {},
            onDeclineInviteAndBlockUser = {},
            acceptDeclineInviteView = {},
            leaveRoomView = {},
        )
    }

    @Test
    fun conversationAndComposer() = snapshot {
        MessagesView(
            state = aMessagesState(),
            onBackClick = {},
            onRoomDetailsClick = {},
            onEventContentClick = { _, _ -> false },
            onGalleryEventItemClick = { _, _, _ -> false },
            onUserDataClick = {},
            onLinkClick = { _, _ -> },
            onSendLocationClick = {},
            onCreatePollClick = {},
            onJoinCallClick = {},
            onViewAllPinnedMessagesClick = {},
            onThreadsListClick = {},
            knockRequestsBannerView = {},
            composerEmojiPicker = {},
            customReactionBottomSheet = {},
        )
    }

    @Test
    fun settings() = snapshot {
        PreferencesRootView(
            state = aPreferencesRootState(),
            emojiPickerRenderer = NoOpEmojiPickerRenderer,
            onBackClick = {},
            onAddAccountClick = {},
            onSwitchToSession = {},
            onOpenAnalytics = {},
            onOpenLockScreenSettings = {},
            onOpenAbout = {},
            onOpenDeveloperSettings = {},
            onOpenMediaSettings = {},
            onOpenLocationSettings = {},
            onOpenLabs = {},
            onEditProfileClick = {},
            onSecureBackupClick = {},
            onManageAccountClick = {},
            onLinkNewDeviceClick = {},
            onOpenRageShake = {},
            onModerationAndSafetyClick = {},
            onOpenNotificationSettings = {},
            onSignOutClick = {},
            onDeactivateClick = {},
        )
    }

    @Test
    fun launcherIcon() = snapshot {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AndroidView(
                modifier = Modifier.size(108.dp),
                factory = { context ->
                    ImageView(context).apply {
                        setImageResource(R.mipmap.ic_launcher)
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                },
            )
        }
    }

    private fun snapshot(content: @Composable () -> Unit) {
        val palette = OkoPalette.branded(SemanticColorsLightDark.default)
        paparazzi.snapshot {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                ElementPreview(theme = theme, showBackground = false) {
                    ElementTheme(
                        theme = theme,
                        applySystemBarsUpdate = false,
                        compoundLight = palette.light,
                        compoundDark = palette.dark,
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(ElementTheme.colors.bgCanvasDefault)) {
                            content()
                        }
                    }
                }
            }
        }
    }
}
