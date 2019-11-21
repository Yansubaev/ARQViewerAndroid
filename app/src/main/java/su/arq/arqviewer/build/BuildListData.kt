package su.arq.arqviewer.build

import su.arq.arqviewer.build.entities.ARQBuild

interface BuildListData {
    val builds: Array<ARQBuild>
    val count: Int
        get() = builds.size
}