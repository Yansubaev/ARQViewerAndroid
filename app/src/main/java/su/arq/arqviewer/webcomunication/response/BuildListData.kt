package su.arq.arqviewer.webcomunication.response

import su.arq.arqviewer.entities.ARQBuild

interface BuildListData {
    val builds: Array<ARQBuild>
}