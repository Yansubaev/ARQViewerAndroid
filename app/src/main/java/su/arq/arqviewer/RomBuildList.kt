package su.arq.arqviewer

import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.webcomunication.response.BuildListData

class RomBuildList(override val builds: Array<ARQBuild>) : BuildListData