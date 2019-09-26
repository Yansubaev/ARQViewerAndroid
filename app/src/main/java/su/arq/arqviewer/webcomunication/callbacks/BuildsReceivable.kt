package su.arq.arqviewer.webcomunication.callbacks

import su.arq.arqviewer.entities.ARQBuild

interface BuildsReceivable {
    fun receiveBuilds(build: Array<ARQBuild>)

    fun receiveError()
}