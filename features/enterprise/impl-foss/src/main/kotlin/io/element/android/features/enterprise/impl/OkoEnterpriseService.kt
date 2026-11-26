/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.features.enterprise.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.element.android.compound.colors.OkoPalette
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.androidutils.json.JsonProvider
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.matrix.api.TemporaryMatrixClientFactory
import io.element.android.libraries.matrix.api.accountprovider.AccountProvider
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.map

// Empty until the Matrix server name is approved.
private const val OKO_HOMESERVER = ""

@ContributesBinding(AppScope::class, replaces = [DefaultEnterpriseService::class])
class OkoEnterpriseService private constructor(
    private val upstream: DefaultEnterpriseService,
) : EnterpriseService by upstream {
    @Inject
    constructor(
        temporaryMatrixClientFactory: TemporaryMatrixClientFactory,
        jsonProvider: JsonProvider,
        dispatchers: CoroutineDispatchers,
    ) : this(DefaultEnterpriseService(temporaryMatrixClientFactory, jsonProvider, dispatchers))

    private val configuredHomeserver = OKO_HOMESERVER.takeIf(String::isNotEmpty)?.let { AccountProvider.Generic(it) }

    override fun accountProviderAllowList() =
        configuredHomeserver?.let(::listOf) ?: upstream.accountProviderAllowList()

    override fun canConnectToAnyAccountProvider() =
        configuredHomeserver == null && upstream.canConnectToAnyAccountProvider()

    override suspend fun isAllowedToConnectToAccountProvider(accountProvider: AccountProvider) =
        configuredHomeserver?.matches(accountProvider) ?: upstream.isAllowedToConnectToAccountProvider(accountProvider)

    override fun semanticColorsFlow(sessionId: SessionId?) =
        upstream.semanticColorsFlow(sessionId).map(OkoPalette::branded)
}
