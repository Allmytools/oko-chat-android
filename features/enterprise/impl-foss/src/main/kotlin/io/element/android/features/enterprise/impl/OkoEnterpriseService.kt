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
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.map

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

    override fun semanticColorsFlow(sessionId: SessionId?) =
        upstream.semanticColorsFlow(sessionId).map(OkoPalette::branded)
}
