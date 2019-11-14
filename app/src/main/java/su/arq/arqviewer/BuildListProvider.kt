package su.arq.arqviewer

import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.webcomunication.response.BuildListData

interface BuildListProvider {

    fun startLoading()

    var onBuildListLoaded: ((builds: BuildListData) -> Unit)?
    fun setOnBuildListLoadedListener( l: ((builds: BuildListData) -> Unit)){
        onBuildListLoaded = l
    }
    var onBuildListLoadingError: ((message: String) -> Unit)?
    fun setOnBuildListLoadingErrorListener(l: ((message: String) -> Unit)){
        onBuildListLoadingError = l
    }
}